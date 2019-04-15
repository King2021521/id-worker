package com.zxm.idworker.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Author zxm
 * @Description 基于zk的注册中心
 * @Date Create in 上午 9:44 2019/4/12 0012
 */
@Slf4j
public class ZookeeperRegistry implements Registry {
    private static final int DEFAULT_SESSION_TIMEOUT = 3000;
    private static final String ROOT_NODE = "/idWorker";

    private ZooKeeper zkClient;

    public ZookeeperRegistry(String connectString) throws Exception {
        this(connectString, DEFAULT_SESSION_TIMEOUT);
    }

    public ZookeeperRegistry(String connectString, int sessionTimeOut) throws Exception {
        try {
            zkClient = new ZooKeeper(connectString, sessionTimeOut, watchedEvent -> log.info("path:{}, state:{}", watchedEvent.getPath(), watchedEvent.getState()));
            initRootNode(zkClient);
        } catch (IOException e) {
            log.error("zookeeper connect error,url:{},errorMsg:{}", connectString, e.getMessage());
            throw e;
        }
    }

    private void initRootNode(ZooKeeper zkClient) throws KeeperException, InterruptedException {
        if (zkClient.exists(ROOT_NODE, false) == null) {
            String path = zkClient.create(ROOT_NODE, "idWorker".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (StringUtils.isNotBlank(path)) {
                log.debug("root node init success,path:{}", path);
            }
        }
    }

    @Override
    public long getWorkerId() throws KeeperException, InterruptedException {
        String path = zkClient.create(ROOT_NODE + "/_", "idWorker".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        if (StringUtils.isNotBlank(path)) {
            log.debug("node create success,path:{}", path);
            return Long.valueOf(path.substring(ROOT_NODE.length() + 2, path.length())) % DEFAULT_MOD_VALUE;
        }
        return -1;
    }
}
