package com.example.demo;

import org.springframework.data.annotation.Id;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
import lombok.Data;

//Cosmos DBのユーザーデータテーブルのエンティティ
//collectionにCosmos DBのコンテナ名を指定する
@Data
@Document(collection = "purinitcontainer")
public class UserData {

	/** ID */
	@Id
	private String id;

	/** 名前 */
	private String name;

	/** 生年月日_年 */
	private Integer birth_year;

	/** 生年月日_月 */
	private Integer birth_month;

	/** 生年月日_日 */
	private Integer birth_day;

	/** 性別 */
	private String sex;

}
