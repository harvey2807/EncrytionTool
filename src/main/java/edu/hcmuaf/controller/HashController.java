package edu.hcmuaf.controller;

import edu.hcmuaf.model.hash.HashCipher;
import edu.hcmuaf.view.HashUI;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class HashController {
    private HashCipher hashCipher;
    private HashUI hashUI;
    private JComboBox<String> algorithm;

    public HashController(HashUI hashUI) {
        this.hashCipher = new HashCipher();
        this.hashUI = hashUI;
        this.algorithm = hashUI.getChooseAlgorithmComboBox();
        this.initController();
    }

    public void initController() {
        System.out.println("Init controller of HashController");
        hashUI.setHashButtonListener(e -> {
            try {
                hashButtonListener();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });
        hashUI.setHashFileButtonListener(e -> {
            try {
                hashFileButtonListener();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        hashUI.setChooseFileButtonListener(e -> setChooseFileListener());
    }

    private void setChooseFileListener() {
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showOpenDialog(hashUI);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File inputFile = chooser.getSelectedFile();
            System.out.println("file" + inputFile.getAbsolutePath());
            if (!inputFile.exists()) {
                JOptionPane.showMessageDialog(hashUI, "Selected file does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!inputFile.canRead()) {
                JOptionPane.showMessageDialog(hashUI, "Cannot read the selected file due to permissions!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String path = inputFile.getAbsolutePath();
            if (path.contains("onedrive") || path.contains("google drive")) {
                JOptionPane.showMessageDialog(hashUI,
                        "Warning: Cloud-synced files may cause issues if the provider is not running. Consider using a local file.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            hashUI.getTxtInput().setText(path);
        }
    }

    private void hashButtonListener() throws NoSuchAlgorithmException {
        String algorithmName = algorithm.getSelectedItem().toString();
        String text = hashUI.getTxtInput().getText().toString();
        System.out.println("text" + text);
        String textHashed = "";
        switch (algorithmName) {
            case "MD5":
                textHashed = hashCipher.hash(text, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtOutputFile().setText(textHashed);
                break;
            case "SHA-1":
                textHashed = hashCipher.hash(text, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtOutputFile().setText(textHashed);
                break;
            case "SHA-256":
                textHashed = hashCipher.hash(text, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtOutputFile().setText(textHashed);
                break;
            case "SHA-512":
                textHashed = hashCipher.hash(text, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtOutputFile().setText(textHashed);
                break;
            case "SHA-512/256":
                textHashed = hashCipher.hash(text, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtOutputFile().setText(textHashed);
                break;
            case "Whirlpool":
                textHashed = hashCipher.hash(text, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtOutputFile().setText(textHashed);
                break;

            default:
                JOptionPane.showMessageDialog(hashUI, "Invalid Algorithm Selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidOutputPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        File file = new File(path.trim());
        File parentDir = file.getParentFile();
        return parentDir != null && parentDir.exists() && parentDir.isDirectory() && parentDir.canWrite();
    }

    private void hashFileButtonListener() throws NoSuchAlgorithmException, IOException {
        String algorithmName = algorithm.getSelectedItem().toString();
        String src = hashUI.getTxtInput().getText().toString();
        System.out.println("text" + src);
        String textHashed = "";

        File f = new File(src);
        String fileName = f.getName();
        File filepath = new File(f.getParent(), fileName + ".hash");
        String outputFilePath = filepath.getAbsolutePath();
        if (!isValidOutputPath(outputFilePath)) {
            JOptionPane.showMessageDialog(hashUI, "Invalid output path", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (algorithmName) {
            case "MD5":
                textHashed = hashCipher.hashFile(src, outputFilePath, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtHash().setText(outputFilePath);
                break;
            case "SHA-1":
                textHashed = hashCipher.hashFile(src, outputFilePath, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtHash().setText(outputFilePath);
                break;
            case "SHA-256":
                textHashed = hashCipher.hashFile(src, outputFilePath, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtHash().setText(outputFilePath);
                break;
            case "SHA-512":
                textHashed = hashCipher.hashFile(src, outputFilePath, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtHash().setText(outputFilePath);
                break;
            case "SHA-512/256":
                textHashed = hashCipher.hashFile(src, outputFilePath, algorithmName);
                System.out.println("Hashed Text : " + textHashed);
                hashUI.getTxtHash().setText(outputFilePath);
                break;

            default:
                JOptionPane.showMessageDialog(hashUI, "Invalid Algorithm Selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
