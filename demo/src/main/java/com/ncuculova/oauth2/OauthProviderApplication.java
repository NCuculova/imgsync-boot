package com.ncuculova.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;

@SpringBootApplication
public class OauthProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthProviderApplication.class, args);
    }
}
