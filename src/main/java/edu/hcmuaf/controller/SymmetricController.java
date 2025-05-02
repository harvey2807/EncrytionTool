package edu.hcmuaf.controller;

import edu.hcmuaf.model.modern.symmetric.*;
import edu.hcmuaf.view.SymmetricUI;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.Base64;

public class SymmetricController {
    private AESCipher aesCipher;
    private BlowfishCipher blowfishCipher;
    private ChaChaCipher chaChaCipher;
    private RC2Cipher rc2Cipher;
    private RC4Cipher rc4Cipher;
    private DESedeCipher desedeCipher;
    private DESCipher desCipher;
    private CamelliaCipher camelliaCipher;
    private SymmetricUI symmetricUI;
    private JComboBox<String> algorithm;
    private JComboBox<String> modes;
    private JComboBox<String> paddings;
    private JComboBox<Integer> sizes;
    private String currentKey;
    private String currentIV;
    private String currentPadding;


    public SymmetricController(SymmetricUI symmetricUI) throws Exception {
        this.symmetricUI = symmetricUI;
        this.algorithm = symmetricUI.getChooseAlgorithmComboBox();
        this.modes = symmetricUI.getChooseModeComboBox();
        this.sizes = symmetricUI.getChooseKeySizeComboBox();
        this.paddings = symmetricUI.getChoosePaddingComboBox();
        this.aesCipher = new AESCipher();
        this.blowfishCipher = new BlowfishCipher();
        this.camelliaCipher = new CamelliaCipher();
        this.rc2Cipher = new RC2Cipher();
        this.rc4Cipher = new RC4Cipher();
        this.desedeCipher = new DESedeCipher();
        this.chaChaCipher = new ChaChaCipher();
        this.desCipher = new DESCipher();
        initController();
    }

    public void initController() {
        System.out.println("Initializing Symmetric Controller");
        symmetricUI.setCreateKeyButtonListener(e -> createKeyButtonListener());
        symmetricUI.setSaveKeyButtonListener(e -> saveKeyButtonListener());
        symmetricUI.setLoadKeyButtonListener(e -> loadKeyButtonListener());
        symmetricUI.setChooseFileButtonListener(e -> chooseFileButtonListener());
        symmetricUI.setCancelFileButtonListener(e -> cancelFileButtonListener());
        symmetricUI.setEncryptButtonListener(e -> {
            try {
                encryptButtonListener();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        symmetricUI.setDecryptButtonListener(e -> decryptButtonListener());

        symmetricUI.setEncryptFileButtonListener(e -> encryptFileButtonListener());
        symmetricUI.setDecryptFileButtonListener(e -> decryptFileButtonListener());
        algorithm.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateComboBoxes();
                }
            }
        });

