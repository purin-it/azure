package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DemoController {

	/**
	 * 呼出先画面を表示する.
	 * @param model Modelオブジェクト
	 * @param session Httpセッション
	 * @return 呼出先画面
	 */
	@GetMapping("/sub")
	public String index(@RequestParam("addParam")String paramData
			, Model model, HttpSession session) {
		// セッションからのデータを取得
		String sessionData = (String)session.getAttribute("addSession");
		
		// セッションからのデータを取得とリクエストパラメータの値を、画面に表示
		model.addAttribute("sessionData", sessionData);
		model.addAttribute("paramData", paramData);
		return "callee";
	}
	
}
