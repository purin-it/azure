package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class DemoConfigBean {

	/**
	 * RestTemplateオブジェクトを作成する
	 * @return RestTemplateオブジェクト
	 */
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/**
	 * ObjectMapperオブジェクトを作成する
	 * @return ObjectMapperオブジェクト
	 */
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
}
