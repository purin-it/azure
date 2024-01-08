package com.example.config;

import org.apache.ibatis.mapping.Environment;
import org.springframework.context.annotation.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.example.mybatis.batch"}, sqlSessionFactoryRef = "sqlSessionFactoryBatch")
public class DemoBatchDataSourceConfig {

    /**
     * バッチで利用するためのデータソースプロパティを生成する
     * @return バッチで利用するためのデータソースプロパティ
     */
    @Bean(name = {"datasourceBatchProperties"})
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties datasourceBatchProperties() {
        return new DataSourceProperties();
    }

    /**
     * バッチで利用するためのデータソースを生成する
     * @param properties バッチで利用するためのデータソースプロパティ
     * @return バッチで利用するためのデータソース
     */
    @Bean(name = {"dataSourceBatch"})
    @Primary
    public DataSource datasourceBatch(@Qualifier("datasourceBatchProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * バッチで利用するためのトランザクションマネージャを生成する
     * @param dataSourceBatch バッチで利用するためのデータソース
     * @return バッチで利用するためのトランザクションマネージャ
     */
    @Bean(name = {"txManagerBatch"})
    @Primary
    public PlatformTransactionManager txManagerBatch(@Qualifier("dataSourceBatch") DataSource dataSourceBatch) {
        return new DataSourceTransactionManager(dataSourceBatch);
    }

    /**
     * バッチで利用するためのSQLセッションファクトリを生成する
     * @param dataSourceBatch バッチで利用するためのデータソース
     * @return バッチで利用するためのSQLセッションファクトリ
     * @throws Exception 任意例外
     */
    @Bean(name = {"sqlSessionFactoryBatch"})
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceBatch") DataSource dataSourceBatch) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSourceBatch);

        // MyBatisでバッチモードで処理できるよう設定を変更
        Environment env = new Environment("development", new JdbcTransactionFactory(), dataSourceBatch);
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration(env);
        config.setDefaultExecutorType(ExecutorType.BATCH);
        sqlSessionFactory.setConfiguration(config);
        
        return sqlSessionFactory.getObject();
    }
}
