package edu.hcmuaf.model.basic;

import java.io.*;
import java.util.Scanner;

public class CaesarCipher {
    private int key;
    private static final String LOWERCASE_ALPHABET =
            "abcdefghijklmnopqrstuvwxyzàáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ";
    private static final String UPPERCASE_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴ";
    private static final int ALPHABET_SIZE = LOWERCASE_ALPHABET.length();

    public CaesarCipher() {
    }

    public int genKey() {
        this.key = (int) (Math.random() * (ALPHABET_SIZE - 1) + 1);
        System.out.println("CaesarCipher Key: " + this.key);
        return key;
    }

    public void saveKey(String input) {
        try (FileOutputStream fos = new FileOutputStream(input);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            String keyStr = Integer.toString(this.key);
            bos.write(keyStr.getBytes());
            System.out.println("Key saved to " + input);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key!");
        }
    }

    public void loadKey(String input) {
        try (FileInputStream fis = new FileInputStream(input);
             BufferedInputStream bis = new BufferedInputStream(fis);
             Scanner sc = new Scanner(bis)) {
            if (sc.hasNextInt()) {
                this.key = sc.nextInt();
                System.out.println("Key loaded from " + input);
            } else {
                System.out.println("Key not found");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String encrypt(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            int index = LOWERCASE_ALPHABET.indexOf(c);
            if (index != -1) {
                sb.append(LOWERCASE_ALPHABET.charAt((index + this.key) % ALPHABET_SIZE));
            } else {
                index = UPPERCASE_ALPHABET.indexOf(c);
                if (index != -1) {
                    sb.append(UPPERCASE_ALPHABET.charAt((index + this.key) % ALPHABET_SIZE));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public String decrypt(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            int index = LOWERCASE_ALPHABET.indexOf(c);
            if (index != -1) {
                int newIndex = (index - this.key) % ALPHABET_SIZE;
                if (newIndex < 0) newIndex += ALPHABET_SIZE;
                sb.append(LOWERCASE_ALPHABET.charAt(newIndex));
            } else {
                index = UPPERCASE_ALPHABET.indexOf(c);
                if (index != -1) {
                    int newIndex = (index - this.key) % ALPHABET_SIZE;
                    if (newIndex < 0) newIndex += ALPHABET_SIZE;
                    sb.append(UPPERCASE_ALPHABET.charAt(newIndex));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        CaesarCipher caesarCipher = new CaesarCipher();
        System.out.println("Generated Key: " + caesarCipher.genKey());
        String text = "Tôi yêu Việt Nam";
        System.out.println("Text: " + text);
        String encrypted = caesarCipher.encrypt(text);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = caesarCipher.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}