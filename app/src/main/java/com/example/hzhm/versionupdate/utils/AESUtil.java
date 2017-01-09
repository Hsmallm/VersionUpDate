package com.example.hzhm.versionupdate.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;

/**
 * TODO
 * Created by hzhm on 2016/8/22.
 */
public class AESUtil {

    private AESUtil() {
    }

    public static String encrypt(String encData, String vector)
            throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec skeySpec = new SecretKeySpec(new byte[]{78, 83, 85, 84, 70, 56, 83, 116, 114, 105, 110, 103, 69, 110, 99, 111}, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
        return new Decoder.BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
    }

    // 加密
    public static String encrypt(String sSrc) {
        try {
            if(TextUtils.isEmpty(sSrc)){
                return "";
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(new byte[]{78, 83, 85, 84, 70, 56, 83, 116, 114, 105, 110, 103, 69, 110, 99, 111}, "AES");
            IvParameterSpec iv = new IvParameterSpec(new byte[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54});// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return new Decoder.BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
        } catch (NoSuchAlgorithmException e) {
//            LogUtil.e(e);
        } catch (NoSuchPaddingException e) {
//            LogUtil.e(e);
        } catch (InvalidKeyException e) {
//            LogUtil.e(e);
        } catch (InvalidAlgorithmParameterException e) {
//            LogUtil.e(e);
        } catch (IllegalBlockSizeException e) {
//            LogUtil.e(e);
        } catch (BadPaddingException e) {
//            LogUtil.e(e);
        } catch (UnsupportedEncodingException e) {
//            LogUtil.e(e);
        }
        return sSrc;
    }

    // 解密
    public static String decrypt(String sSrc) {
        try {
            if(TextUtils.isEmpty(sSrc)){
                return "";
            }
            byte[] raw = new byte[]{78, 83, 85, 84, 70, 56, 83, 116, 114, 105, 110, 103, 69, 110, 99, 111};
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(new byte[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54});
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return sSrc;
        }
    }

    public static String decrypt(String sSrc,String ivs) throws Exception {
        try {
            byte[] raw = new byte[]{78, 83, 85, 84, 70, 56, 83, 116, 114, 105, 110, 103, 69, 110, 99, 111};
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ('a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ('a')));
        }

        return strBuf.toString();
    }

    public static void main(String[] args) throws Exception {
        // 需要加密的字串
        String cSrc = "[{\"request_no\":\"1001\",\"service_code\":\"FS0001\",\"contract_id\":\"100002\",\"order_id\":\"0\",\"phone_id\":\"13913996922\",\"plat_offer_id\":\"100094\",\"channel_id\":\"1\",\"activity_id\":\"100045\"}]";

        // 加密
        long lStart = System.currentTimeMillis();
        String enString = AESUtil.encrypt(cSrc);
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        lStart = System.currentTimeMillis();
        String DeString = AESUtil.decrypt(enString);
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }
}
