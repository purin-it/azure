package com.example.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SearchForm {

	/** 検索用名前 */
	private String searchName;

	/** 検索用性別 */
	private String searchSex;

	/** 検索結果リスト */
	private ArrayList<UserData> userDataList = new ArrayList<>();

	/** 性別のMapオブジェクト */
	@JsonIgnore
	public Map<String, String> getSexItems() {
		Map<String, String> sexMap = new LinkedHashMap<String, String>();
		sexMap.put("1", "男");
		sexMap.put("2", "女");
		return sexMap;
	}
}