package edu.hcmuaf.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AsymmetricUI extends JPanel {
    private JLabel settingEncryptLabel, inputKeyLabel, inputIVLabel, chooseModeLabel, choosePaddingLabel,
            chooseAlgorithmLabel, chooseKeySizeLabel, sourceTextLabel, inputFileLabel, outputFileLabel, sizeRSALabel;
    private JButton saveKeyButton, loadKeyButton, chooseFileButton, cancelFileButton, encryptButton,
            decryptButton, createKeyButton, encryptFileButton, decryptFileButton;
    private JTextField txtInputKey, txtInputIV, txtSizeRSA;
    private JTextArea txtEncrypt, txtDecrypt, txtInputFilePath, txtOutputFilePath;
    private JComboBox<String> chooseModeComboBox, choosePaddingComboBox, chooseAlgorithmComboBox;
    private JComboBox<Integer> chooseKeySizeComboBox;

    public AsymmetricUI() {
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

        inputKeyLabel = new JLabel("Input AES Key:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        settingsPanel.add(inputKeyLabel, gbc);

        txtInputKey = new JTextField(20);
        txtInputKey.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        settingsPanel.add(txtInputKey, gbc);

        inputIVLabel = new JLabel("Input RSA Key:");
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

        String[] listMode = {"ECB"}; // RSA typically uses ECB for simplicity
        chooseModeComboBox = new JComboBox<>(listMode);
        gbc.gridx = 1;
        gbc.gridy = 3;
        settingsPanel.add(chooseModeComboBox, gbc);

        choosePaddingLabel = new JLabel("Choose Padding:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        settingsPanel.add(choosePaddingLabel, gbc);

        String[] listPadding = {"PKCS1Padding"};
        choosePaddingComboBox = new JComboBox<>(listPadding);
        gbc.gridx = 1;
        gbc.gridy = 4;
        settingsPanel.add(choosePaddingComboBox, gbc);

        chooseAlgorithmLabel = new JLabel("Choose Algorithm:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        settingsPanel.add(chooseAlgorithmLabel, gbc);

        String[] listAlgorithm = {"RSA"};
        chooseAlgorithmComboBox = new JComboBox<>(listAlgorithm);
        gbc.gridx = 1;
        gbc.gridy = 5;
        settingsPanel.add(chooseAlgorithmComboBox, gbc);

        sizeRSALabel = new JLabel("Size of RSA:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        settingsPanel.add(sizeRSALabel, gbc);

        txtSizeRSA = new JTextField(20);
        txtSizeRSA.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 6;
        settingsPanel.add(txtSizeRSA, gbc);

        chooseKeySizeLabel = new JLabel("Choose Key Size:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        settingsPanel.add(chooseKeySizeLabel, gbc);

        Integer[] listKeySize = {512, 1024, 2048, 4096};
        chooseKeySizeComboBox = new JComboBox<>(listKeySize);
        gbc.gridx = 1;
        gbc.gridy = 7;
        settingsPanel.add(chooseKeySizeComboBox, gbc);

        createKeyButton = new JButton("Create Key");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        settingsPanel.add(createKeyButton, gbc);

        saveKeyButton = new JButton("Save Key");
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        settingsPanel.add(saveKeyButton, gbc);

        loadKeyButton = new JButton("Load Key");
        gbc.gridx = 1;
        gbc.gridy = 9;
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
        txtInputFilePath.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane inputFileScroll = new JScrollPane(txtInputFilePath);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        filePanel.add(inputFileScroll, gbc);

        txtOutputFilePath = new JTextArea(3, 30);
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

        // Add ActionListener for Save Key and Load Key
        saveKeyButton.addActionListener(e -> showSaveKeyOptions());
        loadKeyButton.addActionListener(e -> showLoadKeyOptions());
    }

    public void showSaveKeyOptions() {
        String[] options = {"Save Private Key", "Save Public Key"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose key type to save:",
                "Save Key",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // Save Private Key
            ActionListener listener = saveKeyButton.getActionListeners().length > 0 ? saveKeyButton.getActionListeners()[0] : null;
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(saveKeyButton, ActionEvent.ACTION_PERFORMED, "SavePrivateKey"));
            }
        } else if (choice == 1) {
            // Save Public Key
            ActionListener listener = saveKeyButton.getActionListeners().length > 0 ? saveKeyButton.getActionListeners()[0] : null;
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(saveKeyButton, ActionEvent.ACTION_PERFORMED, "SavePublicKey"));
            }
        }
    }

    public void showLoadKeyOptions() {
        String[] options = {"Load Private Key", "Load Public Key"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose key type to load:",
                "Load Key",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // Load Private Key
            ActionListener listener = loadKeyButton.getActionListeners().length > 0 ? loadKeyButton.getActionListeners()[0] : null;
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(loadKeyButton, ActionEvent.ACTION_PERFORMED, "LoadPrivateKey"));
            }
        } else if (choice == 1) {
            // Load Public Key
            ActionListener listener = loadKeyButton.getActionListeners().length > 0 ? loadKeyButton.getActionListeners()[0] : null;
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(loadKeyButton, ActionEvent.ACTION_PERFORMED, "LoadPublicKey"));
            }
        }
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
    public JTextField getTxtSizeRSA() {
        return txtSizeRSA;
    }

    public void setTxtSizeRSA(JTextField txtSizeRSA) {
        this.txtSizeRSA = txtSizeRSA;
    }

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
            JFrame frame = new JFrame("Asymmetric Encryption UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new AsymmetricUI());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}