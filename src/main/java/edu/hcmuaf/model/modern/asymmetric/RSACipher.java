package edu.hcmuaf.model.modern.asymmetric;

import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class RSACipher {
    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private SecretKey aesKey;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public String genKeyAES(int size) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGen.init(size);
        this.aesKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(aesKey.getEncoded());
    }

    public KeyPair genPairKey(int size) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(size);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        return keyPair;

//        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
//        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
//
//        Map<String, String> keys = new HashMap<>();
//        keys.put("publicKey", publicKeyString);
//        keys.put("privateKey", privateKeyString);
//        System.out.println(keys);
//        return keys;
    }

    public void saveKey(String encrypted, String file) throws Exception {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeUTF(encrypted);
            System.out.println("Key saved to " + file);
        }
    }

    public String loadKey(String file) throws Exception {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            in.readUTF();
            System.out.println("Key loaded from " + file);
        }
        return file;
    }

    public String encodeKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String encrypt(String input, String mode, String padding) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM + "/" + mode + "/" + padding);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String data, String mode, String padding) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM + "/" + mode + "/" + padding);
        cipher.init(Cipher.DECRYPT_MODE, privateKey); // dùng private key để giải mã
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public String encryptAESKey() throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public SecretKey decryptAESKEY(String encryptedKey) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(encryptedKey);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new SecretKeySpec(decrypted, AES_ALGORITHM);
    }


    public void encryptFile(String inputFile, String outputFile, String encryptedKey)
            throws Exception {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile));
             FileInputStream fis = new FileInputStream(inputFile)) {
            dos.writeUTF(encryptedKey);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
            CipherOutputStream cos = new CipherOutputStream(dos, cipher);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
            cos.close();
        }
    }

    public void decryptFile(String encryptedFile, String outputFile) throws Exception {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(encryptedFile));
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            String encryptedAESKey = dis.readUTF();
            SecretKey decryptedAESKey = decryptAESKEY(encryptedAESKey);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, decryptedAESKey);
            CipherInputStream cis = new CipherInputStream(dis, cipher);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        RSACipher rsaCipher = new RSACipher();
        // Generate keys
        String mode = "ECB";
        String padding_AES = "PKCS5Padding";
        String padding_RSA = "PKCS1Padding";
        rsaCipher.genPairKey(2048); // Use 2048-bit for better security
        String aesKeyStr = rsaCipher.genKeyAES(256); // 256-bit AES key
        String encryptedAESKey = rsaCipher.encryptAESKey();

        // Test text encryption/decryption
        String plaintext = "Hello World! Xin Chào Việt Nam";
        String encryptedText = rsaCipher.encrypt(plaintext, mode, padding_RSA);
        String decryptedText = rsaCipher.decrypt(encryptedText, mode, padding_RSA);
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Encrypted: " + encryptedText);
        System.out.println("Decrypted: " + decryptedText);


        // Test file encryption/decryption
//        String inputFile = "input.txt";
//        String encryptedFile = "encrypted.bin";
//        String decryptedFile = "decrypted.txt";
//        try (FileOutputStream fos = new FileOutputStream(inputFile)) {
//            fos.write(plaintext.getBytes(StandardCharsets.UTF_8));
//        }
//        rsaCipher.encryptFile(inputFile, encryptedFile, encryptedAESKey);
//        rsaCipher.decryptFile(encryptedFile, decryptedFile);
//        System.out.println("File encryption/decryption completed.");
    }
}
