package com.example;

import java.util.Optional;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import com.example.model.Greeting;
import com.example.model.User;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class HelloHandler extends AzureSpringBootRequestHandler<User, Greeting> {

	/**
	 * HTTP要求に応じて、HelloFunctionクラスのhelloメソッドを呼び出し、その戻り値をボディに設定したレスポンスを返す
	 * 
	 * @param request リクエストオブジェクト
	 * @param context コンテキストオブジェクト
	 * @return レスポンスオブジェクト
	 */
	@FunctionName("hello")
	public HttpResponseMessage execute(@HttpTrigger(name = "request", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<User>> request,
			ExecutionContext context) {
		// リクエストオブジェクトからUserオブジェクトを取得する
		User user = request.getBody().filter((u -> u.getName() != null))
				.orElseGet(() -> new User(
						request.getQueryParameters().getOrDefault("name", "<no name supplied> please provide a name as "
								+ "either a query string parameter or in a POST body")));
		context.getLogger().info("Greeting user name: " + user.getName());
		// handleRequestメソッド内でHelloFunctionクラスのhelloメソッドを呼び出し、
		// その戻り値をボディに設定したレスポンスを、JSON形式で返す
		return request.createResponseBuilder(HttpStatus.OK).body(handleRequest(user, context))
				.header("Content-Type", "text/json").build();
	}
}
