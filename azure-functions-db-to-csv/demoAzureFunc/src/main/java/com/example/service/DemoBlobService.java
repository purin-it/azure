package com.example.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

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
	 * 引数で指定されたBlobが存在する場合、削除する.
	 * @param blobName Blob名
	 */
	public void deleteBlockBlob(String blobName) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(blobName)) {
			return;
		}
		
		// Blob内のコンテナー内の指定したファイルが存在する場合、削除する
		try {
			CloudBlockBlob cbb = getContainer().getBlockBlobReference(blobName);
			cbb.deleteIfExists();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 引数で指定したローカルファイルを、引数で指定されたBlobにアップロードする.
	 * @param localFile ローカルファイル
	 * @param blobName Blob名
	 */
	public void uploadBlobFromFile(String localFile, String blobName) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(localFile)
				|| StringUtil.isNullOrEmpty(blobName)) {
			return;
		}
		
		try {
			CloudBlockBlob cbb = getContainer().getBlockBlobReference(blobName);
			cbb.uploadFromFile(localFile);
		} catch (Exception ex) {
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
		CloudStorageAccount storageAccount 
		    = CloudStorageAccount.parse(storageConnectionString);

		// Blobクライアントオブジェクトを取得
		CloudBlobClient blobClient 
		    = storageAccount.createCloudBlobClient();

		// Blob内のコンテナーを取得
		CloudBlobContainer container 
		    = blobClient.getContainerReference(storageContainerName);
		return container;
	}

}
