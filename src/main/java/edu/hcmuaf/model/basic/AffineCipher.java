package edu.hcmuaf.model.basic;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class AffineCipher {
    private static final String LOWERCASE_ALPHABET =
            "abcdefghijklmnopqrstuvwxyzàáảãạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ";
    private static final String UPPERCASE_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÈÉẺẼẸÊỀẾỂỄỆÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴ";
    private static final int M = LOWERCASE_ALPHABET.length();
    private int a;
    private int b;

    public AffineCipher() {
    }

    public String genKey() {
        System.out.println("Generating key...");
        Random rand = new Random();
        do {
            a = rand.nextInt(M);
            System.out.println("Random a: " + a);
        } while (gcd(a, M) != 1);
        b = rand.nextInt(M);
        System.out.println("Random b: " + b);
        return a + "," + b;
    }

    // Calculate GCD
    private int gcd(int a, int b) {
        return b == 0 ? Math.abs(a) : gcd(b, a % b);
    }

    public void saveKey(String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            String keyStr = a + "," + b;
            bos.write(keyStr.getBytes());
            System.out.println("Key saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key!");
        }
    }

    public void loadKey(String filename) {
        try (FileInputStream fis = new FileInputStream(filename);
             BufferedInputStream bis = new BufferedInputStream(fis);
             Scanner sc = new Scanner(bis)) {
            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] keys = line.split(",");
                if (keys.length == 2) {
                    a = Integer.parseInt(keys[0]);
                    b = Integer.parseInt(keys[1]);
                    if (gcd(a, M) != 1) {
                        throw new IllegalArgumentException("Loaded 'a' must be coprime with " + M);
                    }
                    System.out.println("Key loaded from " + filename + ": a=" + a + ", b=" + b);
                } else {
                    System.out.println("Invalid key format in " + filename);
                }
            } else {
                System.out.println("No key found in " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading key!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid key format in " + filename);
        }
    }

    public String encrypt(String input) {
        if (gcd(this.a, M) != 1) {
            throw new IllegalArgumentException("'a' must be coprime with " + M);
        }
        StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            int index = LOWERCASE_ALPHABET.indexOf(ch);
            if (index != -1) {
                int enc = (a * index + b) % M;
                sb.append(LOWERCASE_ALPHABET.charAt(enc));
            } else {
                index = UPPERCASE_ALPHABET.indexOf(ch);
                if (index != -1) {
                    int enc = (a * index + b) % M;
                    sb.append(UPPERCASE_ALPHABET.charAt(enc));
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    public String decrypt(String input) {
        if (gcd(this.a, M) != 1) {
            throw new IllegalArgumentException("'a' must be coprime with " + M);
        }
        StringBuilder result = new StringBuilder();
        int a_inv = modInverse(a, M);

        for (char ch : input.toCharArray()) {
            int index = LOWERCASE_ALPHABET.indexOf(ch);
            if (index != -1) {
                int dec = (a_inv * (index - b + M)) % M;
                if (dec < 0) dec += M;
                result.append(LOWERCASE_ALPHABET.charAt(dec));
            } else {
                index = UPPERCASE_ALPHABET.indexOf(ch);
                if (index != -1) {
                    int dec = (a_inv * (index - b + M)) % M;
                    if (dec < 0) dec += M;
                    result.append(UPPERCASE_ALPHABET.charAt(dec));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    private int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        throw new ArithmeticException("Modular inverse does not exist for a=" + a + ", m=" + m);
    }

    public String getKey() {
        return a + "," + b;
    }

    public static void main(String[] args) {
        AffineCipher cipher = new AffineCipher();
        cipher.genKey();
        String text = "Hello World! Xin Chào Việt Nam";
        String encrypted = cipher.encrypt(text);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = cipher.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}