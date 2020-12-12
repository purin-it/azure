package com.example.model;

import lombok.Data;

@Data
public class User {

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	/** 名前 */
	private String name;
	
}
