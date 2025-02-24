package Jeans.Jeans.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class FastApiConfig {

    @Value("${fastapi.url}")
    private String fastApiUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
