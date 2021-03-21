package com.example;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.model.GetUserPassParam;
import com.example.model.GetUserPassResult;
import com.example.model.UpdUserPassParam;
import com.example.model.UpdUserPassResult;
import com.example.service.GetUserPassService;
import com.example.service.UpdUserPassService;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;

// DB接続を手動管理にするため、Spring Bootのデータソース自動設定機能を無効にする
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DemoAzureFunction {

	/** keyVaultのクライアントID */
	@Value("${keyVaultClientId}")
	private String keyVaultClientId;

	/** keyVaultのクライアントキー */
	@Value("${keyVaultClientKey}")
	private String keyVaultClientKey;
	
	/** Key Vaultの認証の接続設定が終わっているかどうかを判定するフラグ */
	private static boolean keyVaultProviderFlg = false;

	/** USER_PASSデータ取得サービスクラスのオブジェクト */
	@Autowired
	private GetUserPassService getUserPassService;
	
	/** USER_PASSデータ更新サービスクラスのオブジェクト */
	@Autowired
	private UpdUserPassService updUserPassService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}

	/**
	 * Key Vaultの認証の接続設定を登録する.
	 * @throws SQLException SQL例外
	 */
	@PostConstruct
	public void postConstruct() throws SQLException {
		if (!keyVaultProviderFlg) {
			SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider = new SQLServerColumnEncryptionAzureKeyVaultProvider(
					keyVaultClientId, keyVaultClientKey);
			Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
			keyStoreMap.put(akvProvider.getName(), akvProvider);
			SQLServerConnection.registerColumnEncryptionKeyStoreProviders(keyStoreMap);
			keyVaultProviderFlg = true;
		}
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
	 * USER_PASSテーブルのデータを更新し結果を返却する関数
	 * @return USER_PASSテーブルデータ更新サービスクラスの呼出結果
	 */
	@Bean
	public Function<UpdUserPassParam, UpdUserPassResult> updUserPass(){
		return updUserPassParam -> updUserPassService.updUserPass(updUserPassParam);
	}
}
