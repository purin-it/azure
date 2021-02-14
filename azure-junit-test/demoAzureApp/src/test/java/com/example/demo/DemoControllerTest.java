package com.example.demo;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DemoControllerTest {
	
	/**
	 * テスト対象のクラス
	 */
	@Autowired
	private DemoController demoController;

	/**
	 * テスト対象のクラス内で呼ばれるクラスのMockオブジェクト
	 */
	@Autowired
	private RestTemplate restTemplate;
	
	/**
     * MockMvcオブジェクト
     */
	private MockMvc mockMvc;
	
	/**
	 * RestTemplateクラスのメソッドをMock化するためのサーバー
	 */
	private MockRestServiceServer mockServer;
	
	/** application.propertiesからdemoAzureFunc.urlBaseの値を取得 */
	@Value("${demoAzureFunc.urlBase}")
	private String demoAzureFuncBase;

	/**
	 * 前処理(各テストケースを実行する前に行われる処理)
	 */
	@Before
	public void init() {
		// MockMvcオブジェクトにテスト対象メソッドを設定
		mockMvc = MockMvcBuilders.standaloneSetup(demoController).build();
		
		// テスト対象のクラスで使用するRestTemplateクラスのメソッドをMock化するためのサーバーを設定
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	public void testIndex() throws Exception {
		// テスト対象メソッド(index)を実行
		mockMvc.perform(get("/"))
		          // HTTPステータスがOKであることを確認
				.andExpect(status().isOk())
				  // 次画面の遷移先がlist.htmlであることを確認
				.andExpect(view().name("list"))
				  // Modelオブジェクトに検索Formが設定されていることを確認
				.andExpect(model().attribute("searchForm", new SearchForm()))
				  // Modelオブジェクトにエラーが無いことを確認
				.andExpect(model().hasNoErrors());
	}

	@Test
	public void testSearch() throws Exception {
		// Azure FunctionsのgetUserDataList関数を呼び出した結果をMock化
		mockServer.expect(requestTo(demoAzureFuncBase + "getUserDataList"))
			.andExpect(method(HttpMethod.POST)) // リクエストヘッダ内容の検証
			.andExpect(content().string("{\"searchName\":[\"\"],\"searchSex\":[\"\"]}"))
			.andRespond(withSuccess(makeGetUserDataListRes(), MediaType.APPLICATION_JSON));
		
		// テスト対象メソッド(search)を実行
		mockMvc.perform(post("/search/")
				  // 検索条件のForm値を設定
				.param("searchName", "")
				.param("searchSex", ""))
		          // HTTPステータスがOKであることを確認
				.andExpect(status().isOk())
				  // 次画面の遷移先がlist.htmlであることを確認
				.andExpect(view().name("list"))
				  // Modelオブジェクトの検索Formに
				  // getUserDataList関数を呼び出した結果が設定されていることを確認
				.andExpect(model().attribute("searchForm", makeSearchFormRes()))
				  // Modelオブジェクトにエラーが無いことを確認
				.andExpect(model().hasNoErrors());
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
	
	/**
	 * Azure FunctionsのgetUserDataList関数を呼び出した結果となる文字列を生成
	 * @return 生成した文字列
	 */
	private String makeGetUserDataListRes() {
		String jsonResponseBody = null;
		SearchResult searchResult = new SearchResult();
		searchResult.setUserDataList(makeUserDataList());
		try { 
			ObjectMapper objectMapper = new ObjectMapper(); 
			jsonResponseBody = objectMapper.writeValueAsString(searchResult);
		} catch (Exception ex) {
			System.err.println(ex); 
		}
		return jsonResponseBody;
	}

	/**
	 * Azure FunctionsのgetUserDataList関数を呼び出した結果となるFormオブジェクトを生成
	 * @return 生成したFormオブジェクト
	 */
	private SearchForm makeSearchFormRes() {
		SearchForm resultSearchForm = new SearchForm();
		resultSearchForm.setSearchName("");
		resultSearchForm.setSearchSex("");
		resultSearchForm.setUserDataList(makeUserDataList());
		return resultSearchForm;
	}
}
