package com.dynamicwebservice.util;

import com.dynamicwebservice.dto.WebServiceRequest;
import com.zipe.util.classloader.FileClassLoader;
import com.zipe.util.crypto.AesUtil;
import com.zipe.util.file.FileUtil;
import org.apache.cxf.Bus;
import org.springframework.context.ApplicationContext;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Test {
    public static void main(String[] args) throws Exception {


        String originalString = "WebComm";

        // 将字符串转换为字节数组
        byte[] byteArray = originalString.getBytes();

        // 将字节数组转换为 16 进制字符串
        String hexString = StringUtil.bytesToHex(byteArray);
        String fixedLengthHexString = StringUtil.fixLength(hexString, 16);
        System.out.println("Hex String: " + fixedLengthHexString);

        AesUtil aesUtil = new AesUtil(fixedLengthHexString);
        File encryptedFile = new File("D:/tmp/classes/WebServiceHandler.en");
        File file = new File("D:/tmp/classes/WebServiceHandler.class");
        aesUtil.encryptFile(file, encryptedFile);


//        File decryptedFile = new File("D:/tmp/Haha");
//        aesUtil.decryptFile(file, decryptedFile);
//        FileClassLoader diskClassLoader = new FileClassLoader(decryptedFile.getPath());
//        try {
//            Class<?> c = diskClassLoader.loadClass("com.dynamicwebservice.util.WebServiceHandler");
//            if (c != null) {
//                Object obj = c.getDeclaredConstructor().newInstance();
//                Method method = c.getDeclaredMethod("registerWebService", WebServiceRequest.class, ApplicationContext.class, Bus.class);
//                method.invoke(obj, "I'm back");
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
//                 InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }

    }

    // 生成 AES 密钥（可以替换为从存储中获取密钥）
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom()); // 生成256位密钥
        return keyGen.generateKey();
    }

    // 解密文件并返回解密后的 ByteArrayInputStream
    public static ByteArrayInputStream decryptFileToStream(SecretKey key, String encryptedFilePath) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(encryptedFilePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    baos.write(output);
                }
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                baos.write(outputBytes);
            }

            return new ByteArrayInputStream(baos.toByteArray());
        }
    }
}