        updateComboBoxes();
    }


    private void updateComboBoxes() {
        String algorithmSelected = algorithm.getSelectedItem().toString();
        modes.removeAllItems();
        paddings.removeAllItems();
        sizes.removeAllItems();
        boolean isStreamCipher = false;

        symmetricUI.getTxtInputIV().setEnabled(true);
        modes.setEnabled(true);
        paddings.setEnabled(true);

        switch (algorithmSelected) {
            case "AES":
                addItems(modes, "ECB", "CBC", "CFB", "OFB", "CTR", "GCM");
                addItems(paddings, "NoPadding", "PKCS5Padding");
                addItems(sizes, 128, 192, 256);
                break;
            case "DES":
                addItems(modes, "ECB", "CBC", "CFB", "OFB");
                addItems(paddings, "NoPadding", "PKCS5Padding");
                addItems(sizes, 56);
                break;
            case "DESede":
                addItems(modes, "ECB", "CBC", "CFB", "OFB");
                addItems(paddings, "NoPadding", "PKCS5Padding");
                addItems(sizes, 112, 168);
                break;
            case "Blowfish":
                addItems(modes, "ECB", "CBC", "CFB", "OFB");
                addItems(paddings, "NoPadding", "PKCS5Padding");
                addItems(sizes, 32, 128, 256, 448);
                break;
            case "RC4":
                isStreamCipher = true;
                addItems(sizes, 40, 128, 256, 512, 1024, 2048);
                break;
            case "RC2":
                addItems(modes, "ECB", "CBC", "CFB", "OFB");
                addItems(paddings, "NoPadding", "PKCS5Padding");
                addItems(sizes, 40, 128, 256, 512, 1024);
                break;
            case "ChaCha":
                isStreamCipher = true;
                addItems(sizes, 256);
                break;
            case "Camellia":
                addItems(modes, "ECB", "CBC");
                addItems(paddings, "PKCS5Padding");
                addItems(sizes, 128, 192, 256);
                break;
            default:
                JOptionPane.showMessageDialog(symmetricUI, "Unknown algorithm selected", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
        if (isStreamCipher) {
            modes.setEnabled(false);
            paddings.setEnabled(false);
            symmetricUI.getTxtInputIV().setEnabled(false);
            modes.addItem("N/A");
            paddings.addItem("N/A");
        }
    }

    private void addItems(JComboBox<String> comboBox, String... items) {
        for (String item : items) {
            comboBox.addItem(item);
        }
    }

    private void addItems(JComboBox<Integer> comboBox, Integer... items) {
        for (Integer item : items) {
            comboBox.addItem(item);
        }
    }

    private void createKeyButtonListener() {
        System.out.println("Creating key...");
        String selectedAlgorithm = (String) algorithm.getSelectedItem();
        String selectedMode = (String) modes.getSelectedItem();
        Integer selectedKeySize = (Integer) sizes.getSelectedItem();

        if (selectedAlgorithm == null) {
            JOptionPane.showMessageDialog(symmetricUI, "Please select an algorithm", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedMode == null && !isStreamCipher(selectedAlgorithm)) {
            JOptionPane.showMessageDialog(symmetricUI, "Please select a mode", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedKeySize == null) {
            JOptionPane.showMessageDialog(symmetricUI, "Please select a key size", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            switch (selectedAlgorithm) {
                case "AES":
                    currentKey = aesCipher.genKey(selectedKeySize).toString();
                    if (!selectedMode.equals("ECB")) {
                        currentIV = aesCipher.genIV().toString();
                    }
                    break;
                case "Camellia":
                    currentKey = camelliaCipher.genKey(selectedKeySize);
                    if (!selectedMode.equals("ECB")) {
                        currentIV = camelliaCipher.genIV();
                    }
                    break;

                case "Blowfish":
                    currentKey = blowfishCipher.genKey(selectedKeySize).toString();
                    if (!selectedMode.equals("ECB")) {
                        currentIV = blowfishCipher.genIV().toString();
                    }
                    break;
                case "DESede":
                    currentKey = desedeCipher.genKey(selectedKeySize).toString();
                    if (!selectedMode.equals("ECB")) {
                        currentIV = desedeCipher.genIV().toString();
                    }
                    break;
                case "RC4":
                    currentKey = rc4Cipher.genKey(selectedKeySize).toString();
                    currentIV = null;
                    break;
                case "RC2":
                    currentKey = rc2Cipher.genKey(selectedKeySize).toString();
                    if (!selectedMode.equals("ECB")) {
                        currentIV = rc2Cipher.genIV().toString();
                    }
                    break;
                case "ChaCha":
                    currentKey = chaChaCipher.genKey(selectedKeySize).toString();
                    currentIV = chaChaCipher.genNonce().toString();
                    break;
                case "DES":
                    currentKey = desCipher.genKey(selectedKeySize).toString();
                    if (!selectedMode.equals("ECB")) {
                        currentIV = desCipher.genIV().toString();
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(symmetricUI, "Invalid algorithm", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            symmetricUI.getTxtInputKey().setText(currentKey);
            symmetricUI.getTxtInputIV().setText(currentIV != null ? currentIV : "");
            JOptionPane.showMessageDialog(symmetricUI, "Key created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Generated key: " + currentKey);
            System.out.println("Generated IV: " + currentIV);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(symmetricUI, "Error generating key: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isStreamCipher(String algorithm) {
        return algorithm.equals("RC4") || algorithm.equals("ChaCha") || algorithm.equals("ARCF");
    }

    private void saveKeyButtonListener() {
        String key = symmetricUI.getTxtInputKey().getText();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(symmetricUI, "Please enter a key", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showSaveDialog(symmetricUI);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedWriter writter = new BufferedWriter(new FileWriter(file))) {
                writter.write(key);
                JOptionPane.showMessageDialog(symmetricUI, "Key saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(symmetricUI, "Error saving key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }


    private void loadKeyButtonListener() {
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showOpenDialog(symmetricUI);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String key = reader.readLine();
                symmetricUI.getTxtInputKey().setText(key);
                JOptionPane.showMessageDialog(symmetricUI, "Key loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(symmetricUI, "Error saving key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isFilePath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        File file = new File(path.trim());
        return file.exists() && file.isFile() && file.canRead();
    }

    private void chooseFileButtonListener() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int choice = chooser.showOpenDialog(symmetricUI);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File inputFile = chooser.getSelectedFile();
            System.out.println("file" + inputFile.getAbsolutePath());
            if (!inputFile.exists()) {
                JOptionPane.showMessageDialog(symmetricUI, "Selected file does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!inputFile.canRead()) {
                JOptionPane.showMessageDialog(symmetricUI, "Cannot read the selected file due to permissions!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
//            String path = inputFile.getAbsolutePath().toLowerCase();
            String path = inputFile.getAbsolutePath();
            if (path.contains("onedrive") || path.contains("google drive")) {
                JOptionPane.showMessageDialog(symmetricUI,
                        "Warning: Cloud-synced files may cause issues if the provider is not running. Consider using a local file.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            symmetricUI.getTxtInputFilePath().setText(path);
        }
    }

    private void cancelFileButtonListener() {
        symmetricUI.getTxtInputFilePath().setText("");
        symmetricUI.getTxtOutputFilePath().setText("");
        JOptionPane.showMessageDialog(symmetricUI, "File selection canceled", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void encryptButtonListener() throws Exception {
        try {
            System.out.println("asdhasdkjasldjasldjalsdjasd");
            String key = symmetricUI.getTxtInputKey().getText();
            String iv = symmetricUI.getTxtInputIV().getText();
            String mode = symmetricUI.getChooseModeComboBox().getSelectedItem().toString();
            String padding = symmetricUI.getChoosePaddingComboBox().getSelectedItem().toString();
            String algorithmName = symmetricUI.getChooseAlgorithmComboBox().getSelectedItem().toString();
            Integer sizeOfKey = (Integer) sizes.getSelectedItem();
            String text = symmetricUI.getTxtEncrypt().getText();
            String encryted = "";
//            File file = new File(text);
//            if (file.isFile())

            switch (algorithmName) {
                case "AES":
                    encryted = aesCipher.encrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "Camellia":
                    encryted = camelliaCipher.encrypt(text, key, mode, iv, padding);
                    System.out.println("asdn");
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "Blowfish":
                    encryted = blowfishCipher.encrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "DESede":
                    encryted = desedeCipher.encrypt(text, key, iv, mode, padding);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "RC4":
                    encryted = rc4Cipher.encrypt(text, key);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "RC2":
                    encryted = rc2Cipher.encrypt(text, key, iv, mode, padding);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "ChaCha":
                    encryted = chaChaCipher.encrypt(text, key, iv);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                case "DES":
                    encryted = desCipher.encrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(encryted);
                    break;
                default:
                    JOptionPane.showMessageDialog(symmetricUI, "Invalid algorithm", "Error", JOptionPane.ERROR_MESSAGE);
                    return;

            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(symmetricUI, "Encrypted failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void decryptButtonListener() {
        try {
            String key = symmetricUI.getTxtInputKey().getText();
            String iv = symmetricUI.getTxtInputIV().getText();
            String mode = symmetricUI.getChooseModeComboBox().getSelectedItem().toString();
            String padding = symmetricUI.getChoosePaddingComboBox().getSelectedItem().toString();
            String algorithmName = symmetricUI.getChooseAlgorithmComboBox().getSelectedItem().toString();
//            Integer sizeOfKey = (Integer) sizes.getSelectedItem();
            String text = symmetricUI.getTxtEncrypt().getText();
            String decrypted = "";


            switch (algorithmName) {
                case "AES":
                    decrypted = aesCipher.decrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Camellia":
                    decrypted = camelliaCipher.decrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "Blowfish":
                    decrypted = blowfishCipher.decrypt(text, key, mode, padding, iv);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "DESede":
                    decrypted = desedeCipher.decrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "RC4":
                    decrypted = rc4Cipher.decrypt(text, key);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "RC2":
                    decrypted = rc2Cipher.decrypt(text, key, iv, mode, padding);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "ChaCha":
                    decrypted = chaChaCipher.decrypt(text, key, iv);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                case "DES":
                    decrypted = desCipher.decrypt(text, key, mode, iv, padding);
                    symmetricUI.getTxtDecrypt().setText(decrypted);
                    break;
                default:
                    JOptionPane.showMessageDialog(symmetricUI, "Invalid algorithm", "Error", JOptionPane.ERROR_MESSAGE);
                    return;

            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(symmetricUI, "Encrypted failed!", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void encryptFileButtonListener() {
        try {
            String key = symmetricUI.getTxtInputKey().getText();
            String iv = symmetricUI.getTxtInputIV().getText();
            String mode = symmetricUI.getChooseModeComboBox().getSelectedItem().toString();
            String padding = symmetricUI.getChoosePaddingComboBox().getSelectedItem().toString();
            String algorithmName = symmetricUI.getChooseAlgorithmComboBox().getSelectedItem().toString();
            String src = symmetricUI.getTxtInputFilePath().getText().trim();
            System.out.println("source of file path: " + src);

            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(symmetricUI, "Key cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!mode.equals("ECB") && iv.isEmpty() && !isStreamCipher(algorithmName)) {
                JOptionPane.showMessageDialog(symmetricUI, "IV cannot be empty for this mode", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File f = new File(src);
            String fileName = f.getName();
            File filepath = new File(f.getParent(), fileName + ".en");
            String outputFilePath = filepath.getAbsolutePath();
            if (!isValidOutputPath(outputFilePath)) {
                JOptionPane.showMessageDialog(symmetricUI, "Invalid output path", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            switch (algorithmName) {
                case "AES":
                    aesCipher.encryptFile(src, outputFilePath, key, iv, mode, padding);
                    break;
                case "Camellia":
                    camelliaCipher.encryptFile(src, outputFilePath, key, iv, mode, padding);
                    break;
                case "Blowfish":
                    blowfishCipher.encryptFile(src, outputFilePath, key, iv, mode, padding);
                    break;
                case "DESede":
                    desedeCipher.encryptFile(src, outputFilePath, key, iv, mode, padding);
                    break;
                case "RC4":
                    rc4Cipher.encryptFile(src, outputFilePath, key);
                    break;
                case "RC2":
                    rc2Cipher.encryptFile(src, outputFilePath, key, iv, mode, padding);
                    break;
                case "ChaCha":
                    chaChaCipher.encryptFile(src, outputFilePath, key, iv);
                    break;
                case "DES":
                    desCipher.encryptFile(src, outputFilePath, key, iv, mode, padding);
                    break;
                default:
                    JOptionPane.showMessageDialog(symmetricUI, "Invalid algorithm", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            symmetricUI.getTxtOutputFilePath().setText(outputFilePath);
            JOptionPane.showMessageDialog(symmetricUI, "File encrypted successfully to " + outputFilePath, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(symmetricUI, "Encryption failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decryptFileButtonListener() {
        try {
            String key = symmetricUI.getTxtInputKey().getText();
            String iv = symmetricUI.getTxtInputIV().getText();
            String mode = symmetricUI.getChooseModeComboBox().getSelectedItem().toString();
            String padding = symmetricUI.getChoosePaddingComboBox().getSelectedItem().toString();
            String algorithmName = symmetricUI.getChooseAlgorithmComboBox().getSelectedItem().toString();
            String src = symmetricUI.getTxtInputFilePath().getText().trim();

            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(symmetricUI, "Key cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isFilePath(src)) {
                JOptionPane.showMessageDialog(symmetricUI,
                        "Invalid or non-existent input file. Please check the path and permissions.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!mode.equals("ECB") && iv.isEmpty() && !isStreamCipher(algorithmName)) {
                JOptionPane.showMessageDialog(symmetricUI, "IV cannot be empty for this mode", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Output File for Decrypted Data");
            int choice = chooser.showSaveDialog(symmetricUI);
            if (choice != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String outputFilePath = chooser.getSelectedFile().getAbsolutePath();
            if (!isValidOutputPath(outputFilePath)) {
                JOptionPane.showMessageDialog(symmetricUI,
                        "Output directory is not writable or does not exist",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            switch (algorithmName) {
                case "AES":
                    aesCipher.decryptFile(src, outputFilePath, key, mode, padding);
                    break;
                case "Camellia":
                    camelliaCipher.decryptFile(src, outputFilePath, key, mode, padding);
                    break;
                case "Blowfish":
                    blowfishCipher.decryptFile(src, outputFilePath, key, mode, padding);
                    break;
                case "DESede":
                    desedeCipher.decryptFile(src, outputFilePath, key, mode, padding);
                    break;
                case "RC4":
                    rc4Cipher.decryptFile(src, outputFilePath, key);
                    break;
                case "RC2":
                    rc2Cipher.decryptFile(src, outputFilePath, key, mode, padding);
                    break;
                case "ChaCha":
                    chaChaCipher.decryptFile(src, outputFilePath, key);
                    break;
                case "DES":
                    desCipher.decryptFile(src, outputFilePath, key, mode, padding);
                    break;
                default:
                    JOptionPane.showMessageDialog(symmetricUI, "Invalid algorithm", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            symmetricUI.getTxtOutputFilePath().setText(outputFilePath);
            JOptionPane.showMessageDialog(symmetricUI,
                    "File decrypted successfully to " + outputFilePath,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(symmetricUI,
                    "Input file not found: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(symmetricUI,
                    "Decryption failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
