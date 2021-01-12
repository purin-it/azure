package com.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.GetUserPassParam;
import com.example.model.GetUserPassResult;
import com.example.service.GetUserPassService;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerKeyVaultAuthenticationCallback;

@SpringBootApplication
public class DemoAzureFunction {

	/** USER_PASSデータ取得サービスクラスのオブジェクト */
	@Autowired
	private GetUserPassService getUserPassService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}

	/**
	 * Key Vaultの認証の接続設定を登録する
	 * @throws SQLException       SQL例外
	 * @throws URISyntaxException　URI文法エラー
	 */
	@PostConstruct
	public void postConstruct() throws SQLException, URISyntaxException {
		SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider = new SQLServerColumnEncryptionAzureKeyVaultProvider(
				tryAuthenticationCallback());
		Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
		keyStoreMap.put(akvProvider.getName(), akvProvider);
		SQLServerConnection.registerColumnEncryptionKeyStoreProviders(keyStoreMap);
	}

	/**
	 * USER_PASSテーブルのデータを取得し結果を返却する関数
	 * @return USER_PASSテーブルデータ取得サービスクラスの呼出結果
	 */
	@Bean
	public Function<GetUserPassParam, GetUserPassResult> getUserPass() {
		return getUserPassParam -> getUserPassService.getUserPass(getUserPassParam);
	}

	/**
	 * SQLServerKeyVaultAuthenticationCallbackオブジェクトを生成する
	 * @return SQLServerKeyVaultAuthenticationCallbackオブジェクト
	 * @throws URISyntaxException URI文法エラー
	 * @throws SQLServerException SQLServerエラー
	 */
	private static SQLServerKeyVaultAuthenticationCallback tryAuthenticationCallback()
			throws URISyntaxException, SQLServerException {
		SQLServerKeyVaultAuthenticationCallback authenticationCallback = new SQLServerKeyVaultAuthenticationCallback() {
			@Override
			public String getAccessToken(String authority, String resource, String scope) {
				AppServiceMSICredentials credentials = new AppServiceMSICredentials(AzureEnvironment.AZURE);
				try {
					return credentials.getToken(resource);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		};
		return authenticationCallback;
	}
}
