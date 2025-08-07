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
import java.util.Map; 
import java.util.HashMap; 
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class StajyerListForm extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(StajyerListForm.class.getName());
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.YYYY");
    private JFrame spesifikStajyerJFrame;
    private JFrame spesifikStajyerFormWindow;
    private StajyerTableModel stajyerTableModel;
    private StajyerService stajyerService;
    private TableRowSorter<StajyerTableModel> sorter;

    private List<Stajyer> allStajyerler;

  
    private String currentBolumFilter = null;
    private String currentOkulTuruFilter = null;
    private String currentStajDurumuFilter = null;
    private LocalDate currentBaslangicTarihiMin = null;
    private LocalDate currentBaslangicTarihiMax = null;
    private LocalDate currentBitisTarihiMin = null;
    private LocalDate currentBitisTarihiMax = null;
    private LocalDate currentActiveDateFilter = null; 

    
    private Long currentMinWorkdayFilter = null;
    private Long currentMaxWorkdayFilter = null;
    


    public StajyerListForm() {
        initComponents();
        stajyerService = new StajyerService();
        initializeCustomComponents();
        loadAllStajyerData();
        applyFilters();
        this.setLocationRelativeTo(null);
          jTable1.getTableHeader().setBackground(new Color(173, 216, 230));
    jTable1.getTableHeader().setForeground(Color.DARK_GRAY);
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                Component c = e.getComponent();
                // Sadece JTextField'larda ve Enter'a basıldığında tetikle
                if (c instanceof JTextField) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                    return true; // Olayı tüket, başka bir şey olmasın
                }
            }
            return false; // Olayın standart şekilde devam etmesine izin ver
        }
    });  
          
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

        
        filterbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFilterDialog();
            }
        });

        
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

    
    private void filterTable() {
        String searchText = jTextField1.getText().trim();
        LocalDate filterDate = null;

        if (!searchText.isEmpty()) {
            try {
                
                filterDate = LocalDate.parse(searchText, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                
                filterDate = null;
            }
        }

        if (filterDate != null) {
            
            final LocalDate finalFilterDate = filterDate; 
            sorter.setRowFilter(new RowFilter<StajyerTableModel, Integer>() {
                @Override
                public boolean include(RowFilter.Entry<? extends StajyerTableModel, ? extends Integer> entry) {
                    Stajyer stajyer = entry.getModel().getStajyerAt(entry.getIdentifier());

                    LocalDate baslangic = stajyer.getStajBaslangicTarihi();
                    LocalDate bitis = stajyer.getStajBitisTarihi();

                    
                    
                    
                    return baslangic != null && !baslangic.isAfter(finalFilterDate) &&
                           bitis != null && !bitis.isBefore(finalFilterDate);
                }
            });
        } else {
            
            if (searchText.length() == 0) {
                sorter.setRowFilter(null); 
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); 
            }
        }
    }

    
    private void openFilterDialog() {
        Set<String> uniqueBolumler = new HashSet<>();
        Set<String> uniqueStajDurumlari = new HashSet<>();

        
        
        for (Stajyer stajyer : allStajyerler) {
            if (stajyer.getBolum() != null && !stajyer.getBolum().isEmpty()) {
                uniqueBolumler.add(stajyer.getBolum());
            }
            
            
            
            
        }

        StajyerFilterDialog filterDialog = new StajyerFilterDialog(this, stajyerService,
                                                                     new ArrayList<>(uniqueBolumler),
                                                                     new ArrayList<>(uniqueStajDurumlari));

        
        filterDialog.setInitialFilters(currentBolumFilter, currentOkulTuruFilter, currentStajDurumuFilter,
                                        currentBaslangicTarihiMin, currentBaslangicTarihiMax,
                                        currentBitisTarihiMin, currentBitisTarihiMax,
                                        currentActiveDateFilter,
                                        currentMinWorkdayFilter, currentMaxWorkdayFilter); 

        filterDialog.setVisible(true); 

        
        if (filterDialog.isApplyFilterConfirmed()) {
            
            currentBolumFilter = filterDialog.getBolumFilter();
            currentOkulTuruFilter = filterDialog.getOkulTuruFilter();
            currentStajDurumuFilter = filterDialog.getStajDurumuFilter();

            currentActiveDateFilter = filterDialog.getActiveDateFilter();

            if (currentActiveDateFilter != null) {
                
                currentBaslangicTarihiMin = null;
                currentBaslangicTarihiMax = null;
                currentBitisTarihiMin = null;
                currentBitisTarihiMax = null;
            } else {
                
                currentBaslangicTarihiMin = filterDialog.getBaslangicTarihiMin();
                currentBaslangicTarihiMax = filterDialog.getBaslangicTarihiMax();
                currentBitisTarihiMin = filterDialog.getBitisTarihiMin();
                currentBitisTarihiMax = filterDialog.getBitisTarihiMax();
            }

            
            currentMinWorkdayFilter = filterDialog.getMinWorkdayResult();
            currentMaxWorkdayFilter = filterDialog.getMaxWorkdayResult();
            

            applyFilters(); 
        }
    }

    
    private void applyFilters() {
        List<Stajyer> filteredList = new ArrayList<>();

        for (Stajyer stajyer : allStajyerler) {
            boolean matches = true;

            
            if (currentBolumFilter != null && !currentBolumFilter.equals("Tüm Bölümler")) {
                if (stajyer.getBolum() == null || !stajyer.getBolum().equalsIgnoreCase(currentBolumFilter)) {
                    matches = false;
                }
            }

            
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

            
            
            
            
            
            

            
            if (matches && currentActiveDateFilter != null) {
                LocalDate baslangic = stajyer.getStajBaslangicTarihi();
                LocalDate bitis = stajyer.getStajBitisTarihi();

                if (!(baslangic != null && !baslangic.isAfter(currentActiveDateFilter) &&
                      bitis != null && !bitis.isBefore(currentActiveDateFilter))) {
                    matches = false;
                }
            } else {
                
                
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
    
    
    if (hesaplananIsGunu == null || hesaplananIsGunu < currentMinWorkdayFilter) {
        matches = false;
    }
}

if (matches && currentMaxWorkdayFilter != null) {
    Long hesaplananIsGunu = stajyer.getHesaplananIsGunu(); 
    
    
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
     // Yeni bir JFrame oluştur
    JFrame detailFrame = new JFrame(stajyerId == 0 ? "Yeni Stajyer Ekle" : "Stajyer Bilgilerini Düzenle");
    detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Callback Metodunu Tanımla
    Runnable onSaveCallback = this::loadAllStajyerDataAndApplyFilters;

    // SpesifikStajyerForm JPanel'ini Oluştur
    SpesifikStajyerForm spesifikFormPanel = new SpesifikStajyerForm(stajyerId, onSaveCallback);

    // JPanel'i JFrame'e Ekle
    detailFrame.getContentPane().add(spesifikFormPanel);

    // Pencereyi Boyutlandır ve Konumlandır
    detailFrame.pack();
    detailFrame.setLocationRelativeTo(this);

    // Pencereyi Görünür Yap
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
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        filterbutton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1282, 503));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel1.setText("STAJYER TAKİP SİSTEMİ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 21, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1245;
        gridBagConstraints.ipady = 314;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(71, 21, 0, 21);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 103;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 21, 0, 0);
        getContentPane().add(jTextField1, gridBagConstraints);

        jButton1.setText("Ara");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = -19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 18, 0, 0);
        getContentPane().add(jButton1, gridBagConstraints);

        jButton2.setText("Çıkış Yap");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 345, 16, 0);
        getContentPane().add(jButton2, gridBagConstraints);

        jButton3.setText("Ekle");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 283, 0, 0);
        getContentPane().add(jButton3, gridBagConstraints);

        jButton4.setText("Sil");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 6, 0, 0);
        getContentPane().add(jButton4, gridBagConstraints);

        jButton5.setText("Yenile");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 6, 0, 0);
        getContentPane().add(jButton5, gridBagConstraints);

        filterbutton.setText("Filtrele");
        filterbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterbuttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 18, 0, 0);
        getContentPane().add(filterbutton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.ipadx = 1281;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 0, 0, 0);
        getContentPane().add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.ipadx = 1281;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        getContentPane().add(jSeparator2, gridBagConstraints);

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
        
    }//GEN-LAST:event_filterbuttonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
    }//GEN-LAST:event_jButton1ActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton filterbutton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
