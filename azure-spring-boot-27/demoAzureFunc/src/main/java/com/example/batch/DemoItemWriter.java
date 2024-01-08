package com.example.batch;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Component;

import com.example.mybatis.batch.UserDataMapperBatch;
import com.example.mybatis.model.UserData;

@Component
public class DemoItemWriter implements ItemWriter<UserData> {
	
	/** USER_DATAテーブルにアクセスするためのMapper */
	@Autowired
	private UserDataMapperBatch userDataMapperBatch;
	
	/** SQLセッションファクトリ */
	@Autowired
	@Qualifier("sqlSessionFactoryBatch")
	private SqlSessionFactory sqlSessionFactoryBatch;
	
	/** SQLセッションテンプレート */
	private SqlSessionTemplate sqlSessionTemplate  = null;
	
	/**
	 * 更新件数を取得するため、SQLセッションテンプレートを初期化
	 */
	@PostConstruct
	public void init() {
		sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBatch, ExecutorType.BATCH);
	}
	
	/**
	 * 読み込んだファイルのデータを、DBに書き込む処理を定義する.
	 */
	@Override
	public void write(List<? extends UserData> items) {
		for(UserData item : items) {
			// 指定したIDをもつUserDataオブジェクトを、DBからロックをかけて取得する
			Integer id = Integer.parseInt(item.getId());
			UserData userData = userDataMapperBatch.findByIdRowLock(id);
			
			// id=8の場合、バージョンを更新し、楽観ロックエラーにする
			/* if(id == 8) {
				userDataMapperBatch.updateVersion(userData);
			} */
			
			// DBにデータが無ければ追加、あれば更新する
			if(userData == null) {
				userDataMapperBatch.insert(item);
			} else {
				userDataMapperBatch.update(item);
			}
			
			// 更新件数を確認し、1件でなければエラーとする
			List<BatchResult> results = sqlSessionTemplate.flushStatements();
			if(results == null || results.size() != 1) {
				throw new InvalidDataAccessResourceUsageException("楽観ロックエラーが発生しました。");
			}
		}
	}

}
