package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.GirisEvrak;
import com.sts.stajyertakipsistem.model.CikisEvrak;
import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.model.Okul; // Okul sınıfını import et
import com.sts.stajyertakipsistem.model.Referans; // Referans sınıfını import et
import com.sts.stajyertakipsistem.service.StajyerService;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import javax.swing.*;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern; 

/**
 *
 * @author kadir
 */
public class SpesifikStajyerForm extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(SpesifikStajyerForm.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Path DOCS_DIR;
    private File currentPdfFile;
    private String currentStajyerId; 
    private Stajyer currentStajyer; 
    private StajyerService stajyerService;
    private EvrakTuru evrakTuru; 
    private Runnable onSaveCallback; 

    public enum EvrakTuru { GIRIS, CIKIS }
    
    // Yeni stajyer eklemek için constructor
    public SpesifikStajyerForm(Runnable onSaveCallback) {
        this(null, null, onSaveCallback); 
    }

    // Mevcut stajyeri düzenlemek veya evraklarını yönetmek için constructor
    public SpesifikStajyerForm(String stajyerId, EvrakTuru evrakTuru, Runnable onSaveCallback) {
        initComponents();
        this.currentStajyerId = stajyerId;
        this.evrakTuru = evrakTuru; 
        this.onSaveCallback = onSaveCallback;

        stajyerService = new StajyerService();

        // Belgeler dizini oluştur veya kontrol et
        DOCS_DIR = Paths.get(System.getProperty("user.dir"), "docs");
        try {
            Files.createDirectories(DOCS_DIR);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Belgeler dizini oluşturulamadı: " + DOCS_DIR.toString(), e);
            JOptionPane.showMessageDialog(this, "Belge kaydetme dizini oluşturulamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // PDF sürükle-bırak paneli kurulumu
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        new DropTarget(jPanel1, new FileDropTargetListener());
        
        // Formun moduna göre başlığı ve evrak panelinin görünürlüğünü ayarla
        if (currentStajyerId == null) {
            // Yeni Stajyer Ekleme Modu
            jLabel2.setText("YENİ STAJYER EKLE");
            // Yeni stajyer eklerken evrak kısımlarını başlangıçta gizle
            jPanel1.setVisible(false);
            jLabel1.setVisible(false);
            jButton1.setText("Kaydet ve Stajyer Oluştur"); 
            clearTextFields(); // Tüm alanları boşalt
        } else {
            // Mevcut Stajyer Düzenleme veya Evrak Yönetimi Modu
            jLabel2.setText("STAJYER BİLGİLERİ DÜZENLE");
            loadStajyerData(); // Mevcut stajyer verilerini yükle ve textfield'lara doldur
            
            if (evrakTuru == null) {
                // Sadece stajyer bilgileri düzenleniyor
                jPanel1.setVisible(false);
                jLabel1.setVisible(false);
                jButton1.setText("Değişiklikleri Kaydet"); 
            } else {
                // Belirli bir evrak türü için açıldı (GİRİŞ veya ÇIKIŞ)
                // Evrak paneli görünür, stajyer bilgileri de düzenlenebilir kalsın
                jPanel1.setVisible(true);
                jLabel1.setVisible(true);
                loadExistingEvrak(); // Mevcut evrağı yükle (varsa)
                jButton1.setText("Evrağı Kaydet"); 
            }
        }
    }
    
    // Tüm textfield'ları boşaltan yardımcı metod (sadece yeni stajyer eklerken kullanılır)
    private void clearTextFields() {
        jTextField1.setText(""); 
        jTextField10.setText("");
        jTextField9.setText("");
        jTextField8.setText("");
        jTextField7.setText("");
        jTextField6.setText(""); // Okul Adı
        jTextField5.setText(""); // Referans Adı Soyadı
        jTextField4.setText(""); // TCKimlikNo
        jTextField3.setText("");
        jTextField2.setText("");
        jTextField12.setText("");
        jTextField13.setText("");
    }

    // Mevcut stajyer bilgilerini yükleyen metod
    // Mevcut stajyer bilgilerini yükleyen metod
    private void loadStajyerData() {
        if (currentStajyerId != null) {
            currentStajyer = stajyerService.getStajyerById(currentStajyerId);
            if (currentStajyer != null) {
                // TextField'lara mevcut verileri doldur
                jTextField1.setText(currentStajyer.getAdSoyad());
                jTextField10.setText(currentStajyer.getAdres());
                jTextField9.setText(currentStajyer.getTelefonNo());
                jTextField8.setText(currentStajyer.getIbanNo());
                jTextField7.setText(currentStajyer.getDogumTarihi() != null ? currentStajyer.getDogumTarihi().format(DATE_FORMATTER) : "");
                
                // Okul objesinden okulAdi'ni çek
                jTextField6.setText(currentStajyer.getOkul() != null ? currentStajyer.getOkul().getOkulAdi() : "");
                
                // Referans objesinden adSoyad'ı çek
                jTextField5.setText(currentStajyer.getReferans() != null ? currentStajyer.getReferans().getAdSoyad() : "");
                
                // TCKimlik long olduğu için String'e çevir
                jTextField4.setText(String.valueOf(currentStajyer.getTcKimlik()));
                
                // Staj başlangıç ve bitiş tarihleri
                jTextField3.setText(currentStajyer.getStajBaslangicTarihi() != null ? currentStajyer.getStajBaslangicTarihi().format(DATE_FORMATTER) : "");
                jTextField2.setText(currentStajyer.getStajBitisTarihi() != null ? currentStajyer.getStajBitisTarihi().format(DATE_FORMATTER) : "");
                
                jTextField12.setText(currentStajyer.getBolum());
                jTextField13.setText(String.valueOf(currentStajyer.getSinif())); 
                
            } else {
                JOptionPane.showMessageDialog(this, "Stajyer bilgileri yüklenirken hata oluştu: Stajyer bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                currentStajyerId = null; // Stajyer bulunamazsa yeni stajyer moduna geç
                currentStajyer = null;
                jLabel2.setText("YENİ STAJYER EKLE");
                jButton1.setText("Kaydet ve Stajyer Oluştur");
                clearTextFields(); // Alanları temizle
            }
        }
    }

    // Mevcut evrağı (PDF) yükler ve bir buton olarak panelde gösterir.
    private void loadExistingEvrak() {
        if (evrakTuru == null || currentStajyerId == null) {
            return; 
        }

        jPanel1.removeAll(); 

        Stajyer stajyer = stajyerService.getStajyerById(currentStajyerId);
        if (stajyer != null) {
            Evrak existingEvrak = null;
            if (evrakTuru == EvrakTuru.GIRIS) {
                existingEvrak = stajyer.getGirisEvrak();
            } else if (evrakTuru == EvrakTuru.CIKIS) {
                existingEvrak = stajyer.getCikisEvrak();
            }

            if (existingEvrak != null && existingEvrak.getDosyayolu() != null && !existingEvrak.getDosyayolu().isEmpty()) {
                File existingPdf = new File(existingEvrak.getDosyayolu());
                if (existingPdf.exists() && existingPdf.isFile()) {
                    currentPdfFile = existingPdf; 
                    addPdfButtonToPanel(existingPdf);
                } else {
                    LOGGER.log(Level.WARNING, "Mevcut evrak dosyası bulunamadı veya geçerli değil: " + existingEvrak.getDosyayolu());
                    JOptionPane.showMessageDialog(this, "Mevcut evrak dosyası bulunamadı veya hasarlı! Lütfen yeni bir dosya yükleyin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                     JLabel hintLabel = new JLabel("Lütfen bir PDF dosyası sürükleyip bırakın.");
                    hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    jPanel1.add(hintLabel);
                }
            } else {
                JLabel hintLabel = new JLabel("Lütfen bir PDF dosyası sürükleyip bırakın.");
                hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
                jPanel1.add(hintLabel);
            }
        }
        jPanel1.revalidate();
        jPanel1.repaint();
    }


    // PDF dosyasını temsil eden bir buton ekler
    private void addPdfButtonToPanel(File pdfFile) {
        jPanel1.removeAll(); 

        JButton pdfButton = new JButton(pdfFile.getName(), UIManager.getIcon("FileView.fileIcon"));
        pdfButton.setHorizontalAlignment(SwingConstants.LEFT);
        pdfButton.setToolTipText("Açmak için tıklayın. Yeni dosya sürükleyerek değiştirebilirsiniz.");
        pdfButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Dosya açma işlemi desteklenmiyor!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "PDF açılamadı: " + pdfFile.getAbsolutePath(), ex);
                    JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "PDF açılamadı: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPanel1.add(pdfButton);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    // PDF sürükle-bırak işlevselliği için DropTargetListener
    private class FileDropTargetListener implements DropTargetListener {
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
            // No action needed
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
            // No action needed
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            // EvrakTuru null ise, sürükle-bırakı kabul etme (sadece stajyer bilgisi düzenleme modunda)
            if (evrakTuru == null) {
                JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Bu form stajyer bilgilerini düzenlemek içindir. Evrak yüklemek için lütfen 'Giriş Evrağı' veya 'Çıkış Evrağı' butonlarını kullanın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                dtde.rejectDrop();
                return;
            }

            try {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    List<File> droppedFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                    if (droppedFiles.size() > 1) {
                        JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Lütfen sadece bir dosya sürükleyip bırakın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        dtde.dropComplete(false);
                        return;
                    }

                    File file = droppedFiles.get(0);

                    if (file.getName().toLowerCase().endsWith(".pdf")) {
                        // Eğer stajyerId henüz atanmamışsa (yeni stajyer için), evrak yüklemeyi engelle.
                        // Önce stajyerin kaydedilmesi ve bir ID alması gerekiyor.
                        if (currentStajyerId == null || currentStajyer == null) {
                             JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Önce stajyer bilgilerini kaydedin ve ardından evrağı yüklemek için 'Evrağı Kaydet' butonuna basın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                             dtde.dropComplete(false);
                             return;
                        }
                        
                        // Hedef dosya adını belirle: Stajyer ID + Evrak Türü + UUID.pdf
                        String fileName = currentStajyerId + "_" + evrakTuru.name() + "_" + UUID.randomUUID().toString() + ".pdf";
                        Path targetFilePath = DOCS_DIR.resolve(fileName);

                        try {
                            Files.copy(file.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                            currentPdfFile = targetFilePath.toFile();
                            addPdfButtonToPanel(currentPdfFile);
                            JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "PDF dosyası başarıyla kopyalandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                            dtde.dropComplete(true);
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, "PDF kopyalama hatası: " + file.getAbsolutePath(), e);
                            JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Dosya kopyalama hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                            dtde.dropComplete(false);
                        }
                    } else {
                        JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Sadece PDF dosyası bırakabilirsiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        dtde.dropComplete(false);
                    }
                } else {
                    dtde.rejectDrop();
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                LOGGER.log(Level.SEVERE, "Sürükle-bırak işlemi sırasında hata oluştu.", ex);
                JOptionPane.showMessageDialog(SpesifikStajyerForm.this, "Sürükle-bırak hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                dtde.dropComplete(false);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
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
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator11 = new javax.swing.JSeparator();
        jSeparator13 = new javax.swing.JSeparator();
        jSeparator14 = new javax.swing.JSeparator();
        jSeparator15 = new javax.swing.JSeparator();

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("STAJYER TAKİP SİSTEMİ");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setForeground(new java.awt.Color(153, 153, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 173, Short.MAX_VALUE)
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

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Bitiş Tarihi");

        jTextField2.setText("jTextField2");

        jLabel4.setText("Başlangıç Tarihi:");

        jTextField3.setText("jTextField3");

        jLabel5.setText("T.C: Kimlik No:");

        jTextField4.setText("jTextField4");

        jTextField5.setText("jTextField5");
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel6.setText("Referans:");

        jTextField6.setText("jTextField6");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel7.setText("Okul:");

        jTextField7.setText("jTextField7");
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel8.setText("Doğum Tarihi");

        jLabel9.setText("IBAN No:");

        jTextField8.setText("jTextField8");

        jTextField9.setText("jTextField9");

        jLabel10.setText("Telefon No:");

        jLabel11.setText("Adres:");

        jTextField10.setText("jTextField2");

        jLabel12.setText("Ad Soyad:");

        jTextField12.setText("jTextField12");

        jLabel13.setText("Bölüm:");

        jLabel14.setText("Sınıf:");

        jTextField13.setText("jTextField13");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2)
                                .addGap(56, 56, 56)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator6, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator10, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator11, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator13, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator14)
                                    .addComponent(jSeparator15)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(195, 195, 195))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(57, 57, 57)
                                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addGap(29, 29, 29)
                                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(41, 41, 41)
                                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(65, 65, 65)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(44, 44, 44)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(9, 9, 9)
                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(57, 57, 57)
                                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(194, 194, 194)
                                        .addComponent(jButton1))
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(21, 21, 21))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addContainerGap(264, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            int response = JOptionPane.showConfirmDialog(parentFrame, "Uygulamadan çıkmak istediğinize emin misiniz?", "Çıkış Onayı", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                parentFrame.dispose(); 
            }
        }
                                           

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
   String adSoyad = jTextField1.getText().trim();
        String adres = jTextField10.getText().trim();
        String telefonNo = jTextField9.getText().trim();
        String ibanNo = jTextField8.getText().trim();
        String dogumTarihiStr = jTextField7.getText().trim();
        String okulAdiStr = jTextField6.getText().trim(); 
        String referansAdSoyadStr = jTextField5.getText().trim(); 
        String tcKimlikStr = jTextField4.getText().trim(); // tcKimlik olarak değiştirildi
        String stajBaslangicTarihiStr = jTextField3.getText().trim(); // Field adı güncellendi
        String stajBitisTarihiStr = jTextField2.getText().trim(); // Field adı güncellendi
        String bolum = jTextField12.getText().trim();
        String sinifStr = jTextField13.getText().trim();

        if (evrakTuru == null) { 
            // Stajyer bilgisi kaydetme veya güncelleme
            if (adSoyad.isEmpty() || adres.isEmpty() || telefonNo.isEmpty() || ibanNo.isEmpty() || 
                dogumTarihiStr.isEmpty() || okulAdiStr.isEmpty() || referansAdSoyadStr.isEmpty() || 
                tcKimlikStr.isEmpty() || stajBaslangicTarihiStr.isEmpty() || stajBitisTarihiStr.isEmpty() ||
                bolum.isEmpty() || sinifStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tüm stajyer bilgileri alanları doldurulmalıdır!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dogumTarihi, stajBaslangicTarihi, stajBitisTarihi; // Değişken adları güncellendi
            long tcKimlik; // Değişken adı güncellendi
            int sinif;

            try {
                dogumTarihi = LocalDate.parse(dogumTarihiStr, DATE_FORMATTER);
                stajBaslangicTarihi = LocalDate.parse(stajBaslangicTarihiStr, DATE_FORMATTER); // Değişken adı güncellendi
                stajBitisTarihi = LocalDate.parse(stajBitisTarihiStr, DATE_FORMATTER); // Değişken adı güncellendi
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Tarih formatı geçersiz! Lütfen GG.AA.YYYY formatını kullanın.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                tcKimlik = Long.parseLong(tcKimlikStr); // Değişken adı güncellendi
                // T.C. Kimlik No validasyonu (11 haneli ve pozitif sayı)
                if (String.valueOf(tcKimlik).length() != 11 || tcKimlik <= 0) {
                    JOptionPane.showMessageDialog(this, "T.C. Kimlik No 11 haneli ve pozitif bir sayı olmalıdır.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "T.C. Kimlik No alanı sadece rakam içermelidir.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                sinif = Integer.parseInt(sinifStr);
                if (sinif <= 0) {
                     JOptionPane.showMessageDialog(this, "Sınıf pozitif bir sayı olmalıdır.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                     return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Sınıf alanı sayı olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!telefonNo.matches("^\\d+$")) { 
                 JOptionPane.showMessageDialog(this, "Telefon No sadece rakam içermelidir.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            
            if (ibanNo.length() < 15) { 
                JOptionPane.showMessageDialog(this, "IBAN No geçersiz formatta. En az 15 karakter olmalı.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (stajBaslangicTarihi.isAfter(stajBitisTarihi)) { // Değişken adları güncellendi
                JOptionPane.showMessageDialog(this, "Başlangıç Tarihi, Bitiş Tarihinden sonra olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // OKUL NESNESİNİN OLUŞTURULMASI
            Okul okul = new Okul(0, okulAdiStr, ""); // Varsayılan okulId ve okulTuru ile
            
            // REFERANS NESNESİNİN OLUŞTURULMASI
            // Formda telefonNo ve kurum bilgileri olmadığı için varsayılan değerler kullanıldı
            Referans referans = new Referans(
                UUID.randomUUID().toString(), // referansId için yeni bir UUID üretildi
                referansAdSoyadStr, 
                "", // Telefon No için boş string
                ""  // Kurum için boş string
            ); 

            boolean isNewStajyer = (currentStajyer == null);

            try {
                if (isNewStajyer) {
                    currentStajyer = new Stajyer(
                        UUID.randomUUID().toString(), // stajyerId
                        adSoyad, 
                        adres, 
                        telefonNo, 
                        ibanNo, 
                        dogumTarihi, 
                        stajBaslangicTarihi, // Yeni eklenen tarih alanı
                        stajBitisTarihi,   // Yeni eklenen tarih alanı
                        okul,         
                        referans,     
                        tcKimlik,     // tcKimlik olarak değiştirildi
                        null,         // girisEvrak (şimdilik null)
                        null,         // cikisEvrak (şimdilik null)
                        bolum, 
                        sinif
                    );
                    boolean success = stajyerService.addStajyer(currentStajyer);
                    if (success) {
                        currentStajyerId = currentStajyer.getStajyerId(); // getStajyerId() kullanıldı
                        JOptionPane.showMessageDialog(this, "Yeni stajyer başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Stajyer eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                } else {
                    currentStajyer.setAdSoyad(adSoyad);
                    currentStajyer.setAdres(adres);
                    currentStajyer.setTelefonNo(telefonNo);
                    currentStajyer.setIbanNo(ibanNo);
                    currentStajyer.setDogumTarihi(dogumTarihi);
                    currentStajyer.setStajBaslangicTarihi(stajBaslangicTarihi); // Setter güncellendi
                    currentStajyer.setStajBitisTarihi(stajBitisTarihi);     // Setter güncellendi
                    currentStajyer.setOkul(okul); 
                    
                    // Mevcut referans nesnesini güncelle veya yeni bir referans nesnesi oluştur
                    if (currentStajyer.getReferans() != null) {
                        currentStajyer.getReferans().setAdSoyad(referansAdSoyadStr);
                        // Eğer UI'da referans telefon ve kurum alanları varsa, burada onları da güncelleyin:
                        // currentStajyer.getReferans().setTelefonNo(referansTelefonNoStr);
                        // currentStajyer.getReferans().setKurum(referansKurumStr);
                    } else {
                        // Eğer stajyerin daha önce referansı yoksa, yeni bir referans nesnesi oluştur
                        currentStajyer.setReferans(new Referans(
                            UUID.randomUUID().toString(), // Yeni referans için yeni ID
                            referansAdSoyadStr, 
                            "", // Telefon No (UI'da yoksa boş)
                            ""  // Kurum (UI'da yoksa boş)
                        ));
                    }
                    
                    currentStajyer.setTcKimlik(tcKimlik); // setTcKimlik olarak değiştirildi
                    currentStajyer.setBolum(bolum);
                    currentStajyer.setSinif(sinif);
                    
                    boolean success = stajyerService.updateStajyer(currentStajyer);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Stajyer bilgileri başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Stajyer bilgileri güncellenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Stajyer kaydetme/güncelleme sırasında beklenmeyen hata.", e);
                JOptionPane.showMessageDialog(this, "Stajyer kaydedilirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                return; 
            }
        }

        if (evrakTuru != null) { 
            // Evrak kaydetme veya güncelleme
            if (currentPdfFile == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce bir PDF dosyası sürükleyip bırakın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return; 
            }
            if (currentStajyerId == null || currentStajyer == null) {
                JOptionPane.showMessageDialog(this, "Önce stajyer bilgileri kaydedilmeli!", "Hata", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            String dosyaYolu = currentPdfFile.getAbsolutePath();
            boolean evrakSuccess = false;

            try {
                if (evrakTuru == EvrakTuru.GIRIS) {
                    GirisEvrak girisEvrak;
                    if (currentStajyer.getGirisEvrak() != null) {
                        girisEvrak = currentStajyer.getGirisEvrak();
                        girisEvrak.setDosyayolu(dosyaYolu);
                    } else {
                        girisEvrak = new GirisEvrak(UUID.randomUUID().toString(), dosyaYolu); 
                    }
                    evrakSuccess = stajyerService.updateStajyerEvrak(currentStajyerId, girisEvrak, evrakTuru);
                    if (evrakSuccess) {
                        currentStajyer.setGirisEvrak(girisEvrak); 
                    }
                } else if (evrakTuru == EvrakTuru.CIKIS) {
                    CikisEvrak cikisEvrak;
                    if (currentStajyer.getCikisEvrak() != null) {
                        cikisEvrak = currentStajyer.getCikisEvrak();
                        cikisEvrak.setDosyayolu(dosyaYolu);
                    } else {
                        cikisEvrak = new CikisEvrak(UUID.randomUUID().toString(), dosyaYolu); 
                    }
                    evrakSuccess = stajyerService.updateStajyerEvrak(currentStajyerId, cikisEvrak, evrakTuru);
                    if (evrakSuccess) {
                        currentStajyer.setCikisEvrak(cikisEvrak); 
                    }
                }

                if (evrakSuccess) {
                    JOptionPane.showMessageDialog(this, "Evrak başarıyla kaydedildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    loadExistingEvrak(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Evrak kaydedilirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Evrak kaydetme sırasında beklenmeyen hata.", e);
                JOptionPane.showMessageDialog(this, "Evrak kaydedilirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        if (onSaveCallback != null) {
            onSaveCallback.run(); 
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.dispose(); 
        }
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
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
