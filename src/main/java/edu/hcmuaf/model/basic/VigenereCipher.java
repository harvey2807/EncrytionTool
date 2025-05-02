package edu.hcmuaf.model.basic;

import java.io.*;
import java.util.Random;

public class VigenereCipher {
    private static final String LOWERCASE_ALPHABET =
            "abcdefghijklmnopqrstuvwxyzàáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ";
    private static final String UPPERCASE_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴ";
    private static final int M = LOWERCASE_ALPHABET.length();
//    private static final int LENGTH = 5;
    private String key = "";

    public VigenereCipher() {
    }

    public VigenereCipher(String key) {
        this.key = key;
    }

    public String genKey(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            char randomChar = LOWERCASE_ALPHABET.charAt(rand.nextInt(M));
            sb.append(randomChar);
        }
        this.key = sb.toString();
        return key;
    }

    public void saveKey(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeUTF(this.key);
            System.out.println("Key saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key");
        }
    }

    public void loadKey(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {
            this.key = dis.readUTF();
            System.out.println("Key loaded from " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading key");
        }
    }

    public String encrypt(String text) {
        StringBuilder result = new StringBuilder();
        int keyLen = key.length();
        int j = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int index = LOWERCASE_ALPHABET.indexOf(Character.toLowerCase(c));
            if (index != -1) {
                int shift = LOWERCASE_ALPHABET.indexOf(Character.toLowerCase(key.charAt(j % keyLen)));
                int encIndex = (index + shift) % M;
                char encChar = Character.isUpperCase(c) ? UPPERCASE_ALPHABET.charAt(encIndex) : LOWERCASE_ALPHABET.charAt(encIndex);
                result.append(encChar);
                j++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String decrypt(String text) {
        StringBuilder result = new StringBuilder();
        int keyLen = key.length();
        int j = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int index = LOWERCASE_ALPHABET.indexOf(Character.toLowerCase(c));
            if (index != -1) {
                int shift = LOWERCASE_ALPHABET.indexOf(Character.toLowerCase(key.charAt(j % keyLen)));
                int decIndex = (index - shift + M) % M;
                char decChar = Character.isUpperCase(c) ? UPPERCASE_ALPHABET.charAt(decIndex) : LOWERCASE_ALPHABET.charAt(decIndex);
                result.append(decChar);
                j++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String getKey() {
        return key;
    }

    public static void main(String[] args) {
        VigenereCipher cipher = new VigenereCipher();
        String key = cipher.genKey(5);
        System.out.println("Generated Key: " + key);
        String plaintext = "Hello World! Xin Chào Việt Nam";
        String encrypted = cipher.encrypt(plaintext);
        String decrypted = cipher.decrypt(encrypted);
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
//        cipher.saveKey("key.txt");
//        cipher.loadKey("key.txt");
    }
}