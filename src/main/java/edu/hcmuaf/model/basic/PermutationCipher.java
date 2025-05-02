package edu.hcmuaf.model.basic;

import java.io.*;
import java.util.*;

public class PermutationCipher {
    private static int KEY;

    //Tạo khóa ngẫu nhiên
    public int genKey() {
        KEY = (int) (Math.random() * 10);
        if (KEY < 2) genKey();
        return KEY;
    }

    public void saveKey(String filePath) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath);
                DataOutputStream dos = new DataOutputStream(fos);) {
            dos.writeInt(KEY);
            System.out.println("Key saved to" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving key");
        }
    }

    //Load khóa lên
    public void loadKey(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {
            dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading key");
        }
    }

    //Mã hóa chuỗi đầu vào
    public String encrypt(String input) {
        if (KEY > input.length()) genKey();
        input = input.replaceAll(" ", "*"); // Thay khoảng trắng bằng dấu '*'
        int numRows = (int) Math.ceil((double) input.length() / KEY);


        char[][] grid = new char[numRows][KEY];
        int index = 0;

        // Điền văn bản vào bảng theo hàng
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < KEY; c++) {
                if (index < input.length()) {
                    grid[r][c] = input.charAt(index++);
                } else {
                    grid[r][c] = '_'; // Điền ký tự trống nếu cần
                }
            }
        }

        printMatrix(grid);

        // Đọc theo thứ tự cột để mã hóa
        StringBuilder cipherText = new StringBuilder();
        for (int c = 0; c < KEY; c++) {
            for (int r = 0; r < numRows; r++) {
                cipherText.append(grid[r][c]);
            }
        }
        return cipherText.toString();
    }

    //Giải mã chuỗi đầu vào
    public String decrypt(String input, int key) {
        int numRows = (int) Math.ceil((double) input.length() / key);
        char[][] grid = new char[numRows][key];
        int index = 0;

        // Điền văn bản mã hóa vào bảng theo cột
        for (int c = 0; c < key; c++) {
            for (int r = 0; r < numRows; r++) {
                if (index < input.length()) {
                    grid[r][c] = input.charAt(index++);
                }
            }
        }
        printMatrix(grid);

        // Đọc theo hàng để giải mã
        StringBuilder plainText = new StringBuilder();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < key; c++) {
                if (grid[r][c] != '_') { // Bỏ qua ký tự điền thêm
                    plainText.append(grid[r][c]);
                }
            }
        }

        // Thay thế ký tự '*' trở lại khoảng trắng
        return plainText.toString().replace('*', ' ');
    }

    //phương thức in ma trận
    public void printMatrix(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        PermutationCipher t = new PermutationCipher();
        String text = "Đại học nông lâm";
        String key = String.valueOf(t.genKey());

        String resultEncrypt = t.encrypt(text);
        System.out.println(resultEncrypt);
        System.out.println("Decrypted" + t.decrypt(resultEncrypt, Integer.parseInt(key)));

    }
}
