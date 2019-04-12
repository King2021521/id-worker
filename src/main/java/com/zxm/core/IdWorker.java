package com.zxm.core;

import com.zxm.adapter.RegistryAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Id_Worker<br>
 * Id_Worker的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 0000000000 - 0000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的机器位(最多支持1023台机器)<br>
 * 12位序列，毫秒内的计数，7位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 */
@Slf4j
public class IdWorker {

    /**
     * 开始时间截 (2018-01-01 00:00:00)
     */
    private static final long twepoch = 1514736000000L;

    /**
     * 机器id所占的位数
     */
    private static final long workerIdBits = 10L;

    /**
     * 序列在id中占的位数
     */
    private static final long sequenceBits = 12L;


    private static final long workerIdShift = sequenceBits;

    /**
     * 时间截向左移22位(12+10)
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits;

    /**
     * 生成序列的掩码，这里为4095
     */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~1023)
     */
    private static long workerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private static long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private static long lastTimestamp = -1L;

    public IdWorker(RegistryAdapter registryAdapter) throws Exception {
        if (null == registryAdapter) {
            throw new Exception("registryAdapter init fail");
        }
        workerId = registryAdapter.getWorkerId();
        log.info("GLOBAL_WORkER_ID INIT:" + workerId);
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return id
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (workerId << workerIdShift)
                | exchangeSequence(sequence);
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 打乱自增序列
     *
     * @param sequence
     * @return
     * @since
     */
    protected static long exchangeSequence(long sequence) {
        String tmp = Long.toBinaryString(sequence | (1 << 12));
        StringBuffer sb = new StringBuffer(tmp.substring(1));
        long sqr = Long.parseLong(sb.reverse().toString(), 2) & sequenceMask;
        return sqr;
    }
}
