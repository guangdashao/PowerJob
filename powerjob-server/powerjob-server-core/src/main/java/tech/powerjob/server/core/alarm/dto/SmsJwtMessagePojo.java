package tech.powerjob.server.core.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author psychenDa
 * @description：
 * @date 2023/9/12 21:26
 **/
@Data
@AllArgsConstructor
public class SmsJwtMessagePojo {
    /**
     * SmsMessageEntity 的jwt字符串
     */
    private String jwt;
}
