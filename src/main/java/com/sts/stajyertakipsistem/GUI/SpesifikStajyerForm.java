package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.Okul;
import com.sts.stajyertakipsistem.model.Referans;
import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.service.StajyerService;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// DEĞİŞİKLİK: JFrame yerine JPanel'den kalıtım alınıyor.
public class SpesifikStajyerForm extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(SpesifikStajyerForm.class.getName());
    private final StajyerService stajyerService;
    private Stajyer currentStajyer;
    private final Runnable onSaveCallback;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    private boolean isEditMode = false;

    public SpesifikStajyerForm(int stajyerId, Runnable onSaveCallback) {
        this.stajyerService = new StajyerService();
        this.onSaveCallback = onSaveCallback;
        initComponents();
        setupDragAndDrop();
        
        if (stajyerId > 0) {
            isEditMode = true;
            loadStajyerData(stajyerId);
        } else {
            isEditMode = false;
            this.currentStajyer = new Stajyer();
            this.currentStajyer.setOkul(new Okul());
            this.currentStajyer.setReferans(new Referans());
        }
    }

    private void loadStajyerData(int stajyerId) {
        this.currentStajyer = stajyerService.getStajyerById(stajyerId);
        if (currentStajyer != null) {
            if (currentStajyer.getOkul() == null) currentStajyer.setOkul(new Okul());
            if (currentStajyer.getReferans() == null) currentStajyer.setReferans(new Referans());
            
            populateFormFields();
        } else {
            JOptionPane.showMessageDialog(this, "Stajyer bilgileri yüklenemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    private void populateFormFields() {
        jTextField1.setText(currentStajyer.getAdSoyad());
        jTextField4.setText(currentStajyer.getTcKimlik());
        jTextField9.setText(currentStajyer.getTelefonNo());
        jTextField8.setText(currentStajyer.getIbanNo());
        jTextField10.setText(currentStajyer.getAdres());
        jTextField12.setText(currentStajyer.getBolum());
        jTextField13.setText(String.valueOf(currentStajyer.getSinif()));
        jTextField7.setText(currentStajyer.getDogumTarihi() != null ? currentStajyer.getDogumTarihi().format(formatter) : "");
        jTextField3.setText(currentStajyer.getStajBaslangicTarihi() != null ? currentStajyer.getStajBaslangicTarihi().format(formatter) : "");
        jTextField2.setText(currentStajyer.getStajBitisTarihi() != null ? currentStajyer.getStajBitisTarihi().format(formatter) : "");
        
        jTextField6.setText(currentStajyer.getOkul().getOkulAdi());
        jTextField11.setText(currentStajyer.getOkul().getOkulTuru());
        jTextField5.setText(currentStajyer.getReferans().getAdSoyad());
         if (currentStajyer.getReferans() != null) {
            jTextField14.setText(currentStajyer.getReferans().getTelefonNo());
            jTextField15.setText(currentStajyer.getReferans().getKurum());
        }    
        if (currentStajyer.getGirisEvrak() != null && currentStajyer.getGirisEvrak().getDosyaYolu() != null) {
            girisEvrakPathLabel.setText(currentStajyer.getGirisEvrak().getDosyaYolu());
        }
        if (currentStajyer.getCikisEvrak() != null && currentStajyer.getCikisEvrak().getDosyaYolu() != null) {
            cikisEvrakPathLabel.setText(currentStajyer.getCikisEvrak().getDosyaYolu());
        }
    }
    
    private boolean validateAndCollectFormData() {
        String adSoyad = jTextField1.getText();
        if (adSoyad == null || adSoyad.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ad Soyad alanı boş bırakılamaz.", "Zorunlu Alan", JOptionPane.WARNING_MESSAGE);
            jTextField1.requestFocus();
            return false;
        }
        currentStajyer.setAdSoyad(adSoyad);
        currentStajyer.setTcKimlik(jTextField4.getText());
        currentStajyer.setTelefonNo(jTextField9.getText());
        currentStajyer.setIbanNo(jTextField8.getText());
        currentStajyer.setAdres(jTextField10.getText());
        currentStajyer.setBolum(jTextField12.getText());

        try {
            if(!jTextField13.getText().trim().isEmpty()) currentStajyer.setSinif(Integer.parseInt(jTextField13.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sınıf alanı geçerli bir sayı olmalıdır.", "Hatalı Giriş", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (!jTextField7.getText().trim().isEmpty()) currentStajyer.setDogumTarihi(LocalDate.parse(jTextField7.getText(), formatter));
            if (!jTextField3.getText().trim().isEmpty()) currentStajyer.setStajBaslangicTarihi(LocalDate.parse(jTextField3.getText(), formatter));
            if (!jTextField2.getText().trim().isEmpty()) currentStajyer.setStajBitisTarihi(LocalDate.parse(jTextField2.getText(), formatter));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Lütfen tarihleri 'gg.aa.yyyy' formatında girin.", "Hatalı Tarih Formatı", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        currentStajyer.getOkul().setOkulAdi(jTextField6.getText());
        currentStajyer.getOkul().setOkulTuru(jTextField11.getText());
        String referansAdSoyad = jTextField5.getText();
        String referansTel = jTextField14.getText(); // Bu alanın referansın telefon numarası olduğu varsayılıyor
        String referansKurum = jTextField15.getText();

         boolean referansBilgisiGirildi = (referansAdSoyad != null && !referansAdSoyad.trim().isEmpty()) ||
                                       (referansTel != null && !referansTel.trim().isEmpty()) ||
                                       (referansKurum != null && !referansKurum.trim().isEmpty());
          if (referansBilgisiGirildi) {
            // Eğer herhangi bir referans bilgisi girildiyse, Ad Soyad ve Telefon No alanları zorunludur.
            if (referansAdSoyad == null || referansAdSoyad.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Referans Ad Soyad alanı boş bırakılamaz.", "Zorunlu Alan", JOptionPane.WARNING_MESSAGE);
                jTextField5.requestFocus();
                return false;
            }
            if (referansTel == null || referansTel.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veritabanı hatasını önlemek için Referans Telefon No alanı zorunludur.", "Zorunlu Alan", JOptionPane.WARNING_MESSAGE);
                jTextField14.requestFocus();
                return false;
            }

            // Stajyerin referans nesnesi null ise (daha önce boş kaydedilmiş olabilir), yeniden oluştur.
            if (currentStajyer.getReferans() == null) {
                currentStajyer.setReferans(new Referans());
            }
            
            // Varsayım: Referans modelinde setTelefonNo() ve setKurum() metodları var.
            currentStajyer.getReferans().setAdSoyad(referansAdSoyad);
            currentStajyer.getReferans().setTelefonNo(referansTel);
            currentStajyer.getReferans().setKurum(referansKurum);

        } else {
            // Hiç referans bilgisi girilmediyse, stajyerin referansını null yap.
            // Bu, veritabanına boş/hatalı bir referans kaydı eklemeye çalışmayı önler.
            currentStajyer.setReferans(null);
        }
        String girisEvrakYolu = girisEvrakPathLabel.getText();
        if (girisEvrakYolu != null && !girisEvrakYolu.trim().isEmpty() && !girisEvrakYolu.equals("...")) {
            if (currentStajyer.getGirisEvrak() == null) currentStajyer.setGirisEvrak(new Evrak());
            currentStajyer.getGirisEvrak().setDosyaYolu(girisEvrakYolu);
        }

        String cikisEvrakYolu = cikisEvrakPathLabel.getText();
        if (cikisEvrakYolu != null && !cikisEvrakYolu.trim().isEmpty() && !cikisEvrakYolu.equals("...")) {
            if (currentStajyer.getCikisEvrak() == null) currentStajyer.setCikisEvrak(new Evrak());
            currentStajyer.getCikisEvrak().setDosyaYolu(cikisEvrakYolu);
        }
        
        return true;
    }
    
    private void saveStajyerAndEvrak() {
        if (!validateAndCollectFormData()) {
            return;
        }

        try {
            if (isEditMode) {
                stajyerService.updateStajyer(currentStajyer);
                JOptionPane.showMessageDialog(this, "Stajyer bilgileri başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                stajyerService.addStajyer(currentStajyer);
                JOptionPane.showMessageDialog(this, "Stajyer başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            // Pencereyi kapatma
            SwingUtilities.getWindowAncestor(this).dispose();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer kaydedilirken bir hata oluştu.", e);
            JOptionPane.showMessageDialog(this, "Stajyer kaydedilirken bir hata oluştu: " + e.getMessage(), "Kayıt Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupDragAndDrop() {
        DropTarget dtGiris = new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                handleDrop(evt, girisEvrakPathLabel);
            }
        };
        girisEvrakPanel.setDropTarget(dtGiris);

        DropTarget dtCikis = new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                handleDrop(evt, cikisEvrakPathLabel);
            }
        };
        cikisEvrakPanel.setDropTarget(dtCikis);
    }

    private void handleDrop(DropTargetDropEvent evt, JLabel pathLabel) {
        try {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            if (!droppedFiles.isEmpty()) {
                File file = droppedFiles.get(0);
                Path targetDir = Paths.get("evraklar");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                Path targetPath = targetDir.resolve(file.getName());
                Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                pathLabel.setText(targetPath.toString());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Dosya sürükle bırak hatası", ex);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        girisEvrakPanel = new javax.swing.JPanel();
        girisEvrakPathLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        cikisEvrakPanel = new javax.swing.JPanel();
        cikisEvrakPathLabel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("STAJYER TAKİP SİSTEMİ");

        girisEvrakPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        girisEvrakPanel.setForeground(new java.awt.Color(153, 153, 0));

        javax.swing.GroupLayout girisEvrakPanelLayout = new javax.swing.GroupLayout(girisEvrakPanel);
        girisEvrakPanel.setLayout(girisEvrakPanelLayout);
        girisEvrakPanelLayout.setHorizontalGroup(
            girisEvrakPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(girisEvrakPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(girisEvrakPathLabel)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        girisEvrakPanelLayout.setVerticalGroup(
            girisEvrakPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(girisEvrakPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(girisEvrakPathLabel)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jLabel1.setText("Evrakları Buraya Sürükleyip Bırakabilirsiniz");

        jButton2.setText("Çıkış Yap");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Kaydet");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Bitiş Tarihi:");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setText("Başlangıç Tarihi:");

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setText("T.C: Kimlik No:");

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel6.setText("Referans:");

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel7.setText("Okul:");

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel8.setText("Doğum Tarihi");

        jLabel9.setText("IBAN No:");

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel10.setText("Telefon No:");

        jLabel11.setText("Adres:");

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });

        jLabel12.setText("Ad Soyad:");

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel13.setText("Bölüm:");

        jLabel14.setText("Sınıf:");

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        cikisEvrakPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout cikisEvrakPanelLayout = new javax.swing.GroupLayout(cikisEvrakPanel);
        cikisEvrakPanel.setLayout(cikisEvrakPanelLayout);
        cikisEvrakPanelLayout.setHorizontalGroup(
            cikisEvrakPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cikisEvrakPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cikisEvrakPathLabel)
                .addContainerGap(96, Short.MAX_VALUE))
        );
        cikisEvrakPanelLayout.setVerticalGroup(
            cikisEvrakPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cikisEvrakPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(cikisEvrakPathLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel15.setText("Okul Türü:");

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });

        jLabel16.setText("Referans No: ");

        jLabel17.setText("Referans Kurum:");

        jTextField14.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField15.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel18.setText("Giriş Evrakları ");

        jLabel19.setText("Çıkış Evrakları");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(62, 62, 62))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16)
                                            .addComponent(jLabel17))
                                        .addGap(8, 8, 8)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField15)
                                            .addComponent(jTextField14)))
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel10))
                                        .addGap(32, 32, 32)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField9)
                                            .addComponent(jTextField10)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel14))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField13, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                            .addComponent(jTextField12)
                                            .addComponent(jTextField3)
                                            .addComponent(jTextField2)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                            .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jTextField11, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jTextField5)
                                            .addComponent(jTextField7))))
                                .addGap(0, 18, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(40, 40, 40)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(girisEvrakPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel18))
                                                .addGap(28, 28, 28)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel19)
                                                    .addComponent(cikisEvrakPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(249, 249, 249))))))
            .addComponent(jSeparator3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButton2))
                .addGap(47, 47, 47)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField10))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField9))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField8))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15))
                                .addGap(10, 10, 10)
                                .addComponent(jLabel6))
                            .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(girisEvrakPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cikisEvrakPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addGap(33, 33, 33)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField15))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jButton1)
                        .addGap(47, 47, 47))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField4))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField3))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(64, 64, 64))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
          SwingUtilities.getWindowAncestor(this).dispose();
                                      

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    saveStajyerAndEvrak();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField11ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cikisEvrakPanel;
    private javax.swing.JLabel cikisEvrakPathLabel;
    private javax.swing.JPanel girisEvrakPanel;
    private javax.swing.JLabel girisEvrakPathLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables



}
