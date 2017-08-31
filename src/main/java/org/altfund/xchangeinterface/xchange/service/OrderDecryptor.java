package org.altfund.xchangeinterface.xchange.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import java.util.Base64;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.InitializingBean;
import org.altfund.xchangeinterface.config.ApplicationPropertyException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import java.security.InvalidKeyException;


@Slf4j
public class OrderDecryptor implements EnvironmentAware, InitializingBean {

    private static final String AES_KEY = "AES_KEY";

    private Environment environment;
    private String aesKey;

    public OrderDecryptor() {
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
                                                                 BadPaddingException {
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
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes("UTF-8"), "AES");

            log.debug("init cipher");
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            log.debug("cipher decrypt");
            plainText = new String(cipher.doFinal(encryptedDecodedBytes));
            log.debug("decrypted plain text {}.", plainText);
            return plainText;

        }  catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        return plainText;
    }

}
