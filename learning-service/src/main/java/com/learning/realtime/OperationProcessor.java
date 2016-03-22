package com.learning.realtime;

import com.google.common.base.Strings;
import com.learning.config.LearningConfiguration;
import com.learning.rediswebsocket.EventChannel;
import com.learning.util.JsonUtilities;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaustubh on 3/21/16.
 */
@Slf4j
public class OperationProcessor {


    private String message = null;

    private Pool<Jedis> pool = null;

    private EventChannel dataChannel;

    private JsonUtilities jsonUtil = new JsonUtilities();

    private LearningConfiguration configuration = null;

    public OperationProcessor(final String message, final EventChannel dataChannel, final Pool<Jedis> pool,
                              final LearningConfiguration configuration) {
        this.message = message;
        this.dataChannel = dataChannel;
        this.pool = pool;
        this.configuration = configuration;
    }

    public void initLatestState() {
        if (null == getEventLatestState()) {
            Long revision = getLatestRevision();
            setEventLatestState(revision, message);
        }
    }

    private enum OperationPrefix {
        EVENT_RECEIVED_BUT_NOT_PROCESSED,
        EVENT_REVISION_LOG,
        EVENT_LATEST_STATE,
    }

    private String getEventRevisionLogKey() {
        val sbRevisionLogKey = getOperationKey(OperationPrefix.EVENT_REVISION_LOG);
        log.debug("generated sbRevisionLogKey: {}", sbRevisionLogKey);
        return sbRevisionLogKey;
    }

    private String getEventLatestStateKey() {
        val sbLatestStateKey = getOperationKey(OperationPrefix.EVENT_LATEST_STATE);
        log.debug("generated sbLatestStateKey: {}", sbLatestStateKey);
        return sbLatestStateKey;
    }

    private String getReceivedButNotProcessedKey() {
        val receivedButNotProcessedKey = getOperationKey(OperationPrefix.EVENT_RECEIVED_BUT_NOT_PROCESSED);
        log.debug("generated receivedButNotProcessedKey: {}", receivedButNotProcessedKey);
        return receivedButNotProcessedKey;
    }

    private String getOperationKey(final OperationPrefix opPrefix) {
        return String.format("%s.websocket.100.%s", configuration.getRedisConfiguration().getPrefix(), opPrefix);
    }

    public void handleNewOperation(final Operation op) throws IOException {
        addToReceivedButNotProcessedQueue(op);
    }

    public void processOperation() {
        try {
            while (hasMoreOperationsInQueue()) {
                Operation op = getNextOperationFromQueue();
                //handleOperation(op);
                updateEventLatestState(op);
                addToRevisionLog(getEventRevisionLogKey(), op);
                updateClients(op);
            }
        } catch (Exception e) {
            log.error("Could not process operation", e);
        }
    }

    private void updateClients(final Operation op) {
        String dataChannelName = getChannelName(configuration.getRedisConfiguration().getPrefix());
        try {
            dataChannel.publish(dataChannelName, jsonUtil.asString(op));
        } catch (IOException e) {
            log.error("Could not publish update to clients", e);
        }
    }

    private String getChannelName(final String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            // we don't want to accidentally create an invalid channel that somehow leaks information
            throw new IllegalArgumentException();
        }
        val channelName = String.format("%s.websocket.100", prefix);
        log.debug("generated channel name: {}", channelName);
        return channelName;
    }

    private void addToReceivedButNotProcessedQueue(final Operation op) throws IOException {
        Jedis jedi = pool.getResource();
        try {
            String key = getReceivedButNotProcessedKey();
            String opString = jsonUtil.asString(op);
            jedi.rpush(key, opString);
        } catch (Exception e) {
            log.error("Could not add operation to queue for processing.", e);
            pool.returnBrokenResource(jedi);
            throw e;
        } finally {
            pool.returnResource(jedi);
        }
    }

    private Operation getNextOperationFromQueue() {
        Operation op = null;
        try (Jedis jedi = pool.getResource()) {
            String nextOp = jedi.lpop(getReceivedButNotProcessedKey());
            if (!Strings.isNullOrEmpty(nextOp)) {
                op = jsonUtil.pojoFromJson(nextOp, Operation.class);
            }
        } catch (IOException ioe) {
            log.error("Could not fetch the next operation to be processed from queue.", ioe);
        }
        return op;
    }

    private boolean hasMoreOperationsInQueue() {
        Jedis jedi = pool.getResource();
        try {
            Long length = jedi.llen(getReceivedButNotProcessedKey());
            if (length > 0) {
                return true;
            } else {
                jedi.del(getReceivedButNotProcessedKey());
            }
        } catch (Exception e) {
            log.error("Could not find out if there are more operations in queue.", e);
            pool.returnBrokenResource(jedi);
        } finally {
            pool.returnResource(jedi);
        }
        return false;
    }

    private void addToRevisionLog(final String key, final Operation op) {
        try (Jedis jedi = pool.getResource()) {
            jedi.hset(key, Long.toString(op.getRevision()), jsonUtil.asString(op));
        } catch (IOException ioe) {
            log.error("Could not add operation to revision log.", ioe);
        }
    }

    private void setEventLatestState(final Long revision, final String message) {
        Jedis jedi = pool.getResource();
        String key = getEventLatestStateKey();
        HashMap<String, String> state = new HashMap<>();
        state.put("revision", Long.toString(revision));
        jedi.hmset(key, state);
    }

    private void updateEventLatestState(final Operation op) {
        Jedis jedi = pool.getResource();
        String key = getEventLatestStateKey();
        HashMap<String, String> state = new HashMap<>();
        Long revision = getLatestRevision();
        revision = (revision != null ? revision : 0) + 1;
        state.put("revision", Long.toString(revision));
        jedi.hmset(key, state);
        op.setRevision(revision);
    }

    private Long getLatestRevision() {
        Long revision = 0L;
        Jedis jedi = pool.getResource();
        try {
            String revisionString = jedi.hget(getEventLatestStateKey(), "revision");
            if (Strings.isNullOrEmpty(revisionString)) {
                revision = jedi.hlen(getEventRevisionLogKey());
            } else {
                revision = Long.parseLong(revisionString);
            }
        } catch (Exception e) {
            log.error("Could not get latest revision number.", e);
            pool.returnBrokenResource(jedi);
        } finally {
            pool.returnResource(jedi);
        }
        return revision;
    }

    private Map<String, Object> getEventLatestState() {
        Jedis jedi = pool.getResource();
        try {
            String jsonFromRedis = jedi.hget(getEventLatestStateKey(), "event");
            if (!Strings.isNullOrEmpty(jsonFromRedis)) {
                return jsonUtil.toObjectMap(jsonFromRedis);
            }
        } catch (Exception e) {
            log.error("Could not get latest event state.", e);
            pool.returnBrokenResource(jedi);

        } finally {
            pool.returnResource(jedi);
        }
        return null;
    }

}
