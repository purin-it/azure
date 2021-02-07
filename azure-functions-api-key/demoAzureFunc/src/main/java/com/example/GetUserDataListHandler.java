package com.example;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import com.example.model.SearchForm;
import com.example.model.SearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetUserDataListHandler extends AzureSpringBootRequestHandler<SearchForm, SearchResult> {

	/**
	 * HTTP要求に応じて、DemoAzureFunctionクラスのgetUserDataListメソッドを呼び出し、その戻り値をボディに設定したレスポンスを返す
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("getUserDataList")
	public HttpResponseMessage execute(@HttpTrigger(name = "request", methods = HttpMethod.POST
	      , authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) {

		// リクエストオブジェクトからパラメータ値を取得し、検索条件Formに設定する
		ObjectMapper mapper = new ObjectMapper();
		String jsonParam = request.getBody().get();
		jsonParam = jsonParam.replaceAll("\\[", "").replaceAll("\\]", "");

		SearchForm searchForm = new SearchForm();
		try {
			searchForm = mapper.readValue(jsonParam, new TypeReference<SearchForm>() {});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// handleRequestメソッド内でDemoAzureFunctionクラスのgetUserDataListメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK).body(handleRequest(searchForm, context))
				.header("Content-Type", "text/json").build();
	}
}
