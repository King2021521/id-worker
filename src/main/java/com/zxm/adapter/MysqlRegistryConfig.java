package com.zxm.adapter;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @Author zxm
 * @Description
 * @Date Create in 上午 9:51 2019/4/15 0015
 */
@Data
public class MysqlRegistryConfig {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 数据域
     */
    private String data = "mysql-registry";

    /**
     * 生成时间
     */
    private Date createTime;

    public MysqlRegistryConfig(String data) {
        if(StringUtils.isNotBlank(data)){
            this.data = data;
        }
    }
}
