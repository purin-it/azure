package com.example.demo;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.specialized.BlobOutputStream;
import com.azure.storage.blob.specialized.BlockBlobClient;

@Controller
public class DemoController {

	/** Azure Storageのアカウント名 */
	@Value("${azure.storage.accountName}")
	private String storageAccountName;

	/** Azure StorageのBlobコンテナー名 */
	@Value("${azure.storage.containerName}")
	private String storageContainerName;

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

		// ファイルアップロード処理
		try {
			// Blob Storage内のコンテナーのクライアントオブジェクトを取得
			BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
					.endpoint("https://" + storageAccountName + ".blob.core.windows.net")
					.credential(new DefaultAzureCredentialBuilder().build()).containerName(storageContainerName)
					.buildClient();

			// Blobコンテナー内のBlobオブジェクトを生成
			BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(uploadFile.getOriginalFilename())
					.getBlockBlobClient();

			// Blob内のコンテナーにデータを書き込む(上書きも可能)
			BlobOutputStream output = blockBlobClient.getBlobOutputStream(true);
			output.write(IOUtils.toByteArray(uploadFile.getInputStream()));
			output.close();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// メイン画面へ遷移
		model.addAttribute("message", "ファイルアップロードが完了しました。");
		return "main";
	}
}