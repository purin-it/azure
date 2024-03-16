package com.example.service;

import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.AppendBlobClient;
import com.azure.storage.blob.specialized.BlockBlobClient;

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
			BlockBlobClient blockBlobClient = getBlobContainerClient()
					.getBlobClient(fileName).getBlockBlobClient();
			byte[] data = message.getBytes(characterCode);
			blockBlobClient.upload(new ByteArrayInputStream(data), data.length);
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
			BlockBlobClient blockBlobClient = getBlobContainerClient()
					.getBlobClient(fileName).getBlockBlobClient();
			if(blockBlobClient.exists()) {
				blockBlobClient.delete();
			}
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
		try {
			// Blob内のコンテナー内に、指定したファイル(追加Blob)を作成する
			AppendBlobClient appendBlobClient = getBlobContainerClient()
					.getBlobClient(fileName).getAppendBlobClient();
			if(!appendBlobClient.exists()) {
				appendBlobClient.create();
			}
			// Blob内のコンテナー内の指定したファイル(追加Blob)に、指定したメッセージを追記モードで書き込む
			byte[] data = message.getBytes(characterCode);
			appendBlobClient.appendBlock(new ByteArrayInputStream(data), data.length);
		} catch(Exception ex) {
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
			AppendBlobClient appendBlobClient = getBlobContainerClient()
					.getBlobClient(fileName).getAppendBlobClient();
			if(appendBlobClient.exists()) {
				appendBlobClient.delete();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Blobコンテナクライアントオブジェクトを取得する.
	 * @return Blobコンテナクライアントオブジェクト
	 */
	private BlobContainerClient getBlobContainerClient() {
		// Blobサービスクライアントオブジェクトを取得
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
		        .connectionString(storageConnectionString).buildClient();
		
		// Blobコンテナクライアントオブジェクトを取得
		BlobContainerClient blobContainerClient = blobServiceClient
				.getBlobContainerClient(storageContainerName);
		return blobContainerClient;
	}

}
