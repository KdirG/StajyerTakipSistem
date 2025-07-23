package com.sts.stajyertakipsistem.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files; // java.nio.file.Files import'u eklendi
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap; // Map için
import java.util.List;
import java.util.Map; // Map için
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL; // URL sınıfını import ettik
import java.awt.datatransfer.DataFlavor; // DataFlavor için
import java.awt.datatransfer.UnsupportedFlavorException; // UnsupportedFlavorException için
import java.awt.dnd.DnDConstants; // DnDConstants için
import java.awt.dnd.DropTarget; // DropTarget için
import java.awt.dnd.DropTargetDropEvent; // DropTargetDropEvent için
import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.Referans;
import com.sts.stajyertakipsistem.model.Stajyer; // Stajyer sınıfı için de aynı pakette olduğunu varsayıyorum
import com.sts.stajyertakipsistem.model.Okul;     // Okul sınıfı için de aynı pakette olduğunu varsayıyorum
import com.sts.stajyertakipsistem.service.StajyerService; 
import java.time.DayOfWeek;


public class SpesifikStajyerForm extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(SpesifikStajyerForm.class.getName());
    private final StajyerService stajyerService;
    private Stajyer currentStajyer;
    private final Runnable onSaveCallback;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private boolean isEditMode = false;

    // YENİ: PDF ikonlarını tutacak map
    private Map<String, ImageIcon> pdfIcons;
    // YENİ: Eklenen tüm dosyaları ve onların GUI panellerini takip etmek için map
    // (Aynı dosya birden fazla kez eklenmesini engellemek ve açma kolaylığı için)
    private Map<File, JPanel> addedFilePanelsMap;

    // YENİ: Giriş ve Çıkış evrakları için asıl listeleme panelleri
    // Bu paneller GUI Builder'da eklediğiniz JScrollPane'lerin içine yerleştirilecek.
    private JPanel girisFileListPanel;
    private JPanel cikisFileListPanel;

    // YENİ: İkon boyutu sabiti
    private static final int ICON_SIZE = 24; // Örneğin 24x24 piksel

    // Not: girisEvrakScrollPane ve cikisEvrakScrollPane GUI Builder tarafından
    // initComponents() metodunda tanımlanıp başlatılmalıdır.
    // Eğer butonlarınız varsa, onların isimlerini de kontrol edin ve ActionPerformed metodlarına bağlayın.
    // Örneğin: btnGirisEvrakSec (JButton) ve btnCikisEvrakSec (JButton)

    public SpesifikStajyerForm(int stajyerId, Runnable onSaveCallback) {
        this.stajyerService = new StajyerService();
        this.onSaveCallback = onSaveCallback;
        initComponents(); // GUI Builder tarafından oluşturulan bileşenleri başlatır

        // YENİ: Özel bileşenleri başlat (panelleri oluştur ve scroll panellere bağla)
        initializeCustomComponents();
        // YENİ: PDF ikonlarını yükle (artık tek bir scaled ikon yüklenecek)
        loadPdfIcons();
        // YENİ: AddedFilesMap'i başlat
        addedFilePanelsMap = new HashMap<>();

        // Drag-and-drop kurulumu
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
       private void calculateAndDisplayBusinessDays() {
    LocalDate startDate;
    LocalDate endDate;

    try {
        // JTextField'lardan tarihleri al ve parse et
        startDate = LocalDate.parse(jTextField3.getText().trim(), formatter); // Başlangıç tarihi
        endDate = LocalDate.parse(jTextField2.getText().trim(), formatter);   // Bitiş tarihi
    } catch (DateTimeParseException e) {
        // Geçersiz tarih formatı durumunda hata mesajı göster
        jLabelBusinessDaysResult.setText("Hata: Geçersiz tarih formatı.");
        JOptionPane.showMessageDialog(this,
                "Geçersiz tarih formatı. Lütfen 'YYYY.MM.DD' formatını kullanın.",
                "Tarih Formatı Hatası", JOptionPane.ERROR_MESSAGE);
        
        // Hata durumunda stajyer nesnesindeki iş gününü sıfırla
        if (currentStajyer != null) {
            currentStajyer.setHesaplananIsGunu(0);
        }
        return;
    }

    // Başlangıç tarihi bitiş tarihinden sonra olamaz kontrolü
    if (startDate.isAfter(endDate)) {
        jLabelBusinessDaysResult.setText("Hata: Başlangıç tarihi bitiş tarihinden sonra olamaz.");
        JOptionPane.showMessageDialog(this,
                "Başlangıç tarihi bitiş tarihinden sonra olamaz.",
                "Geçersiz Tarih Aralığı", JOptionPane.WARNING_MESSAGE);
        
        // Hata durumunda stajyer nesnesindeki iş gününü sıfırla
        if (currentStajyer != null) {
            currentStajyer.setHesaplananIsGunu(0);
        }
        return;
    }

    long businessDays = 0;
    LocalDate currentDay = startDate;

    // Başlangıç tarihinden bitiş tarihine kadar her günü döngüyle kontrol et
    while (!currentDay.isAfter(endDate)) {
        DayOfWeek dayOfWeek = currentDay.getDayOfWeek();

        // Pazar her zaman iş günü değildir
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            // Pazar günü atla
        }
        // Cumartesi kontrolü: Eğer jCheckBoxSaturday işaretli değilse, Cumartesiyi atla
        // NOT: Sizin kodunuzda addSaturdayCheckbox olarak belirtilmiş, onu kullandım.
        else if (dayOfWeek == DayOfWeek.SATURDAY && !addSaturdayCheckbox.isSelected()) { 
            // Cumartesi günü atla
        }
        // Diğer günler iş günüdür (Pazartesi-Cuma ve Cumartesi dahilse Cumartesi)
        else {
            businessDays++;
        }
        currentDay = currentDay.plusDays(1); // Bir sonraki güne geç
    }

    // Hesaplanan iş günü sayısını jLabelBusinessDaysResult'ta göster
    jLabelBusinessDaysResult.setText(businessDays + " gün");

    // *** KRİTİK NOKTA: Hesaplanan iş gününü Stajyer nesnesine ata ***
    if (currentStajyer != null) {
        currentStajyer.setHesaplananIsGunu(businessDays);
        System.out.println("DEBUG: calculateAndDisplayBusinessDays() - Stajyer nesnesine atanan iş günü: " + currentStajyer.getHesaplananIsGunu());
    } else {
        System.err.println("HATA: calculateAndDisplayBusinessDays() - currentStajyer nesnesi null! İş günü atanamadı.");
    }
}

    // YENİ METOT: Özel GUI bileşenlerini başlatır
    private void initializeCustomComponents() {
        // Giriş Evrakları için panel oluştur ve girisEvrakScrollPane'e bağla
        girisFileListPanel = new JPanel();
        girisFileListPanel.setLayout(new BoxLayout(girisFileListPanel, BoxLayout.Y_AXIS));
        // **ÖNEMLİ:** Buradaki `girisEvrakScrollPane` ve `cikisEvrakScrollPane` adları
        // sizin GUI Builder'da verdiğiniz adlarla eşleşmeli!
        girisEvrakScrollPane.setViewportView(girisFileListPanel);

        // Çıkış Evrakları için panel oluştur ve cikisEvrakScrollPane'e bağla
        cikisFileListPanel = new JPanel();
         cikisFileListPanel.setLayout(new BoxLayout(cikisFileListPanel, BoxLayout.Y_AXIS)); // Yatayda 10, dikeyde 10 boşluk
        cikisEvrakScrollPane.setViewportView(cikisFileListPanel);
    }

    private void loadStajyerData(int stajyerId) {
        this.currentStajyer = stajyerService.getStajyerById(stajyerId);
        if (currentStajyer != null) {
            if (currentStajyer.getOkul() == null) currentStajyer.setOkul(new Okul());
            if (currentStajyer.getReferans() == null) currentStajyer.setReferans(new Referans());

            populateFormFields();

            // YENİ: Mevcut evrakları GUI listelerine yükle
            if (currentStajyer.getGirisEvrak() != null && currentStajyer.getGirisEvrak().getDosyaYolu() != null && !currentStajyer.getGirisEvrak().getDosyaYolu().isEmpty()) {
                File file = new File(currentStajyer.getGirisEvrak().getDosyaYolu());
                if (file.exists()) {
                    addPdfFile(file, girisFileListPanel);
                }
            }
            if (currentStajyer.getCikisEvrak() != null && currentStajyer.getCikisEvrak().getDosyaYolu() != null && !currentStajyer.getCikisEvrak().getDosyaYolu().isEmpty()) {
                File file = new File(currentStajyer.getCikisEvrak().getDosyaYolu());
                if (file.exists()) {
                    addPdfFile(file, cikisFileListPanel);
                }
            }

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
        // ESKİ: girisEvrakPathLabel ve cikisEvrakPathLabel artık kullanılmıyor
        // if (currentStajyer.getGirisEvrak() != null && currentStajyer.getGirisEvrak().getDosyaYolu() != null) {
        //     girisEvrakPathLabel.setText(currentStajyer.getGirisEvrak().getDosyaYolu());
        // } else {
        //     girisEvrakPathLabel.setText("Dosya Seçilmedi");
        // }
        // if (currentStajyer.getCikisEvrak() != null && currentStajyer.getCikisEvrak().getDosyaYolu() != null) {
        //     cikisEvrakPathLabel.setText(currentStajyer.getCikisEvrak().getDosyaYolu());
        // } else {
        //     cikisEvrakPathLabel.setText("Dosya Seçilmedi");
        // }
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
            if (!jTextField13.getText().trim().isEmpty()) currentStajyer.setSinif(Integer.parseInt(jTextField13.getText()));
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
        String referansTel = jTextField14.getText();
        String referansKurum = jTextField15.getText();

        boolean referansBilgisiGirildi = (referansAdSoyad != null && !referansAdSoyad.trim().isEmpty()) ||
                (referansTel != null && !referansTel.trim().isEmpty()) ||
                (referansKurum != null && !referansKurum.trim().isEmpty());
        if (referansBilgisiGirildi) {
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

            if (currentStajyer.getReferans() == null) {
                currentStajyer.setReferans(new Referans());
            }

            currentStajyer.getReferans().setAdSoyad(referansAdSoyad);
            currentStajyer.getReferans().setTelefonNo(referansTel);
            currentStajyer.getReferans().setKurum(referansKurum);

        } else {
            currentStajyer.setReferans(null);
        }

        // YENİ: Evrak yolu toplama mantığı güncellendi
        // Eğer Stajyer modeli tek bir evrak tutuyorsa, ilk eklenen dosyayı alıyoruz.
        // Eğer birden fazla evrak tutuyorsa (List<Evrak>), bu kısım buna göre revize edilmeli.
        File girisFile = getFirstFileInPanel(girisFileListPanel);
        if (girisFile != null) {
            if (currentStajyer.getGirisEvrak() == null) currentStajyer.setGirisEvrak(new Evrak());
            currentStajyer.getGirisEvrak().setDosyaYolu(girisFile.getAbsolutePath());
        } else {
            currentStajyer.setGirisEvrak(null);
        }

        File cikisFile = getFirstFileInPanel(cikisFileListPanel);
        if (cikisFile != null) {
            if (currentStajyer.getCikisEvrak() == null) currentStajyer.setCikisEvrak(new Evrak());
            currentStajyer.getCikisEvrak().setDosyaYolu(cikisFile.getAbsolutePath());
        } else {
            currentStajyer.setCikisEvrak(null);
        }

        return true;
    }
    
    // YENİ METOT: Bir paneldeki ilk dosyanın File nesnesini döndürür
    private File getFirstFileInPanel(JPanel panel) {
        // panel.getComponents() içinde JPanel'ler var (her biri bir dosya girişi)
        // her bir JPanel'in UserData'sına File nesnesini saklayacağız (aşağıda addPdfFile metodunda)
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel fileEntryPanel = (JPanel) comp;
                // Bu paneli oluştururken File nesnesini bir client property olarak sakladığımızı varsayalım
                File file = (File) fileEntryPanel.getClientProperty("file");
                if (file != null) {
                    return file;
                }
            }
        }
        return null;
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
            SwingUtilities.getWindowAncestor(this).dispose();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer kaydedilirken bir hata oluştu.", e);
            JOptionPane.showMessageDialog(this, "Stajyer kaydedilirken bir hata oluştu: " + e.getMessage(), "Kayıt Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupDragAndDrop() {
        // YENİ: girisEvrakIconPanel ve cikisEvrakIconPanel'e DropTarget ekle
        // Bu panellerin zaten GUI Builder'da var olduğunu varsayıyoruz.
        girisEvrakIconPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                // handleDrop metodunu dosyanın ekleneceği paneli belirterek çağır
                handleDrop(evt, girisFileListPanel);
            }
        });

        cikisEvrakIconPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                // handleDrop metodunu dosyanın ekleneceği paneli belirterek çağır
                handleDrop(evt, cikisFileListPanel);
            }
        });
    }

    // handleDrop metodunu güncelledik: pathLabel yerine hedef paneli alıyor
    private void handleDrop(DropTargetDropEvent evt, JPanel targetListPanel) {
        try {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            if (!droppedFiles.isEmpty()) {
                for (File file : droppedFiles) { // Sürüklenen tüm dosyaları işle
                    // Sadece PDF dosyalarını kabul et
                    if (!file.getName().toLowerCase().endsWith(".pdf")) {
                        JOptionPane.showMessageDialog(this, "Lütfen sadece PDF dosyaları sürükleyip bırakın.", "Geçersiz Dosya", JOptionPane.WARNING_MESSAGE);
                        continue; // Bir sonraki dosyaya geç
                    }

                    // Dosyayı 'evraklar' klasörüne kopyala
                    Path targetDir = Paths.get("evraklar");
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    // YENİ: Kopyalanan dosyayı GUI listesine ekle
                    addPdfFile(targetPath.toFile(), targetListPanel);
                    JOptionPane.showMessageDialog(this, "Dosya başarıyla kaydedildi: " + targetPath.getFileName(), "Dosya Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            LOGGER.log(Level.SEVERE, "Dosya sürükle bırak hatası", ex);
            JOptionPane.showMessageDialog(this, "Dosya sürükle bırak işleminde bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // YENİ METOT: PDF ikonlarını yükler ve ilgili JLabel'lara atar.
    // Artık bu metod sadece tek bir ölçeklenmiş PDF ikonu oluşturup map'e koyuyor.
    private void loadPdfIcons() {
        pdfIcons = new HashMap<>(); // Haritayı başlat
        try {
            // Kaynak klasördeki PDF ikonunun URL'sini al
            URL pdfIconURL = getClass().getResource("/icon/pdf.png"); // "/icon/pdf.png" ikonunuzun yolu olmalı
            if (pdfIconURL != null) {
                ImageIcon originalIcon = new ImageIcon(pdfIconURL);
                // Orijinal ikonu istenen boyuta ölçekle
                Image scaledImage = originalIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                pdfIcons.put("pdf", new ImageIcon(scaledImage)); // Ölçeklenmiş ikonu map'e kaydet
            } else {
                LOGGER.log(Level.WARNING, "pdf.png dosyası kaynaklarda bulunamadı.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PDF ikonları yüklenirken hata oluştu.", e);
        }
    }

    // ESKİ METOT: setupIconClickListeners() artık kullanılmıyor,
    // tıklama dinleyicileri her bir dosya paneline (fileEntryPanel) ekleniyor.
    // private void setupIconClickListeners() { ... }

    // YENİ METOT: Belirtilen yoldaki PDF dosyasını varsayılan uygulama ile açar.
    private void openPdfFile(File file) { // filePath yerine doğrudan File nesnesi alıyor
        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(this, "Açılacak bir PDF dosyası bulunamadı veya geçersiz.", "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "PDF dosyası açılırken hata oluştu: " + file.getAbsolutePath(), ex);
                JOptionPane.showMessageDialog(this, "PDF dosyası açılamadı. Lütfen sisteminizde bir PDF okuyucu yüklü olduğundan emin olun ve dosya yolunu kontrol edin.", "Açma Hatası", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Masaüstü işlemleri bu sistemde desteklenmiyor.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    // YENİ METOT: Dosyayı listeye ekler ve GUI'de gösterir
    // targetPanel: Dosyanın ekleneceği JPanel (girisFileListPanel veya cikisFileListPanel)
    private void addPdfFile(File file, JPanel targetPanel) {
        if (file == null || !file.exists()) {
            return;
        }

        // Zaten eklenmişse tekrar ekleme (dosya yolu aynıysa)
        // Not: Bu kontrol sadece tam dosya yoluna bakar. İçerik değişse bile aynı yol aynı dosya sayılır.
        if (addedFilePanelsMap.containsKey(file)) {
            // Aynı dosyanın zaten eklendiği listede olup olmadığını kontrol et
            if (targetPanel.getComponents().length > 0) { // Check if the target panel has components
                for (Component comp : targetPanel.getComponents()) {
                    if (comp instanceof JPanel) {
                        JPanel existingPanel = (JPanel) comp;
                        File existingFile = (File) existingPanel.getClientProperty("file");
                        if (existingFile != null && existingFile.equals(file)) {
                            JOptionPane.showMessageDialog(this, "Bu dosya zaten bu listeye eklenmiş: " + file.getName(), "Uyarı", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
            }
        }


        // Her dosya için özel bir panel oluştur
        JPanel fileEntryPanel = new JPanel();
        fileEntryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); // İkon ve isim arasında boşluk
        fileEntryPanel.setBorder(BorderFactory.createEtchedBorder()); // Hafif bir çerçeve ekleyebiliriz

        // İkon ekle
        JLabel iconLabel = new JLabel(pdfIcons.get("pdf")); // Yüklediğimiz ölçeklenmiş ikonu kullan
        fileEntryPanel.add(iconLabel);
        fileEntryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ICON_SIZE + 10));
        // Dosya adını ekle
        JLabel fileNameLabel = new JLabel(file.getName());
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Yazı tipini küçültebiliriz
        fileEntryPanel.add(fileNameLabel);
        fileEntryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Kapatma butonu ekle (isteğe bağlı)
        JButton removeButton = new JButton("X");
        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
        removeButton.setMargin(new Insets(1, 4, 1, 4)); // Butonun kenar boşluklarını ayarla
        removeButton.setFocusPainted(false); // Odak çizgisini kapat
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> {
            targetPanel.remove(fileEntryPanel);
            addedFilePanelsMap.remove(file); // Map'ten de kaldır
            targetPanel.revalidate();
            targetPanel.repaint();
        });
        fileEntryPanel.add(removeButton);


        // Panel tıklama dinleyicisini ekle
        fileEntryPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Tek tıklama
                    openPdfFile(file); // Doğrudan File nesnesini aç
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                fileEntryPanel.setBackground(new Color(220, 230, 240)); // Üzerine gelince renk değiştir
            }
            @Override
            public void mouseExited(MouseEvent e) {
                fileEntryPanel.setBackground(UIManager.getColor("Panel.background")); // Ayrılınca varsayılan renge dön
            }
        });

        // Dosya nesnesini JPanel'e bir Client Property olarak bağla
        // Bu, daha sonra hangi dosyanın hangi panele ait olduğunu anlamamızı sağlar.
        fileEntryPanel.putClientProperty("file", file);


        // Ana listeleme paneline bu dosya panelini ekle
        targetPanel.add(fileEntryPanel);
        addedFilePanelsMap.put(file, fileEntryPanel); // Dosyayı genel takip haritasına ekle
        targetPanel.revalidate(); // Bileşenleri yeniden düzenle
        targetPanel.repaint(); // Yeniden çiz
    }


    // YENİ: Giriş Evrakları için dosya seçme butonu ActionListener'ı
    // Bu metodu, GUI Builder'da "Giriş Evrakları" ile ilgili butonun ActionPerformed olayına bağlamalısınız.
    private void btnSelectGirisEvrakActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true); // Çoklu dosya seçimine izin ver

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                // PDF kontrolü yap
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    JOptionPane.showMessageDialog(this, "Sadece PDF dosyaları seçilebilir: " + file.getName(), "Geçersiz Dosya", JOptionPane.WARNING_MESSAGE);
                    continue; // Bir sonraki dosyaya geç
                }

                // Dosyayı 'evraklar' klasörüne kopyala
                try {
                    Path targetDir = Paths.get("evraklar");
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    addPdfFile(targetPath.toFile(), girisFileListPanel); // Giriş paneline kopyalanan dosyayı ekle
                    JOptionPane.showMessageDialog(this, "Dosya başarıyla kaydedildi: " + targetPath.getFileName(), "Dosya Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Dosya kopyalama hatası: " + file.getName(), ex);
                    JOptionPane.showMessageDialog(this, "Dosya kopyalanırken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // YENİ: Çıkış Evrakları için dosya seçme butonu ActionListener'ı
    // Bu metodu, GUI Builder'da "Çıkış Evrakları" ile ilgili butonun ActionPerformed olayına bağlamalısınız.
    private void btnSelectCikisEvrakActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true); // Çoklu dosya seçimine izin ver

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                // PDF kontrolü yap
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    JOptionPane.showMessageDialog(this, "Sadece PDF dosyaları seçilebilir: " + file.getName(), "Geçersiz Dosya", JOptionPane.WARNING_MESSAGE);
                    continue; // Bir sonraki dosyaya geç
                }

                // Dosyayı 'evraklar' klasörüne kopyala
                try {
                    Path targetDir = Paths.get("evraklar");
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    addPdfFile(targetPath.toFile(), cikisFileListPanel); // Çıkış paneline kopyalanan dosyayı ekle
                    JOptionPane.showMessageDialog(this, "Dosya başarıyla kaydedildi: " + targetPath.getFileName(), "Dosya Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Dosya kopyalama hatası: " + file.getName(), ex);
                    JOptionPane.showMessageDialog(this, "Dosya kopyalanırken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        girisEvrakIconPanel = new javax.swing.JPanel();
        girisEvrakIconLabel = new javax.swing.JLabel();
        girisEvrakScrollPane = new javax.swing.JScrollPane();
        cikisEvrakIconPanel = new javax.swing.JPanel();
        cikisEvrakScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
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
        jLabel15 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        addSaturdayCheckbox = new javax.swing.JCheckBox();
        jLabelBusinessDaysResult = new javax.swing.JLabel();
        calculateWorkdayButton = new javax.swing.JButton();

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("STAJYER TAKİP SİSTEMİ");

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

        jLabel18.setText("Giriş Evrakları ");

        jLabel19.setText("Çıkış Evrakları");

        girisEvrakIconPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        girisEvrakIconPanel.setLayout(new javax.swing.BoxLayout(girisEvrakIconPanel, javax.swing.BoxLayout.LINE_AXIS));
        girisEvrakIconPanel.add(girisEvrakIconLabel);
        girisEvrakIconPanel.add(girisEvrakScrollPane);

        cikisEvrakIconPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cikisEvrakIconPanel.setLayout(new javax.swing.BoxLayout(cikisEvrakIconPanel, javax.swing.BoxLayout.LINE_AXIS));
        cikisEvrakIconPanel.add(cikisEvrakScrollPane);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 28, 366, -1));

        jLabel3.setText("Bitiş Tarihi:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 430, -1, -1));

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 427, 272, -1));

        jLabel4.setText("Başlangıç Tarihi:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 398, -1, -1));

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 395, 272, -1));

        jLabel5.setText("T.C: Kimlik No:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 366, -1, -1));

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 363, 272, -1));

        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, 272, -1));

        jLabel6.setText("Referans:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 266, -1, -1));

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 202, 272, -1));

        jLabel7.setText("Okul:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 205, -1, -1));

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 272, -1));

        jLabel8.setText("Doğum Tarihi");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 173, -1, -1));

        jLabel9.setText("IBAN No:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 141, -1, -1));

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 138, 272, -1));

        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 106, 272, -1));

        jLabel10.setText("Telefon No:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 109, -1, -1));

        jLabel11.setText("Adres:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 77, -1, -1));

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 74, 272, -1));

        jLabel12.setText("Ad Soyad:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField12, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 459, 272, -1));

        jLabel13.setText("Bölüm:");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 462, -1, -1));

        jLabel14.setText("Sınıf:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 491, -1, -1));

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField13, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 491, 272, -1));

        jLabel15.setText("Okul Türü:");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 234, -1, -1));

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField11, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 234, 272, -1));

        jLabel16.setText("Referans No: ");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 295, -1, -1));

        jLabel17.setText("Referans Kurum:");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 334, -1, -1));

        jTextField14.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 292, 272, -1));

        jTextField15.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(jTextField15, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 331, 272, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setMaximumSize(new java.awt.Dimension(212, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addSaturdayCheckbox.setText("Cumartesi Dahil ");
        addSaturdayCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSaturdayCheckboxActionPerformed(evt);
            }
        });
        jPanel2.add(addSaturdayCheckbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));
        jPanel2.add(jLabelBusinessDaysResult, new org.netbeans.lib.awtextra.AbsoluteConstraints(196, 25, -1, -1));

        calculateWorkdayButton.setText("Hesapla");
        calculateWorkdayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateWorkdayButtonActionPerformed(evt);
            }
        });
        jPanel2.add(calculateWorkdayButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addGap(45, 45, 45))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(girisEvrakIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel20)
                                                .addGap(30, 30, 30)
                                                .addComponent(jLabel18)))
                                        .addGap(58, 58, 58)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel19)
                                            .addComponent(cikisEvrakIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(30, 30, 30))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(164, 164, 164))))
            .addComponent(jSeparator3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jButton2))
                .addGap(116, 116, 116)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(girisEvrakIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cikisEvrakIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addGap(8, 8, 8)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jButton1)))
                .addContainerGap(44, Short.MAX_VALUE))
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

    private void addSaturdayCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSaturdayCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addSaturdayCheckboxActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void calculateWorkdayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateWorkdayButtonActionPerformed
        calculateAndDisplayBusinessDays();
    }//GEN-LAST:event_calculateWorkdayButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addSaturdayCheckbox;
    private javax.swing.JButton calculateWorkdayButton;
    private javax.swing.JPanel cikisEvrakIconPanel;
    private javax.swing.JScrollPane cikisEvrakScrollPane;
    private javax.swing.JLabel girisEvrakIconLabel;
    private javax.swing.JPanel girisEvrakIconPanel;
    private javax.swing.JScrollPane girisEvrakScrollPane;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBusinessDaysResult;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
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
