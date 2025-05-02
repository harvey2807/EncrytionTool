package edu.hcmuaf.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SymmetricUI extends JPanel {
    private JLabel settingEncryptLabel, inputKeyLabel, inputIVLabel, chooseModeLabel, choosePaddingLabel,
            chooseAlgorithmLabel, chooseKeySizeLabel, sourceTextLabel, inputFileLabel, outputFileLabel;
    private JButton saveKeyButton, loadKeyButton, chooseFileButton, cancelFileButton, encryptButton,
            decryptButton, createKeyButton, encryptFileButton, decryptFileButton;
    private JTextField txtInputKey, txtInputIV;
    private JTextArea txtEncrypt, txtDecrypt, txtInputFilePath, txtOutputFilePath;
    private JComboBox<String> chooseModeComboBox, choosePaddingComboBox, chooseAlgorithmComboBox;
    private JComboBox<Integer> chooseKeySizeComboBox;

    public SymmetricUI() {
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Encryption Settings Panel
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Encryption Settings"));

        settingEncryptLabel = new JLabel("Encryption Settings");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        settingsPanel.add(settingEncryptLabel, gbc);

        inputKeyLabel = new JLabel("Input Key:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        settingsPanel.add(inputKeyLabel, gbc);

        txtInputKey = new JTextField(20);
        txtInputKey.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        settingsPanel.add(txtInputKey, gbc);

        inputIVLabel = new JLabel("Input IV:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsPanel.add(inputIVLabel, gbc);

        txtInputIV = new JTextField(20);
        txtInputIV.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        settingsPanel.add(txtInputIV, gbc);

        chooseModeLabel = new JLabel("Choose Mode:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        settingsPanel.add(chooseModeLabel, gbc);

        String[] listMode = {"CTR", "ECB", "PCBC", "CFB", "OFB", "CBC"};
        chooseModeComboBox = new JComboBox<>(listMode);
        gbc.gridx = 1;
        gbc.gridy = 3;
        settingsPanel.add(chooseModeComboBox, gbc);

        choosePaddingLabel = new JLabel("Choose Padding:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        settingsPanel.add(choosePaddingLabel, gbc);

        String[] listPadding = {"PKCS5Padding", "NoPadding", "ISO10126Padding"};
        choosePaddingComboBox = new JComboBox<>(listPadding);
        gbc.gridx = 1;
        gbc.gridy = 4;
        settingsPanel.add(choosePaddingComboBox, gbc);

        chooseAlgorithmLabel = new JLabel("Choose Algorithm:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        settingsPanel.add(chooseAlgorithmLabel, gbc);

        String[] listAlgorithm = {"AES", "DES", "Camellia", "ChaCha", "DESede", "RC2", "RC4", "Blowfish"};
        chooseAlgorithmComboBox = new JComboBox<>(listAlgorithm);
        gbc.gridx = 1;
        gbc.gridy = 5;
        settingsPanel.add(chooseAlgorithmComboBox, gbc);

        chooseKeySizeLabel = new JLabel("Choose Key Size:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        settingsPanel.add(chooseKeySizeLabel, gbc);

        Integer[] listKeySize = {64, 128, 256, 512, 1024, 2048};
        chooseKeySizeComboBox = new JComboBox<>(listKeySize);
        gbc.gridx = 1;
        gbc.gridy = 6;
        settingsPanel.add(chooseKeySizeComboBox, gbc);

        createKeyButton = new JButton("Create Key");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        settingsPanel.add(createKeyButton, gbc);

        saveKeyButton = new JButton("Save Key");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        settingsPanel.add(saveKeyButton, gbc);

        loadKeyButton = new JButton("Load Key");
        gbc.gridx = 1;
        gbc.gridy = 8;
        settingsPanel.add(loadKeyButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(settingsPanel, gbc);

        // File Selection Panel
        JPanel filePanel = new JPanel(new GridBagLayout());
        filePanel.setBorder(BorderFactory.createTitledBorder("File Selection"));

        inputFileLabel = new JLabel("Input File:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        filePanel.add(inputFileLabel, gbc);

        outputFileLabel = new JLabel("Output File:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        filePanel.add(outputFileLabel, gbc);

        txtInputFilePath = new JTextArea(3, 30);
//        txtInputFilePath.setEditable(false);
        txtInputFilePath.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane inputFileScroll = new JScrollPane(txtInputFilePath);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        filePanel.add(inputFileScroll, gbc);

        txtOutputFilePath = new JTextArea(3, 30);
//        txtOutputFilePath.setEditable(false);
        txtOutputFilePath.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane outputFileScroll = new JScrollPane(txtOutputFilePath);
        gbc.gridx = 1;
        gbc.gridy = 1;
        filePanel.add(outputFileScroll, gbc);

        chooseFileButton = new JButton("Choose File");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        filePanel.add(chooseFileButton, gbc);

        cancelFileButton = new JButton("Cancel");
        gbc.gridx = 1;
        gbc.gridy = 2;
        filePanel.add(cancelFileButton, gbc);

        encryptFileButton = new JButton("Encrypt File");
        gbc.gridx = 0;
        gbc.gridy = 3;
        filePanel.add(encryptFileButton, gbc);

        decryptFileButton = new JButton("Decrypt File");
        gbc.gridx = 1;
        gbc.gridy = 3;
        filePanel.add(decryptFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(filePanel, gbc);

        // Text Input/Output Panel
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBorder(BorderFactory.createTitledBorder("Text Input/Output"));

        sourceTextLabel = new JLabel("Source Text:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        textPanel.add(sourceTextLabel, gbc);

        txtEncrypt = new JTextArea(7, 30);
        txtEncrypt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane encryptScroll = new JScrollPane(txtEncrypt);
        gbc.gridx = 0;
        gbc.gridy = 1;
        textPanel.add(encryptScroll, gbc);

        txtDecrypt = new JTextArea(7, 30);
        txtDecrypt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane decryptScroll = new JScrollPane(txtDecrypt);
        gbc.gridx = 1;
        gbc.gridy = 1;
        textPanel.add(decryptScroll, gbc);

        encryptButton = new JButton("Encrypt");
        gbc.gridx = 0;
        gbc.gridy = 2;
        textPanel.add(encryptButton, gbc);

        decryptButton = new JButton("Decrypt");
        gbc.gridx = 1;
        gbc.gridy = 2;
        textPanel.add(decryptButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(textPanel, gbc);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Action Listeners
    public void setCreateKeyButtonListener(ActionListener e) {
        createKeyButton.addActionListener(e);
    }

    public void setSaveKeyButtonListener(ActionListener e) {
        saveKeyButton.addActionListener(e);
    }

    public void setLoadKeyButtonListener(ActionListener e) {
        loadKeyButton.addActionListener(e);
    }

    public void setChooseFileButtonListener(ActionListener e) {
        chooseFileButton.addActionListener(e);
    }

    public void setCancelFileButtonListener(ActionListener e) {
        cancelFileButton.addActionListener(e);
    }

    public void setEncryptButtonListener(ActionListener e) {
        encryptButton.addActionListener(e);
    }

    public void setDecryptButtonListener(ActionListener e) {
        decryptButton.addActionListener(e);
    }

    public void setEncryptFileButtonListener(ActionListener e) {
        encryptFileButton.addActionListener(e);
    }

    public void setDecryptFileButtonListener(ActionListener e) {
        decryptFileButton.addActionListener(e);
    }

    // Getters and Setters
    public JComboBox<Integer> getChooseKeySizeComboBox() {
        return chooseKeySizeComboBox;
    }

    public void setChooseKeySizeComboBox(JComboBox<Integer> chooseKeySizeComboBox) {
        this.chooseKeySizeComboBox = chooseKeySizeComboBox;
    }

    public JComboBox<String> getChooseAlgorithmComboBox() {
        return chooseAlgorithmComboBox;
    }

    public void setChooseAlgorithmComboBox(JComboBox<String> chooseAlgorithmComboBox) {
        this.chooseAlgorithmComboBox = chooseAlgorithmComboBox;
    }

    public JComboBox<String> getChoosePaddingComboBox() {
        return choosePaddingComboBox;
    }

    public void setChoosePaddingComboBox(JComboBox<String> choosePaddingComboBox) {
        this.choosePaddingComboBox = choosePaddingComboBox;
    }

    public JComboBox<String> getChooseModeComboBox() {
        return chooseModeComboBox;
    }

    public void setChooseModeComboBox(JComboBox<String> chooseModeComboBox) {
        this.chooseModeComboBox = chooseModeComboBox;
    }

    public JTextArea getTxtDecrypt() {
        return txtDecrypt;
    }

    public void setTxtDecrypt(JTextArea txtDecrypt) {
        this.txtDecrypt = txtDecrypt;
    }

    public JTextArea getTxtEncrypt() {
        return txtEncrypt;
    }

    public void setTxtEncrypt(JTextArea txtEncrypt) {
        this.txtEncrypt = txtEncrypt;
    }

    public JTextArea getTxtInputFilePath() {
        return txtInputFilePath;
    }

    public void setTxtInputFilePath(JTextArea txtInputFilePath) {
        this.txtInputFilePath = txtInputFilePath;
    }

    public JTextArea getTxtOutputFilePath() {
        return txtOutputFilePath;
    }

    public void setTxtOutputFilePath(JTextArea txtOutputFilePath) {
        this.txtOutputFilePath = txtOutputFilePath;
    }

    public JTextField getTxtInputIV() {
        return txtInputIV;
    }

    public void setTxtInputIV(JTextField txtInputIV) {
        this.txtInputIV = txtInputIV;
    }

    public JTextField getTxtInputKey() {
        return txtInputKey;
    }

    public void setTxtInputKey(JTextField txtInputKey) {
        this.txtInputKey = txtInputKey;
    }

    public JButton getCreateKeyButton() {
        return createKeyButton;
    }

    public void setCreateKeyButton(JButton createKeyButton) {
        this.createKeyButton = createKeyButton;
    }

    public JButton getDecryptButton() {
        return decryptButton;
    }

    public void setDecryptButton(JButton decryptButton) {
        this.decryptButton = decryptButton;
    }

    public JButton getEncryptButton() {
        return encryptButton;
    }

    public void setEncryptButton(JButton encryptButton) {
        this.encryptButton = encryptButton;
    }

    public JButton getCancelFileButton() {
        return cancelFileButton;
    }

    public void setCancelFileButton(JButton cancelFileButton) {
        this.cancelFileButton = cancelFileButton;
    }

    public JButton getChooseFileButton() {
        return chooseFileButton;
    }

    public void setChooseFileButton(JButton chooseFileButton) {
        this.chooseFileButton = chooseFileButton;
    }

    public JButton getLoadKeyButton() {
        return loadKeyButton;
    }

    public void setLoadKeyButton(JButton loadKeyButton) {
        this.loadKeyButton = loadKeyButton;
    }

    public JButton getSaveKeyButton() {
        return saveKeyButton;
    }

    public void setSaveKeyButton(JButton saveKeyButton) {
        this.saveKeyButton = saveKeyButton;
    }

    public JButton getEncryptFileButton() {
        return encryptFileButton;
    }

    public void setEncryptFileButton(JButton encryptFileButton) {
        this.encryptFileButton = encryptFileButton;
    }

    public JButton getDecryptFileButton() {
        return decryptFileButton;
    }

    public void setDecryptFileButton(JButton decryptFileButton) {
        this.decryptFileButton = decryptFileButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Symmetric Encryption UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SymmetricUI());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}