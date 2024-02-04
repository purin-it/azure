package com.example.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
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
	
	/** 文字コード */
	@Value("${character.code}")
	private String characterCode;
	
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
	 * 引数で指定されたファイル(BlockBlob)に、引数で指定されたメッセージを書き込む.
	 * @param fileName ファイル名(BlockBlob)
	 * @param message メッセージ
	 */
	public void writeBlockBlob(String fileName, String message) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(fileName)
				|| StringUtil.isNullOrEmpty(message)) {
			return;
		}
		
		// ファイルアップロード処理
		// Blob内のコンテナー内の指定したファイル(BlockBlob)に、指定したメッセージを書き込む
		try {
			CloudBlockBlob cbb = getContainer().getBlockBlobReference(fileName);
			cbb.deleteIfExists();
			BlobRequestOptions options = new BlobRequestOptions();
			options.setAbsorbConditionalErrorsOnRetry(true);
			cbb.uploadText(message, characterCode, null, options, null);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 引数で指定されたBlob(BlockBlob)が存在する場合、削除する.
	 * @param blobName Blob名(BlockBlob)
	 */
	public void deleteBlockBlob(String fileName) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(fileName)) {
			return;
		}
		
		// Blob内のコンテナー内の指定したファイル(BlockBlob)が存在する場合、削除する
		try {
			CloudBlockBlob cbb = getContainer().getBlockBlobReference(fileName);
			cbb.deleteIfExists();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 引数で指定されたファイル(追加Blob)に、引数で指定されたメッセージを追加する.
	 * @param fileName ファイル名(追加Blob)
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
		// Blob内のコンテナー内の指定したファイル(追加Blob)に、指定したメッセージを追記モードで書き込む
		try {
			CloudAppendBlob cab = getContainer().getAppendBlobReference(fileName);
			if (!cab.exists()) {
				cab.createOrReplace();
			}
			BlobRequestOptions options = new BlobRequestOptions();
			options.setAbsorbConditionalErrorsOnRetry(true);
			cab.appendText(message, characterCode, null, options, null);		
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 引数で指定されたファイル(追加Blob)が存在する場合、削除する.
	 * @param fileName ファイル名(追加Blob)
	 */
	public void deleteAppendBlob(String fileName) {
		// 未指定の項目がある場合、何もしない
		if(StringUtil.isNullOrEmpty(storageAccountName) 
				|| StringUtil.isNullOrEmpty(storageAccessKey) 
				|| StringUtil.isNullOrEmpty(storageContainerName)
				|| StringUtil.isNullOrEmpty(fileName)) {
			return;
		}
		
		// Blob内のコンテナー内の指定したファイル(追加Blob)が存在する場合、削除する
		try {
			CloudAppendBlob cab = getContainer().getAppendBlobReference(fileName);
			cab.deleteIfExists();		
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
