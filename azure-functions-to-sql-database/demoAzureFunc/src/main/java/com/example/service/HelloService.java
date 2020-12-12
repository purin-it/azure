package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Greeting;
import com.example.model.User;
import com.example.mybatis.UserDataMapper;
import com.example.mybatis.model.UserData;

@Service
public class HelloService {

	/** DBからユーザーデータを取得するMapperオブジェクト */
	@Autowired
	private UserDataMapper mapper;

	/**
	 * DBからユーザーデータを取得しGreetingオブジェクトを返却する
	 * @param user HttpRequestの引数のユーザー
	 * @return　Greetingオブジェクト
	 */
	public Greeting sayHello(User user) {
		UserData userData = mapper.getUserData(1);
		return new Greeting("Welcome, " + user.getName() + ", DBからの取得結果 : " + userData.toString());
	}

}
