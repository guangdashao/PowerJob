package tech.powerjob.server.core.alarm.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tech.powerjob.common.OmsConstant;
import tech.powerjob.common.utils.HttpUtils;
import tech.powerjob.server.common.PowerJobServerConfigKey;
import tech.powerjob.server.core.alarm.dto.SmsMessageEntity;
import tech.powerjob.server.extension.alarm.Alarm;
import tech.powerjob.server.extension.alarm.AlarmTarget;
import tech.powerjob.server.extension.alarm.Alarmable;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * http 回调报警
 *
 * @author tjq
 * @since 11/14/20
 */
@Slf4j
@Service
public class SmsAlarmService implements Alarmable {

    private static final String HTTP_PROTOCOL_PREFIX = "http://";
    private static final String HTTPS_PROTOCOL_PREFIX = "https://";
    private final Environment environment;
    private SmsRsaUtils smsRsaUtils;
    private String app_name;
    private String app_public_key;
    private String app_url;

    public SmsAlarmService(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onFailed(Alarm alarm, List<AlarmTarget> targetUserList) {
        if(smsRsaUtils==null) return ;
        if (CollectionUtils.isEmpty(targetUserList)) {
            return;
        }
        List<String> phones= targetUserList.stream()
                .filter(user -> StringUtils.isNotBlank(user.getPhone()))
                .map(AlarmTarget::getPhone)
                .distinct()
                .collect(Collectors.toList());

        SmsMessageEntity smsMessageEntity = new SmsMessageEntity();
        smsMessageEntity.setTelNum(phones);
        smsMessageEntity.setLevel(5);
        smsMessageEntity.setMessage(alarm.fetchContent());
        // 自动添加协议头
        MediaType jsonType = MediaType.parse(OmsConstant.JSON_MEDIA_TYPE);

        RequestBody requestBody = RequestBody.create(jsonType, JSONObject.toJSONString(smsMessageEntity));

        try {
            String response = HttpUtils.post(app_url, requestBody);
            log.info("[SmsAlarmService] invoke sms[url={}] successfully, response is {}", app_url, response);
        }catch (Exception e) {
            log.warn("[SmsAlarmService] invoke sms[url={}] failed!", app_url, e);
        }
    }
    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        app_name = environment.getProperty(PowerJobServerConfigKey.SMS_APP_NAME);
        app_public_key = environment.getProperty(PowerJobServerConfigKey.SMS_APP_PUBLIC_KEY);
        app_url=environment.getProperty(PowerJobServerConfigKey.SMS_APP_URL);

        log.info("[SMSAlarmService] init with app_name:{},app_public_key:{},app_url{}", app_name, app_public_key,app_url);

        if (StringUtils.isAnyBlank( app_name, app_public_key,app_url)) {
            log.warn("[SMSAlarmService] cannot get  app_name, app_public_key,app_url at the same time, this service is unavailable");
            return;
        }
        if (!app_url.startsWith(HTTP_PROTOCOL_PREFIX) && !app_url.startsWith(HTTPS_PROTOCOL_PREFIX)) {
            app_url = HTTP_PROTOCOL_PREFIX + app_url;
        }

        smsRsaUtils=new SmsRsaUtils(new ObjectMapper(),app_public_key);
        log.info("[SMSAlarmService] init SMSAlarmService successfully!");
    }
}
