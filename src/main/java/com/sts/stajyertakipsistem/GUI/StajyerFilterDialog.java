package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.service.StajyerService;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class StajyerFilterDialog extends JDialog {

    // Tarih formatımız YYYY.MM.DD
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

     // Bu Tarihte Aktif Staj filtresi için

    // Filtre değerlerini geri döndürmek için kullanılacak değişkenler
    private String bolumFilterResult;
    private String okulTuruFilterResult;
    private String stajDurumuFilterResult; // Eğer cmbStajDurumu kullanılıyorsa
    private LocalDate baslangicTarihiMinResult;
    private LocalDate baslangicTarihiMaxResult;
    private LocalDate bitisTarihiMinResult;
    private LocalDate bitisTarihiMaxResult;
    private LocalDate activeDateFilterResult; // Yeni aktif tarih filtresi sonucu
        private Long minWorkdayResult;
        private Long maxWorkdayResult;
    private boolean applyFilterConfirmed = false;

    private StajyerService stajyerService;
    private List<String> uniqueBolumler;
    private List<String> uniqueStajDurumlari;

    public StajyerFilterDialog(JFrame parent, StajyerService service,
                               List<String> uniqueBolumler, List<String> uniqueStajDurumlari) {
        super(parent, "Filtreleme Seçenekleri", true);
        this.stajyerService = service;
        this.uniqueBolumler = uniqueBolumler;
        this.uniqueStajDurumlari = uniqueStajDurumlari;

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
        jPanel6 = new javax.swing.JPanel();
        applybutton = new javax.swing.JButton();
        btnClearFilters = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtMinWorkday = new javax.swing.JTextField();
        txtMaxWorkday = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

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

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox4);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox1);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        minendtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minendtxtActionPerformed(evt);
            }
        });

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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(minendtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        minbegintxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minbegintxtActionPerformed(evt);
            }
        });

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
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(minbegintxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        jPanel6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        applybutton.setText("Uygula");
        applybutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applybuttonActionPerformed(evt);
            }
        });

        btnClearFilters.setText("Temizle");

        btnCancel.setText("İptal");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(applybutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearFilters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applybutton)
                .addGap(16, 16, 16))
        );

        jPanel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel9.setText("İş Günü");

        txtMinWorkday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMinWorkdayActionPerformed(evt);
            }
        });

        txtMaxWorkday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxWorkdayActionPerformed(evt);
            }
        });

        jLabel10.setText("Minimum");

        jLabel11.setText("Maksimum");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtMinWorkday, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(txtMaxWorkday, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(19, 19, 19))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(14, 14, 14)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMinWorkday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaxWorkday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

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
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10))
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

    private void minendtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minendtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minendtxtActionPerformed

    private void minbegintxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minbegintxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minbegintxtActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtMinWorkdayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinWorkdayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMinWorkdayActionPerformed

    private void txtMaxWorkdayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxWorkdayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaxWorkdayActionPerformed

 private void setupListeners() {
        applybutton.addActionListener(this::applyFiltersAndClose);
        btnClearFilters.addActionListener(e -> clearFilters());
        btnCancel.addActionListener(e -> dispose());
    }

    private void populateComboBoxes() {
        jComboBox4.removeAllItems();
        jComboBox4.addItem("Tüm Bölümler");
        if (uniqueBolumler != null) {
            uniqueBolumler.forEach(jComboBox4::addItem);
        }

        jComboBox1.removeAllItems();
        jComboBox1.addItem("Tümü");
        jComboBox1.addItem("Üniversite");
        jComboBox1.addItem("Lise");
        // Eğer başka okul türleri varsa buraya ekleyebilirsiniz.
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

        // <<-- YENİ EKLENEN KISIM: İş Günü Filtreleri -->>
        try {
            minWorkdayResult = parseLong(txtMinWorkday.getText());
            maxWorkdayResult = parseLong(txtMaxWorkday.getText());
            
            // Min > Max kontrolü
            if (minWorkdayResult != null && maxWorkdayResult != null && minWorkdayResult > maxWorkdayResult) {
                 JOptionPane.showMessageDialog(this, "Minimum iş günü, maksimum iş gününden büyük olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                 minWorkdayResult = null; // Hatalı durumu sıfırla
                 maxWorkdayResult = null;
                 return; // Hata durumunda işlemi durdur ve dialog'u kapatma
            }

        } catch (DateTimeParseException | NumberFormatException ex) { // DateTimeParseException'ı yakalamaya gerek yok, parseLong'da zaten NumberFormatException yakalanır.
            JOptionPane.showMessageDialog(this, "İş günü değeri sayı formatında olmalı.", "Hata", JOptionPane.ERROR_MESSAGE);
            minWorkdayResult = null; // Hatalı durumu sıfırla
            maxWorkdayResult = null;
            return; // Hata durumunda işlemi durdur ve dialog'u kapatma
        }
        // <<---------------------------------------------->

        applyFilterConfirmed = true;
        dispose();
    }

    private void clearFilters() {
        jComboBox4.setSelectedItem("Tüm Bölümler");
        jComboBox1.setSelectedItem("Tümü");

        // Tarih alanlarını temizle
        minbegintxt.setText("");
        maxbegintxt.setText("");
        minendtxt.setText("");
        maxendtxt.setText("");
        jTextField1.setText(""); // jTextField1'i de temizle

        bolumFilterResult = null;
        okulTuruFilterResult = null;
        // if (cmbStajDurumu != null) cmbStajDurumu.setSelectedItem("Tümü"); // Eğer cmbStajDurumu varsa bu yorumu kaldırın
        stajDurumuFilterResult = null;
        baslangicTarihiMinResult = null;
        baslangicTarihiMaxResult = null;
        bitisTarihiMinResult = null;
        bitisTarihiMaxResult = null;
        activeDateFilterResult = null; // Aktif tarih filtresi sonucunu da sıfırla
    }

    /**
     * Verilen tarih stringini "yyyy.MM.dd" formatında parse ederek LocalDate nesnesine çevirir.
     * Geçersiz format durumunda null döner ve kullanıcıya uyarı mesajı gösterir.
     */
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı: " + dateString + ". Lütfen YYYY.MM.DD formatını kullanın.", "Tarih Formatı Hatası", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    private Long parseLong(String longStr) throws NumberFormatException {
        if (longStr == null || longStr.trim().isEmpty()) {
            return null; // Boş veya sadece boşluk içeren bir string ise null döndür
        }
        return Long.parseLong(longStr.trim()); // String'i Long'a çevir
    }
     public Long getMinWorkdayResult() {
        return minWorkdayResult;
    }
    public Long getMaxWorkdayResult() {
        return maxWorkdayResult;
    } 
    // Getter Metotları
    public String getBolumFilter() {
        return (bolumFilterResult != null && !bolumFilterResult.equals("Tüm Bölümler")) ? bolumFilterResult : null;
    }

    public String getOkulTuruFilter() {
        return (okulTuruFilterResult != null && !okulTuruFilterResult.equals("Tümü")) ? okulTuruFilterResult : null;
    }

    public String getStajDurumuFilter() {
        // Eğer cmbStajDurumu bileşeni gerçekten varsa ve kullanılıyorsa, bu metodu doldurun.
        // Aksi takdirde null dönecektir.
        return stajDurumuFilterResult;
    }
    public LocalDate getBaslangicTarihiMin() { return baslangicTarihiMinResult; }
    public LocalDate getBaslangicTarihiMax() { return baslangicTarihiMaxResult; }
    public LocalDate getBitisTarihiMin() { return bitisTarihiMinResult; }
    public LocalDate getBitisTarihiMax() { return bitisTarihiMaxResult; }
    public LocalDate getActiveDateFilter() { return activeDateFilterResult; } // Yeni getter

    public boolean isApplyFilterConfirmed() {
        return applyFilterConfirmed;
    }

    /**
     * Diyaloğu mevcut filtre değerleriyle başlatır.
     * StajyerListForm'dan gelen mevcut filtre değerlerini GUI bileşenlerine yansıtır.
     */
    public void setInitialFilters(String bolum, String okulTuru, String durum,
                                  LocalDate basMin, LocalDate basMax, LocalDate bitMin, LocalDate bitMax,
                                  LocalDate activeDate,
                                  Long minWorkday, Long maxWorkday) {

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

        
        if (activeDate != null) {
            jTextField1.setText(activeDate.format(DATE_FORMATTER));
            minbegintxt.setText("");
            maxbegintxt.setText("");
            minendtxt.setText("");
            maxendtxt.setText("");
        } else {
            jTextField1.setText("");

            minbegintxt.setText(basMin != null ? basMin.format(DATE_FORMATTER) : "");
            maxbegintxt.setText(basMax != null ? basMax.format(DATE_FORMATTER) : "");
            minendtxt.setText(bitMin != null ? bitMin.format(DATE_FORMATTER) : "");
            maxendtxt.setText(bitMax != null ? bitMax.format(DATE_FORMATTER) : "");
        }

       
        if (txtMinWorkday != null) {
            txtMinWorkday.setText(minWorkday != null ? String.valueOf(minWorkday) : "");
        }
        if (txtMaxWorkday != null) {
            txtMaxWorkday.setText(maxWorkday != null ? String.valueOf(maxWorkday) : "");
        }
    }

    /**
     * ComboBox'ın belirli bir öğeyi içerip içermediğini kontrol eder.
     */
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClearFilters;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField maxbegintxt;
    private javax.swing.JTextField maxendtxt;
    private javax.swing.JTextField minbegintxt;
    private javax.swing.JTextField minendtxt;
    private javax.swing.JTextField txtMaxWorkday;
    private javax.swing.JTextField txtMinWorkday;
    // End of variables declaration//GEN-END:variables
}
