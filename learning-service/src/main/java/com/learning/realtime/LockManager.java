package com.learning.realtime;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.core.RLock;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * Created by kaustubh on 3/21/16.
 */
@Slf4j
public class LockManager {

    private static final String OPERATION_QUEUE_LOCK_KEY_PREFIX = "EVENT_OPERATION_QUEUE_LOCK_";

    public String getLockName(final Long id) {
        StringBuilder sb = new StringBuilder(OPERATION_QUEUE_LOCK_KEY_PREFIX);
        return sb.append(id.toString()).toString();
    }

    public boolean tryLock(final Redisson redisson, final Pool<Jedis> jedisPool) {
        try (Jedis jedi = jedisPool.getResource()) {
            String name = getLockName(100L);
            RLock rLock = redisson.getLock(name);
            // RedissonLock.isLocked() method has a bug in Redisson library where
            // it tries to make a command call on a connection that has already been shutdown.
            // This bug manifests itself only in sentinel mode.
            //
            // Under the hood, isLocked calls the EXISTS command in Redis to check if the lock key exists.
            // So, I'm replacing the above problematic call with Jedis.EXISTS
            if (!jedi.exists(name)) {
                return rLock.tryLock();
            }
        } catch (Exception e) {
            log.error("Exception occured when trying to get lock.", e);
        }
        return false;
    }
}
