package com.example.hzhm.versionupdate.utils.serves;

/**
 * 功能描述：定义一个enum枚举类型列举请求返回码信息
 * 注：1、此枚举类型为自带String属性的定制枚举类型（定制枚举类型的，枚举体结尾必须以“;”分号结束）
 *     2、定制枚举类型的构造函数并不能被public、protected修饰，只能被private修饰
 *     3、枚举的相等的判断“==”和“equel"都可以
 * Created by hzhm on 2016/8/18.
 */
public enum  ServerResultCode {

    OK("000000"), //操作成功
    ACCESS_FAIL("100000"),//操作失败
    INVAIL_REQUEST("200000"), //非法请求
    PAY_PWD_ERROR("300000"), //交易密码错误
    SERVER_IN_MENDING("400000"),//服务器维护中
    BALANCE_ERROR("100002"), //账户余额不足
    TENDER_BALANCE_ERROR("100003"), //标的可投余额不足
    MUST_RESET_LOGIN_PWD("601"),//需要重置登录密码
    DATA_PARSE_ERROR("-999"),
    NETWORK_ERROR("-998"), //无网络
    TIME_OUT("-995"), //连接超时
    NEED_LOGIN("-997"), //需要重新登录
    SERVER_DOWN("-996"),
    IS_BINDED_WECHAT("24"), //即将绑定的泰然城帐号已绑定过微信
    IS_BIND_TRC_ACCOUNT("25"), //即将绑定的微信已绑定过泰然城帐号
    UNKNOWN_ERROR("-1"),
    NO_DATA("-2"); //无数据

    private String code;

    ServerResultCode(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
