package com.sesame.dbhelp.controller;

import com.alibaba.fastjson.JSONObject;
import com.sesame.common.annotation.ReqParamsCheck;
import com.sesame.common.controller.AbstractWebController;
import com.sesame.common.exception.BizException;
import com.sesame.common.response.Response;
import com.sesame.dbhelp.config.BaseConfig;
import com.sesame.dbhelp.entity.Column;
import com.sesame.dbhelp.entity.DbInfo;
import com.sesame.dbhelp.entity.DirVo;
import com.sesame.dbhelp.entity.Table;
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

@Slf4j
@RestController
@RequestMapping("/api/db")
public class DbController extends AbstractWebController {

    @Autowired
    private BaseConfig baseConfig;

    @ReqParamsCheck
    @PostMapping("/getTables")
    public Response getTables(@RequestBody DbInfo bean) {

        bean.viferyDbType();
        Connection conn = DBService.getConn(bean);

        if (conn == null) {
            throw new BizException("数据库连接失败");
        } else {

            List<Table> list = DBServicePool.getDbService(bean.getDbDriver()).getTables(conn);

            DBService.closeConn(conn);

            return returnSuccess(list);
        }

    }

    @ReqParamsCheck
    @PostMapping("/searchTableDetail")
    public Response searchTableDetail(@RequestBody DbInfo bean) {
        bean.viferyDbType();
        List<Column> list = new ArrayList<>();

        String tableName = bean.getTableName();
        if (bean == null || StringUtils.isEmpty(tableName)) {
            return returnSuccess(list);
        }
        Connection conn = DBService.getConn(bean);

        Table table = DBServicePool.getDbService(bean.getDbDriver()).parseTable(tableName, conn);

        DBService.closeConn(conn);
        return returnSuccess(table);
    }

    /**
     * 查询这个数据库的所有表以及表结构
     *
     * @param bean
     * @return
     */
    @RequestMapping("/queryDbTAbleInfo")
    public Response queryDbTAbleInfo(@RequestBody DbInfo bean) {
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

            return returnSuccess(list);
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
    public Response preview(@RequestBody DbInfo bean, HttpServletRequest request, HttpServletResponse response) {
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

        return returnSuccess(map);
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
        List<ThemeVo> fileList = Theme.getTheme(bean.getFkType());

        Connection conn = DBService.getConn(bean);

        Map<String, String> exMap = bean.getExMap();
        if (exMap != null) {
            log.info("扩展参数 : {}", JSONObject.toJSONString(exMap));
        }
        HashMap<String, Object> params = null; // 封装参数

        for (Table t : bean.getTables()) {

            params = new HashMap<>();
            if (t.getClassName() == null || t.getClassName().equals("")) {
                t.setClassName(TableUtil.hump(t.getClassName(), true));
            }
            Table table = DBServicePool.getDbService(bean.getDbDriver()).parseTable(t.getTableName(), conn);
            List<Column> list = table.getColumns();
            if (list == null || list.size() == 0) {
                System.out.println("**********  " + t.getTableName() + " 表不存在,注意表名区分大小写    *******************************");
                continue;
            }
            String fileAll = TableUtil.fileAll(list);
            params.put("fileAll", fileAll);
            String fileAllSelect = TableUtil.fileAllSelect(list);
            params.put("fileAllSelect", fileAllSelect);

            params.put("tableName", t.getTableName());//
            params.put("className", t.getClassName());//
            params.put("systime", DateUtil.formatString(new Date()));// 注释上的时间
            params.put("list", list);// 表结构
            params.put("fk", TableUtil.getPrimary(list));// 表的主键
            params.put("fk_java", TableUtil.hump(params.get("fk").toString(), false));// 表的主键
            params.put("tableComment", t.getComment());// 表注释
            params.put("dir1", t.getDir1().toLowerCase());
            params.put("dir2", t.getDir2().toLowerCase());
            params.put("remarkVal", t.getRemarkVal());

            if (exMap != null) {
                params.putAll(exMap);
            }

            for (ThemeVo vo : fileList) {
                Generate.file(params, vo, path, bean.isTableNameGruop());
            }

        } // for
        // 循环完之后关闭数据库连接
        DBService.closeConn(conn);
    }

    @RequestMapping("/getnode")
    @ResponseBody
    public Response getnode(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

        String outPath = (String) request.getSession().getAttribute("outPath");
        String fileDir = (String) request.getSession().getAttribute("fileDir");

        File file = new File(outPath);
        List<Map> list = searchfile(file, null);

        return returnSuccess(list);
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
    public Response getfilecontent(String path) {

        List<String> list;
        try {
            path = StringUtil.removeQuotes(path);
            list = org.apache.commons.io.FileUtils.readLines(new File(path), "UTF-8");
        } catch (IOException e) {
            list = new ArrayList<String>();
            e.printStackTrace();
        }

        return returnSuccess(list);
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
}
