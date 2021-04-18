package org.geektimes.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.geektimes.cache.AbstractCacheManager;
import redis.clients.jedis.Jedis;
import sun.rmi.rmic.iiop.ValueType;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

public class LettuceCacheManager extends AbstractCacheManager {

    private final RedisClient redisClient;

    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
//        this.redisClient = RedisClient.create(uri.toString());
        this.redisClient = RedisClient.create(uri.toString().replaceFirst("lettuce", "redis"));
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
        return new LettuceCache(this, cacheName, configuration, redisConnection);
    }

    @Override
    protected void doClose() {
        this.redisClient.shutdown();
    }
}
