package com.learning.redis;

/**
 * Created by kaustubh on 3/19/16.
 */
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class RedisExample {

    public static void main(String[] args) {
        verifyRedisServer();
        getRedisListOfKeys();
        //redisListExample();
        //redisStringExample();
    }

    private static void verifyRedisServer() {
        // Get redis server
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server successful");
        // verify server status
        System.out.println("Server is running: "+jedis.ping());
    }

    private static void getRedisListOfKeys() {
        // Get redis server
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server successful");

        // Get list of keys
        Set<String> list = jedis.keys("*");
        for(String name : list) {
            System.out.println("List of stored keys:: "+name);
        }
    }

    private static void redisListExample() {
        // Get redis server
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server successful");

        jedis.lpush("tutorial-list", "Redis");
        jedis.lpush("tutorial-list", "Mongodb");
        jedis.lpush("tutorial-list", "Mysql");
        // Get redis list
        List<String> list = jedis.lrange("tutorial-list", 0 ,5);
        for (String listValue : list) {
            System.out.println("Stored string in redis:: "+listValue);
        }
    }

    private static void redisStringExample() {
        // Get redis server
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server successful");

        // Set redis string key
        jedis.set("w3ckey", "Redis tutorial");
        // Get redis string key
        System.out.println("Stored string in redis:: "+ jedis.get("w3ckey"));
    }

}
