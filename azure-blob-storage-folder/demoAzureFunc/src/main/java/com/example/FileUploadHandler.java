package com.example;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import org.springframework.http.MediaType;

import com.example.model.FileUploadParam;
import com.example.model.FileUploadResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class FileUploadHandler 
    extends FunctionInvoker<FileUploadParam, FileUploadResult> {

	/**
	 * HTTP要求に応じて、DemoAzureFunctionクラスのfileUploadメソッドを呼び出し、
	 * その戻り値をボディに設定したレスポンスを返す.
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("fileUpload")
	public HttpResponseMessage execute(
			@HttpTrigger(name = "request"
			    , methods = HttpMethod.POST
			    , authLevel = AuthorizationLevel.ANONYMOUS) 
			  HttpRequestMessage<Optional<FileUploadParam>> request,
			ExecutionContext context) {

		// リクエストオブジェクトからFileUploadParamオブジェクトを取得する
		FileUploadParam fileUploadParam = request.getBody().get();

		// handleRequestメソッド内でDemoAzureFunctionクラスのfileUploadメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK)
				.body(handleRequest(fileUploadParam, context))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}
