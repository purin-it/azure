package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class DemoControllerAdvice {

	/**
	 * Exceptionクラスのサブクラスの例外が発生した場合の処理.
	 * @param exception Exceptionクラスのサブクラスの例外
	 * @param model     Modelオブジェクト
	 * @return 画面遷移先
	 */
	@ExceptionHandler(Exception.class)
	public String occurException(Exception exception, Model model) {
		// HTTP 429(Too Many Requests)エラーが発生した場合は、error429.htmlに遷移する
		if (exception instanceof HttpClientErrorException) {
			HttpClientErrorException e = (HttpClientErrorException) exception;
			if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
				return "error429";
			}
		}

		// 上記以外の場合は、error.htmlに遷移し、発生したエラーメッセージを表示する
		model.addAttribute("errMsg", exception);
		return "error";
	}
}
