package edu.hcmuaf.model.hash;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class HashCipher {
    //WhirlPool
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public String hash(String data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] digest = md.digest(data.getBytes());
        BigInteger number = new BigInteger(1, digest);
        return number.toString(16);
    }

    public String hashFile(String src, String ouput, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        InputStream fis = new BufferedInputStream(new FileInputStream(src));
        DigestInputStream dis = new DigestInputStream(fis, md);
        byte[] byteRead = new byte[1024];
        int i;
        while ((i = dis.read(byteRead)) != -1) {

        }
        byte[] digest = dis.getMessageDigest().digest();
        BigInteger number = new BigInteger(1, digest);
        String hash = number.toString(16);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ouput))) {
            bw.write(hash);
        }
        return hash;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        HashCipher cipher = new HashCipher();
        System.out.println(cipher.hash("dang tran tan luc", "Whirlpool"));
    }
}
