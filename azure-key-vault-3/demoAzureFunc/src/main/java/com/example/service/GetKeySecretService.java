package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.model.GetKeySecretForm;
import com.example.model.GetKeySecretResult;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;

@Service
public class GetKeySecretService {

	/** keyVaultのURI */
	@Value("${keyVaultUri}")
	private String keyVaultUri;

	/** keyVaultのシークレットのキー */
	@Value("${keyVaultSecretKey}")
	private String keyVaultSecretKey;

	/**
	 * 検索用Formに設定したキーにあてはまるシークレットを取得する
	 * 
	 * @param getKeySecretForm 検索用Form
	 * @return 結果情報オブジェクト
	 */
	public GetKeySecretResult getKeySecret(GetKeySecretForm getKeySecretForm) {
		GetKeySecretResult getKeySecretResult = new GetKeySecretResult();
		if (keyVaultSecretKey.equals(getKeySecretForm.getKey())) {
			// KeyVaultからシークレットを取得し設定する
			getKeySecretResult.setSecret(getKeySecret());
		}
		return getKeySecretResult;
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
