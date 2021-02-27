package com.example.demo;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

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
	 * @param model Modelオブジェクト
	 * @return メイン画面
	 */
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "アップロードするファイルを指定し、アップロードボタンを押下してください。");
		return "main";
	}

	/**
	 * ファイルデータをAzure Blob Storageに登録する.
	 * @param uploadFile アップロードファイル
	 * @param model      Modelオブジェクト
	 * @return メイン画面
	 */
	@PostMapping("/upload")
	public String add(@RequestParam("upload_file") MultipartFile uploadFile, Model model) {
		// ファイルが未指定の場合はエラーとする
		if (uploadFile == null || StringUtils.isEmptyOrWhitespace(uploadFile.getOriginalFilename())) {
			model.addAttribute("errMessage", "ファイルを指定してください。");
			return "main";
		}

		// Azure FunctionsのfileUpload関数を呼び出すためのヘッダー情報を設定する
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Azure FunctionsのfileUpload関数を呼び出すための引数を設定する
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		try {
			map.add("fileName", uploadFile.getOriginalFilename());
			map.add("fileData", IOUtils.toByteArray(uploadFile.getInputStream()));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

		// Azure FunctionsのfileUpload関数を呼び出す
		ResponseEntity<String> response = restTemplate.exchange(demoAzureFuncBase + "fileUpload", HttpMethod.POST,
				entity, String.class);

		// ファイルアップロード処理完了のメッセージを設定する
		FileUploadResult fileUploadResult = null;
		try {
			fileUploadResult = objectMapper.readValue(response.getBody(), FileUploadResult.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// メイン画面へ遷移
		model.addAttribute("message", fileUploadResult.getMessage());
		return "main";
	}
}