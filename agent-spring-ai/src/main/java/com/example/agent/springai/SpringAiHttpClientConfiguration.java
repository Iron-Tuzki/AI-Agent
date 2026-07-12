package com.example.agent.springai;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Spring AI HTTP 客户端配置，用于统一设置千问接口普通调用和流式调用的网络超时时间。
 */
@Configuration
public class SpringAiHttpClientConfiguration {

    /**
     * 定制普通 Chat 调用使用的 RestClient 超时时间。
     *
     * @param connectTimeout 建立连接的超时时间
     * @param readTimeout 等待模型返回响应的读取超时时间
     * @return RestClient 定制器
     */
    @Bean
    public RestClientCustomizer springAiRestClientTimeoutCustomizer(
            @Value("${agent.spring-ai.connect-timeout:10s}") Duration connectTimeout,
            @Value("${agent.spring-ai.read-timeout:120s}") Duration readTimeout) {
        return builder -> {
            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(connectTimeout)
                    .build();
            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            requestFactory.setReadTimeout(readTimeout);
            builder.requestFactory(requestFactory);
        };
    }

    /**
     * 定制流式 Chat 调用使用的 WebClient 超时时间。
     *
     * @param connectTimeout 建立连接的超时时间
     * @param readTimeout 等待模型流式响应的读取超时时间
     * @return WebClient 定制器
     */
    @Bean
    public WebClientCustomizer springAiWebClientTimeoutCustomizer(
            @Value("${agent.spring-ai.connect-timeout:10s}") Duration connectTimeout,
            @Value("${agent.spring-ai.read-timeout:120s}") Duration readTimeout) {
        return builder -> {
            HttpClient httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(connectTimeout.toMillis()))
                    .responseTimeout(readTimeout);
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        };
    }
}
