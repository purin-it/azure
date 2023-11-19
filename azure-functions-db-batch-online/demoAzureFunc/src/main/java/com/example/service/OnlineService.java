package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.OnlineServiceParam;
import com.example.model.OnlineServiceResult;
import com.example.mybatis.model.UserData;
import com.example.mybatis.online.UserDataMapperOnline;

@Service
public class OnlineService {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OnlineService.class);
	
	@Autowired
	private UserDataMapperOnline userDataMapperOnline;
	
	/**
	 * 指定されたIDのバージョンを更新し、更新後データを返却するサービス.
	 * @param onlineServiceParam オンラインサービス呼出用Param
	 * @return オンラインサービスの処理結果
	 */
	public OnlineServiceResult online(OnlineServiceParam onlineServiceParam) {
		OnlineServiceResult result = new OnlineServiceResult();
		Integer tmpId = null;
		
		// 引数のIDが数値でなければ、処理を終了
		try {
			tmpId = Integer.parseInt(onlineServiceParam.getId());
		}catch(Exception ex) {
			return result;
		}
		
		// 指定されたIDのバージョンを更新
		int updCnt = userDataMapperOnline.updateVersion(tmpId);
		LOGGER.info("更新ID : " + tmpId + ", 更新件数 : " + updCnt);
		
		// 指定されたIDのUserDataオブジェクトを返却
		UserData userData = userDataMapperOnline.findById(tmpId);
		if(userData == null) {
			userData = new UserData();
		}
		result.setUserData(userData.toString());
		return result;
	}
}
