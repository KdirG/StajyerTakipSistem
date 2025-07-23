package com.sts.stajyertakipsistem;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.sts.stajyertakipsistem.GUI.LoginPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

public class StajyerTakipSistem {

    public static void main(String[] args) {
        try {
            // Önce FlatLaf temasını yükle
            UIManager.setLookAndFeel(new FlatIntelliJLaf());

            // Sonra renkleri override et
            UIManager.put("Button.background", new Color(173, 216, 230));       // Açık mavi
            UIManager.put("Button.focusedBackground", new Color(135, 206, 250)); // Daha parlak açık mavi
            UIManager.put("Button.hoverBackground", new Color(176, 224, 230));   // Hover için açık mavi
            UIManager.put("Panel.background", new Color(240, 248, 255));        // Alice Blue (çok açık mavi)
            UIManager.put("TextField.background", new Color(224, 238, 238));     // Açık gri-mavi ton
            UIManager.put("TextField.foreground", Color.DARK_GRAY);              // Yazı koyu gri
            UIManager.put("Label.foreground", new Color(0, 51, 102));            // Koyu mavi
            UIManager.put("PasswordField.background", new Color(224, 238, 238)); // Şifre alanı
            UIManager.put("PasswordField.foreground", Color.DARK_GRAY);
            UIManager.put("PasswordField.caretForeground", Color.BLUE);

        } catch (Exception ex) {
            System.err.println("FlatLaf teması yüklenemedi: " + ex);
        }

        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Stajyer Takip Sistemi - Giriş");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLocationRelativeTo(null);

            LoginPanel loginPanel = new LoginPanel();
            loginPanel.setParentFrame(loginFrame);

            loginFrame.setContentPane(loginPanel);
            loginFrame.pack();
            loginFrame.setVisible(true);
        });
    }
}
