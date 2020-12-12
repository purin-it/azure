package com.example;

import com.example.model.Greeting;
import com.example.model.User;
import com.example.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class HelloFunction {

	/** サービスクラスのオブジェクト */
	@Autowired
	private HelloService helloService;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HelloFunction.class, args);
	}

	/**
	 * Userオブジェクトを引数に、サービスクラスの呼出結果を返却する関数
	 * @return サービスクラスの呼出結果
	 */
	@Bean
	public Function<User, Greeting> hello() {
		return user -> helloService.sayHello(user);
	}
}
