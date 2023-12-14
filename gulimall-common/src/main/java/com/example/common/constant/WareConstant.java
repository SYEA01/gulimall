package com.example.common.constant;

/**
 * 库存常量
 *
 * @author taoao
 */
public class WareConstant {
    public enum PurchaseStatusEnum {
        CREATE(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISH(3, "已完成"),
        HASERROR(4, "有异常"),
        ;

        private int code;

        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        PurchaseStatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public enum PurchaseDetailStatusEnum {
        CREATE(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HASERROR(4, "采购失败"),
        ;

        private int code;

        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        PurchaseDetailStatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
