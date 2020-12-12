package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.SearchForm;
import com.example.model.SearchResult;
import com.example.mybatis.UserDataMapper;

@Service
public class SearchService {

	/** DBからユーザーデータを取得するMapperオブジェクト */
	@Autowired
	private UserDataMapper userDataMapper;

	/**
	 * DBから検索条件に合うユーザーデータを取得し結果を返却する
	 * @param user HttpRequestの引数のユーザー
	 * @return 結果情報オブジェクト
	 */
	public SearchResult getUserDataList(SearchForm searchForm) {
		SearchResult searchResult = new SearchResult();
		searchResult.setUserDataList(userDataMapper.searchUserDataList(searchForm));
		return searchResult;
	}

}
