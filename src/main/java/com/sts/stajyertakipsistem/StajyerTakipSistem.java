package com.sts.stajyertakipsistem;

import com.formdev.flatlaf.FlatLightLaf; // FlatLaf importu
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
        // 🔹 FLATLAF TEMA AYARI
        try {
            UIManager.setLookAndFeel(new FlatLightLaf()); // veya new FlatDarkLaf() için koyu tema
        } catch (Exception ex) {
            System.err.println("FlatLaf teması yüklenemedi: " + ex);
        }

        SwingUtilities.invokeLater(() -> {
            // 1. Giriş Ekranı için bir JFrame oluşturun
            JFrame loginFrame = new JFrame("Stajyer Takip Sistemi - Giriş");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLocationRelativeTo(null); // Ekranın ortasında başlat

            // 2. LoginPanel'i oluşturun
            LoginPanel loginPanel = new LoginPanel();

            // 3. LoginPanel'e ebeveyn frame'i set edin
            loginPanel.setParentFrame(loginFrame);

            // 4. LoginPanel'i loginFrame'e ekleyin
            loginFrame.add(loginPanel);

            // 5. Frame'i paketle
            loginFrame.pack();

            // 6. Frame'i görünür yap
            loginFrame.setVisible(true);
        });
    }
}
