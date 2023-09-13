package tech.powerjob.server.core.alarm.dto;

/**
 * @author psychenDa
 * @description：
 * @date 2023/9/12 21:26
 **/
public class SmsJwtMessagePojo {
    public SmsJwtMessagePojo(String jwt) {
        this.jwt = jwt;
    }

    /**
     * SmsMessageEntity 的jwt字符串
     */
    private String jwt;
}
