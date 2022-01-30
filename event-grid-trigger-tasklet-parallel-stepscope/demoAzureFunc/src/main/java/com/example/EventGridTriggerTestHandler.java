package com.example;

import java.time.LocalDateTime;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.example.model.EventGridTriggerParam;
import com.example.model.EventGridTriggerResult;
import com.example.model.EventSchema;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

public class EventGridTriggerTestHandler extends FunctionInvoker<EventGridTriggerParam, EventGridTriggerResult> {

	/**
	 * EventGridTriggerによって、DemoAzureFunctionクラスのeventGridTriggerTestメソッドを呼び出す.
	 * @param event   EventGridTriggerイベント情報
	 * @param context コンテキストオブジェクト
	 */
	@FunctionName("eventGridTriggerTest")
	public void eventGridTriggerTest(@EventGridTrigger(name = "event") EventSchema event,
			final ExecutionContext context) {
		context.getLogger().info("EventGridTriggerTestHandler eventGridTriggerTest triggered: " + event.eventTime);

		// EventGridTriggerイベント情報から、Blob Storageの読み込むファイル名を取得し、
		// EventGridTriggerParamに設定する
		EventGridTriggerParam param = new EventGridTriggerParam();
		String subject = event.subject;
		param.setFileName(subject.substring(subject.lastIndexOf("/") + 1));
		param.setTimerInfo(LocalDateTime.now().toString());

		handleRequest(param, context);
	}
	
	/**
	 * EventGridTriggerによって、DemoAzureFunctionクラスのeventGridTriggerTestメソッドを呼び出す.
	 * @param event   EventGridTriggerイベント情報
	 * @param context コンテキストオブジェクト
	 */
	/* @FunctionName("eventGridTriggerTest")
	public void eventGridTriggerTest(@EventGridTrigger(name = "event") String event,
			final ExecutionContext context) {
		// EventGridTriggerイベント情報をログに出力する
		context.getLogger().info("EventGridTriggerTestHandler eventGridTriggerTest triggered: " + LocalDateTime.now().toString());
		context.getLogger().info(event);
	} */
}
