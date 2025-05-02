package edu.hcmuaf.model.modern.symmetric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Set;
import java.util.logging.Logger;

public class CamelliaCipher {
    private static final Logger LOGGER = Logger.getLogger(CamelliaCipher.class.getName());
    private SecretKey secretKey;
    private IvParameterSpec iv;
    private static final String ALGORITHM = "Camellia";
    private static final int IV_LENGTH = 16;
    private static final Set<String> SUPPORTED_MODES = Set.of("ECB", "CBC");
    private static final Set<String> SUPPORTED_PADDINGS = Set.of("PKCS5Padding", "NoPadding");

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public CamelliaCipher() {
    }

    public String genKey(int size) throws NoSuchAlgorithmException {
        if (size != 128 && size != 192 && size != 256) {
            throw new IllegalArgumentException("Key size must be 128, 192, or 256 bits");
        }
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(size);
        this.secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
    }

    public String genIV() {
        byte[] ivBytes = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivBytes);
        this.iv = new IvParameterSpec(ivBytes);
        return Base64.getEncoder().encodeToString(ivBytes);
    }

    public void saveKey(String file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeUTF(getBased64Key());
            LOGGER.info("Key saved to " + file);
        } catch (IOException e) {
            throw new IOException("Error saving key to " + file, e);
        }
    }

    public void setKeyFromBased64(String keyBase64) throws IllegalArgumentException {
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyBase64);
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public void loadKey(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {
            String key = dis.readUTF();
            this.setKeyFromBased64(key);
            LOGGER.info("Key loaded from " + filePath);
        } catch (IOException e) {
            throw new IOException("Error loading key from " + filePath, e);
        }
    }

    public String getBased64Key() {
        if (this.secretKey == null) {
            throw new IllegalStateException("Secret key is not set");
        }
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
    }

    public String encrypt(String plainText, String keyBase64, String mode, String ivBase64, String padding)
            throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!SUPPORTED_MODES.contains(mode)) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
        if (!SUPPORTED_PADDINGS.contains(padding)) {
            throw new IllegalArgumentException("Unsupported padding: " + padding);
        }
        if ("ECB".equals(mode)) {
            LOGGER.warning("ECB mode is insecure and should not be used for sensitive data.");
        }
        this.setKeyFromBased64(keyBase64);
        if (this.secretKey == null) {
            throw new IllegalStateException("Secret key is not set");
        }
        if (!"ECB".equals(mode)) {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty for " + mode + " mode");
            }
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            this.iv = new IvParameterSpec(ivBytes);
        }
        if ("NoPadding".equals(padding)) {
            byte[] inputBytes = plainText.getBytes(StandardCharsets.UTF_8);
            if (inputBytes.length % 16 != 0) {
                throw new IllegalArgumentException("Plaintext length must be a multiple of 16 bytes for NoPadding");
            }
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.iv);
        }
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText, String keyBase64, String mode, String ivBase64, String padding)
            throws Exception {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!SUPPORTED_MODES.contains(mode)) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
        if (!SUPPORTED_PADDINGS.contains(padding)) {
            throw new IllegalArgumentException("Unsupported padding: " + padding);
        }
        this.setKeyFromBased64(keyBase64);
        if (this.secretKey == null) {
            throw new IllegalStateException("Secret key is not set");
        }
        if (!"ECB".equals(mode)) {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                throw new IllegalArgumentException("IV cannot be null or empty for " + mode + " mode");
            }
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            this.iv = new IvParameterSpec(ivBytes);
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
        }
        byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String encryptFile(String src, String outputFilePath, String keyBase64, String ivBase64, String mode, String padding)
            throws Exception {
        if (src == null || outputFilePath == null) {
            throw new IllegalArgumentException("File paths cannot be null");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!SUPPORTED_MODES.contains(mode)) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
        if (!SUPPORTED_PADDINGS.contains(padding)) {
            throw new IllegalArgumentException("Unsupported padding: " + padding);
        }
        if ("ECB".equals(mode)) {
            LOGGER.warning("ECB mode is insecure and should not be used for sensitive data.");
        }
        this.setKeyFromBased64(keyBase64);
        if (this.secretKey == null) {
            throw new IllegalStateException("Secret key is not set");
        }
        byte[] ivBytes;
        String generatedIV = null;
        if ("ECB".equals(mode)) {
            ivBytes = null;
        } else {
            if (ivBase64 == null || ivBase64.isEmpty()) {
                ivBytes = new byte[IV_LENGTH];
                new SecureRandom().nextBytes(ivBytes);
                generatedIV = Base64.getEncoder().encodeToString(ivBytes);
            } else {
                ivBytes = Base64.getDecoder().decode(ivBase64);
            }
            this.iv = new IvParameterSpec(ivBytes);
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.iv);
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            if (!"ECB".equals(mode) && ivBytes != null) {
                bos.write(ivBytes); // Write IV to the beginning of the file
            }
            try (CipherOutputStream cos = new CipherOutputStream(bos, cipher)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
            LOGGER.info("File encrypted to " + outputFilePath);
        } catch (IOException e) {
            throw new IOException("Error encrypting file " + src, e);
        }
        return generatedIV; // Return generated IV for non-ECB modes
    }

    public void decryptFile(String inputFilePath, String outputFilePath, String keyBase64, String mode, String padding)
            throws Exception {
        if (inputFilePath == null || outputFilePath == null) {
            throw new IllegalArgumentException("File paths cannot be null");
        }
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!SUPPORTED_MODES.contains(mode)) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
        if (!SUPPORTED_PADDINGS.contains(padding)) {
            throw new IllegalArgumentException("Unsupported padding: " + padding);
        }
        this.setKeyFromBased64(keyBase64);
        if (this.secretKey == null) {
            throw new IllegalStateException("Secret key is not set");
        }
        byte[] ivBytes = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath))) {
            if (!"ECB".equals(mode)) {
                ivBytes = new byte[IV_LENGTH];
                int bytesRead = bis.read(ivBytes);
                if (bytesRead != IV_LENGTH) {
                    throw new IOException("Invalid IV length in encrypted file");
                }
                this.iv = new IvParameterSpec(ivBytes);
            }
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);
            if ("ECB".equals(mode)) {
                cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
            }
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));
                 CipherInputStream cis = new CipherInputStream(bis, cipher)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
            }
            LOGGER.info("File decrypted to " + outputFilePath);
        } catch (IOException e) {
            throw new IOException("Error decrypting file " + inputFilePath, e);
        }
    }

    public static void main(String[] args) {
        try {
            CamelliaCipher camellia = new CamelliaCipher();
            String key = camellia.genKey(128);
            String iv = camellia.genIV();
            String mode = "CBC";
            String padding = "PKCS5Padding";
            String plaintext = "Hello, My name is Dang Tran Tan Luc!";
            String encrypted = camellia.encrypt(plaintext, key, mode, iv, padding);
            String decrypted = camellia.decrypt(encrypted, key, mode, iv, padding);

            System.out.println("Base64 Key: " + camellia.getBased64Key());
            System.out.println("Base64 IV : " + iv);
            System.out.println("Plaintext : " + plaintext);
            System.out.println("Encrypted : " + encrypted);
            System.out.println("Decrypted : " + decrypted);

        } catch (Exception e) {
            LOGGER.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}