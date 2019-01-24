package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by rabbit on 2018/2/6.
 */
public class Const {
    public static final String CURRENT_USER = "current_user";

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";

    public static final String TOKEN_PREFIX = "token_";

    public interface RedisCacheExTime{
        int REDIS_SESSION_EXTIME = 60 * 30;
        int REDIS_SESSION_EXPIRED = -2;
        int REDIS_FORGET_TOKEN_CACHE_TIME = 60 * 60 * 12;
    }

    public interface productListOrderBy{
        /**
         * 为何使用Set集呢?
         * 因为Set集里面的查找元素的时间复杂度为O(1),而List集的时间复杂度为O(n)
         * 虽然现在的存放元素数量少,但我们秉承着提高查找效率为准则,故使用Set集
         */
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }


    public interface ROLE{
        int ROLE_CUSTOMER = 0;  //普通用户
        int ROLE_ADMIN = 1;  //管理员
    }

    public interface Cart{
        int CHECKED = 1;
        int UN_CHECKED = 0;

        String LIMIT_NUM_SUCCESS = "LINIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
    }

    public enum ProductStatusEnum{

        ON_SALE(1,"在线");

        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.value = value;
            this.code = code;
        }
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private String value;
        private int code;
        OrderStatusEnum(int code,String value){
            this.value = value;
            this.code = code;
        }
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : values()) {
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到相对应的枚举！");
        }
    }

    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{

        ALIPAY(1,"支付宝");

        private String value;
        private int code;
        PayPlatformEnum(int code,String value){
            this.value = value;
            this.code = code;
        }
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        private String value;
        private int code;
        PaymentTypeEnum(int code,String value){
            this.value = value;
            this.code = code;
        }
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if(code == paymentTypeEnum.getCode()){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到相对应的枚举！");
        }
    }

    public interface RedisKey{
        String CLOSE_ORDER_TASK_LOCK = "CLOSE_ORDER_TASK_LOCK";
    }

}
