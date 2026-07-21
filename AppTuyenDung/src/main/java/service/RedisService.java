package service;

import com.google.gson.Gson;
import config.RedisConfig;
import redis.clients.jedis.RedisClient;

import java.lang.reflect.Type;

public class RedisService {
    private final RedisClient redis = RedisConfig.getClient();

    private final Gson gson = new Gson();

    public void set(String key, String value) {
        redis.set(key, value);
    }
// ttl : Time to live
    public void set(String key, String value, long ttlSeconds) {
        redis.setex(key, ttlSeconds, value);
    }

    public void delete(String key) {
        redis.del(key);
    }
    public long increment(String key) {
        return redis.incr(key);
    }

    public void expire(String key, long seconds) {
        redis.expire(key, seconds);
    }

    public long ttl(String key) {
        return redis.ttl(key);
    }

    public String get(String key) {
        return redis.get(key);
    }

    public boolean exists(String key) {
        return redis.exists(key);
    }

    public String ping() {
        return redis.ping();
    }
    public void setObjiect(String key, Object value, long ttlSeconds) {
        String json = gson.toJson(value);
        redis.setex(key, ttlSeconds, json);
    }

    public void setWithTtl(String key, String value, long seconds) {
        redis.setex(key, seconds, value);
    }

    public <T> T getObject(String key, Type type) {
        String json = redis.get(key);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, type);
    }

    public <T> T getObjiect(String key, Class<T> objectClass) {
        return getObject(key, objectClass);
    }
}
