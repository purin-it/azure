package com.example;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.OnlineServiceParam;
import com.example.model.OnlineServiceResult;
import com.example.model.TimerTriggerParam;
import com.example.model.TimerTriggerResult;
import com.example.service.OnlineService;
import com.example.service.TimerTriggerService;

@SpringBootApplication
public class DemoAzureFunction {

	/** タイマートリガーのテストを行うサービスクラスのオブジェクト */
	@Autowired
	private TimerTriggerService timerTriggerService;
	
	/** オンライン処理を行うサービスクラスのオブジェクト */
	@Autowired
	private OnlineService onlineService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}

	/**
	 * タイマートリガーのテストを行い結果を返却する関数
	 * @return タイマートリガーのテストを行うサービスクラスの呼出結果
	 */
	@Bean
	public Function<TimerTriggerParam, TimerTriggerResult> timerTriggerTest() {
		return timerTriggerParam -> timerTriggerService.timerTriggerTest(timerTriggerParam);
	}

	/**
	 * オンライン処理を行い結果を返却する関数
	 * @return オンライン処理を行うサービスクラスの呼出結果
	 */
	@Bean
	public Function<OnlineServiceParam, OnlineServiceResult> online(){
		return onlineServiceParam -> onlineService.online(onlineServiceParam);
	}
}
