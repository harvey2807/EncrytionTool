package edu.hcmuaf.model.modern.symmetric;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class AESCipher {
    private SecretKey secretKey;
    private IvParameterSpec iv;
    private static final String ALGORITHM = "AES";
    private static final int IV_LENGTH = 16;

    public AESCipher() throws Exception {
    }


    public String genKey(int size) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(size);
        SecretKey key = keyGen.generateKey();
        this.secretKey = key;
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
//size of block is 128 bit
    //length of block is 128, 192, 256

    public String genIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        this.iv = new IvParameterSpec(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    public void saveKey(String file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream dos = new DataOutputStream(fos);) {
            dos.writeUTF(getBased64Key());
            System.out.println("Key saved to" + file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key");
        }
    }

    public void setKeyFromBased64(String keyBased64) throws Exception {
        if (keyBased64 == null || keyBased64.isEmpty()) {
            throw new Exception("Key cannot be null or empty");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyBased64);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
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
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
    }

    public String padPlaintext(String input) {
        int blockSize = 16;
        int paddingLength = blockSize - (input.getBytes().length % blockSize);
        StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < paddingLength; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public String encrypt(String plainText, String key, String mode, String iv, String padding) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        //set key
        this.setKeyFromBased64(key);

        System.out.println("Padding:" + padding);
        System.out.println("Mode:" + mode);
        if (!"ECB".equals(mode)) {
            if (iv == null || iv.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty");
            }
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            this.iv = new IvParameterSpec(ivBytes);
        }
        //validate input length for Nopadding
        if ("NoPadding".equals(padding)) {
//            byte[] inputBytes = plainText.getBytes();
//            if (inputBytes.length % 16 != 0) {
//                throw new IllegalArgumentException("plain text's length is invalid");
//            }
            plainText = padPlaintext(plainText);
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.iv);
        }
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText, String key, String mode, String iv, String padding) throws Exception {
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
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
        }
        byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);
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
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
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
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            // Skip the IV in the input file (for non-ECB modes)
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
        try {
            AESCipher aes = new AESCipher();
            String key = aes.genKey(128);
            String iv = aes.genIV();
            String mode = "CBC";
            String padding = "PKCS5Padding";
            System.out.println("Generate IV " + aes.genIV());
            String plaintext = "Hello, My name is Dang Tran Tan Luc!";
            String encrypted = aes.encrypt(plaintext, key, iv, mode, padding);
            String decrypted = aes.decrypt(encrypted, key, iv, mode, padding);

            System.out.println("Base64 Key: " + aes.getBased64Key());
            System.out.println("Plaintext : " + plaintext);
            System.out.println("Encrypted : " + encrypted);
            System.out.println("Decrypted : " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }
}
