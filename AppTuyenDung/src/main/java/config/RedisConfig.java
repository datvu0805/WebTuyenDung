package config;

import redis.clients.jedis.RedisClient;

public final class RedisConfig {

    private static final String DEFAULT_REDIS_URL =
            "redis://localhost:6379";

    private static final RedisClient CLIENT =
            RedisClient.create(getRedisUrl());

    private RedisConfig() {
    }

    private static String getRedisUrl() {
        String redisUrl = System.getenv("REDIS_URL");

        if (redisUrl == null || redisUrl.isBlank()) {
            return DEFAULT_REDIS_URL;
        }

        return redisUrl;
    }

    public static RedisClient getClient() {
        return CLIENT;
    }

    public static void close() {
        CLIENT.close();
    }
}