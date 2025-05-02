package edu.hcmuaf.controller;

import edu.hcmuaf.model.basic.*;
import edu.hcmuaf.view.BasicUI;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.*;

public class BasicUIController {
    private BasicUI basicUI;
    private JComboBox<String> algorithm;
    private CaesarCipher caesarCipher;
    private SubstitutionCipher substitutionCipher;
    private HillCipher hillCipher;
    private PermutationCipher permutationCipher;
    private VigenereCipher vigenereCipher;
    private AffineCipher affineCipher;
    private JTextField sizes;
    private String currentKey;

    public BasicUIController(BasicUI basicUI) {
        this.basicUI = basicUI;
        this.algorithm = basicUI.getChooseAlgorithmComboBox();
        this.sizes = basicUI.getTxtSize();
        this.caesarCipher = new CaesarCipher();
        this.substitutionCipher = new SubstitutionCipher();
        this.hillCipher = new HillCipher();
        this.vigenereCipher = new VigenereCipher();
        this.affineCipher = new AffineCipher();
        this.permutationCipher = new PermutationCipher();
        initController();
    }

    public void initController() {
        System.out.println("Init controller of BasicUIController");
        basicUI.setCreateKeyButtonListener(e -> createKeyListener());
        basicUI.setSaveKeyButtonListener(e -> saveKeyListener());
        basicUI.setLoadKeyButtonListener(e -> loadKeyListener());
        basicUI.setEncryptButtonListener(e -> encryptListener());
        basicUI.setDecryptButtonListener(e -> decryptListener());

        algorithm.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedAlgorithm = (String) e.getItem();
                sizes.setEditable("Vigenere".equals(selectedAlgorithm));
                if (!"Vigenere".equals(selectedAlgorithm)) {
                    sizes.setText("");
                }
            }
        });

        sizes.setEditable("Vigenere".equals(algorithm.getSelectedItem()));

    }


    public void createKeyListener() {
        System.out.println("Create Key button clicked");

        Object selectedAlgorithm = algorithm.getSelectedItem();
        if (selectedAlgorithm == null) {
            JOptionPane.showMessageDialog(basicUI, "Vui lòng chọn thuật toán", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String algorithmName = selectedAlgorithm.toString();
        String sizeText = sizes.getText();
        int sizeOfKey = 0;
        if ("Vigenere".equals(algorithmName)) {
            try {
                sizeOfKey = Integer.parseInt(sizeText);
                if (sizeOfKey <= 0) {
                    JOptionPane.showMessageDialog(basicUI, "Kích thước khóa phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(basicUI, "Vui lòng nhập kích thước khóa hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            switch (algorithmName) {
                case "Caesar":
                    int caesarKey = caesarCipher.genKey();
                    currentKey = String.valueOf(caesarKey);
                    basicUI.getTxtInputKey().setText(currentKey);
                    break;
                case "Substitution":
                    currentKey = substitutionCipher.genKey();
                    basicUI.getTxtInputKey().setText(currentKey);
                    break;
                case "Vigenere":
                    currentKey = vigenereCipher.genKey(sizeOfKey);
                    basicUI.getTxtInputKey().setText(currentKey);
                    break;
                case "Hill":
                    currentKey = hillCipher.genKey();
                    System.out.println("currentKey: " + currentKey);
                    basicUI.getTxtInputKey().setText(currentKey);
                    break;
                case "Transposition":
                    currentKey = String.valueOf(permutationCipher.genKey());
                    basicUI.getTxtInputKey().setText(currentKey);
                    break;
                case "Affine":
                    currentKey = String.valueOf(affineCipher.genKey());
                    basicUI.getTxtInputKey().setText(currentKey);
                    break;
                default:
                    JOptionPane.showMessageDialog(basicUI, "Thuật toán không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
            }

//            basicUI.getTxtInputKey().setText(currentKey);
            JOptionPane.showMessageDialog(basicUI, "Khóa đã được tạo thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Generated key: " + currentKey);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(basicUI, "Lỗi khi tạo khóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveKeyListener() {
        String key = basicUI.getTxtInputKey().getText();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(basicUI, "Key field is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;

        }
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showSaveDialog(basicUI);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(key);
                JOptionPane.showMessageDialog(basicUI, "Key saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(basicUI, "Error saving key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadKeyListener() {
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showOpenDialog(basicUI);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String key = reader.readLine();
                basicUI.getTxtInputKey().setText(key);
                JOptionPane.showMessageDialog(basicUI, "Key loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(basicUI, "Error saving key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void encryptListener() {
        try {
            String key = basicUI.getTxtInputKey().getText();
            String algorithmName = algorithm.getSelectedItem().toString();
//            int sizeOfKey = sizes.getSelectedIndex();
            String text = basicUI.getTxtEncrypt().getText();
            String encrypted = "";

            switch (algorithmName) {
                case "Caesar":
                    encrypted = caesarCipher.encrypt(text);
                    basicUI.getTxtDecrypt().setText(encrypted);
                    break;
                case "Substitution":
                    encrypted = substitutionCipher.encrypt(text);
                    basicUI.getTxtDecrypt().setText(encrypted);
                    break;
                case "Vigenere":
                    encrypted = vigenereCipher.encrypt(text);
                    basicUI.getTxtDecrypt().setText(encrypted);
                    break;
                case "Hill":
                    encrypted = String.valueOf(hillCipher.encrypt(text, key));
                    basicUI.getTxtDecrypt().setText(encrypted);
                    break;
                case "Transposition":
                    encrypted = String.valueOf(permutationCipher.encrypt(text));
                    basicUI.getTxtDecrypt().setText(encrypted);
                    break;
                case "Affine":
                    encrypted = affineCipher.encrypt(text);
                    basicUI.getTxtDecrypt().setText(encrypted);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(basicUI, "Encrypted failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decryptListener() {
        try {
            String key = basicUI.getTxtInputKey().getText();
            String algorithmName = algorithm.getSelectedItem().toString();
//            int sizeOfKey = sizes.getSelectedIndex();
            String text = basicUI.getTxtEncrypt().getText();
            String decrypted = "";

            switch (algorithmName) {
                case "Caesar":
                    decrypted = caesarCipher.decrypt(text);
                    basicUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Substitution":
                    decrypted = substitutionCipher.decrypt(text);
                    basicUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Vigenere":
                    decrypted = vigenereCipher.decrypt(text);
                    basicUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Hill":
                    decrypted = String.valueOf(hillCipher.decrypt(text, key));
                    basicUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Transposition":
                    decrypted = String.valueOf(permutationCipher.decrypt(text, Integer.parseInt(String.valueOf(key))));
                    basicUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Affine":
                    decrypted = affineCipher.decrypt(text);
                    basicUI.getTxtDecrypt().setText(decrypted);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(basicUI, "Decrypted  failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BasicUI ui = new BasicUI();
            new BasicUIController(ui);
            ui.setVisible(true);
        });
    }

}
