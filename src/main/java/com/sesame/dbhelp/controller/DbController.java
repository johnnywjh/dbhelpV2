package com.sesame.dbhelp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sesame.dbhelp.entity.*;
import kim.sesame.common.annotation.ReqParamsCheck;
import kim.sesame.common.result.ApiResult;
import kim.sesame.common.web.controller.AbstractWebController;
import kim.sesame.common.exception.BizException;
import com.sesame.dbhelp.config.BaseConfig;
import com.sesame.dbhelp.service.DBService;
import com.sesame.dbhelp.service.DBServicePool;
import com.sesame.dbhelp.service.Generate;
import com.sesame.dbhelp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/db")
public class DbController extends AbstractWebController {

    @Autowired
    private Theme theme;
    @Autowired
    private BaseConfig baseConfig;

    @ReqParamsCheck
    @PostMapping("/getTables")
    public ApiResult getTables(@RequestBody DbInfo bean) {

        bean.viferyDbType();
        Connection conn = DBService.getConn(bean);

        if (conn == null) {
            throw new BizException("数据库连接失败");
        } else {

            List<Table> list = DBServicePool.getDbService(bean.getDbDriver()).getTables(conn);

            DBService.closeConn(conn);

            return success(list);
        }

    }

    @ReqParamsCheck
    @PostMapping("/searchTableDetail")
    public ApiResult searchTableDetail(@RequestBody DbInfo bean) {
        bean.viferyDbType();
        List<Column> list = new ArrayList<>();

        String tableName = bean.getTableName();
        if (bean == null || StringUtils.isEmpty(tableName)) {
            return success(list);
        }
        Connection conn = DBService.getConn(bean);

        Table table = DBServicePool.getDbService(bean.getDbDriver()).parseTable(tableName, conn);

        DBService.closeConn(conn);
        return success(table);
    }

    /**
     * 查询这个数据库的所有表以及表结构
     *
     * @param bean
     * @return
     */
    @RequestMapping("/queryDbTAbleInfo")
    public ApiResult queryDbTAbleInfo(@RequestBody DbInfo bean) {
        bean.viferyDbType();
        Connection conn = DBService.getConn(bean);

        if (conn == null) {
            throw new BizException("数据库连接失败");
        } else {

            List<Table> list = DBServicePool.getDbService(bean.getDbDriver()).getTables(conn);

            list.parallelStream().forEach(l -> {
                Table table = DBServicePool.getDbService(bean.getDbDriver()).parseTable(l.getTableName(), conn);
                l.setColumns(table.getColumns());
                l.setDdl(table.getDdl());
                log.info(l.getTableName());
            });

            DBService.closeConn(conn);

            return success(list);
        }
    }

