package com.sesame.dbhelp.run;

import com.sesame.dbhelp.config.BaseConfig;
import com.sesame.dbhelp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 系统启动加载数据
 */
@Slf4j
@Component
public class LoadTheme implements ApplicationRunner {

    @Autowired
    private BaseConfig baseConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        log.info("\n----------------------------------------------------------\n\t" +
                "BaseConfig 配置:\n" +
                "basePath: " + baseConfig.getBasePath() + "\n" +
                "themeDicName: " + baseConfig.getThemeDicName() + "\n" +
                "gitEnable: " + baseConfig.getBasePath() + "\n" +
                "git:url: " + baseConfig.getGitUrl() + "\n" +
                "git:user: " + baseConfig.getGitUser() + "\n" +
                "git:pwd: " + baseConfig.getGitPwd() + "\n" +
                "----------------------------------------------------------");

        if (!baseConfig.isGitEnable()) {
            return;
        }

        String userPath = baseConfig.getBasePath();
        String path = userPath + "/" + baseConfig.getThemeDicName();
        File file = new File(path);
        if (file.exists()) {
            FileUtil.cleanDir(file);
            log.info("删除路径成功: {}", path);
        } else {
            file.mkdir();
            log.info("创建路径成功: {}", path);
        }

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(baseConfig.getGitUrl())
                .setDirectory(file);
        if (StringUtils.isNotEmpty(baseConfig.getGitUser())) {
            cloneCommand.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(
                            baseConfig.getGitUser(), baseConfig.getGitPwd()
                    )
            );
        }
        if (StringUtils.isNotEmpty(baseConfig.getBranch())) {
            cloneCommand.setBranch(baseConfig.getBranch());
        }
        cloneCommand.call();
        log.info("git clone 成功: {}", path);
    }
}
