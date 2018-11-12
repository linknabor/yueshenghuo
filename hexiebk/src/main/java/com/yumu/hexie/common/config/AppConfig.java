package com.yumu.hexie.common.config;

import java.beans.PropertyVetoException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yumu.hexie.model.system.SystemConfig;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@Configuration
@ComponentScan(basePackages = {"com.yumu.hexie"}, includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.yumu.hexie.backend.web.*"}))
@EnableJpaRepositories({"com.yumu.hexie.model.*"})
@EnableScheduling
@EnableAspectJAutoProxy
@EnableCaching(proxyTargetClass=true)
public class AppConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
    @Value(value = "${jdbc.dialect}")
    private String dialect;
    @Value(value = "${jdbc.url}")
    private String url;
    @Value(value = "${jdbc.username}")
    private String userName;
    @Value(value = "${jdbc.password}")
    private String password;
    @Value(value = "${jdbc.driverClassName}")
    private String driverClassName;
    
    @Value(value = "${redis.host}")
    private String redisHost;
    @Value(value = "${redis.port}")
    private String redisPort;
    
    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer(){
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(8888);
        return factory;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("com.yumu.hexie.model");
        factoryBean.setDataSource(jpaDataSource());
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(dialect);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        return factoryBean;
    }

    @Bean
    public DataSource jpaDataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUser(userName);
        dataSource.setPassword(password);
        try {
            dataSource.setDriverClass(driverClassName);
            dataSource.setMaxPoolSize(2);
            dataSource.setMinPoolSize(0);
            dataSource.setMaxIdleTime(1200);
        } catch (PropertyVetoException e) {
            LOGGER.error("Can not create Data source.");
        }
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(redisHost);
        connectionFactory.setPort(Integer.valueOf(redisPort));
        //connectionFactory.setPassword(redisPassword);
        connectionFactory.setUsePool(true);
        return connectionFactory;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate getStringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory());
    }

    @Bean(name = "redisTemplate")
    public <V> RedisTemplate<String, V> getRedisTemplate() {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
    
    @Bean(name = "systemConfigRedisTemplate")
    public RedisTemplate<String, SystemConfig> systemConfigRedisTemplate(){
        RedisTemplate<String, SystemConfig> redisTemplate = new RedisTemplate<String, SystemConfig>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<SystemConfig>(SystemConfig.class));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    };
    
    public KeyGenerator keyGenerator() {
        return new KeyGenerator(){

        	@Override
        	public Object generate(Object target, Method method, Object... params) {
        		return generateKey(params);
        	}

        	/**
        	 * Generate a key based on the specified parameters.
        	 */
        	public Object generateKey(Object... params) {
        		LOGGER.error("------------------------------------------"+params.length+"***");
        		if (params.length == 0) {
        			return SimpleKey.EMPTY;
        		}
        		if (params.length == 1) {
        			Object param = params[0];
        			if (param != null && !param.getClass().isArray()) {
                		LOGGER.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+param.toString()+"***");
        				return param.toString();
        			}
        		}
        		return new SimpleKey(params);
        	}

        };
    }
    @Bean
    public CacheManager getCacheManager() {
    	RedisCacheManager m = new RedisCacheManager(getRedisTemplate());
    	m.setDefaultExpiration(1800);//
    	return m;
    }
}
