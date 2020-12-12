package com.example.model;

import java.util.ArrayList;
import com.example.mybatis.model.UserData;
import lombok.Data;

@Data
public class SearchResult {

	/** 検索結果リスト */
	private ArrayList<UserData> userDataList = new ArrayList<>();

}
