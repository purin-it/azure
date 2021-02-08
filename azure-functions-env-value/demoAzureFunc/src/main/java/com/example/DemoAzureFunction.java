package com.example;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.SearchForm;
import com.example.model.SearchResult;
import com.example.service.SearchService;

@SpringBootApplication
public class DemoAzureFunction {

	/** ユーザーデータ取得サービスクラスのオブジェクト */
	@Autowired
	private SearchService searchService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}

	/**
	 * DBから検索条件に合うユーザーデータを取得し結果を返却する関数
	 * @return ユーザーデータ取得サービスクラスの呼出結果
	 */
	@Bean
	public Function<SearchForm, SearchResult> getUserDataList() {
		return searchForm -> searchService.getUserDataList(searchForm);
	}
}
