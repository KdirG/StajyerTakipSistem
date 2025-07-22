package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.service.StajyerService;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class StajyerFilterDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

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
    private LocalDate activeDateFilterResult;
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
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        minendtxt = new javax.swing.JTextField();
        maxendtxt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        minbegintxt = new javax.swing.JTextField();
        maxbegintxt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        applybutton = new javax.swing.JButton();

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

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        minendtxt.setText("jTextField1");
        minendtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minendtxtActionPerformed(evt);
            }
        });

        maxendtxt.setText("jTextField1");

        jLabel3.setText("Minimum");

        jLabel4.setText("Maksimum");

        jLabel2.setText("Staj BitişTarihi");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(minendtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxendtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxendtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minendtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        minbegintxt.setText("jTextField1");
        minbegintxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minbegintxtActionPerformed(evt);
            }
        });

        maxbegintxt.setText("jTextField1");

        jLabel5.setText("Minimum");

        jLabel6.setText("Maksimum");

        jLabel7.setText("Staj Başlangıç Tarihi");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(minbegintxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxbegintxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxbegintxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minbegintxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel8.setText("Belirtilen Staj Gününde Aktiflik");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        applybutton.setText("Uygula");
        applybutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applybuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(464, 464, 464)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(applybutton)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 124, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(applybutton)
                        .addGap(20, 20, 20))))
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

    private void minendtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minendtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minendtxtActionPerformed

    private void minbegintxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minbegintxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minbegintxtActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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
    bolumFilterResult = (String) jComboBox4.getSelectedItem();
    okulTuruFilterResult = (String) jComboBox1.getSelectedItem();

    // jTextField1'deki aktif tarih filtresini parse et
    activeDateFilterResult = parseDate(jTextField1.getText());

    if (activeDateFilterResult != null) {
        // Eğer aktif tarih filtresi geçerliyse, diğer tarih aralığı filtrelerini sıfırla
        baslangicTarihiMinResult = null;
        baslangicTarihiMaxResult = null;
        bitisTarihiMinResult = null;
        bitisTarihiMaxResult = null;
    } else {
        // Aktif tarih filtresi yoksa, diğer tarih aralığı filtrelerini kullan
        baslangicTarihiMinResult = parseDate(minbegintxt.getText());
        baslangicTarihiMaxResult = parseDate(maxbegintxt.getText());
        bitisTarihiMinResult = parseDate(minendtxt.getText());
        bitisTarihiMaxResult = parseDate(maxendtxt.getText());
    }

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField maxbegintxt;
    private javax.swing.JTextField maxendtxt;
    private javax.swing.JTextField minbegintxt;
    private javax.swing.JTextField minendtxt;
    // End of variables declaration//GEN-END:variables
}
