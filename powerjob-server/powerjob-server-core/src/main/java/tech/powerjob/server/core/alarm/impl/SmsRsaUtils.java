package tech.powerjob.server.core.alarm.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * @author psychenDa
 * @description：
 * @date 2023/9/12 21:24
 **/
@Slf4j
public class SmsRsaUtils {
    private KeyFactory keyFactory;
    private ObjectMapper objectMapper;
    private RSAPublicKey publicKey;

    public SmsRsaUtils(ObjectMapper objectMapper, String publicKey) throws NoSuchAlgorithmException {
        this.keyFactory = KeyFactory.getInstance("RSA");
        this.objectMapper = objectMapper;
        this.publicKey = publicKey(publicKey);

    }

    /**
     * author: psychenDa ,date: 2022-05-17 10:16
     * <p>
     * mock 发送短信的加密过程
     *
     * @param userName
     * @param object
     * @return
     * @throws JsonProcessingException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws JOSEException
     */
    public String encodeToJwt(String userName, Object object) throws JsonProcessingException, JOSEException {
        if (log.isDebugEnabled()) {
            log.debug("encodeToJwt username:{} ,object:{}", userName, object);
        }
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.claim("messagePOJO", objectMapper.writeValueAsString(object));

        Date now = new Date();
        JWTClaimsSet jwtClaims = builder
                .issuer("middle-sms")
                .subject("sms") // 账号同步为accout，组织机构同步为org
                .audience(userName) // 目标系统,appBo.getAppCode()
                .expirationTime(new Date(now.getTime() + 1000 * 10)) // expires in 10 seconds
                .notBeforeTime(now)
                .issueTime(now)
                .build();

        /**
         * 从页面参数的额外参数中获取加密算法
         * {
         *   "alg": "RSA-OAEP-256",
         *   "enc": "A128GCM"
         * }
         */

        JWEHeader header = new JWEHeader(JWEAlgorithm.parse("RSA-OAEP-256"), EncryptionMethod.parse("A128GCM"));
//        JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);

        // Create the encrypted JWT object
        EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

        // Create an encrypter with the specified public RSA key
        JWEEncrypter encrypter = new RSAEncrypter(this.publicKey);

        // Do the actual encryption
        jwt.encrypt(encrypter);

        // Serialise to JWT compact form
        String jwtString = jwt.serialize();
        if (log.isDebugEnabled()) {
            log.debug("encodeToJwt result:{}", jwtString);
        }
        return jwtString;
    }

    // 页面参数中获取公钥
    RSAPublicKey publicKey(String key) {
        byte[] bytes = Base64.getDecoder().decode(key);
        try {
            return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (InvalidKeySpecException e) {
            log.error("公钥解析失败：", e);
            throw new IllegalStateException("公钥错误，请查看");
        }


    }
}
