package com.zxm.adapter;

/**
 * @Author zxm
 * @Description mysql适配器
 * @Date Create in 上午 9:31 2019/4/15 0015
 */
public interface MysqlAdapter {
    /**
     * 向mysql中注册节点，自动返回ID
     * @param config
     * @return
     */
    int registry(MysqlRegistryConfig config);
}
