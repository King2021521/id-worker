package com.zxm.idworker.registry;

import com.zxm.idworker.adapter.MysqlAdapter;
import com.zxm.idworker.adapter.MysqlRegistryConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zxm
 * @Description 基于mysql为注册中心
 * @Date Create in 上午 9:15 2019/4/15 0015
 */
@Slf4j
public class MysqlRegistry implements Registry {
    private MysqlAdapter mysqlAdapter;
    private String dataField;

    public MysqlRegistry(MysqlAdapter mysqlAdapter, String dataField) {
        this.mysqlAdapter = mysqlAdapter;
        this.dataField = dataField;
    }

    @Override
    public long getWorkerId() throws Exception {
        MysqlRegistryConfig config = new MysqlRegistryConfig(dataField);
        int result = mysqlAdapter.registry(config);
        if (result == 1) {
            log.info("节点注册成功，sequenceId:{}", config.getId());
        }
        Long sequenceId = config.getId();
        if (sequenceId != null) {
            return sequenceId % DEFAULT_MOD_VALUE;
        }
        return -1;
    }
}
