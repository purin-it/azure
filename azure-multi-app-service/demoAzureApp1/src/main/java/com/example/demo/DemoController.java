package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DemoController {

	/** application.propertiesからdemoAzureFunc.urlBaseの値を取得 */
	@Value("${demoAzureApp2.urlBase}")
	private String demoAzureApp2Base;
	
	/**
	 * 呼出元画面を表示する.
	 * @return 呼出元画面
	 */
	@GetMapping("/")
	public String index() {
		return "caller";
	}
	
	/**
	 * 引数dataの値をセッションに格納し、呼出先画面を開くためのダミー画面に遷移
	 * @param data テキストのデータ
	 * @param model Modelオブジェクト
	 * @param session Httpセッション
	 * @return ダミー画面
	 */
	@PostMapping("/showCallee")
	public String showCallee(@RequestParam("addSession")String data
			, Model model, HttpSession session) {
		// セッションへの格納処理
		session.setAttribute("addSession", data);
		
		// 呼出先(calleeUrl),次画面に渡すパラメータ(addParam)を設定し、ダミー画面に遷移
		model.addAttribute("calleeUrl", demoAzureApp2Base);
		model.addAttribute("addParam", data);
		return "dummy";
	}
	
}
