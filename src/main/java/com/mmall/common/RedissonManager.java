package com.mmall.common;

import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * Created by rabbit on 2019/1/23.
 */
@Component
@Slf4j
public class RedissonManager {

    private Config config = new Config();
    private Redisson redisson = null;

    private static String redis1_Ip = PropertiesUtil.getProperty("redis1.ip");
    private static int redis1_Port = PropertiesUtil.getPropertyInInteger("redis1.port");
    private static String redis2_Ip = PropertiesUtil.getProperty("redis2.ip");
    private static int redis2_Port = PropertiesUtil.getPropertyInInteger("redis2.port");


    // 与 spring session 一样,目前的 redisson 版本也不支持redis分片连接.
    @PostConstruct
    public void initRedisson(){
        config.useSingleServer().setAddress(new StringBuilder(redis1_Ip).append(":").append(redis1_Port).toString());
        try {
            redisson = (Redisson) Redisson.create(config);
            log.info("{}, Redisson初始化完成.", DateTimeUtil.dateToStr(new Date()));
        } catch (Exception e) {
            log.error("{}, Redisson初始化过程中发生错误!", DateTimeUtil.dateToStr(new Date()));
            e.printStackTrace();
        }
    }

    public Redisson getRedisson() {
        return redisson;
    }
}
