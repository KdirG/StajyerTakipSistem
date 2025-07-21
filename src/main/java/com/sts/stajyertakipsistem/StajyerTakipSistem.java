 package com.sts.stajyertakipsistem;

import com.sts.stajyertakipsistem.GUI.LoginPanel;
import com.sts.stajyertakipsistem.GUI.StajyerListForm; // StajyerListForm'u import edin
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Uygulamanın ana başlangıç noktası.
 * Kullanıcı girişini yönetir ve başarılı girişte StajyerListForm'u başlatır.
 */
public class StajyerTakipSistem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Giriş Ekranı için bir JFrame oluşturun
            JFrame loginFrame = new JFrame("Stajyer Takip Sistemi - Giriş");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // loginFrame.setSize(400, 300); // İhtiyacınıza göre boyutlandırın
            loginFrame.setLocationRelativeTo(null); // Ekranın ortasında başlat

            // 2. LoginPanel'i oluşturun
            LoginPanel loginPanel = new LoginPanel();

            // 3. LoginPanel'e ebeveyn frame'i (loginFrame) set edin
            // Bu, LoginPanel'in kendisini kapatmak istediğinde hangi JFrame'i kapatacağını bilmesini sağlar.
            loginPanel.setParentFrame(loginFrame);

            // 4. LoginPanel'i loginFrame'e ekleyin
            loginFrame.add(loginPanel);

            // 5. Frame'i, eklenen panelin tercih edilen boyutlarına göre paketleyin
            // Bu, bileşenlerin düzenlenmesinden sonra en uygun boyutu bulmasını sağlar.
            loginFrame.pack();

            // 6. loginFrame'i görünür yapın
            loginFrame.setVisible(true);

            // Buraya başka bir kod eklemeye gerek yok.
            // Başarılı giriş yapıldığında, LoginPanel içindeki jButton1ActionPerformed metodu
            // loginFrame'i kapatıp StajyerListForm'u açacaktır.
        });
    }
}