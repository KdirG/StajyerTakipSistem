/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sts.stajyertakipsistem;
import com.sts.stajyertakipsistem.GUI.LoginPanel; // LoginPanel'i import ediyoruz
import javax.swing.JFrame; // JFrame'i import ediyoruz
import javax.swing.SwingUtilities; 


/**
 *
 * @author kadir
 */
public class StajyerTakipSistem {

    public static void main(String[] args) {
        // MainApp'ten alınan LoginPanel başlatma kodları buraya kopyalandı
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Stajyer Takip Sistemi - Giriş");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setResizable(false); // Pencere boyutunun değiştirilmesini engelle

            LoginPanel loginPanel = new LoginPanel();
            loginPanel.setParentFrame(loginFrame); // LoginPanel'e JFrame referansını ver

            loginFrame.add(loginPanel); // Paneli frame'e ekle
            loginFrame.pack(); // Bileşenlerin boyutlarına göre frame'i ayarla
            loginFrame.setLocationRelativeTo(null); // Pencereyi ekranın ortasında aç
            loginFrame.setVisible(true); // Pencereyi görünür yap
        });

        // Eğer StajyerTakipSistem'in başka başlangıç mantığı varsa buraya eklenebilir.
        // Şimdilik sadece LoginPanel'i başlatacak şekilde.
    }
}
