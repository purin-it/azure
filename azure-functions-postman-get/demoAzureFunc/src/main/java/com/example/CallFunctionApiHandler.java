package com.example;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.example.model.SearchParam;
import com.example.model.SearchResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class CallFunctionApiHandler extends FunctionInvoker<SearchParam, SearchResult> {

	/**
	 * HTTP要求に応じて、DemoAzureFunctionクラスのcallFunctionApiメソッドを呼び出し、その戻り値をボディに設定したレスポンスを返す.
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("callFunctionApi")
	public HttpResponseMessage execute(
			@HttpTrigger(name = "request", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) {

		// リクエストパラメータ値を取得し、検索用パラメータに設定する
		SearchParam searchParam = new SearchParam();
		searchParam.setParam(request.getQueryParameters().get("param"));

		// handleRequestメソッド内でDemoAzureFunctionクラスのcallFunctionApiメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK).body(handleRequest(searchParam, context))
				.header("Content-Type", "text/json").build();
	}
}
