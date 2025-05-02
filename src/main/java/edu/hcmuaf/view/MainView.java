package edu.hcmuaf.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    public MainView() {
        init();
    }

    private void init() {
        setTitle("Ứng Dụng Mã Hóa");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Thêm viền cho tabbedPane

        try {
            BasicUI basicUI = new BasicUI();
            SymmetricUI symmetricUI = new SymmetricUI();
            HashUI hashUI = new HashUI();
            AsymmetricUI asymmetricUI = new AsymmetricUI();

            try {
                new edu.hcmuaf.controller.BasicUIController(basicUI);
                new edu.hcmuaf.controller.RSAUIController(asymmetricUI);
                new edu.hcmuaf.controller.SymmetricController(symmetricUI);
                new edu.hcmuaf.controller.HashController(hashUI);
            } catch (Exception e) {
                System.err.println("Lỗi khi gắn Controller: " + e.getMessage());
            }

            tabbedPane.addTab("Original", basicUI);
            tabbedPane.addTab("Symmetric", symmetricUI);
            tabbedPane.addTab("ASymmetric", asymmetricUI);
            tabbedPane.addTab("Hash", hashUI);

        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo giao diện: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Không thể khởi tạo một số giao diện. Vui lòng kiểm tra lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane, BorderLayout.CENTER);
        pack();
        setMinimumSize(new Dimension(800, 600));
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MainView();
            } catch (Exception e) {
                System.err.println("Lỗi khi thiết lập giao diện: " + e.getMessage());
            }
        });
    }
}
//