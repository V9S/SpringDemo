package com.jiuqi.oauth.service;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtil2 {

    // 密码，长度要是8的倍数 注意此处为简单密码 简单应用 要求不高时可用此密码
    // /*DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，24小时内即可被破解。*/
    private static String password = "9588888888880288";

    // 测试
    public static void main(String args[]) {
        // 待加密内容

        String result = "qVutou/RKn6+Da1N04VNC5hBjwipHacEFtUwH/4R7cu4aj8AZtwoiWsqlXYoN29e";


        try {
            String decryResult = DESUtil.decryptor(result);
            System.out.println("解密后：" + new String(decryResult));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 
     * @Method: encrypt
     * @Description: 加密数据
     * @param data
     * @return
     * @throws Exception
     * @date 2016年7月26日
     */
    public static String encrypt(String data) { // 对string进行BASE64Encoder转换
        byte[] bt = encryptByKey(data.getBytes(), password);
        sun.misc.BASE64Encoder base64en = new sun.misc.BASE64Encoder();
        String strs = new String(base64en.encode(bt));
        return strs;
    }

    

    /**
     * 加密
     * 
     * @param datasource
     *            byte[]
     * @param password
     *            String
     * @return byte[]
     */
    private static byte[] encryptByKey(byte[] datasource, String key) {
        try {
            SecureRandom random = new SecureRandom();

            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    
}