package edu.hcmuaf.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HashUI extends JPanel {
    private JLabel chooseAlgorithmLabel, sourceTextLabel, hashResultLabel, inputFileLabel, outputFileLabel;
    private JButton hashButton, verifyButton, chooseFileButton;
    private JTextArea txtInput, txtHash, txtInputFile, txtOutputFile;
    private JComboBox<String> chooseAlgorithmComboBox;

    public HashUI() {
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel algoPanel = new JPanel(new GridBagLayout());
        algoPanel.setBorder(BorderFactory.createTitledBorder("Hash Algorithm"));

        chooseAlgorithmLabel = new JLabel("Choose Algorithm:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        algoPanel.add(chooseAlgorithmLabel, gbc);

        String[] algorithms = {"MD5", "SHA-1", "SHA-256", "SHA-512", "SHA-512/256", "Whirlpool"};
        chooseAlgorithmComboBox = new JComboBox<>(algorithms);
        gbc.gridx = 1;
        gbc.gridy = 0;
        algoPanel.add(chooseAlgorithmComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(algoPanel, gbc);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBorder(BorderFactory.createTitledBorder("Hash Operations"));

        // Input Text and Hash Result (1 row, 2 columns)
        JPanel inputHashPanel = new JPanel(new GridLayout(1, 2, 5, 5));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        sourceTextLabel = new JLabel("Input File:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(sourceTextLabel, gbc);

        txtInput = new JTextArea(7, 30);
        txtInput.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane inputScroll = new JScrollPane(txtInput);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(inputScroll, gbc);

        JPanel hashPanel = new JPanel(new GridBagLayout());
        hashResultLabel = new JLabel("Output Result:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        hashPanel.add(hashResultLabel, gbc);

        txtHash = new JTextArea(7, 30);
        txtHash.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane hashScroll = new JScrollPane(txtHash);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        hashPanel.add(hashScroll, gbc);

        inputHashPanel.add(inputPanel);
        inputHashPanel.add(hashPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        textPanel.add(inputHashPanel, gbc);

        // Verify and Choose File Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        verifyButton = new JButton("Hash File");
        chooseFileButton = new JButton("Choose File");
        buttonPanel.add(verifyButton);
        buttonPanel.add(chooseFileButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textPanel.add(buttonPanel, gbc);

        // Input File and Output File (1 row, 2 columns)
        JPanel filePanel = new JPanel(new GridLayout(1, 2, 5, 5));

        JPanel inputFilePanel = new JPanel(new GridBagLayout());
        inputFileLabel = new JLabel("Input Text:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputFilePanel.add(inputFileLabel, gbc);

        txtInputFile = new JTextArea(7, 30);
        txtInputFile.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane inputFileScroll = new JScrollPane(txtInputFile);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        inputFilePanel.add(inputFileScroll, gbc);

        JPanel outputFilePanel = new JPanel(new GridBagLayout());
        outputFileLabel = new JLabel("Output Result:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        outputFilePanel.add(outputFileLabel, gbc);

        txtOutputFile = new JTextArea(7, 30);
        txtOutputFile.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane outputFileScroll = new JScrollPane(txtOutputFile);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        outputFilePanel.add(outputFileScroll, gbc);

        filePanel.add(inputFilePanel);
        filePanel.add(outputFilePanel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        textPanel.add(filePanel, gbc);

        // Hash Button
        JPanel hashButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hashButton = new JButton("Hash");
        hashButtonPanel.add(hashButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textPanel.add(hashButtonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(textPanel, gbc);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Action Listeners
    public void setHashButtonListener(ActionListener listener) {
        hashButton.addActionListener(listener);
    }

    public void setHashFileButtonListener(ActionListener listener) {
        verifyButton.addActionListener(listener);
    }

    public void setChooseFileButtonListener(ActionListener listener) {
        chooseFileButton.addActionListener(listener);
    }

    // Getters
    public JTextArea getTxtInput() {
        return txtInput;
    }

    public JTextArea getTxtHash() {
        return txtHash;
    }

    public JTextArea getTxtInputFile() {
        return txtInputFile;
    }

    public JTextArea getTxtOutputFile() {
        return txtOutputFile;
    }

    public JComboBox<String> getChooseAlgorithmComboBox() {
        return chooseAlgorithmComboBox;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hash UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new HashUI());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}