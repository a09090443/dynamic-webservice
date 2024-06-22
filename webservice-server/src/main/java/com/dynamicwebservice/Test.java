package com.dynamicwebservice;

import com.zipe.util.crypto.AesUtil;

import java.io.File;
import java.net.InetAddress;

public class Test {

    public static void main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        System.out.println("Local HostAddress:" + addr.getHostAddress());
        String hostname = addr.getHostName();
        System.out.println("Local host name: " + hostname);
        AesUtil aesUtil = new AesUtil("2022120220221202");
        long d = System.currentTimeMillis();
        File source = new File("D:\\tmp\\InstallCert.java");
        File encryptedFile = new File("D:\\tmp\\InstallCert.javaen");
        File decryptedFile = new File("D:\\tmp\\InstallCert.javade");
        aesUtil.encryptFile(source, encryptedFile);
        aesUtil.decryptFile(encryptedFile, decryptedFile);
    }
}
