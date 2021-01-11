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
	 * 検索一覧画面を初期表示する.
	 * 
	 * @param model Modelオブジェクト
	 * @return 検索一覧画面
	 */
	@GetMapping("/")
	public String index(Model model) {
		return "main";
	}

	@PostMapping("/getUserPass")
	public String getUserPass(Model model) {
		// Azure FunctionsのgetUserPass関数を呼び出すためのヘッダー情報を設定する
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Azure FunctionsのgetUserPass関数を呼び出すための引数を設定する
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		// Azure FunctionsのgetUserPass関数を呼び出す
		ResponseEntity<String> response = restTemplate.exchange(demoAzureFuncBase + "getUserPass", HttpMethod.POST,
				entity, String.class);

		// Azure Functionsの呼出結果のユーザーデータ一覧を、検索条件Formに設定する
		try {
			GetUserPassResult getUserPassResult = objectMapper.readValue(response.getBody(), GetUserPassResult.class);
			if (getUserPassResult != null && getUserPassResult.getUserPass() != null) {
				model.addAttribute("getUserPass", getUserPassResult.getUserPass());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return "main";
	}

}