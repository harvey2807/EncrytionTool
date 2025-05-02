package edu.hcmuaf.model.modern.symmetric;

import javax.crypto.*;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class ChaChaCipher {
    private static final String ALGORITHM = "ChaCha20";
    private SecretKey key;
    private static final int NONCE_LENGTH = 12;
    private ChaCha20ParameterSpec paramSpec;
    private static final int IV_LENGTH = 12;
    private byte[] nonce;
    private IvParameterSpec iv;

    public ChaChaCipher() {
    }


    public String genKey(int size) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(size);
        SecretKey key = keyGenerator.generateKey();
        this.key = key;
        return Base64.getEncoder().encodeToString(this.key.getEncoded());
    }

    //    public String genIV() throws NoSuchAlgorithmException {
//        nonce = new byte[IV_LENGTH];
//        new SecureRandom().nextBytes(nonce);
//        this.paramSpec = new ChaCha20ParameterSpec(nonce, 1);
//        return Base64.getEncoder().encodeToString(nonce);
//    }
    public String genNonce() {
        nonce = new byte[NONCE_LENGTH];
        new SecureRandom().nextBytes(nonce);
        this.paramSpec = new ChaCha20ParameterSpec(nonce, 1);
        return Base64.getEncoder().encodeToString(nonce);
    }

    public void saveKey(String file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream dos = new DataOutputStream(fos);) {
//            String key = genKey(key);
            dos.writeUTF(Base64.getEncoder().encodeToString(this.key.getEncoded()));
            System.out.println("Key saved to" + file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key");
        }
    }

    public void setKeyFromBased64(String keyBased64) throws Exception {
        if (keyBased64.isEmpty() || keyBased64 == null) {
            throw new Exception("Key based 64 characters are not allowed");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyBased64);
        if (decodedKey.length != 32) { // chacha20 yeu cau 256- bit (32 byte ) key
            throw new IllegalArgumentException("Invalid key length");
        }
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

    public String encrypt(String plainText, String keyBase64, String nonceBase64) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (nonceBase64 == null || nonceBase64.isEmpty()) {
            throw new IllegalArgumentException("Nonce cannot be null or empty");
        }

        this.setKeyFromBased64(keyBase64);
        this.nonce = Base64.getDecoder().decode(nonceBase64);
        if (this.nonce.length != NONCE_LENGTH) {
            throw new IllegalArgumentException("Invalid nonce length");
        }
        this.paramSpec = new ChaCha20ParameterSpec(this.nonce, 1);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.paramSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText, String keyBase64, String nonceBase64) throws Exception {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (nonceBase64 == null || nonceBase64.isEmpty()) {
            throw new IllegalArgumentException("Nonce cannot be null or empty");
        }

        this.setKeyFromBased64(keyBase64);
        this.nonce = Base64.getDecoder().decode(nonceBase64);
        if (this.nonce.length != NONCE_LENGTH) {
            throw new IllegalArgumentException("Invalid nonce length");
        }
        this.paramSpec = new ChaCha20ParameterSpec(this.nonce, 1);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, this.key, this.paramSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted);
    }

    public void encryptFile(String src, String outputFilePath, String keyBase64, String nonceBase64) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBased64(keyBase64);

        if (nonceBase64 == null || nonceBase64.isEmpty()) {
            this.genNonce(); // Generate a new nonce if none provided
        } else {
            this.nonce = Base64.getDecoder().decode(nonceBase64);
            if (this.nonce.length != NONCE_LENGTH) {
                throw new IllegalArgumentException("Invalid nonce length");
            }
            this.paramSpec = new ChaCha20ParameterSpec(this.nonce, 1);
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.paramSpec);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            bos.write(this.nonce);

            try (CipherOutputStream cos = new CipherOutputStream(bos, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("File encrypted to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error encrypting file " + src + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void decryptFile(String inputFilePath, String outputFilePath, String keyBase64) throws Exception {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.setKeyFromBased64(keyBase64);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath))) {
            this.nonce = new byte[NONCE_LENGTH];
            int bytesRead = bis.read(this.nonce);
            if (bytesRead != NONCE_LENGTH) {
                throw new IOException("Invalid nonce length in encrypted file");
            }
            this.paramSpec = new ChaCha20ParameterSpec(this.nonce, 1);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, this.key, this.paramSpec);

            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));
                 CipherInputStream cis = new CipherInputStream(bis, cipher)) {
                byte[] buffer = new byte[4096];
                while ((bytesRead = cis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("File decrypted to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error decrypting file " + inputFilePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        try {
//            ChaChaCipher chacha = new ChaChaCipher();
//            String key = chacha.genKey(256);
//            String nonce = chacha.genIV();
//
//            String plaintext = "Hello, My name is Dang Tran Tan Luc!";
//            String encrypted = chacha.encrypt(plaintext);
//            String decrypted = chacha.decrypt(encrypted);
//
//            System.out.println("Base64 Key: " + chacha.getBased64Key());
//            System.out.println("Plaintext : " + plaintext);
//            System.out.println("Encrypted : " + encrypted);
//            System.out.println("Decrypted : " + decrypted);
//
//            // Test nạp lại key và nonce
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
