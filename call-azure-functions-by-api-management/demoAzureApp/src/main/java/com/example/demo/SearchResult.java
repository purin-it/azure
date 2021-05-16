package com.example.demo;

import lombok.Data;

@Data
public class SearchResult {

	/** 検索結果 */
	private String result;
	
	public SearchResult() {}
	
	public SearchResult(String result) {
		this.result = result;
	}
}
