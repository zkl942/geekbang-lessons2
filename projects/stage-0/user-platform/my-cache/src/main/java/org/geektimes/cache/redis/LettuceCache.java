package org.geektimes.cache.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import org.geektimes.cache.AbstractCache;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.*;
import java.util.Iterator;

public class LettuceCache <K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {
    StatefulRedisConnection<K, V> redisConnection;

    protected LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, StatefulRedisConnection redisConnection) {
        super(cacheManager, cacheName, configuration);
        this.redisConnection = redisConnection;
    }

    @Override
    protected V doGet(K key) throws CacheException, ClassCastException {
        return (V) redisConnection.sync().get(key).toString();
    }

    @Override
    protected V doPut(K key, V value) throws CacheException, ClassCastException {
        return (V) redisConnection.sync().set(key, value).toString();
    }

    @Override
    protected V doRemove(K key) throws CacheException, ClassCastException {
        return (V) redisConnection.sync().del(key).toString();
    }

    @Override
    protected void doClear() throws CacheException {

    }

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return null;
    }

    private byte[] serialize(Object value) throws CacheException {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(value);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    private V deserialize(byte[] bytes) throws CacheException {
        if (bytes == null) {
            return null;
        }
        V value = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = (V) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }

    @Override
    protected void doClose() {
        this.redisConnection.close();
    }
}
