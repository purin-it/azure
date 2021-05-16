package com.example.demo;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DemoCustomConfigration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        // HTTP 429(Too Many Requests)エラーが発生した場合に、パス「/toError」に遷移するよう設定
        factory.addErrorPages(new ErrorPage(HttpStatus.TOO_MANY_REQUESTS, "/toError"));
    }

}