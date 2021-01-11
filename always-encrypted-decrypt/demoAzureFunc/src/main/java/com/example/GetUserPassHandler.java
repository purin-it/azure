package com.example;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import com.example.model.GetUserPassParam;
import com.example.model.GetUserPassResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetUserPassHandler extends AzureSpringBootRequestHandler<GetUserPassParam, GetUserPassResult> {

	/**
	 * HTTP要求に応じて、DemoAzureFunctionクラスのgetUserPassメソッドを呼び出し、その戻り値をボディに設定したレスポンスを返す
	 * 
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("getUserPass")
	public HttpResponseMessage execute(
			@HttpTrigger(name = "request", methods = HttpMethod.POST
				, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) {

		// handleRequestメソッド内でDemoAzureFunctionクラスのgetUserPassメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK).body(handleRequest(new GetUserPassParam(), context))
				.header("Content-Type", "text/json").build();
	}
}
