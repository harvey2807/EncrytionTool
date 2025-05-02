package edu.hcmuaf.model.basic;

import java.io.*;
import java.util.*;

public class SubstitutionCipher {
    private static final String LOWERCASE_ALPHABET =
            "abcdefghijklmnopqrstuvwxyzàáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ";
    private static final String UPPERCASE_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴ";
    private String substitution = "";
    private Map<Character, Character> encryptMap = new HashMap<>();
    private Map<Character, Character> decryptMap = new HashMap<>();

    public String genKey() {
        List<Character> chars = new ArrayList<>();
        for (char c : LOWERCASE_ALPHABET.toCharArray()) {
            chars.add(c);
        }
        for (char c : UPPERCASE_ALPHABET.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);

        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(c);
        }
        substitution = sb.toString();
        buildMapping();
        return substitution;
    }

    public void saveKey(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeUTF(substitution);
            System.out.println("Key saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key");
        }
    }

    public void loadKey(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {
            substitution = dis.readUTF();
            buildMapping();
            System.out.println("Key loaded from " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading key");
        }
    }

    private void buildMapping() {
        encryptMap.clear();
        decryptMap.clear();
        if (substitution.length() != LOWERCASE_ALPHABET.length() + UPPERCASE_ALPHABET.length()) {
            throw new IllegalStateException("Invalid substitution key length");
        }
        List<Character> original = new ArrayList<>();
        for (char c : LOWERCASE_ALPHABET.toCharArray()) {
            original.add(c);
        }
        for (char c : UPPERCASE_ALPHABET.toCharArray()) {
            original.add(c);
        }

        for (int i = 0; i < original.size(); i++) {
            char origChar = original.get(i);
            char substChar = substitution.charAt(i);
            encryptMap.put(origChar, substChar);
            decryptMap.put(substChar, origChar);
        }
    }

    public String encrypt(String input) {
        if (encryptMap.isEmpty()) {
            buildMapping();
        }
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(encryptMap.getOrDefault(c, c));
        }
        return result.toString();
    }

    public String decrypt(String input) {
        if (decryptMap.isEmpty()) {
            buildMapping();
        }
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(decryptMap.getOrDefault(c, c));
        }
        return result.toString();
    }

    public String getKey() {
        return substitution;
    }

    public static void main(String[] args) {
        SubstitutionCipher cipher = new SubstitutionCipher();
        String key = cipher.genKey();
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