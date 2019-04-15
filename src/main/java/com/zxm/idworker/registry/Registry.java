package com.zxm.idworker.registry;

/**
 * @Author zxm
 * @Description 注册适配器
 * @Date Create in 上午 9:45 2019/4/12 0012
 */
public interface Registry {
    int DEFAULT_MOD_VALUE = 1024;
    long getWorkerId() throws Exception;
}
