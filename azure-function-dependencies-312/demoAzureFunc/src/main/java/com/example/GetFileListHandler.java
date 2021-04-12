package com.example;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.example.model.GetFileListParam;
import com.example.model.GetFileListResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetFileListHandler extends FunctionInvoker<GetFileListParam, GetFileListResult> {

	/**
	 * HTTP要求に応じて、DemoAzureFunctionクラスのfileUploadメソッドを呼び出し、その戻り値をボディに設定したレスポンスを返す.
	 * 
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("getFileList")
	public HttpResponseMessage execute(
			@HttpTrigger(name = "request", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) {

		// handleRequestメソッド内でDemoAzureFunctionクラスのfileUploadメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK).body(handleRequest(new GetFileListParam(), context))
				.header("Content-Type", "text/json").build();
	}
}
