package com.example;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import com.example.model.SearchForm;
import com.example.model.SearchResult;
import com.example.mybatis.model.UserData;

public class GetUserDataListHandlerTest {

	/**
	 * GetUserDataListHandlerクラスのexecuteメソッドをテストするメソッド
	 * @throws Exception
	 */
	@Test
	public void executeTest() {
		AzureSpringBootRequestHandler<SearchForm, SearchResult> handler = new AzureSpringBootRequestHandler<>(
				DemoAzureFunction.class);
		SearchResult result = handler.handleRequest(new SearchForm(), null);
		handler.close();
		
		// 取得内容をコンソールに表示
        System.out.println("*** result.getUserDataList()の実行結果 ***");
        for(UserData userData : result.getUserDataList()){
            System.out.println(userData.toString());
        }
        System.out.println();
        
        // 取得結果を確認
		assertEquals(3, result.getUserDataList().size());
		assertEquals(makeUserDataList().toString(), result.getUserDataList().toString());
	}

	/**
	 * 返却されるユーザーデータリストを生成
	 * @return ユーザーデータリスト
	 */
	private ArrayList<UserData> makeUserDataList() {
		ArrayList<UserData> userDataList = new ArrayList<>();

		UserData userData = new UserData();
		userData.setId("1");
		userData.setName("テスト　プリン");
		userData.setBirthYear("2012");
		userData.setBirthMonth("1");
		userData.setBirthDay("15");
		userData.setSex("女");
		userDataList.add(userData);

		userData = new UserData();
		userData.setId("2");
		userData.setName("テスト　プリン２");
		userData.setBirthYear("2013");
		userData.setBirthMonth("2");
		userData.setBirthDay("16");
		userData.setSex("男");
		userDataList.add(userData);

		userData = new UserData();
		userData.setId("3");
		userData.setName("テスト　プリン３");
		userData.setBirthYear("2014");
		userData.setBirthMonth("3");
		userData.setBirthDay("17");
		userData.setSex("女");
		userDataList.add(userData);

		return userDataList;
	}
}