    @RequestMapping("/generate")
    public void generate(@RequestBody DbInfo bean, HttpServletRequest request, HttpServletResponse response) {
        bean.viferyDbType();
        String basePath = getFileDir("download");
        String systime = DateUtil.formatString(new Date(), "yyyyMMddHHmmss");
        String fileDir = bean.getFkType() + "_" + systime; // 文件夹的名字

        generateCode(bean, basePath, fileDir);

        try {

            String outPath = basePath + "/" + fileDir;

            // 文件压缩
            ZipCompressor zc = new ZipCompressor(outPath + ".zip");
            zc.compress(outPath);

            // 实现文件下载
            response.setContentType("text/plain");
            response.setHeader("Location", fileDir + ".zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileDir + ".zip");
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = new FileInputStream(outPath + ".zip");

            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            outputStream.write(b);

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // 删除源文件
            FileUtil.clearFiles(new File(outPath));
            FileUtil.clearFiles(new File(outPath + ".zip"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/preview")
    @ResponseBody
    public ApiResult preview(@RequestBody DbInfo bean, HttpServletRequest request, HttpServletResponse response) {
        bean.viferyDbType();
        String basePath = getFileDir("preview");// preview
        String systime = DateUtil.formatString(new Date(), "yyyyMMddHHmmss");
        String fileDir = bean.getFkType() + "_" + systime; // 文件夹的名字

        generateCode(bean, basePath, fileDir);

        String outPath = basePath + "/" + fileDir;
        request.getSession().setAttribute("outPath", outPath);
        request.getSession().setAttribute("fileDir", fileDir);

        File file = new File(outPath);
        List<Map> list = searchfile(file, null);

        Map<String, Object> map = new HashMap<>();
        map.put("dirVo", new DirVo(outPath, fileDir));
        map.put("list", list);

        return success(map);
    }

    public String getFileDir(String name) {
        String basePath = baseConfig.getBasePath() + "/dbhelp/" + name;
        File f = new File(basePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return basePath;
    }

    private void generateCode(DbInfo bean, String basePath, String fileDir) {

        String path = basePath + "/" + fileDir;
        String resourcePath = theme.getResourcePath();
        List<ThemeVo> fileList = Theme.getTheme(bean.getFkType());
        String fkType = bean.getFkType();
        Connection conn = DBService.getConn(bean);

        Map<String, String> exMap = bean.getExMap();
        if (exMap != null) {
            log.info("扩展参数 : {}", JSONObject.toJSONString(exMap));
        }
        LinkedHashMap<String, Object> params = null; // 封装参数

        for (Table t : bean.getTables()) {

            params = new LinkedHashMap<>();
            if (t.getClassName() == null || t.getClassName().equals("")) {
                t.setClassName(TableUtil.hump(t.getClassName(), true));
            }
            Table table = DBServicePool.getDbService(bean.getDbDriver()).parseTable(t.getTableName(), conn);
            List<Column> list = table.getColumns();
            if (list == null || list.size() == 0) {
                log.info("**********  " + t.getTableName() + " 表不存在,注意表名区分大小写    *******************************");
                continue;
            }
            String fieldAll = list.stream().map(Column::getName).collect(Collectors.joining(","));
            String fieldAllSelect = list.stream().map(l -> l.getName() + " " + l.getJavaName()).collect(Collectors.joining(","));
//            List<String> fieldNameList = list.stream().map(Column::getName).collect(Collectors.toList());
            List<String> javaNameList = list.stream().map(Column::getJavaName).collect(Collectors.toList());

            //判断字段时候存在
            params.put("fieldAll", fieldAll);
            params.put("fieldAllSelect", fieldAllSelect);
            List<String> commonFieldList = Arrays.asList(theme.getThemeMap().get(fkType).split(","));
            for (String javaName : commonFieldList) {
                params.put("is_" + javaName, javaNameList.contains(javaName));
            }

            // 主键
            String fk = TableUtil.getPrimary(list);
            String fkJava = TableUtil.hump(fk, false);

            // 区分字段类型,为了模板里好使用
            for (Column c : list) {
                if (fk.equals(c.getName())) {
                    c.setFieldType(1);
                    params.put("fkJavaType", c.getJavaType());// 主键的java类型
                } else if (!commonFieldList.contains(c.getJavaName())) {
                    c.setFieldType(2);
                } else {
                    c.setFieldType(3);
                }
            }

            params.put("packagePath", bean.getPackagePath());//
            if (StringUtils.isNotEmpty(bean.getModelName())) {
                params.put("modelName", "." + bean.getModelName());//
            } else {
                params.put("modelName", "");//
            }
            params.put("tableName", t.getTableName());//
            params.put("className", t.getClassName());//
            params.put("sysdatetime", cn.hutool.core.date.DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));// 注释上的时间
            params.put("sysdate", cn.hutool.core.date.DateUtil.format(new Date(), "yyyy-MM-dd"));// 注释上的时间
            params.put("javaNameList", javaNameList);// 表结构
            params.put("list", list);// 表结构
            params.put("fk", fk);// 表的主键
            params.put("fkJava", fkJava);// 表的主键
            params.put("tableComment", t.getComment());// 表注释
            params.put("dir1", t.getDir1().toLowerCase());
            params.put("dir2", t.getDir2().toLowerCase());
            params.put("remarkVal", t.getRemarkVal());

            params.put("strUtil", new BeetlStringUtil());
            if (exMap != null) {
                params.putAll(exMap);
            }

            // 把全部的参数按顺序整理一下,重新放入参数里去
            JSONObject paramsJson = new JSONObject(true);
            paramsJson.putAll(params);
            String paramsAll = JSON.toJSONString(paramsJson,
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat
            );
            params.put("paramsAll", paramsAll);

            for (ThemeVo vo : fileList) {
                Generate.file(params, vo, path, bean.isTableNameGruop());
            }

        } // for
        // 循环完之后关闭数据库连接
        DBService.closeConn(conn);
    }

    @RequestMapping("/getnode")
    @ResponseBody
    public ApiResult getnode(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

        String outPath = (String) request.getSession().getAttribute("outPath");
        String fileDir = (String) request.getSession().getAttribute("fileDir");

        File file = new File(outPath);
        List<Map> list = searchfile(file, null);

        return success(list);
    }

    public static List<Map> searchfile(File file, String parentId) {
        List<Map> listAll = new ArrayList<Map>();
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];

            String path = f.getAbsolutePath();
            String key = path.hashCode() + "";

            Map map = new HashMap();
            map.put("key", key);
            map.put("parentId", parentId);
            map.put("title", f.getName());
            map.put("basicData", path);
            map.put("dir", f.isDirectory());

            if (f.isDirectory()) {
                List<Map> list = searchfile(f, key);
                map.put("children", list);
            }
            listAll.add(map);
        }
        return listAll;
    }

    /**
     * 读取文件
     */
    @RequestMapping("/getfilecontent")
    @ResponseBody
    public ApiResult getfilecontent(String path) {

        List<String> list;
        try {
            path = StringUtil.removeQuotes(path);
            list = org.apache.commons.io.FileUtils.readLines(new File(path), "UTF-8");
        } catch (IOException e) {
            list = new ArrayList<String>();
            e.printStackTrace();
        }

        return success(list);
    }

    /**
     * 删除预览锁产生的文件
     */
    @RequestMapping("/deletedir")
    @ResponseBody
    public int deletedir(@RequestBody DirVo vo, HttpServletRequest request) {

        String outPath = (String) request.getSession().getAttribute("outPath");
        String fileDir = (String) request.getSession().getAttribute("fileDir");

        // System.out.println(outPath);
        // System.out.println(fileDir);
        //
        FileUtil.clearFiles(new File(vo.getOutPath()));

        return 1;
    }

    @PostMapping("/queryDbTable")
    public ApiResult queryDbTable(@RequestBody QueryDbTableReq req) {
        DbInfo bean = new DbInfo();
        bean.setUrl(req.getUrl());
        bean.setName(req.getName());
        bean.setPwd(req.getPwd());
        bean.viferyDbType();
        Connection conn = DBService.getConn(bean);

        if (conn == null) {
            throw new BizException("数据库连接失败");
        } else {
            List list = null;
            try {
                list = DBServicePool.getDbService(bean.getDbDriver()).queryTableData(req, conn);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DBService.closeConn(conn);
            }
            return success(list);
        }
    }
}
