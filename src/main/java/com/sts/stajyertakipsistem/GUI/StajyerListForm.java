package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.service.StajyerService;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map; // Gerekirse Map için import
import java.util.HashMap; // Gerekirse HashMap için import

public class StajyerListForm extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(StajyerListForm.class.getName());
    // Lütfen tarih formatınızın "YYYY.MM.DD" olduğundan emin olun ve bu satırı buna göre düzeltin.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");


    private StajyerTableModel stajyerTableModel;
    private StajyerService stajyerService;
    private TableRowSorter<StajyerTableModel> sorter;

    private List<Stajyer> allStajyerler;

    // Filtre değerlerini tutmak için alanlar (StajyerFilterDialog'dan dönecek değerler)
    private String currentBolumFilter = null;
    private String currentOkulTuruFilter = null;
    private String currentStajDurumuFilter = null;
    private LocalDate currentBaslangicTarihiMin = null;
    private LocalDate currentBaslangicTarihiMax = null;
    private LocalDate currentBitisTarihiMin = null;
    private LocalDate currentBitisTarihiMax = null;
    private LocalDate currentActiveDateFilter = null; // Yeni: Aktif tarih filtresi değişkeni

    // <<-- YENİ EKLENEN FİLTRE DEĞERLERİ -->>
    private Long currentMinWorkdayFilter = null;
    private Long currentMaxWorkdayFilter = null;
    // <<---------------------------------->>


    public StajyerListForm() {
        initComponents();
        initializeCustomComponents();
        loadAllStajyerData();
        applyFilters();
        this.setLocationRelativeTo(null);
    }

    private void initializeCustomComponents() {
        stajyerTableModel = new StajyerTableModel();
        jTable1.setModel(stajyerTableModel);
        sorter = new TableRowSorter<>(stajyerTableModel);
        jTable1.setRowSorter(sorter);
        stajyerService = new StajyerService();

        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void insertUpdate(DocumentEvent e) { filterTable(); }
        });

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = jTable1.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = jTable1.convertRowIndexToModel(selectedRow);
                        int stajyerId = stajyerTableModel.getStajyerAt(modelRow).getStajyerId();
                        openSpesifikStajyerForm(stajyerId);
                    }
                }
            }
        });

        // filterbutton'a zaten eklenmiş ActionListener var, değişiklik yok.
        filterbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFilterDialog();
            }
        });

        // Diğer buton action listener'ları zaten tanımlı.
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
    }

    private void loadAllStajyerData() {
        try {
            // Burası veritabanından tüm stajyerleri çekmeye devam ediyor.
            // Eğer veritabanı tabanlı filtrelemeye geçilirse bu metod değişir.
            allStajyerler = stajyerService.getAllStajyerler();
            if (allStajyerler == null) {
                allStajyerler = new ArrayList<>();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Tüm stajyer verileri yüklenirken hata.", e);
            JOptionPane.showMessageDialog(this, "Tüm veriler yüklenemedi: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            allStajyerler = new ArrayList<>();
        }
    }

    // Bu metodun içeriği önceki yanıtımda verdiğim "active date" filtreleme mantığıyla güncel kalmalıdır.
    private void filterTable() {
        String searchText = jTextField1.getText().trim();
        LocalDate filterDate = null;

        if (!searchText.isEmpty()) {
            try {
                // jTextField1'deki metni tarih olarak ayrıştırmayı dene
                filterDate = LocalDate.parse(searchText, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                // Metin geçerli bir tarih değil, genel metin aramasına devam et
                filterDate = null;
            }
        }

        if (filterDate != null) {
            // Geçerli bir tarih girildiyse, aktif staj filtrelemesini uygula
            final LocalDate finalFilterDate = filterDate; // Lambda ifadesi içinde kullanabilmek için final olmalı
            sorter.setRowFilter(new RowFilter<StajyerTableModel, Integer>() {
                @Override
                public boolean include(RowFilter.Entry<? extends StajyerTableModel, ? extends Integer> entry) {
                    Stajyer stajyer = entry.getModel().getStajyerAt(entry.getIdentifier());

                    LocalDate baslangic = stajyer.getStajBaslangicTarihi();
                    LocalDate bitis = stajyer.getStajBitisTarihi();

                    // Bir staj, verilen tarihte (finalFilterDate) aktifse:
                    // 1. Başlangıç tarihi null değil VE verilen tarihten sonra değil (yani eşit veya önce)
                    // 2. Bitiş tarihi null değil VE verilen tarihten önce değil (yani eşit veya sonra)
                    return baslangic != null && !baslangic.isAfter(finalFilterDate) &&
                           bitis != null && !bitis.isBefore(finalFilterDate);
                }
            });
        } else {
            // Geçerli bir tarih girilmediyse, mevcut metin bazlı filtrelemeyi uygula
            if (searchText.length() == 0) {
                sorter.setRowFilter(null); // Arama kutusu boşsa tüm filtreyi kaldır
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Büyük/küçük harf duyarsız metin arama
            }
        }
    }

    // openFilterDialog() metodunun güncellenmiş hali
    private void openFilterDialog() {
        Set<String> uniqueBolumler = new HashSet<>();
        Set<String> uniqueStajDurumlari = new HashSet<>();

        // Mevcut allStajyerler listesinden benzersiz değerleri topluyoruz.
        // Eğer veritabanı tabanlı filtrelemeye geçilirse bu kısım değişebilir (örneğin servisten benzersiz bölümleri çekmek).
        for (Stajyer stajyer : allStajyerler) {
            if (stajyer.getBolum() != null && !stajyer.getBolum().isEmpty()) {
                uniqueBolumler.add(stajyer.getBolum());
            }
            // Stajyer modelinizde getStajDurumu() metodu varsa bu kısmı aktif edin.
            // if (stajyer.getStajDurumu() != null && !stajyer.getStajDurumu().isEmpty()) {
            //      uniqueStajDurumlari.add(stajyer.getStajDurumu());
            // }
        }

        StajyerFilterDialog filterDialog = new StajyerFilterDialog(this, stajyerService,
                                                                     new ArrayList<>(uniqueBolumler),
                                                                     new ArrayList<>(uniqueStajDurumlari));

        // Diyalog açılmadan önce, mevcut filtre değerlerini diyaloga gönderir
        filterDialog.setInitialFilters(currentBolumFilter, currentOkulTuruFilter, currentStajDurumuFilter,
                                        currentBaslangicTarihiMin, currentBaslangicTarihiMax,
                                        currentBitisTarihiMin, currentBitisTarihiMax,
                                        currentActiveDateFilter,
                                        currentMinWorkdayFilter, currentMaxWorkdayFilter); // <<-- YENİ İŞ GÜNÜ PARAMETRELERİ EKLENDİ -->>

        filterDialog.setVisible(true); // Diyaloğu göster (modal olduğu için burada kod bloklanır)

        // Diyalog kapandıktan sonra bu kod çalışır.
        if (filterDialog.isApplyFilterConfirmed()) {
            // Diyalogdan filtre değerlerini alır
            currentBolumFilter = filterDialog.getBolumFilter();
            currentOkulTuruFilter = filterDialog.getOkulTuruFilter();
            currentStajDurumuFilter = filterDialog.getStajDurumuFilter();

            currentActiveDateFilter = filterDialog.getActiveDateFilter();

            if (currentActiveDateFilter != null) {
                // Aktif tarih filtresi ayarlandıysa, diğer tarih aralıklarını sıfırla
                currentBaslangicTarihiMin = null;
                currentBaslangicTarihiMax = null;
                currentBitisTarihiMin = null;
                currentBitisTarihiMax = null;
            } else {
                // Aktif tarih filtresi yoksa, diğer tarih aralıklarını al
                currentBaslangicTarihiMin = filterDialog.getBaslangicTarihiMin();
                currentBaslangicTarihiMax = filterDialog.getBaslangicTarihiMax();
                currentBitisTarihiMin = filterDialog.getBitisTarihiMin();
                currentBitisTarihiMax = filterDialog.getBitisTarihiMax();
            }

            // <<-- YENİ EKLENEN: İş Günü Filtrelerini al ve güncelle -->>
            currentMinWorkdayFilter = filterDialog.getMinWorkdayResult();
            currentMaxWorkdayFilter = filterDialog.getMaxWorkdayResult();
            // <<-------------------------------------------------------->>

            applyFilters(); // Yeni filtrelerle tabloyu günceller
        }
    }

    // applyFilters() metodunun güncellenmiş hali (GUI tabanlı filtreleme)
    private void applyFilters() {
        List<Stajyer> filteredList = new ArrayList<>();

        for (Stajyer stajyer : allStajyerler) {
            boolean matches = true;

            // Bölüm Filtresi
            if (currentBolumFilter != null && !currentBolumFilter.equals("Tüm Bölümler")) {
                if (stajyer.getBolum() == null || !stajyer.getBolum().equalsIgnoreCase(currentBolumFilter)) {
                    matches = false;
                }
            }

            // Okul Türü Filtresi
            if (matches && currentOkulTuruFilter != null && !currentOkulTuruFilter.equals("Tümü")) {
                if (stajyer.getOkul() == null || stajyer.getOkul().getOkulTuru() == null) {
                    matches = false;
                } else {
                    String okulTuru = stajyer.getOkul().getOkulTuru().toLowerCase(Locale.getDefault());
                    String filter = currentOkulTuruFilter.toLowerCase(Locale.getDefault());

                    if (filter.equals("üniversite")) {
                        if (!(okulTuru.contains("üniversite") || okulTuru.contains("üni") || okulTuru.contains("uni") || okulTuru.contains("u.n.i."))) {
                            matches = false;
                        }
                    } else if (!okulTuru.equalsIgnoreCase(filter)) {
                        matches = false;
                    }
                }
            }

            // Staj Durumu Filtresi (Eğer Stajyer modelinizde getStajDurumu() metodu varsa bu kısmı aktif edin.)
            // if (matches && currentStajDurumuFilter != null && !currentStajDurumuFilter.equals("Tümü")) {
            //      if (stajyer.getStajDurumu() == null || !stajyer.getStajDurumu().equalsIgnoreCase(currentStajDurumuFilter)) {
            //          matches = false;
            //      }
            // }

            // YENİ: Aktif Tarih Filtresi öncelikli
            if (matches && currentActiveDateFilter != null) {
                LocalDate baslangic = stajyer.getStajBaslangicTarihi();
                LocalDate bitis = stajyer.getStajBitisTarihi();

                if (!(baslangic != null && !baslangic.isAfter(currentActiveDateFilter) &&
                      bitis != null && !bitis.isBefore(currentActiveDateFilter))) {
                    matches = false;
                }
            } else {
                // Sadece Aktif Tarih Filtresi AYARLI DEĞİLSE, diğer tarih aralığı filtrelerini uygula
                // Başlangıç Tarihi Aralığı Filtresi
                if (matches && currentBaslangicTarihiMin != null) {
                    if (stajyer.getStajBaslangicTarihi() == null || stajyer.getStajBaslangicTarihi().isBefore(currentBaslangicTarihiMin)) {
                        matches = false;
                    }
                }
                if (matches && currentBaslangicTarihiMax != null) {
                    if (stajyer.getStajBaslangicTarihi() == null || stajyer.getStajBaslangicTarihi().isAfter(currentBaslangicTarihiMax)) {
                        matches = false;
                    }
                }

                // Bitiş Tarihi Aralığı Filtresi
                if (matches && currentBitisTarihiMin != null) {
                    if (stajyer.getStajBitisTarihi() == null || stajyer.getStajBitisTarihi().isBefore(currentBitisTarihiMin)) {
                        matches = false;
                    }
                }
                if (matches && currentBitisTarihiMax != null) {
                    if (stajyer.getStajBitisTarihi() == null || stajyer.getStajBitisTarihi().isAfter(currentBitisTarihiMax)) {
                        matches = false;
                    }
                }
            }

            
           if (matches && currentMinWorkdayFilter != null) {
    Long hesaplananIsGunu = stajyer.getHesaplananIsGunu();
    // First, check if hesaplananIsGunu is null.
    // If it is null, or if it's less than the filter, set matches to false.
    if (hesaplananIsGunu == null || hesaplananIsGunu < currentMinWorkdayFilter) {
        matches = false;
    }
}

if (matches && currentMaxWorkdayFilter != null) {
    Long hesaplananIsGunu = stajyer.getHesaplananIsGunu(); // Get it again or reuse if scoped
    // First, check if hesaplananIsGunu is null.
    // If it is null, or if it's greater than the filter, set matches to false.
    if (hesaplananIsGunu == null || hesaplananIsGunu > currentMaxWorkdayFilter) {
        matches = false;
    }
}


            if (matches) {
                filteredList.add(stajyer);
            }
        }
        stajyerTableModel.setStajyerList(filteredList);
        
        filterTable();
    }

    private void openSpesifikStajyerForm(int stajyerId) {
        JFrame detailFrame = new JFrame(stajyerId == 0 ? "Yeni Stajyer Ekle" : "Stajyer Bilgilerini Düzenle");
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Runnable onSaveCallback = this::loadAllStajyerDataAndApplyFilters;

        SpesifikStajyerForm spesifikFormPanel = new SpesifikStajyerForm(stajyerId, onSaveCallback);

        detailFrame.getContentPane().add(spesifikFormPanel);
        detailFrame.pack();
        detailFrame.setLocationRelativeTo(this);
        detailFrame.setVisible(true);
    }

    private void deleteSelectedStajyer() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz stajyeri seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Seçili stajyeri silmek istediğinizden emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = jTable1.convertRowIndexToModel(selectedRow);
            int stajyerId = stajyerTableModel.getStajyerAt(modelRow).getStajyerId();

            if (stajyerId > 0) {
                if (stajyerService.deleteStajyer(stajyerId)) {
                    JOptionPane.showMessageDialog(this, "Stajyer başarıyla silindi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    loadAllStajyerDataAndApplyFilters();
                } else {
                    JOptionPane.showMessageDialog(this, "Stajyer silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seçilen stajyerin ID'si geçersiz.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAllStajyerDataAndApplyFilters() {
        loadAllStajyerData();
        applyFilters();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollBar1 = new javax.swing.JScrollBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        filterbutton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("STAJYER TAKİP SİSTEMİ");

        jScrollBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Ad Soyad", "Adres", "Telefon No", "IBAN No", "Doğum Tarihi", "Okul", "Referans", "T.C. Kimlik No", "Başlangıç Tarihi", "Bitiş Tarihi", "Bölüm", "Sınıf", "Okul Türü", "İş Günü"
            }
        ));
        jTable1.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Ara");

        jButton2.setText("Çıkış Yap");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Ekle");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Sil");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Yenile");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        filterbutton.setText("Filtrele");
        filterbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterbuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(66, 66, 66)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(filterbutton)
                        .addGap(27, 27, 27)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(104, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(72, 72, 72))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(filterbutton))
                        .addGap(71, 71, 71)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
       filterTable();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
          openSpesifikStajyerForm(0);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
                         deleteSelectedStajyer();          
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
          currentBolumFilter = null;
        currentOkulTuruFilter = null;
        currentStajDurumuFilter = null;
        currentBaslangicTarihiMin = null;
        currentBaslangicTarihiMax = null;
        currentBitisTarihiMin = null;
        currentBitisTarihiMax = null;
        loadAllStajyerDataAndApplyFilters();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void filterbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterbuttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterbuttonActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton filterbutton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
