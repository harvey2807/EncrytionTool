package edu.hcmuaf.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BasicUI extends JPanel {
    private JLabel settingEncryptLabel, inputKeyLabel, chooseAlgorithmLabel, chooseKeySizeLabel, sourceTextLabel;
    private JButton saveKeyButton, loadKeyButton, encryptButton, decryptButton, createKeyButton;
    private JTextField txtInputKey, txtSize;
    private JTextArea txtEncrypt, txtDecrypt;
    private JComboBox<String> chooseAlgorithmComboBox;

    public BasicUI() {
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Encryption Settings"));

        settingEncryptLabel = new JLabel("Setting Encrypt");
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

        chooseAlgorithmLabel = new JLabel("Choose Algorithm:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsPanel.add(chooseAlgorithmLabel, gbc);

        String[] listAlgorithm = {"Affine", "Caesar", "Hill", "Substitution", "Transposition", "Vigenere"};
        chooseAlgorithmComboBox = new JComboBox<>(listAlgorithm);
        gbc.gridx = 1;
        gbc.gridy = 2;
        settingsPanel.add(chooseAlgorithmComboBox, gbc);

        chooseKeySizeLabel = new JLabel("Choose Key Size:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        settingsPanel.add(chooseKeySizeLabel, gbc);

//        Integer[] listKeySize = {64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384};
//        chooseKeySizeComboBox = new JComboBox<>(listKeySize);
//        gbc.gridx = 1;
//        gbc.gridy = 3;
//        settingsPanel.add(chooseKeySizeComboBox, gbc);
        txtSize = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        settingsPanel.add(txtSize, gbc);

        createKeyButton = new JButton("Create Key");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        settingsPanel.add(createKeyButton, gbc);

        saveKeyButton = new JButton("Save Key");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        settingsPanel.add(saveKeyButton, gbc);

        loadKeyButton = new JButton("Load Key");
        gbc.gridx = 1;
        gbc.gridy = 5;
        settingsPanel.add(loadKeyButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(settingsPanel, gbc);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBorder(BorderFactory.createTitledBorder("Text Input/Output"));

        sourceTextLabel = new JLabel("Source Text:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        textPanel.add(sourceTextLabel, gbc);

        txtEncrypt = new JTextArea(10, 30);
        txtEncrypt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane encryptScroll = new JScrollPane(txtEncrypt);
        gbc.gridx = 0;
        gbc.gridy = 1;
        textPanel.add(encryptScroll, gbc);

        txtDecrypt = new JTextArea(10, 30);
        txtDecrypt.setEditable(false);
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

        // Thêm textPanel vào panel chính
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(textPanel, gbc);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void setSaveKeyButtonListener(ActionListener listener) {
        saveKeyButton.addActionListener(listener);
    }

    public void setLoadKeyButtonListener(ActionListener listener) {
        loadKeyButton.addActionListener(listener);
    }

    public void setCreateKeyButtonListener(ActionListener listener) {
        createKeyButton.addActionListener(listener);
    }

    public void setEncryptButtonListener(ActionListener listener) {
        encryptButton.addActionListener(listener);
    }

    public void setDecryptButtonListener(ActionListener listener) {
        decryptButton.addActionListener(listener);
    }

    public JButton getSaveKeyButton() {
        return saveKeyButton;
    }

    public void setSaveKeyButton(JButton saveKeyButton) {
        this.saveKeyButton = saveKeyButton;
    }

    public JButton getLoadKeyButton() {
        return loadKeyButton;
    }

    public void setLoadKeyButton(JButton loadKeyButton) {
        this.loadKeyButton = loadKeyButton;
    }

    public JButton getEncryptButton() {
        return encryptButton;
    }

    public void setEncryptButton(JButton encryptButton) {
        this.encryptButton = encryptButton;
    }

    public JButton getDecryptButton() {
        return decryptButton;
    }

    public void setDecryptButton(JButton decryptButton) {
        this.decryptButton = decryptButton;
    }

    public JButton getCreateKeyButton() {
        return createKeyButton;
    }

    public void setCreateKeyButton(JButton createKeyButton) {
        this.createKeyButton = createKeyButton;
    }

    public JTextField getTxtInputKey() {
        return txtInputKey;
    }

    public void setTxtInputKey(JTextField txtInputKey) {
        this.txtInputKey = txtInputKey;
    }

    public JTextArea getTxtEncrypt() {
        return txtEncrypt;
    }

    public void setTxtEncrypt(JTextArea txtEncrypt) {
        this.txtEncrypt = txtEncrypt;
    }

    public JTextArea getTxtDecrypt() {
        return txtDecrypt;
    }

    public void setTxtDecrypt(JTextArea txtDecrypt) {
        this.txtDecrypt = txtDecrypt;
    }

    public JComboBox<String> getChooseAlgorithmComboBox() {
        return chooseAlgorithmComboBox;
    }

    public JTextField getTxtSize() {
        return this.txtSize;
    }

    public void setTxtSize(JTextField txtSize) {
        this.txtSize = txtSize;
    }

    public void setChooseAlgorithmComboBox(JComboBox<String> chooseAlgorithmComboBox) {
        this.chooseAlgorithmComboBox = chooseAlgorithmComboBox;
    }


    // Main method để chạy thử giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Encryption UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new BasicUI());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}