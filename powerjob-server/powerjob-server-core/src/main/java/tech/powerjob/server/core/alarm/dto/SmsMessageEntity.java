package tech.powerjob.server.core.alarm.dto;

import lombok.Data;

import java.util.List;

/**
 * @author psychenDa
 * @description：
 * @date 2023/9/12 21:25
 **/
@Data
public class SmsMessageEntity {
    /**
     * 手机列表
     */
    private List<String> telNum;
    /**
     * 短信内容，不可包含：【】
     */
    private String message;

    /**
     * 短信发送时间，指定时间发送，没有要求，可不传
     * 格式为：年年年年月月日日时时分分秒秒，例如20090801123030 表示2009年8月1日12点30分30秒该条短信会发送到用户手机
     */
    private String sendDateTime;

    /**
     * 发送等级
     */
    private int level = 5;

}
