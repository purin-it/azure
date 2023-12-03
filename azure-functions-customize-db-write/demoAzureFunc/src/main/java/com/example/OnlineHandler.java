package com.example;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.example.model.OnlineServiceParam;
import com.example.model.OnlineServiceResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class OnlineHandler extends  FunctionInvoker<OnlineServiceParam, OnlineServiceResult> {

	/**
	 * HTTP要求に応じて、HelloFunctionクラスのonlineメソッドを呼び出し、その戻り値をボディに設定したレスポンスを返す
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("online")
	public HttpResponseMessage execute(@HttpTrigger(name = "request", methods = HttpMethod.GET
	        , authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request
			, ExecutionContext context) {
		// リクエストパラメータからidの値を取得
		String paramId = request.getQueryParameters().get("id");
		
		// オンラインサービス呼出用Paramを生成
		OnlineServiceParam onlineServiceParam = new OnlineServiceParam();
		onlineServiceParam.setId(paramId);
		
		// handleRequestメソッド内でHelloFunctionクラスのonlineメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK)
				.body(handleRequest(onlineServiceParam, context))
				.header("Content-Type", "text/json").build();
	}
}
