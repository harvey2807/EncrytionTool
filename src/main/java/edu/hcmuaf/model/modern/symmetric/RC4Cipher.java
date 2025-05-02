package edu.hcmuaf.model.modern.symmetric;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class RC4Cipher {
    private SecretKey secretKey;
    private static final String ALGORITHM = "RC4";

    public RC4Cipher() {
    }

    public String genKey(int size) throws Exception {
        if (size < 40 || size > 2048) {
            throw new IllegalArgumentException("Key size must be between 40 and 2048 bits for RC4");
        }
        byte[] keyBytes = new byte[size / 8];
        new SecureRandom().nextBytes(keyBytes);
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public void setKeyFromBase64(String keyBase64) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyBase64);
        if (decodedKey.length < 5 || decodedKey.length > 256) { // 40–2048 bits
            throw new IllegalArgumentException("Invalid key length for RC4");
        }
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public void saveKey(String file) throws Exception {
        if (this.secretKey == null) {
            throw new IllegalStateException("Key is not initialized");
        }
        try (FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeUTF(Base64.getEncoder().encodeToString(this.secretKey.getEncoded()));
            System.out.println("Key saved to " + file);
        } catch (IOException e) {
            System.err.println("Error saving key: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadKey(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {
            String keyBase64 = dis.readUTF();
            this.setKeyFromBase64(keyBase64);
            System.out.println("Key loaded from " + filePath);
        } catch (IOException e) {
            System.err.println("Error loading key: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getBase64Key() {
        if (this.secretKey == null) {
            throw new IllegalStateException("Key is not initialized");
        }
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
    }

    public String encrypt(String plainText, String keyBase64) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        this.setKeyFromBase64(keyBase64);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText, String keyBase64) throws Exception {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        this.setKeyFromBase64(keyBase64);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, "UTF-8");
    }

    public void encryptFile(String inputFilePath, String outputFilePath, String keyBase64) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBase64(keyBase64);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));
             CipherOutputStream cos = new CipherOutputStream(bos, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
            System.out.println("File encrypted to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error encrypting file " + inputFilePath + ": " + e.getMessage());
            throw e;
        }
    }

    public void decryptFile(String inputFilePath, String outputFilePath, String keyBase64) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBase64(keyBase64);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));
             CipherInputStream cis = new CipherInputStream(bis, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            System.out.println("File decrypted to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error decrypting file " + inputFilePath + ": " + e.getMessage());
            throw e;
        }
    }
}