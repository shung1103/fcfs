package org.hanghae99.fcfs.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching // Redis 캐시를 사용하는 경우 활성화
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String password;

    /* Redis 저장소와 연결 */
    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
	    redisConfiguration.setHostName(redisHost);
	    redisConfiguration.setPort(redisPort);
	    redisConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisConfiguration);

    }

    /* 어플리케이션에서 사용할 redisTemplate 설정 */
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory());

        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash를 사용할 경우 시리얼라이저
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    // Redis Cache
    @Bean
    public CacheManager productCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // Value Serializer 변경
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(3L)); // 캐시 수명 3분

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("Products", redisCacheConfiguration.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ResourceLoader resourceLoader) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        // value Serializer 변경
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        //disableCachingNullValues()를 호출하여 null 값 캐싱을 비활성화합니다. 이는 Redis에 null 값을 저장하지 않도록 설정합니다.
        //serializeValuesWith()를 호출하여 값 직렬화를 구성합니다.
        // 위의 코드에서는 GenericJackson2JsonRedisSerializer를 사용하여 값 직렬화를 수행합니다.
        // 이는 값이 JSON 형식으로 직렬화되어 Redis에 저장되고 검색됩니다.
        //구성이 완료된 RedisCacheConfiguration을 사용하여 RedisCacheManager를 생성합니다.
        //이렇게 구성된 RedisCacheManager는 Spring Boot 애플리케이션에서 Redis 캐시를 관리하는 데 사용됩니다.
        // 캐시 설정과 Redis 연결을 제어하고 캐시 작업을 수행할 때 RedisCacheManager를 주입받아 사용할 수 있다.

        //redisCacheConfigMap은 Redis 캐시의 구성 정보를 담는 맵입니다.
        Map<String, RedisCacheConfiguration> redisCacheConfigMap
                = new HashMap<>();

        redisCacheConfigMap.put(
                CacheNames.USERBYUSERNAME,
                defaultConfig.entryTtl(Duration.ofHours(4)) //entryTtl()을 호출하여 캐시 항목의 만료 시간(TTL)을 설정합니다.  캐시 수명 4시간
        );

        // ALLUSERS에 대해서만 다른 Serializer 적용
        redisCacheConfigMap.put(
                CacheNames.ALLUSERS,
                defaultConfig.entryTtl(Duration.ofHours(4))
                        .serializeValuesWith( //serializeValuesWith()를 호출하여 값을 직렬화하는 방식을 설정합니다.
                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(new JdkSerializationRedisSerializer())
                        )
        );
        redisCacheConfigMap.put(
                CacheNames.LOGINUSER,
                defaultConfig.entryTtl(Duration.ofHours(2))
        );


        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(redisCacheConfigMap)
                .build();
    }
//    //전체조회시
//    redisCacheConfigMap.put(
//        CacheNames.GETBOARD,
//        defaultConfig.entryTtl(Duration.ofHours(4))
//            .serializeValuesWith(
//                RedisSerializationContext
//                    .SerializationPair
//                    .fromSerializer(new JdkSerializationRedisSerializer(resourceLoader.getClassLoader()))
//            )
//    );
    /**
     * 위의 코드는 캐시 이름을 상수로 정의하는 클래스인 CacheNames를 나타냅니다. 각 상수는 특정 캐시 영역을 식별하는 문자열 값을 가지고 있습니다.
     * 이러한 캐시 이름 상수는 주로 Spring Cache와 같은 캐싱 기능을 사용할 때 캐시 이름을 지정하는 데 사용됩니다.
     * 예를 들어, @Cacheable 애노테이션에서 cacheNames 속성에 캐시 이름을 설정할 때 이러한 상수를 사용할 수 있습니다.
     *
     * 상수를 사용하여 캐시 이름을 정의하면, 캐시 영역을 구분하고 캐시에 저장된 데이터를 관리하기 쉬워집니다.
     * 또한, 캐시 이름을 상수로 정의함으로써 오타나 잘못된 캐시 이름 사용을 방지할 수 있습니다.
     */
    public class CacheNames {
        public static final String USERBYUSERNAME = "CACHE_USERBYUSERNAME";
        public static final String ALLUSERS = "CACHE_ALLUSERS";
        public static final String LOGINUSER = "CACHE_LOGINUSER";

    }
}