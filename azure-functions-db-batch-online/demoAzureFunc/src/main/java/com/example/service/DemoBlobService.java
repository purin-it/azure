package com.example.service;

import java.time.Duration;
import java.time.OffsetDateTime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudAppendBlob;

import io.netty.util.internal.StringUtil;

@Service
public class DemoBlobService {

	/** Azure Storageのアカウント名 */
	@Value("${azure.storage.accountName}")
	private String storageAccountName;

	/** Azure Storageへのアクセスキー */
	@Value("${azure.storage.accessKey}")
	private String storageAccessKey;

	/** Azure StorageのBlobコンテナー名 */
	@Value("${azure.storage.containerName}")
	private String storageContainerName;
	
	/** 改行コード */
	private static final String LINE_SEPARATOR = "\r\n";
	/** 文字コード */
	private static final String CHARCTER_CODE = "UTF-8";
	
	// Blobストレージへの接続文字列
	private String storageConnectionString = null;
	
	/**
	 * Blobストレージへの接続文字列の初期化処理
	 */
	@PostConstruct
	public void init() {
		// Blobストレージへの接続文字列を設定
		storageConnectionString = "DefaultEndpointsProtocol=https;" 
				+ "AccountName=" + storageAccountName + ";"
				+ "AccountKey=" + storageAccessKey + ";";
	}
	
	/**
	 * 引数で指定されたファイルに、引数で指定されたメッセージを追加する.
	 * @param fileName ファイル名
	 * @param message メッセージ
	 */
	public void writeAppendBlob(String fileName, String message) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(fileName)
				|| StringUtil.isNullOrEmpty(message)) {
			return;
		}
		
		// ファイルアップロード処理
		// Blob内のコンテナー内の指定したファイルに、指定したメッセージを追記モードで書き込む
		try {
			CloudAppendBlob cab = getContainer().getAppendBlobReference(fileName);
			if (!cab.exists()) {
				cab.createOrReplace();
			}
			BlobRequestOptions options = new BlobRequestOptions();
			options.setAbsorbConditionalErrorsOnRetry(true);
			cab.appendText(message + LINE_SEPARATOR, CHARCTER_CODE, null, options, null);		
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 引数で指定されたファイルが存在する場合、削除する.
	 * @param fileName ファイル名
	 */
	public void deleteAppendBlob(String fileName) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(fileName)) {
			return;
		}
		
		// Blob内のコンテナー内の指定したファイルが存在する場合、削除する
		try {
			CloudAppendBlob cab = getContainer().getAppendBlobReference(fileName);
			cab.deleteIfExists();		
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 引数で指定されたファイルのURLを返却する.
	 * @param fileName
	 * @return
	 */
	public String getBlobUrl(String fileName) {
		// 未指定の項目がある場合、nullを返す
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(fileName)) {
			return null;
		}
		
		// Blob内のコンテナー内の指定したファイルのURLを返却する
		try {
			// Blobサービスクライアントの生成
			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
						.connectionString(storageConnectionString).buildClient();
			// BlobサービスクライアントからSASを生成
			String sas = this.getServiceSasUriForBlob(blobServiceClient);
			// Blob内のコンテナー内の指定したファイルのURLを返却
			String url = blobServiceClient.getAccountUrl() + "/" 
					+ storageContainerName + "/" + fileName
					+ "?" + sas;
			return url;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Blobストレージのコンテナを取得する.
	 * @return Blobストレージのコンテナ
	 * @throws Exception
	 */
	private CloudBlobContainer getContainer() throws Exception {
		// ストレージアカウントオブジェクトを取得
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Blobクライアントオブジェクトを取得
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		// Blob内のコンテナーを取得
		CloudBlobContainer container = blobClient.getContainerReference(storageContainerName);
		return container;
	}
	
	/**
	 * 引数で指定したBlobのSASトークンを生成し返す.
	 * @param client Blobサービスクライアントオブジェクト
	 * @return SASトークン
	 */
	private String getServiceSasUriForBlob(BlobServiceClient client) {
		// SASトークンのアクセス権を読み取り可能に設定
		AccountSasPermission permissions = new AccountSasPermission().setReadPermission(true);

		// SASトークンがBlobコンテナやオブジェクトにアクセスできるように設定
		AccountSasResourceType resourceTypes = new AccountSasResourceType().setContainer(true).setObject(true);

		// SASトークンがBlobにアクセスできるように設定
		AccountSasService services = new AccountSasService().setBlobAccess(true);

		// SASトークンの有効期限を5分に設定
		OffsetDateTime expiryTime = OffsetDateTime.now().plus(Duration.ofMinutes(5));

		// SASトークンを作成
		AccountSasSignatureValues sasValues = new AccountSasSignatureValues(expiryTime, permissions, services,
				resourceTypes);
		return client.generateAccountSas(sasValues);
	}
}
