package com.example.demo;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import com.example.model.FileData;
import com.example.model.FileUploadParam;
import com.example.model.FileUploadResult;
import com.example.model.GetFileListParam;
import com.example.model.GetFileListResult;

@Controller
public class DemoController {

	/** RestTemplateオブジェクト */
	@Autowired
	private RestTemplate restTemplate;

	/** application.propertiesからdemoAzureFunc.urlBaseの値を取得 */
	@Value("${demoAzureFunc.urlBase}")
	private String demoAzureFuncBase;

	/**
	 * ファイルリストを取得し、メイン画面を初期表示する.
	 * @param model   Modelオブジェクト
	 * @param session HttpSessionオブジェクト
	 * @return メイン画面
	 */
	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		// Azure FunctionsのgetFileList関数を呼び出すためのヘッダー情報を設定する
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Azure FunctionsのgetFileList関数を呼び出すための引数を設定する
		HttpEntity<GetFileListParam> entity = new HttpEntity<>(new GetFileListParam(), headers);

		// Azure FunctionsのgetFileList関数を呼び出す
		ResponseEntity<GetFileListResult> response = restTemplate.exchange(demoAzureFuncBase + "getFileList",
				HttpMethod.POST, new HttpEntity<>(entity), GetFileListResult.class);
		GetFileListResult getFileListResult = response.getBody();

		model.addAttribute("fileDataList", getFileListResult.getFileDataList());
		session.setAttribute("fileDataList", getFileListResult.getFileDataList());

		model.addAttribute("message", "アップロードするファイルを指定し、アップロードボタンを押下してください。");
		
		// DevToolsの実行確認を行うためのメッセージを設定
		model.addAttribute("appServiceMessage", "Azure App Serviceのメッセージ１");
		model.addAttribute("azureFunctionsMessage", getFileListResult.getAzureFunctionsMessage());
				
		return "main";
	}

	/**
	 * ファイルダウンロード処理.
	 * @param id       ID
	 * @param response HttpServletResponse
	 * @param session  HttpSessionオブジェクト
	 * @return 画面遷移先(nullを返す)
	 */
	@RequestMapping("/download")
	public String download(@RequestParam("id") String id, HttpServletResponse response, HttpSession session) {
		// セッションからファイルリストを取得する
		@SuppressWarnings("unchecked")
		ArrayList<FileData> fileDataList = (ArrayList<FileData>) session.getAttribute("fileDataList");
		FileData fileData = fileDataList.get(Integer.parseInt(id) - 1);

		// ファイルダウンロードの設定を実施
		// ファイルの種類は指定しない
		response.setContentType("application/octet-stream");
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "");
		response.setHeader("Content-Disposition",
				"attachment;filename=\"" + getFileNameEncoded(fileData.getFileName()) + "\"");

		// ダウンロードファイルへ出力
		try (OutputStream out = response.getOutputStream()) {
			out.write(ArrayUtils.toPrimitive(fileData.getFileData()));
			out.flush();
		} catch (Exception e) {
			System.err.println(e);
		}

		// 画面遷移先はnullを指定
		return null;
	}

	/**
	 * ファイルデータをAzure Blob Storageに登録する.
	 * @param uploadFile         アップロードファイル
	 * @param model              Modelオブジェクト
	 * @param redirectAttributes リダイレクト先に渡すパラメータ
	 * @return メイン画面
	 */
	@PostMapping("/upload")
	public String add(@RequestParam("upload_file") MultipartFile uploadFile, Model model,
			RedirectAttributes redirectAttributes) {
		// ファイルが未指定の場合はエラーとする
		if (uploadFile == null || StringUtils.isEmptyOrWhitespace(uploadFile.getOriginalFilename())) {
			redirectAttributes.addFlashAttribute("errMessage", "ファイルを指定してください。");
			return "redirect:/";
		}

		// Azure FunctionsのfileUpload関数を呼び出すためのヘッダー情報を設定する
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Azure FunctionsのfileUpload関数を呼び出すための引数を設定する
		FileUploadParam fileUploadParam = new FileUploadParam();
		try {
			fileUploadParam.setFileName(uploadFile.getOriginalFilename());
			fileUploadParam.setFileData(ArrayUtils.toObject(IOUtils.toByteArray(uploadFile.getInputStream())));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		HttpEntity<FileUploadParam> entity = new HttpEntity<>(fileUploadParam, headers);

		// Azure FunctionsのfileUpload関数を呼び出す
		ResponseEntity<FileUploadResult> response = restTemplate.exchange(demoAzureFuncBase + "fileUpload",
				HttpMethod.POST, entity, FileUploadResult.class);
		FileUploadResult fileUploadResult = response.getBody();

		// メイン画面へ遷移
		model.addAttribute("message", fileUploadResult.getMessage());
		return "redirect:/";
	}

	/**
	 * ファイル名をUTF-8でエンコードする.
	 * 
	 * @param filePath ファイル名
	 * @return エンコード後のファイル名
	 */
	private String getFileNameEncoded(String fileName) {
		String fileNameAft = "";
		if (!StringUtils.isEmptyOrWhitespace(fileName)) {
			try {
				// ファイル名をUTF-8でエンコードして返却
				fileNameAft = URLEncoder.encode(fileName, "UTF-8");
			} catch (Exception e) {
				System.err.println(e);
				return "";
			}
		}
		return fileNameAft;
	}

}