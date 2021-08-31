package com.example.model;

import java.util.Date;
import java.util.Map;

public class EventSchema {

	/** トピック */
	public String topic;

	/** サブジェクト */
	public String subject;

	/** イベントタイプ */
	public String eventType;

	/** イベント発生日時 */
	public Date eventTime;

	/** ID */
	public String id;

	/** データのバージョン */
	public String dataVersion;

	/** メタデータのバージョン */
	public String metadataVersion;

	/** データ */
	public Map<String, Object> data;

}