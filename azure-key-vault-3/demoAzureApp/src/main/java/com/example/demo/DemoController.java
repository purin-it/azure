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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;

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

	/** keyVaultのURI */
	@Value("${keyVaultUri}")
	private String keyVaultUri;

	/** keyVaultのシークレットのキー */
	@Value("${keyVaultSecretKey}")
	private String keyVaultSecretKey;

	@GetMapping("/")
	public String index(Model model) {
		// Azure FunctionsのgetKeySecret関数を呼び出すためのヘッダー情報を設定する
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Azure FunctionsのgetKeySecret関数を呼び出すための引数を設定する
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		try {
			map.add("key", "keySecret");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		// Azure FunctionsのgetKeySecret関数を呼び出す
		ResponseEntity<String> response = restTemplate.exchange(demoAzureFuncBase + "getKeySecret", HttpMethod.POST,
				entity, String.class);

		// Azure Functionsの呼出結果のシークレットを、Modelオブジェクトに設定する
		try {
			GetKeySecretResult getKeySecretResult = objectMapper.readValue(response.getBody(),
					GetKeySecretResult.class);
			model.addAttribute("keySecretFunctions", getKeySecretResult.getSecret());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// Azure App Serviceでのシークレットを、Modelオブジェクトに設定する
		model.addAttribute("keySecretAppService", getKeySecret());
		return "index";
	}

	/**
	 * KeyVaultからシークレットを取得する
	 * 
	 * @return シークレット値
	 */
	private String getKeySecret() {
		AppServiceMSICredentials credentials = new AppServiceMSICredentials(AzureEnvironment.AZURE);
		KeyVaultClient keyVaultClient = new KeyVaultClient(credentials);
		SecretBundle bundle = keyVaultClient.getSecret(keyVaultUri, keyVaultSecretKey);
		return bundle.value();
	}

}