package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DemoController {

	/** RestTemplateオブジェクト */
	@Autowired
	private RestTemplate restTemplate;

	/** ObjectMapperオブジェクト */
	@Autowired
	private ObjectMapper objectMapper;

	/** application.propertiesからdemoAzureFunc.urlBaseの値を取得 */
	@Value("${demoAzureFunc.urlBase}")
	private String demoAzureFuncBase;

	/**
	 * メイン画面を初期表示する.
	 * @return メイン画面
	 */
	@GetMapping("/")
	public String index() {
		return "main";
	}

	/**
	 * Azure FunctionsのcallFunctionApi関数呼出処理.
	 * @param model Modelオブジェクト
	 * @return 次画面
	 */
	@RequestMapping("/callFunction")
	public String callFunction(Model model) {
		// Azure FunctionsのcallFunctionApi関数を呼び出すためのヘッダー情報を設定する
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Azure FunctionsのcallFunctionApi関数を呼び出すための引数を設定する
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		try {
			map.add("param", "DemoController callFunction calling.");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

		// Azure FunctionsのcallFunctionApi関数を呼び出す
		ResponseEntity<String> response = restTemplate.exchange(demoAzureFuncBase + "callFunctionApi", HttpMethod.POST,
				entity, String.class);

		// callFunctionApi関数の呼出結果を設定する
		SearchResult searchResult = null;
		try {
			searchResult = objectMapper.readValue(response.getBody(), SearchResult.class);
			if (searchResult != null) {
				model.addAttribute("result", searchResult.getResult());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return "next";
	}

	/**
	 * メイン画面に戻る処理.
	 * @return メイン画面
	 */
	@PostMapping("/backToMain")
	public String backToMain() {
		return "main";
	}
	
	/**
	 * エラー画面に遷移する処理.
	 * @return エラー画面
	 */
	@RequestMapping("/toError")
	public String toError() {
		return "error";
	}

}