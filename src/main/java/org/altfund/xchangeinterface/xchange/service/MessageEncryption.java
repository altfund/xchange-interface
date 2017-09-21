package org.altfund.xchangeinterface.xchange.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import java.util.Base64;
import java.io.UnsupportedEncodingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.InitializingBean;
import org.altfund.xchangeinterface.config.ApplicationPropertyException;
import org.altfund.xchangeinterface.util.JsonHelper;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import java.security.InvalidKeyException;
import java.security.SecureRandom;


@Slf4j
public class MessageEncryption implements EnvironmentAware, InitializingBean {

    private static final String AES_KEY = "AES_KEY";

    private Environment environment;
    private String aesKey;

    private final JsonHelper jh;

    public MessageEncryption(JsonHelper jh) {
        this.jh = jh;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
     @Override
     public void afterPropertiesSet() throws Exception {
        this.aesKey = getAesKey();
        log.debug("property {}.", aesKey);
     }

    protected String getAesKey() {
        return getProperty(AES_KEY);
    }

    protected String getProperty(String aesKey) {

        Optional<String> propertyValue = Optional
            .ofNullable(environment.getProperty(aesKey));

        if (propertyValue.isPresent()) {
            return propertyValue.get();
        }
        else {
            throw new ApplicationPropertyException(aesKey);
            //throw new RuntimeException();
        }

    }

    public String decrypt(EncryptedOrder encryptedOrder) throws  NoSuchAlgorithmException,
                                                                 NoSuchPaddingException,
                                                                 InvalidKeyException,
                                                                 IllegalBlockSizeException,
                                                                 BadPaddingException,
                                                                 UnsupportedEncodingException,
                                                                 InvalidAlgorithmParameterException {
        String plainText = "";

        try{
            log.debug("iv {}.", encryptedOrder.getIv());
            log.debug("base 64 encrytped data {}.", encryptedOrder.getEncryptedData());
            log.debug("base 64 encrytped data byes {}.", encryptedOrder.getEncryptedData().getBytes("UTF-8"));
            byte[] encryptedDecodedBytes = Base64.getDecoder().decode(encryptedOrder.getEncryptedData().getBytes("UTF-8"));
            //byte[] encryptedDecodedBytes = Base64.getDecoder().decode(encryptedOrder.getEncryptedData().getBytes("UTF-8"));
            log.debug("base 64 decoded encrytped data byes {}.", encryptedDecodedBytes);
            //String encryptedDecodedString = new String(encryptedDecodedBytes);
            String ivString = encryptedOrder.getIv();

            log.debug("init iv and key {}.", aesKey);
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes("UTF-8"), "AES");

            log.debug("init cipher");
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            log.debug("cipher decrypt");
            plainText = new String(cipher.doFinal(encryptedDecodedBytes));
            log.debug("decrypted plain text {}.", plainText);
            return plainText;

        }  catch (Exception e) {
            //System.err.println("Caught Exception: " + e.getMessage());
            throw e;
        }
    }

    public String encrypt(String plainText) throws  NoSuchAlgorithmException,
                                                                 NoSuchPaddingException,
                                                                 InvalidKeyException,
                                                                 IllegalBlockSizeException,
                                                                 BadPaddingException,
                                                                 UnsupportedEncodingException,
                                                                 JsonProcessingException,
                                                                 InvalidAlgorithmParameterException {
        try{
            ObjectNode jsonRes = jh.getObjectNode();

            final byte[] iv = new byte[16];
            final SecureRandom theRNG = new SecureRandom();
            theRNG.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes("UTF-8"), "AES");

            log.debug("init cipher");
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

            log.debug("cipher encrypt");

            byte[] base64Iv = Base64.getEncoder().encode(ivSpec.getIV());
            jsonRes.put("iv",ivSpec.getIV());

            byte[] encryptedData = cipher.doFinal(plainText.getBytes("UTF-8"));
            jsonRes.put("encrypted_data", encryptedData);
            return jh.getObjectMapper().writeValueAsString(jsonRes);

        }  catch (Exception e) {
            throw e;
        }
    }

}
