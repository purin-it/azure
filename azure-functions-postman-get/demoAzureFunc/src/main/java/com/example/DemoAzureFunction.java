package com.example;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.SearchParam;
import com.example.model.SearchResult;

@SpringBootApplication
public class DemoAzureFunction {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}
	
	/**
	 * API呼出結果を返却する関数.
	 * @return API呼出結果
	 */
	@Bean
	public Function<SearchParam, SearchResult> callFunctionApi(){
		return searchParam -> new SearchResult("param:" + searchParam + ", result:success");
	}
}
