package com.example;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.EventGridTriggerParam;
import com.example.model.EventGridTriggerResult;
import com.example.service.EventGridTriggerService;

@SpringBootApplication
public class DemoAzureFunction {
	
	/** イベントグリッドトリガーのテストを行うサービスクラスのオブジェクト */
	@Autowired
	private EventGridTriggerService eventGridTriggerService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}
	
	/**
	 * イベントグリッドトリガーのテストを行い結果を返却する関数
	 * @return イベントグリッドトリガーのテストを行うサービスクラスの呼出結果
	 */
	@Bean
	public Function<EventGridTriggerParam, EventGridTriggerResult> eventGridTriggerTest(){
		return eventGridTriggerParam -> eventGridTriggerService.eventGridTriggerTest(eventGridTriggerParam);
	}

}
