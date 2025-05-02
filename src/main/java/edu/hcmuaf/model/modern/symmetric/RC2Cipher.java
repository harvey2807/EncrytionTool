package edu.hcmuaf.model.modern.symmetric;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class RC2Cipher {
    private static final String ALGORITHM = "RC2";
    private static final int IV_LENGTH = 8; // RC2 IV is 8 bytes
    private SecretKey secretKey;
    private IvParameterSpec iv;

    public RC2Cipher() {
    }

    public String genKey(int size) throws Exception {
        if (size < 40 || size > 128) {
            throw new IllegalArgumentException("Key size must be between 40 and 128 bits for RC2");
        }
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(size);
        this.secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
    }

    public String genIV() throws Exception {
        byte[] ivBytes = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivBytes);
        this.iv = new IvParameterSpec(ivBytes);
        return Base64.getEncoder().encodeToString(ivBytes);
    }

    public void setKeyFromBase64(String keyBase64) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyBase64);
        if (decodedKey.length < 5 || decodedKey.length > 16) { // 40–128 bits
            throw new IllegalArgumentException("Invalid key length for RC2");
        }
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public String getBase64Key() {
        if (this.secretKey == null) {
            throw new IllegalStateException("Key is not initialized");
        }
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
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

    public String encrypt(String plainText, String keyBase64, String ivBase64, String mode, String padding) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        this.setKeyFromBase64(keyBase64);

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        } else if ("CBC".equals(mode)) {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty for CBC");
            }
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            if (ivBytes.length != IV_LENGTH) {
                throw new IllegalArgumentException("Invalid IV length");
            }
            this.iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.iv);
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText, String keyBase64, String ivBase64, String mode, String padding) throws Exception {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        this.setKeyFromBase64(keyBase64);

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        } else if ("CBC".equals(mode)) {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty for CBC");
            }
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            if (ivBytes.length != IV_LENGTH) {
                throw new IllegalArgumentException("Invalid IV length");
            }
            this.iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, "UTF-8");
    }

    public void encryptFile(String inputFilePath, String outputFilePath, String keyBase64, String ivBase64, String mode, String padding) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBase64(keyBase64);

        byte[] ivBytes = null;
        if (!"ECB".equals(mode)) {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty for " + mode);
            }
            ivBytes = Base64.getDecoder().decode(ivBase64);
            if (ivBytes.length != IV_LENGTH) {
                throw new IllegalArgumentException("Invalid IV length");
            }
            this.iv = new IvParameterSpec(ivBytes);
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.iv);
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            if (!"ECB".equals(mode)) {
                bos.write(ivBytes);
            }

            // Encrypt the file
            try (CipherOutputStream cos = new CipherOutputStream(bos, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("File encrypted to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error encrypting file " + inputFilePath + ": " + e.getMessage());
            throw e;
        }
    }

    public void decryptFile(String inputFilePath, String outputFilePath, String keyBase64, String mode, String padding) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBase64(keyBase64);

        byte[] ivBytes = null;
        if (!"ECB".equals(mode)) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath))) {
                ivBytes = new byte[IV_LENGTH];
                int bytesRead = bis.read(ivBytes);
                if (bytesRead != IV_LENGTH) {
                    throw new IOException("Invalid IV length in encrypted file");
                }
                this.iv = new IvParameterSpec(ivBytes);
            }
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
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
            System.err.println("Error decrypting file " + inputFilePath + ": " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
//        try {
//            RC2Cipher cipher = new RC2Cipher();
//            String key = cipher.genKey(128); // Generate 128-bit key
//            String iv = cipher.genIV(); // Generate IV (not used for ECB)
//
//            String plaintext = "Hello, My name is Dang Tran Tan Luc!";
//            String encrypted = cipher.encrypt(plaintext, key, iv);
//            String decrypted = cipher.decrypt(encrypted, key, iv);
//
//            System.out.println("Base64 Key: " + cipher.getBase64Key());
//            System.out.println("Base64 IV: " + iv);
//            System.out.println("Plaintext: " + plaintext);
//            System.out.println("Encrypted: " + encrypted);
//            System.out.println("Decrypted: " + decrypted);
//
//            // Test file encryption/decryption
//            String inputFile = "input.txt";
//            String encryptedFile = "encrypted.bin";
//            String decryptedFile = "decrypted.txt";
//
//            // Create a sample input file
//            try (FileWriter writer = new FileWriter(inputFile)) {
//                writer.write(plaintext);
//            }
//
//            cipher.encryptFile(inputFile, encryptedFile, key, iv);
//            cipher.decryptFile(encryptedFile, decryptedFile, key);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error: " + e.getMessage());
//        }
    }
}