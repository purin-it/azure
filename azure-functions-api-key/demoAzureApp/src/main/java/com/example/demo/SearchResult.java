package com.example.demo;

import java.util.ArrayList;

import lombok.Data;

@Data
public class SearchResult {

	/** 検索結果リスト */
	private ArrayList<UserData> userDataList = new ArrayList<>();

}
