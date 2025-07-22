package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.service.StajyerService;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;

public class StajyerFilterDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // GUI Builder tarafından otomatik oluşturulan bileşen deklarasyonları:
    // Sizin initComponents() metodunuzda tanımlı olan bileşenleri buraya ekliyorum.
   
   

    // Filtre değerlerini geri döndürmek için alanlar
    private String bolumFilterResult;
    private String okulTuruFilterResult;
    // Diğer filtre sonuçları (Staj Durumu, Tarih alanları) ilgili bileşenler GUI'de tanımlı olmadığı için null kalacak.
    private String stajDurumuFilterResult = null;
    private LocalDate baslangicTarihiMinResult = null;
    private LocalDate baslangicTarihiMaxResult = null;
    private LocalDate bitisTarihiMinResult = null;
    private LocalDate bitisTarihiMaxResult = null;

    private boolean applyFilterConfirmed = false;

    private StajyerService stajyerService;
    private List<String> uniqueBolumler;
    private List<String> uniqueStajDurumlari; // Stajyer modelinizde stajDurumu alanı yoksa bu kullanılmaz

    // Constructor güncellendi: unique listeleri parametre olarak alacak
    public StajyerFilterDialog(JFrame parent, StajyerService service,
                               List<String> uniqueBolumler, List<String> uniqueStajDurumlari) {
        super(parent, "Filtreleme Seçenekleri", true);
        this.stajyerService = service;
        this.uniqueBolumler = uniqueBolumler;
        this.uniqueStajDurumlari = uniqueStajDurumlari; // Eğer Stajyer modelinizde stajDurumu alanı yoksa yorumda kalsın

        initComponents(); // GUI Builder tarafından oluşturulan metot çağrısı
        setupListeners(); // Kendi listener'larımızı ekliyoruz
        populateComboBoxes(); // ComboBox'ları veri ile dolduruyoruz
        this.pack();
        this.setLocationRelativeTo(parent);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        applybutton = new javax.swing.JButton();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        applybutton.setText("Uygula");
        applybutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applybuttonActionPerformed(evt);
            }
        });
        jPanel2.add(applybutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, -1, -1));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 30, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, -1, -1));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void applybuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applybuttonActionPerformed
        applyFiltersAndClose(evt);
    }//GEN-LAST:event_applybuttonActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
      
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

  private void setupListeners() {
        // applybutton'a zaten initComponents() içinde listener eklendi, tekrar eklemeye gerek yok.
        // Diğer butonlar (btnClearFilters, btnCancel) GUI'de tanımlı olmadığı için yorum satırı yapıldı.
        // Eğer bu butonları GUI Builder'da eklediyseniz, buradaki yorumları kaldırın ve değişken adlarını doğru kullanın.
        // if (btnClearFilters != null) btnClearFilters.addActionListener(e -> clearFilters());
        // if (btnCancel != null) btnCancel.addActionListener(e -> {
        //     applyFilterConfirmed = false;
        //     dispose();
        // });
    }

    private void populateComboBoxes() {
        // jComboBox4 (Bölüm)
        jComboBox4.removeAllItems();
        jComboBox4.addItem("Tüm Bölümler");
        if (uniqueBolumler != null) {
            for (String bolum : uniqueBolumler) {
                jComboBox4.addItem(bolum);
            }
        }

        // jComboBox1 (Okul Türü)
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Tümü");
        jComboBox1.addItem("Lise");
        jComboBox1.addItem("Üniversite");


        // Staj Durumu ComboBox'ı (GUI'de tanımlı değilse bu kısım çalışmaz)
        /*
        if (cmbStajDurumu != null) {
            cmbStajDurumu.removeAllItems();
            cmbStajDurumu.addItem("Tümü");
            if (uniqueStajDurumlari != null) {
                for (String durum : uniqueStajDurumlari) {
                    cmbStajDurumu.addItem(durum);
                }
            }
        }
        */
    }

    private void applyFiltersAndClose(ActionEvent e) {
        bolumFilterResult = (String) jComboBox4.getSelectedItem(); // Bölüm filtresi
        okulTuruFilterResult = (String) jComboBox1.getSelectedItem(); // Okul Türü filtresi

        // Diğer filtre sonuçları (Staj Durumu, Tarih alanları) GUI'de tanımlı olmadığı için null kalacak.
        stajDurumuFilterResult = null;
        baslangicTarihiMinResult = null;
        baslangicTarihiMaxResult = null;
        bitisTarihiMinResult = null;
        bitisTarihiMaxResult = null;

        applyFilterConfirmed = true;
        dispose();
    }

    private void clearFilters() {
        jComboBox4.setSelectedItem("Tüm Bölümler"); // jComboBox4'ü temizliyoruz
        jComboBox1.setSelectedItem("Tümü"); // jComboBox1'i temizliyoruz

        // Diğer bileşenler (Staj Durumu, Tarih alanları) GUI'de tanımlı olmadığı için yorum satırı yapılmıştır.
        /*
        if (cmbStajDurumu != null) cmbStajDurumu.setSelectedItem("Tümü");
        if (txtBaslangicTarihiBaslangic != null) txtBaslangicTarihiBaslangic.setText("");
        if (txtBaslangicTarihiBitis != null) txtBaslangicTarihiBitis.setText("");
        if (txtBitisTarihiBaslangic != null) txtBitisTarihiBaslangic.setText("");
        if (txtBitisTarihiBitis != null) txtBitisTarihiBitis.setText("");
        */

        bolumFilterResult = null;
        okulTuruFilterResult = null;
        stajDurumuFilterResult = null;
        baslangicTarihiMinResult = null;
        baslangicTarihiMaxResult = null;
        bitisTarihiMinResult = null;
        bitisTarihiMaxResult = null;
    }

    // parseDate metodu, tarih alanları kullanılmadığı için şu an çağrılmayacaktır.
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı: " + dateString + ". Lütfen GG.AA.YYYY formatını kullanın.", "Tarih Formatı Hatası", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public String getBolumFilter() {
        return (bolumFilterResult != null && !bolumFilterResult.equals("Tüm Bölümler")) ? bolumFilterResult : null;
    }

    public String getOkulTuruFilter() {
        return (okulTuruFilterResult != null && !okulTuruFilterResult.equals("Tümü")) ? okulTuruFilterResult : null;
    }

    // Diğer getter metotları, ilgili bileşenler tanımlı olmadığı için null dönüyor.
    public String getStajDurumuFilter() { return null; }
    public LocalDate getBaslangicTarihiMin() { return null; }
    public LocalDate getBaslangicTarihiMax() { return null; }
    public LocalDate getBitisTarihiMin() { return null; }
    public LocalDate getBitisTarihiMax() { return null; }

    public boolean isApplyFilterConfirmed() {
        return applyFilterConfirmed;
    }

    // setInitialFilters metodu güncellendi: jComboBox1 ve jComboBox4'ü destekleyecek
    public void setInitialFilters(String bolum, String okulTuru, String durum,
                                  LocalDate basMin, LocalDate basMax, LocalDate bitMin, LocalDate bitMax) {
        if (bolum != null && containsItem(jComboBox4, bolum)) {
            jComboBox4.setSelectedItem(bolum);
        } else {
            jComboBox4.setSelectedItem("Tüm Bölümler");
        }

        if (okulTuru != null && containsItem(jComboBox1, okulTuru)) {
            jComboBox1.setSelectedItem(okulTuru);
        } else {
            jComboBox1.setSelectedItem("Tümü");
        }

        // Diğer bileşenler initComponents() içinde tanımlı olmadığı için yorum satırı yapılmıştır.
        /*
        if (durum != null && cmbStajDurumu != null && containsItem(cmbStajDurumu, durum)) { cmbStajDurumu.setSelectedItem(durum); }
        if (txtBaslangicTarihiBaslangic != null) txtBaslangicTarihiBaslangic.setText(basMin != null ? basMin.format(DATE_FORMATTER) : "");
        if (txtBaslangicTarihiBitis != null) txtBaslangicTarihiBitis.setText(basMax != null ? basMax.format(DATE_FORMATTER) : "");
        if (txtBitisTarihiBaslangic != null) txtBitisTarihiBaslangic.setText(bitMin != null ? bitMin.format(DATE_FORMATTER) : "");
        if (txtBitisTarihiBitis != null) txtBitisTarihiBitis.setText(bitMax != null ? bitMax.format(DATE_FORMATTER) : "");
        */
    }

    private boolean containsItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applybutton;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
