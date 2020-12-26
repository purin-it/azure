package com.example.demo;

import org.springframework.stereotype.Repository;

import com.microsoft.azure.spring.data.cosmosdb.repository.ReactiveCosmosRepository;

import reactor.core.publisher.Flux;

// Cosmos DBのユーザーデータテーブルを操作するためのリポジトリ
@Repository
public interface UserDataRepository extends ReactiveCosmosRepository<UserData, String> {

	/**
	 * ユーザーデータテーブルから、指定した性別に完全一致するデータを返す
	 * @param sex 性別
	 * @return 検索結果
	 */
	public Flux<UserData> findBySex(String sex);

	/**
	 * ユーザーデータテーブルから、指定した名前に部分一致するデータを返す
	 * @param name 名前
	 * @return 検索結果
	 */
	public Flux<UserData> findByNameContains(String name);

	/**
	 * ユーザーデータテーブルから、指定した名前に部分一致し、性別に完全一致するデータを返す
	 * @param name 名前
	 * @param sex  性別
	 * @return 検索結果
	 */
	public Flux<UserData> findByNameContainsAndSex(String name, String sex);

}
