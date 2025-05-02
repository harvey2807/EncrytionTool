package edu.hcmuaf.model.modern.symmetric;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class BlowfishCipher {
    private SecretKey key;
    private IvParameterSpec iv;
    private static final String ALGORITHM = "Blowfish";
//    private String mode = "CBC";
//    private String padding = "PKCS5Padding";
    private static final int IV_LENGTH = 8;

//    public BlowfishCipher(String mode, String padding) {
//        this.mode = mode;
//        this.padding = padding;
//    }

    public BlowfishCipher() {
    }

//    public void setMode(String mode) {
//        this.mode = mode;
//    }
//
//    public void setPadding(String padding) {
//        this.padding = padding;
//    }

    public String genKey(int size) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        keygen.init(size);
        this.key = keygen.generateKey();
        return Base64.getEncoder().encodeToString(this.key.getEncoded());
    }

    public String genIV() throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        this.iv = new IvParameterSpec(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    public void saveKey(String file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream dos = new DataOutputStream(fos);) {
//            String key = genKey();
            dos.writeUTF(key.toString());
            System.out.println("Key saved to" + file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key");
        }
    }

    public void setKeyFromBased64(String keyBased64) throws Exception {
        if (keyBased64 == null) {
            throw new Exception("Key based 64 key cannot be null");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyBased64);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    public void loadKey(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis);) {
            String key = dis.readUTF();
            this.setKeyFromBased64(key);
            System.out.println("Key loaded from" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key!");
        }
    }

    public String getBased64Key() {
        return Base64.getEncoder().encodeToString(this.key.getEncoded());
    }

    public String encrypt(String plainText, String keyBase64, String mode, String ivBase64, String padding) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBased64(keyBase64);

        if (!"ECB".equals(mode) && !"CBC".equals(mode) && !"CFB".equals(mode) && !"OFB".equals(mode)) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
        if (!"PKCS5Padding".equals(padding) && !"NoPadding".equals(padding)) {
            throw new IllegalArgumentException("Unsupported padding: " + padding);
        }

        if (!"ECB".equals(mode)) {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty for " + mode);
            }
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            if (ivBytes.length != IV_LENGTH) {
                throw new IllegalArgumentException("IV must be 8 bytes, got " + ivBytes.length);
            }
            this.iv = new IvParameterSpec(ivBytes);
        }

        if ("NoPadding".equals(padding)) {
            byte[] inputBytes = plainText.getBytes();
            if (inputBytes.length % 8 != 0) {
                throw new IllegalArgumentException("Plaintext length must be a multiple of 8 bytes for NoPadding");
            }
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, this.key);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        }
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText, String key, String mode, String padding, String iv) throws Exception {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBased64(key);
        if (!"ECB".equals(mode)) {
            if (iv == null || iv.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty");
            }
            byte[] ivByte = Base64.getDecoder().decode(iv);
            this.iv = new IvParameterSpec(ivByte);
        }
        byte[] encryptedBytes;
        try {
            encryptedBytes = Base64.getDecoder().decode(cipherText);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 ciphertext", e);
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, this.key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
        }
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }


    public void encryptFile(String src, String outputFilePath, String keyBase64, String ivBase64, String mode, String padding) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBased64(keyBase64);

        byte[] ivBytes;
        if ("ECB".equals(mode)) {
            ivBytes = null;
        } else {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                ivBytes = new byte[IV_LENGTH];
                new SecureRandom().nextBytes(ivBytes);
            } else {
                ivBytes = Base64.getDecoder().decode(ivBase64);
            }
            this.iv = new IvParameterSpec(ivBytes);
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            // Write IV to the output file (for non-ECB modes)
            if (!"ECB".equals(mode)) {
                bos.write(ivBytes);
            }

            try (CipherOutputStream cos = new CipherOutputStream(bos, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("File encrypted to " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error encrypting file " + src + ": " + e.getMessage());
            throw e;
        }
    }
    public void decryptFile(String inputFilePath, String outputFilePath, String keyBase64, String mode, String padding) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBased64(keyBase64);

        byte[] ivBytes = null;
        if (!"ECB".equals(mode)) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath))) {
                ivBytes = new byte[IV_LENGTH];
                int bytesRead = bis.read(ivBytes);
                if (bytesRead != IV_LENGTH) {
                    throw new IOException("Invalid IV length in encrypted file");
                }
            }
            this.iv = new IvParameterSpec(ivBytes);
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            if (!"ECB".equals(mode)) {
                bis.skip(IV_LENGTH);
            }

            try (CipherInputStream cis = new CipherInputStream(bis, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("File decrypted to " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error decrypting file " + inputFilePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        try {
//            BlowfishCipher blow = new BlowfishCipher();
//            blow.genKey(128);
//            blow.genIV();
//            System.out.println("Generate IV " + blow.genIV());
//            String plaintext = "Hello, My name is Dang Tran Tan Luc!";
//            String encrypted = blow.encrypt(plaintext);
//            String decrypted = blow.decrypt(encrypted);
//
//            System.out.println("Base64 Key: " + blow.getBased64Key());
//            System.out.println("Plaintext : " + plaintext);
//            System.out.println("Encrypted : " + encrypted);
//            System.out.println("Decrypted : " + decrypted);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Error");
//        }
    }
}

