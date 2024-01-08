package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.example.mybatis.online"}, sqlSessionFactoryRef = "sqlSessionFactoryOnline")
public class DemoOnlineDataSourceConfig {

    /**
     * オンラインで利用するためのデータソースプロパティを生成する
     * @return オンラインで利用するためのデータソースプロパティ
     */
    @Bean(name = {"datasourceOnlineProperties"})
    @ConfigurationProperties(prefix = "spring.datasource.online")
    public DataSourceProperties datasourceOnlineProperties() {
        return new DataSourceProperties();
    }

    /**
     * オンラインで利用するためのデータソースを生成する
     * @param properties オンラインで利用するためのデータソースプロパティ
     * @return オンラインで利用するためのデータソース
     */
    @Bean(name = {"dataSourceOnline"})
    public DataSource datasourceOnline(@Qualifier("datasourceOnlineProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * オンラインで利用するためのトランザクションマネージャを生成する
     * @param dataSourceOnline オンラインで利用するためのデータソース
     * @return オンラインで利用するためのトランザクションマネージャ
     */
    @Bean(name = {"txManagerOnline"})
    public PlatformTransactionManager txManagerOnline(@Qualifier("dataSourceOnline") DataSource dataSourceOnline) {
        return new DataSourceTransactionManager(dataSourceOnline);
    }

    /**
     * オンラインで利用するためのSQLセッションファクトリを生成する
     * @param dataSourceOnline オンラインで利用するためのデータソース
     * @return オンラインで利用するためのSQLセッションファクトリ
     * @throws Exception 任意例外
     */
    @Bean(name = {"sqlSessionFactoryOnline"})
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceOnline") DataSource dataSourceOnline) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSourceOnline);
        return sqlSessionFactory.getObject();
    }
}
