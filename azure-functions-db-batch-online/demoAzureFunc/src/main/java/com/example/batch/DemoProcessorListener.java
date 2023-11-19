package com.example.batch;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.mybatis.batch.UserDataMapperBatch;
import com.example.mybatis.model.UserData;

@Component
public class DemoProcessorListener implements ItemProcessListener<UserData, UserData> {

	@Autowired
	private UserDataMapperBatch userDataMapperBatch;
	
	/**
	 * データ加工前の処理を定義する.
	 */
	@Override
	public void beforeProcess(UserData item) {
		// 何もしない	
	}

	/**
	 * データ加工後の処理を定義する.
	 */
	@Override
	@Transactional
	public void afterProcess(UserData item, UserData result) {
		// 処理結果がnullの場合は、処理を終了する
		if(result == null) {
			return;
		}
		
		// 既に登録済データのVERSIONを取得する
		Integer id = Integer.parseInt(result.getId());
		UserData ud = userDataMapperBatch.findByIdRowLock(id);
		
		// バージョンの値を結果に設定する
		if(ud != null) {
			result.setVersion(ud.getVersion());
		} else {
			result.setVersion(0);
		}
		
		// id=8の場合、バージョンを更新し、楽観ロックエラーにする
		/* if(id == 8) {
			userDataMapperBatch.updateVersion(result);
		} */
	}

	/**
	 * データ編集エラー後の処理を定義する.
	 */
	@Override
	public void onProcessError(UserData item, Exception e) {
		// 何もしない
	}

}
