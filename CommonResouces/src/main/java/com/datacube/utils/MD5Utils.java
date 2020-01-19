package com.datacube.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Dale
 * @create 2019-12-03 9:34
 */
public class MD5Utils {

    public static String md5(String plainText){
        if(plainText != null){
            byte[] secreBytes = null;
            try {
                secreBytes = MessageDigest.getInstance("md5")
                        .digest(plainText.getBytes());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("没有md5此算法");
            }
            String md5code = new BigInteger(1,secreBytes).toString(16);
            for (int i = 0; i < 32 - md5code.length(); i++) {
               md5code = "0"+md5code;
            }
            return md5code;
        }else {
            return null;
        }
    }

}
