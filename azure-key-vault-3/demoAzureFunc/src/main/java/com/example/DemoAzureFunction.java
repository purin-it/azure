package com.example;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.GetKeySecretForm;
import com.example.model.GetKeySecretResult;
import com.example.service.GetKeySecretService;

@SpringBootApplication
public class DemoAzureFunction {

	/** シークレット取得サービスクラスのオブジェクト */
	@Autowired
	private GetKeySecretService getKeySecretService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}

	/**
	 * キーに対応するシークレットを取得し結果を返却する関数
	 * 
	 * @return シークレット取得サービスクラスの呼出結果
	 */
	@Bean
	public Function<GetKeySecretForm, GetKeySecretResult> getKeySecret() {
		return getKeySecretForm -> getKeySecretService.getKeySecret(getKeySecretForm);
	}
}
