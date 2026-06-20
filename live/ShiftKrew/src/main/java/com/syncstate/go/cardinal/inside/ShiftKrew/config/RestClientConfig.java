package com.syncstate.go.cardinal.inside.ShiftKrew.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
    @Value("${request.connect-timeout}")
    private int connectTimeout;
    @Value("${request.read-timeout}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        /*HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setInterceptors(Collections.singletonList(new LogInterceptor()));*/

        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(connectTimeout);
        rf.setReadTimeout(readTimeout);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(rf));


        return restTemplate;
    }
}
