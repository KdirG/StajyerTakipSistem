package com.sts.stajyertakipsistem;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.sts.stajyertakipsistem.GUI.LoginPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Uygulamanın ana başlangıç noktası.
 * Kullanıcı girişini yönetir ve başarılı girişte StajyerListForm'u başlatır.
 */
public class StajyerTakipSistem {

    public static void main(String[] args) {
        // 🔹 FLATLAF SOLARIZED LIGHT TEMA AYARI
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf teması yüklenemedi: " + ex);
        }

        // 🔹 GUI Başlatma
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Stajyer Takip Sistemi - Giriş");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLocationRelativeTo(null); // Ortala

            LoginPanel loginPanel = new LoginPanel();
            loginPanel.setParentFrame(loginFrame);

            loginFrame.setContentPane(loginPanel);
            loginFrame.pack();
            loginFrame.setVisible(true);
        });
    }
}
