package edu.hcmuaf.controller;

import edu.hcmuaf.model.modern.asymmetric.RSACipher;
import edu.hcmuaf.view.AsymmetricUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.KeyPair;
import java.util.Base64;

public class RSAUIController {
    private RSACipher rsaCipher;
    private AsymmetricUI asymmetricUI;
    private JComboBox<Integer> sizeOfKey;
    private JComboBox<String> algorithm;
    private JComboBox<String> mode;
    private JComboBox<String> padding;
    private String rsaPublicKey;
    private String rsaPrivateKey;
    private String aesKey;
    private String encryptedAESKey;

    public RSAUIController(AsymmetricUI asymmetricUI) {
        this.asymmetricUI = asymmetricUI;
        rsaCipher = new RSACipher();
        this.mode = asymmetricUI.getChooseModeComboBox();
        this.padding = asymmetricUI.getChoosePaddingComboBox();
        this.algorithm = asymmetricUI.getChooseAlgorithmComboBox();
        this.sizeOfKey = asymmetricUI.getChooseKeySizeComboBox();
        initController();
    }

    public void initController() {
        System.out.println("Initializing Asymmetric Controller");
        asymmetricUI.setCreateKeyButtonListener(e -> createKeyButtonListener());
        asymmetricUI.setSaveKeyButtonListener(this::saveKeyButtonListener);
        asymmetricUI.setLoadKeyButtonListener(this::loadKeyButtonListener);
        asymmetricUI.setChooseFileButtonListener(e -> chooseFileButtonListener());
        asymmetricUI.setCancelFileButtonListener(e -> cancelFileButtonListener());
        asymmetricUI.setEncryptButtonListener(e -> {
            try {
                encryptButtonListener();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asymmetricUI, "Encryption error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        asymmetricUI.setDecryptButtonListener(e -> {
            try {
                decryptButtonListener();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asymmetricUI, "Decryption error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        asymmetricUI.setEncryptFileButtonListener(e -> {
            try {
                encryptFileButtonListener();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asymmetricUI, "File encryption error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        asymmetricUI.setDecryptFileButtonListener(e -> {
            try {
                decryptFileButtonListener();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asymmetricUI, "File decryption error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createKeyButtonListener() {
        try {
            int keySize = (Integer) sizeOfKey.getSelectedItem();

            KeyPair keyPair = rsaCipher.genPairKey(keySize);
            rsaPublicKey = rsaCipher.encodeKey(keyPair.getPublic());
            rsaPrivateKey = rsaCipher.encodeKey(keyPair.getPrivate());

            aesKey = rsaCipher.genKeyAES(256);
            encryptedAESKey = rsaCipher.encryptAESKey();

            asymmetricUI.getTxtInputKey().setText(aesKey);
            asymmetricUI.getTxtInputIV().setText(rsaPublicKey);
            asymmetricUI.getTxtSizeRSA().setText(String.valueOf(keySize));
            JOptionPane.showMessageDialog(asymmetricUI, "Keys generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(asymmetricUI, "Key generation error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveKeyButtonListener(ActionEvent e) {
        String command = e.getActionCommand();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Key");
        if (fileChooser.showSaveDialog(asymmetricUI) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (command.equals("SavePrivateKey")) {
                    if (rsaPrivateKey == null) {
                        JOptionPane.showMessageDialog(asymmetricUI, "No private key available!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    rsaCipher.saveKey(rsaPrivateKey, filePath);
                    JOptionPane.showMessageDialog(asymmetricUI, "Private key saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (command.equals("SavePublicKey")) {
                    if (rsaPublicKey == null) {
                        JOptionPane.showMessageDialog(asymmetricUI, "No public key available!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    rsaCipher.saveKey(rsaPublicKey, filePath);
                    JOptionPane.showMessageDialog(asymmetricUI, "Public key saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asymmetricUI, "Error saving key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadKeyButtonListener(ActionEvent e) {
        String command = e.getActionCommand();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Key");
        if (fileChooser.showOpenDialog(asymmetricUI) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                String loadedKey = rsaCipher.loadKey(filePath);
                if (command.equals("LoadPrivateKey")) {
                    rsaPrivateKey = loadedKey;
                    JOptionPane.showMessageDialog(asymmetricUI, "Private key loaded!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (command.equals("LoadPublicKey")) {
                    rsaPublicKey = loadedKey;
                    asymmetricUI.getTxtInputIV().setText(rsaPublicKey);
                    JOptionPane.showMessageDialog(asymmetricUI, "Public key loaded!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asymmetricUI, "Error loading key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void chooseFileButtonListener() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose Input File");
        if (fileChooser.showOpenDialog(asymmetricUI) == JFileChooser.APPROVE_OPTION) {
            String inputFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            asymmetricUI.getTxtInputFilePath().setText(inputFilePath);

            // Suggest output file path
//            String outputFilePath = inputFilePath + ".encrypted";
//            asymmetricUI.getTxtOutputFilePath().setText(outputFilePath);
        }
    }

    private void cancelFileButtonListener() {
        asymmetricUI.getTxtInputFilePath().setText("");
        asymmetricUI.getTxtOutputFilePath().setText("");
    }

    private void encryptButtonListener() throws Exception {
        String plaintext = asymmetricUI.getTxtEncrypt().getText();
        if (plaintext.isEmpty()) {
            JOptionPane.showMessageDialog(asymmetricUI, "Please enter text to encrypt!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rsaPublicKey == null) {
            JOptionPane.showMessageDialog(asymmetricUI, "Please generate or load a public key!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String modeStr = (String) mode.getSelectedItem();
        String paddingStr = (String) padding.getSelectedItem();
        String encryptedText = rsaCipher.encrypt(plaintext, modeStr, paddingStr);
        asymmetricUI.getTxtDecrypt().setText(encryptedText);
        JOptionPane.showMessageDialog(asymmetricUI, "Text encrypted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void decryptButtonListener() throws Exception {
        String encryptedText = asymmetricUI.getTxtDecrypt().getText();
        if (encryptedText.isEmpty()) {
            JOptionPane.showMessageDialog(asymmetricUI, "Please enter text to decrypt!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rsaPrivateKey == null) {
            JOptionPane.showMessageDialog(asymmetricUI, "Please load a private key!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String modeStr = (String) mode.getSelectedItem();
        String paddingStr = (String) padding.getSelectedItem();
        String decryptedText = rsaCipher.decrypt(encryptedText, modeStr, paddingStr);
        asymmetricUI.getTxtDecrypt().setText(decryptedText);
        JOptionPane.showMessageDialog(asymmetricUI, "Text decrypted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isValidOutputPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        File file = new File(path.trim());
        File parentDir = file.getParentFile();
        return parentDir != null && parentDir.exists() && parentDir.isDirectory() && parentDir.canWrite();
    }

    private void encryptFileButtonListener() throws Exception {
        String inputFile = asymmetricUI.getTxtInputFilePath().getText();

        if (encryptedAESKey == null || aesKey == null) {
            JOptionPane.showMessageDialog(asymmetricUI, "Please generate keys first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        File f = new File(inputFile);
        String fileName = f.getName();
        File filepath = new File(f.getParent(), fileName + ".rsaen");
        String outputFilePath = filepath.getAbsolutePath();

        if (!isValidOutputPath(outputFilePath)) {
            JOptionPane.showMessageDialog(asymmetricUI, "Invalid output path", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rsaCipher.encryptFile(inputFile, outputFilePath, encryptedAESKey);
        asymmetricUI.getTxtOutputFilePath().setText(outputFilePath);
        JOptionPane.showMessageDialog(asymmetricUI, "File encrypted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void decryptFileButtonListener() throws Exception {
        String inputFile = asymmetricUI.getTxtInputFilePath().getText();
        if (rsaPrivateKey == null) {
            JOptionPane.showMessageDialog(asymmetricUI, "Please load a private key!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File f = new File(inputFile);
        String fileName = f.getName();
        File filepath = new File(f.getParent(), fileName + ".rsaen");
        String outputFilePath = filepath.getAbsolutePath();

        if (!isValidOutputPath(outputFilePath)) {
            JOptionPane.showMessageDialog(asymmetricUI, "Invalid output path", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        rsaCipher.decryptFile(inputFile, outputFilePath);
        asymmetricUI.getTxtOutputFilePath().setText(outputFilePath);
        JOptionPane.showMessageDialog(asymmetricUI, "File decrypted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}