package com.ecnucrowdsourcing.croudsourcingbackend.entity.constant;

public enum RewardStatus {
    PAID("已支付"), UNPAID("未支付");

    private final String desc;

    private RewardStatus(String s) {
        desc = s;
    }

    public String getDesc() {
        return desc;
    }
}
