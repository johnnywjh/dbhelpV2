package com.sesame.dbhelp.controller;

import com.sesame.common.annotation.ReqParamsCheck;
import com.sesame.common.controller.AbstractWebController;
import com.sesame.common.exception.BizException;
import com.sesame.common.response.Response;
import com.sesame.dbhelp.entity.DbInfo;
import com.sesame.dbhelp.entity.Table;
import com.sesame.dbhelp.service.DBService;
import com.sesame.dbhelp.service.DBServicePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/db")
public class DbController  extends AbstractWebController {

//    @ReqParamsCheck
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

}
