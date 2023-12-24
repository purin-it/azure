package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisHttpSession
public class DemoSessionConfigBean extends AbstractHttpSessionApplicationInitializer {

	/** Azure上のRedisサーバーのホスト名 */
	@Value("${spring.redis.host}")
	private String redisHostName;
	
	/** Azure上のRedisサーバーのポート番号 */
	@Value("${spring.redis.port}")
	private String redisPort;
	
	/** Azure上のRedisサーバーのパスワード */
	@Value("${spring.redis.password}")
	private String redisPassword;
	
	/**
	 * Redisへの値の書き込み・読み込み手段を提供するシリアライザを生成する
	 * @return　Redisへの値の書き込み・読み込み手段を提供するシリアライザ
	 */
	@Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
	
	/**
	 * Spring SessionがAzure上のRedisのCONFIGを実行しないようにする
	 * @return Spring SessionがAzure上のRedisのCONFIGを実行しない設定
	 */
    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    /**
     * Redisへの接続方法を生成する
     * @return Redisへの接続方法
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHostName);
        redisStandaloneConfiguration.setPassword(redisPassword);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().useSsl().build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }
}
