package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.utils.ShardedRedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by rabbit on 2019/1/19.
 */
@Slf4j
@Component
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private RedissonManager redissonManager;

    /**
     * 在指定时间内查询订单列表，若发现有未付款的订单，则对其进行关单处理
     * 规定该定时任务每分钟执行一次(1分钟的整数倍)
     */
//    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderVERSION1(){
        log.info("{},定时调度任务——关闭订单开始执行.", DateTimeUtil.dateToStr(new Date()));
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
//        iOrderService.closeOrder(hour);
        log.info("{},定时调度任务——关闭订单执行结束.", DateTimeUtil.dateToStr(new Date()));
    }


    // 采用Redis分布式锁的方式,避免由于同一个定时任务因集群的情况而导致每个容器都执行一次,由此
    // 造成资源浪费的情况
//    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderVERSION2(){
        log.info("{},定时调度任务——关闭订单开始执行.", DateTimeUtil.dateToStr(new Date()));
        Long timeout = Long.parseLong(PropertiesUtil.getProperty("close.order.task.lock.timeout", "5000"));
        Long result = ShardedRedisPoolUtil.setnx(Const.RedisKey.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()) + timeout);
        if(result != null && result == 1){
            //执行关单业务
            log.info("线程: {}获得分布式锁: {}", Thread.currentThread().getName(), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
            closeOrder();
        }else{
            log.info("线程: {}未获得分布式锁: {}", Thread.currentThread().getName(), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("{},定时调度任务——关闭订单执行结束.", DateTimeUtil.dateToStr(new Date()));
    }


    // Redis分布式锁优化版,加入双重锁验证机制,防止死锁情况发生,提供更可靠的执行逻辑
//    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderVERSION3(){
        log.info("{},定时调度任务——关闭订单开始执行.", DateTimeUtil.dateToStr(new Date()));
        Long timeout = Long.parseLong(PropertiesUtil.getProperty("close.order.task.lock.timeout", "5000"));
        Long result = ShardedRedisPoolUtil.setnx(Const.RedisKey.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()) + timeout);
        if(result != null && result == 1){
            //执行关单业务
            log.info("线程: {}获得分布式锁: {}", Thread.currentThread().getName(), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
            closeOrder();
        }else{
            // 第一重锁: 判断当前锁是否已经过期,也即过了我们设置的timeout时间
            String lockValue = ShardedRedisPoolUtil.get(Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
            log.info("线程: {}未获得分布式锁.开始进行第一重锁验证.", Thread.currentThread().getName());
            if(lockValue != null && (System.currentTimeMillis() > Long.valueOf(lockValue))){
                log.info("通过第一重锁的验证,接着进行第二重锁的验证.");
                // 第二重锁: 再次判断当前锁是否已经expired或者说是否在这个间隙内有被其他线程重新抢到锁设置超时时间
                // Redis的getSet操作是原子性的,因此不必担心在get完值之后会被其他线程乘机重新set新的值
                String getSetValue = ShardedRedisPoolUtil.getSet(Const.RedisKey.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()) + timeout);
                if(getSetValue == null || StringUtils.equals(lockValue, getSetValue)){
                    // 进入该分支,表明之前的锁已经被释放掉了;或者是说之前的锁已经超时了同时没有其他线程抢占锁,那么这时就轮到当前线程拿到锁进行业务操作了
                    log.info("通过第二重锁的验证.");
                    closeOrder();
                }else{
                    log.info("{}, 线程: {}未通过第二重锁的验证,结束本次任务", DateTimeUtil.dateToStr(new Date()), Thread.currentThread().getName());
                }
            }else{
                log.info("{}, 线程: {}未通过第一重锁的验证,结束本次任务.", DateTimeUtil.dateToStr(new Date()), Thread.currentThread().getName());
            }
        }
    }

    // 采用 Redisson 的RLOCK版本,实现Redis分布式锁.
//    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderVERSION4(){
        log.info("{},定时调度任务——关闭订单开始执行.", DateTimeUtil.dateToStr(new Date()));
        Redisson redisson = redissonManager.getRedisson();
        RLock rLock = redisson.getLock(Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
        boolean ifGetLock = false;
        try {
            // 让线程尝试获取锁,不等待,若没有获取到则退出获取锁流程等待下一次机会.否则抢到锁后设置锁过期时间为50s,50s后被动释放锁.
            if (ifGetLock = rLock.tryLock(0, 50, TimeUnit.SECONDS)) {
                log.info("{}, 线程:{} 获取到分布式锁:{}", DateTimeUtil.dateToStr(new Date()), Thread.currentThread().getName(), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
                log.info("开始进行关单处理");
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
//                iOrderService.closeOrder(hour);
            }else{
                log.info("{}, 线程:{} 未获取到分布式锁:{}", DateTimeUtil.dateToStr(new Date()), Thread.currentThread().getName(), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
            }
        }catch(InterruptedException ex){
            log.error("{}, 线程:{} 在获取分布式锁时发生异常!", DateTimeUtil.dateToStr(new Date()), Thread.currentThread().getName());
            ex.printStackTrace();
        }finally{
            if(!ifGetLock){
                return;
            }
            // 让线程主动释放锁
            rLock.unlock();
            log.info("{}, 线程:{} 释放分布式锁:{}", DateTimeUtil.dateToStr(new Date()), Thread.currentThread().getName(), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
        }
    }



    private void closeOrder(){
        log.info("开始进行关单处理");
        //为防止在Redis中产生死锁,对分布式锁进行expire处理
        ShardedRedisPoolUtil.expire(Const.RedisKey.CLOSE_ORDER_TASK_LOCK, 10);
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        iOrderService.closeOrder(hour);
        //业务执行完后也要及时删除锁对应的key,释放锁
        ShardedRedisPoolUtil.del(Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
        log.info("{}, 执行完关单任务,释放掉分布式锁: {}", DateTimeUtil.dateToStr(new Date()), Const.RedisKey.CLOSE_ORDER_TASK_LOCK);
    }

}
