package kim.sesame.common.config;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import cn.hutool.crypto.symmetric.AES;


@Configuration
public class AesConfiguration {

    @Bean
    public AES loadAes(AesConfig aesConfig){
        return new AES(Mode.CTS, Padding.PKCS5Padding,
                // 密钥，可以自定义
                aesConfig.getKey().getBytes(),
                // iv加盐，按照实际需求添加
                aesConfig.getIv().getBytes()
        );
    }
}
