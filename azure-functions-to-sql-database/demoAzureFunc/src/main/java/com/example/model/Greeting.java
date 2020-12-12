package com.example.model;

import lombok.Data;

@Data
public class Greeting {

	public Greeting() {
	}

	public Greeting(String message) {
		this.message = message;
	}

	/** メッセージ */
	private String message;

}
