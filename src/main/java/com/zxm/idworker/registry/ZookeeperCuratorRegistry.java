package com.zxm.idworker.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author Zxm
 * @description https://github.com/zhangxiaomin1993/id-worker
 */
@Slf4j
public class ZookeeperCuratorRegistry implements Registry {
    private static final int DEFAULT_RETRY_TIMES = 3;
    private static final int DEFAULT_SLEEP_TIME_MS = 2000;

    private static final String BASE_PATH = "/";

    private CuratorFramework client;

    public ZookeeperCuratorRegistry(String connectString, String namespace) throws Exception {
        this(connectString, namespace, new ExponentialBackoffRetry(DEFAULT_RETRY_TIMES, DEFAULT_SLEEP_TIME_MS));
    }

    public ZookeeperCuratorRegistry(String connectString, String namespace, RetryPolicy retryPolicy) throws Exception {
        if (StringUtils.isBlank(connectString)) {
            throw new IllegalArgumentException("connectString must is not null");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace must is not blank");
        }
        init(connectString, namespace, retryPolicy);
    }

    private void init(String connectString, String namespace, RetryPolicy retryPolicy) throws Exception {
        client = CuratorFrameworkFactory
                .builder()
                .connectString(connectString)
                .namespace(namespace)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    @Override
    public long getWorkerId() throws Exception {
        String sequencePath = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(BASE_PATH);
        if (StringUtils.isNotBlank(sequencePath)) {
            return Long.valueOf(sequencePath) % DEFAULT_MOD_VALUE;
        }
        throw new IllegalStateException("EPHEMERAL_SEQUENTIAL CREATE FAIL");
    }
}
