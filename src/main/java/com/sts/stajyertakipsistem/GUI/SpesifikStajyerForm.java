package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.dao.StajUygunlukBelgeDAO;
import java.sql.SQLException;
import com.sts.stajyertakipsistem.service.BelgeService;
import com.sts.stajyertakipsistem.model.Izin; 
import com.sts.stajyertakipsistem.dao.IzinDAO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files; 
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap; 
import java.util.List;
import java.util.Map; 
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL; 
import java.awt.datatransfer.DataFlavor; 
import java.awt.datatransfer.UnsupportedFlavorException; 
import java.awt.dnd.DnDConstants; 
import java.awt.dnd.DropTarget; 
import java.awt.dnd.DropTargetDropEvent; 
import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.Referans;
import com.sts.stajyertakipsistem.model.Stajyer; 
import com.sts.stajyertakipsistem.model.Okul;     
import com.sts.stajyertakipsistem.service.StajyerService; 
import java.time.DayOfWeek;
import com.sts.stajyertakipsistem.model.StajUygunlukBelge;
import java.awt.FocusTraversalPolicy;
import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class SpesifikStajyerForm extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(SpesifikStajyerForm.class.getName());
    private final StajyerService stajyerService;
    private Stajyer currentStajyer;
    private final Runnable onSaveCallback;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private IzinDAO izinDAO; // Burada tanımlanır
    private BelgeService belgeService; 
    private boolean isEditMode = false;
    private StajUygunlukBelgeDAO stajUygunlukBelgeDAO;
    
    private Map<String, ImageIcon> pdfIcons;
    
    
    private Map<File, JPanel> addedFilePanelsMap;

    
    
    private JPanel girisFileListPanel;
    private JPanel cikisFileListPanel;

    
    private static final int ICON_SIZE = 24; 

    
    
    
    

    public SpesifikStajyerForm(int stajyerId, Runnable onSaveCallback) {
        this.stajyerService = new StajyerService();
        this.onSaveCallback = onSaveCallback;
        initComponents(); 
        this.izinDAO = new IzinDAO();
        this.belgeService = new BelgeService();
        initializeCustomComponents();
        this.stajUygunlukBelgeDAO = new StajUygunlukBelgeDAO();
        loadPdfIcons();
        
        addedFilePanelsMap = new HashMap<>();

        
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
       private void calculateAndDisplayBusinessDays() {
    LocalDate startDate;
    LocalDate endDate;

    try {
        
        startDate = LocalDate.parse(jTextField3.getText().trim(), formatter); 
        endDate = LocalDate.parse(jTextField2.getText().trim(), formatter);   
    } catch (DateTimeParseException e) {
        
        jLabelBusinessDaysResult.setText("Hata: Geçersiz tarih formatı.");
        JOptionPane.showMessageDialog(this,
                "Geçersiz tarih formatı. Lütfen 'YYYY.MM.DD' formatını kullanın.",
                "Tarih Formatı Hatası", JOptionPane.ERROR_MESSAGE);
        
        
        if (currentStajyer != null) {
            currentStajyer.setHesaplananIsGunu(0);
        }
        return;
    }

    
    if (startDate.isAfter(endDate)) {
        jLabelBusinessDaysResult.setText("Hata: Başlangıç tarihi bitiş tarihinden sonra olamaz.");
        JOptionPane.showMessageDialog(this,
                "Başlangıç tarihi bitiş tarihinden sonra olamaz.",
                "Geçersiz Tarih Aralığı", JOptionPane.WARNING_MESSAGE);
        
        
        if (currentStajyer != null) {
            currentStajyer.setHesaplananIsGunu(0);
        }
        return;
    }

    long businessDays = 0;
    LocalDate currentDay = startDate;

    
    while (!currentDay.isAfter(endDate)) {
        DayOfWeek dayOfWeek = currentDay.getDayOfWeek();

        
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            
        }
        
        
        else if (dayOfWeek == DayOfWeek.SATURDAY && !addSaturdayCheckbox.isSelected()) { 
            
        }
        
        else {
            businessDays++;
        }
        currentDay = currentDay.plusDays(1); 
    }

    
    jLabelBusinessDaysResult.setText(businessDays + " gün");

    
    if (currentStajyer != null) {
        currentStajyer.setHesaplananIsGunu(businessDays);
        System.out.println("DEBUG: calculateAndDisplayBusinessDays() - Stajyer nesnesine atanan iş günü: " + currentStajyer.getHesaplananIsGunu());
    } else {
        System.err.println("HATA: calculateAndDisplayBusinessDays() - currentStajyer nesnesi null! İş günü atanamadı.");
    }
}
   private void calculateEndDateFromBusinessDays() {
    LocalDate startDate;
    int businessDays;

    // 1. Kontrol: Başlangıç tarihi (jTextField3) dolu mu?
    String startDateText = jTextField3.getText().trim();
    if (startDateText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Lütfen öncelikle başlangıç tarihi giriniz.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
        jTextField3.requestFocusInWindow(); // Odakı başlangıç tarihi alanına getir
        return;
    }
    
    // 2. Kontrol: İş günü sayısı (txtBusinessDaysInput) dolu mu?
    String businessDaysText = txtBusinessDaysInput.getText().trim();
    if (businessDaysText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Lütfen iş günü sayısını giriniz.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
        txtBusinessDaysInput.requestFocusInWindow(); // Odakı iş günü alanına getir
        return;
    }

    // 3. Tarih ve gün sayısını oku ve hataları yönet
    try {
        startDate = LocalDate.parse(startDateText, formatter);
        businessDays = Integer.parseInt(businessDaysText);
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(this, "Geçersiz başlangıç tarihi formatı. Lütfen 'dd.MM.yyyy' formatını kullanın.", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        jTextField3.requestFocusInWindow();
        return;
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Geçersiz iş günü sayısı formatı. Lütfen sayı giriniz.", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        txtBusinessDaysInput.requestFocusInWindow();
        return;
    }

    if (businessDays <= 0) {
        JOptionPane.showMessageDialog(this, "İş günü sayısı pozitif bir sayı olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
        txtBusinessDaysInput.requestFocusInWindow();
        return;
    }
    
    // 4. Bitiş tarihini hesapla
    LocalDate calculatedDate = startDate;
    int daysCounter = 0;

    // Girilen iş günü sayısı kadar ileri git
    while (daysCounter < businessDays) {
        calculatedDate = calculatedDate.plusDays(1);
        DayOfWeek dayOfWeek = calculatedDate.getDayOfWeek();

        // Pazar günlerini ve eğer seçili değilse Cumartesiyi atla
        if (dayOfWeek != DayOfWeek.SUNDAY && (dayOfWeek != DayOfWeek.SATURDAY || addSaturdayCheckbox.isSelected())) {
            daysCounter++;
        }
    }
    
    // 5. Hesaplanan bitiş tarihini jTextField2'ye yaz
    jTextField2.setText(calculatedDate.format(formatter));
}
    private void initializeCustomComponents() {
        addBusinessDaysInputbutton.addActionListener(e -> {
    calculateEndDateFromBusinessDays();
});
        girisFileListPanel = new JPanel();
        girisFileListPanel.setLayout(new BoxLayout(girisFileListPanel, BoxLayout.Y_AXIS));
        
        
        girisEvrakScrollPane.setViewportView(girisFileListPanel);

        
        cikisFileListPanel = new JPanel();
         cikisFileListPanel.setLayout(new BoxLayout(cikisFileListPanel, BoxLayout.Y_AXIS)); 
        cikisEvrakScrollPane.setViewportView(cikisFileListPanel);
    }

    private void loadStajyerData(int stajyerId) {
        this.currentStajyer = stajyerService.getStajyerById(stajyerId);
        if (currentStajyer != null) {
            if (currentStajyer.getOkul() == null) currentStajyer.setOkul(new Okul());
            if (currentStajyer.getReferans() == null) currentStajyer.setReferans(new Referans());

            populateFormFields();

            
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
    
    
    private File getFirstFileInPanel(JPanel panel) {
        
        
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel fileEntryPanel = (JPanel) comp;
                
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
        
        
        girisEvrakIconPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                
                handleDrop(evt, girisFileListPanel);
            }
        });

        cikisEvrakIconPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                
                handleDrop(evt, cikisFileListPanel);
            }
        });
    }

    
    private void handleDrop(DropTargetDropEvent evt, JPanel targetListPanel) {
        try {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            if (!droppedFiles.isEmpty()) {
                for (File file : droppedFiles) { 
                    
                    if (!file.getName().toLowerCase().endsWith(".pdf")) {
                        JOptionPane.showMessageDialog(this, "Lütfen sadece PDF dosyaları sürükleyip bırakın.", "Geçersiz Dosya", JOptionPane.WARNING_MESSAGE);
                        continue; 
                    }

                    
                    Path targetDir = Paths.get("evraklar");
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    
                    addPdfFile(targetPath.toFile(), targetListPanel);
                    JOptionPane.showMessageDialog(this, "Dosya başarıyla kaydedildi: " + targetPath.getFileName(), "Dosya Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            LOGGER.log(Level.SEVERE, "Dosya sürükle bırak hatası", ex);
            JOptionPane.showMessageDialog(this, "Dosya sürükle bırak işleminde bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    private void loadPdfIcons() {
        pdfIcons = new HashMap<>(); 
        try {
            
            URL pdfIconURL = getClass().getResource("/icon/pdf.png"); 
            if (pdfIconURL != null) {
                ImageIcon originalIcon = new ImageIcon(pdfIconURL);
                
                Image scaledImage = originalIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                pdfIcons.put("pdf", new ImageIcon(scaledImage)); 
            } else {
                LOGGER.log(Level.WARNING, "pdf.png dosyası kaynaklarda bulunamadı.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PDF ikonları yüklenirken hata oluştu.", e);
        }
    }

    
    
    

    
    private void openPdfFile(File file) { 
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

    
    
    private void addPdfFile(File file, JPanel targetPanel) {
        if (file == null || !file.exists()) {
            return;
        }

        
        
        if (addedFilePanelsMap.containsKey(file)) {
            
            if (targetPanel.getComponents().length > 0) { 
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


        
        JPanel fileEntryPanel = new JPanel();
        fileEntryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0)); 
        fileEntryPanel.setBorder(BorderFactory.createEtchedBorder()); 

        
        JLabel iconLabel = new JLabel(pdfIcons.get("pdf")); 
        fileEntryPanel.add(iconLabel);
        fileEntryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ICON_SIZE + 10));
        
        JLabel fileNameLabel = new JLabel(file.getName());
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        fileEntryPanel.add(fileNameLabel);
        fileEntryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton removeButton = new JButton("X");
        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
        removeButton.setMargin(new Insets(1, 4, 1, 4)); 
        removeButton.setFocusPainted(false); 
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> {
            targetPanel.remove(fileEntryPanel);
            addedFilePanelsMap.remove(file); 
            targetPanel.revalidate();
            targetPanel.repaint();
        });
        fileEntryPanel.add(removeButton);


        
        fileEntryPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { 
                    openPdfFile(file); 
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                fileEntryPanel.setBackground(new Color(220, 230, 240)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                fileEntryPanel.setBackground(UIManager.getColor("Panel.background")); 
            }
        });

        
        
        fileEntryPanel.putClientProperty("file", file);


        
        targetPanel.add(fileEntryPanel);
        addedFilePanelsMap.put(file, fileEntryPanel); 
        targetPanel.revalidate(); 
        targetPanel.repaint(); 
    }


    
    
    private void btnSelectGirisEvrakActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true); 

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    JOptionPane.showMessageDialog(this, "Sadece PDF dosyaları seçilebilir: " + file.getName(), "Geçersiz Dosya", JOptionPane.WARNING_MESSAGE);
                    continue; 
                }

                
                try {
                    Path targetDir = Paths.get("evraklar");
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    addPdfFile(targetPath.toFile(), girisFileListPanel); 
                    JOptionPane.showMessageDialog(this, "Dosya başarıyla kaydedildi: " + targetPath.getFileName(), "Dosya Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Dosya kopyalama hatası: " + file.getName(), ex);
                    JOptionPane.showMessageDialog(this, "Dosya kopyalanırken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    
    
    private void btnSelectCikisEvrakActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true); 

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    JOptionPane.showMessageDialog(this, "Sadece PDF dosyaları seçilebilir: " + file.getName(), "Geçersiz Dosya", JOptionPane.WARNING_MESSAGE);
                    continue; 
                }

                
                try {
                    Path targetDir = Paths.get("evraklar");
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    addPdfFile(targetPath.toFile(), cikisFileListPanel); 
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
        java.awt.GridBagConstraints gridBagConstraints;

        jSeparator2 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        girisEvrakIconPanel = new javax.swing.JPanel();
        girisEvrakScrollPane = new javax.swing.JScrollPane();
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
        jSeparator4 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtdayoffstart = new javax.swing.JTextField();
        txtdayoffend = new javax.swing.JTextField();
        adddayoffbutton = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        txtdayoffcause = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        cikisEvrakIconPanel = new javax.swing.JPanel();
        cikisEvrakScrollPane = new javax.swing.JScrollPane();
        stajUygunlukPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtinterncity = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtinternno = new javax.swing.JTextField();
        addstajuygunlukbutton = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        txtinternfaculty = new javax.swing.JTextField();
        verticalScrollBar = new javax.swing.JScrollBar();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        txtBusinessDaysInput = new javax.swing.JTextField();
        addBusinessDaysInputbutton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel2.setText("STAJYER TAKİP SİSTEMİ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 30, 0, 0);
        add(jLabel2, gridBagConstraints);

        jLabel1.setText("Evrakları Buraya Sürükleyip Bırakabilirsiniz");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(106, 2, 0, 0);
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 60;
        gridBagConstraints.ipadx = 1005;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 0, 0, 0);
        add(jSeparator3, gridBagConstraints);

        jButton2.setText("Geri Dön");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 32;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 59, 0, 0);
        add(jButton2, gridBagConstraints);

        jButton1.setText("Kaydet");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 57;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(42, 13, 0, 0);
        add(jButton1, gridBagConstraints);

        jLabel18.setText("Giriş Evrakları ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(jLabel18, gridBagConstraints);

        girisEvrakIconPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        girisEvrakIconPanel.setLayout(new java.awt.GridBagLayout());

        girisEvrakScrollPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        girisEvrakIconPanel.add(girisEvrakScrollPane, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 177;
        gridBagConstraints.ipady = 217;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 12, 0, 0);
        add(girisEvrakIconPanel, gridBagConstraints);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.ipadx = 302;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel1.add(jTextField1, gridBagConstraints);

        jLabel3.setText("Bitiş Tarihi:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField2, gridBagConstraints);

        jLabel4.setText("Başlangıç Tarihi:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField3, gridBagConstraints);

        jLabel5.setText("T.C: Kimlik No:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel5, gridBagConstraints);

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField4, gridBagConstraints);

        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField5, gridBagConstraints);

        jLabel6.setText("Referans:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 6, 0, 0);
        jPanel1.add(jLabel6, gridBagConstraints);

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField6, gridBagConstraints);

        jLabel7.setText("Okul:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel7, gridBagConstraints);

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField7, gridBagConstraints);

        jLabel8.setText("Doğum Tarihi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel8, gridBagConstraints);

        jLabel9.setText("IBAN No:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel9, gridBagConstraints);

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField8, gridBagConstraints);

        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField9, gridBagConstraints);

        jLabel10.setText("Telefon No:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel10, gridBagConstraints);

        jLabel11.setText("Adres:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(27, 6, 0, 0);
        jPanel1.add(jLabel11, gridBagConstraints);

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 8, 0, 0);
        jPanel1.add(jTextField10, gridBagConstraints);

        jLabel12.setText("Ad Soyad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel1.add(jLabel12, gridBagConstraints);

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField12, gridBagConstraints);

        jLabel13.setText("Bölüm:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel13, gridBagConstraints);

        jLabel14.setText("Sınıf:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 0);
        jPanel1.add(jLabel14, gridBagConstraints);

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField13ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField13, gridBagConstraints);

        jLabel15.setText("Okul Türü:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 0);
        jPanel1.add(jLabel15, gridBagConstraints);

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField11, gridBagConstraints);

        jLabel16.setText("Referans No: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel16, gridBagConstraints);

        jLabel17.setText("Referans Kurum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        jPanel1.add(jLabel17, gridBagConstraints);

        jTextField14.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField14, gridBagConstraints);

        jTextField15.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 208;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        jPanel1.add(jTextField15, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(94, 30, 0, 10);
        add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel20, gridBagConstraints);

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMaximumSize(new java.awt.Dimension(212, 102));

        addSaturdayCheckbox.setText("Cumartesi Dahil ");
        addSaturdayCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSaturdayCheckboxActionPerformed(evt);
            }
        });

        calculateWorkdayButton.setText("Hesapla");
        calculateWorkdayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateWorkdayButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelBusinessDaysResult)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(calculateWorkdayButton))
                    .addComponent(addSaturdayCheckbox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabelBusinessDaysResult)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(addSaturdayCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calculateWorkdayButton))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 60;
        gridBagConstraints.ipadx = 1005;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        add(jSeparator4, gridBagConstraints);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel21.setText("İzin Ekle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        jPanel3.add(jLabel21, gridBagConstraints);

        jLabel23.setText("Bitiş");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 3, 0, 0);
        jPanel3.add(jLabel23, gridBagConstraints);

        jLabel22.setText("Başlangıç");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 36, 0, 0);
        jPanel3.add(jLabel22, gridBagConstraints);

        txtdayoffstart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdayoffstartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 36, 0, 0);
        jPanel3.add(txtdayoffstart, gridBagConstraints);

        txtdayoffend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdayoffendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        jPanel3.add(txtdayoffend, gridBagConstraints);

        adddayoffbutton.setText("Ekle");
        adddayoffbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adddayoffbuttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(26, 74, 0, 0);
        jPanel3.add(adddayoffbutton, gridBagConstraints);

        jLabel24.setText("Sebep");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 3, 0, 0);
        jPanel3.add(jLabel24, gridBagConstraints);

        txtdayoffcause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdayoffcauseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.ipadx = 142;
        gridBagConstraints.ipady = 29;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        jPanel3.add(txtdayoffcause, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 104, 0);
        add(jPanel3, gridBagConstraints);

        jLabel25.setText("Çıkış Evrakları");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(jLabel25, gridBagConstraints);

        cikisEvrakIconPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cikisEvrakIconPanel.setLayout(new java.awt.GridBagLayout());

        cikisEvrakScrollPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cikisEvrakIconPanel.add(cikisEvrakScrollPane, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 24;
        gridBagConstraints.ipadx = 177;
        gridBagConstraints.ipady = 217;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 0, 0);
        add(cikisEvrakIconPanel, gridBagConstraints);

        stajUygunlukPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stajUygunlukPanel.setLayout(new java.awt.GridBagLayout());

        jLabel19.setText("Staj Uygunluk Belgesi Ekle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 0, 0);
        stajUygunlukPanel.add(jLabel19, gridBagConstraints);

        jLabel26.setText("Şehir ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 3, 0, 0);
        stajUygunlukPanel.add(jLabel26, gridBagConstraints);

        txtinterncity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtinterncityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 3, 0, 0);
        stajUygunlukPanel.add(txtinterncity, gridBagConstraints);

        jLabel27.setText("Öğrenci No.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 3, 0, 0);
        stajUygunlukPanel.add(jLabel27, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        stajUygunlukPanel.add(txtinternno, gridBagConstraints);

        addstajuygunlukbutton.setText("Ekle");
        addstajuygunlukbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addstajuygunlukbuttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(37, 31, 0, 0);
        stajUygunlukPanel.add(addstajuygunlukbutton, gridBagConstraints);

        jLabel28.setText("Fakülte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 18, 0, 0);
        stajUygunlukPanel.add(jLabel28, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 18, 0, 0);
        stajUygunlukPanel.add(txtinternfaculty, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 37;
        gridBagConstraints.gridheight = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 38, 104, 0);
        add(stajUygunlukPanel, gridBagConstraints);

        verticalScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                verticalScrollBarAdjustmentValueChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 59;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 61;
        gridBagConstraints.gridheight = 16;
        gridBagConstraints.ipady = 695;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 31, 0, 0);
        add(verticalScrollBar, gridBagConstraints);

        jPanel4.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 58;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.ipady = 181;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(jPanel4, gridBagConstraints);

        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel5.setLayout(new java.awt.BorderLayout());

        txtBusinessDaysInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusinessDaysInputActionPerformed(evt);
            }
        });
        jPanel5.add(txtBusinessDaysInput, java.awt.BorderLayout.PAGE_START);

        addBusinessDaysInputbutton.setText("İş Günü Gir");
        jPanel5.add(addBusinessDaysInputbutton, java.awt.BorderLayout.PAGE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jPanel5, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    saveStajyerAndEvrak();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void addSaturdayCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSaturdayCheckboxActionPerformed
        
    }//GEN-LAST:event_addSaturdayCheckboxActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void calculateWorkdayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateWorkdayButtonActionPerformed
        calculateAndDisplayBusinessDays();
    }//GEN-LAST:event_calculateWorkdayButtonActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void adddayoffbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adddayoffbuttonActionPerformed
        if (currentStajyer == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir stajyer seçin veya yeni bir stajyer kaydedin.", "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String izinSebebi = txtdayoffcause.getText().trim();
        String baslangicStr = txtdayoffstart.getText().trim();
        String bitisStr = txtdayoffend.getText().trim();

        if (izinSebebi.isEmpty() || baslangicStr.isEmpty() || bitisStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen izin sebebi, başlangıç ve bitiş tarihlerini doldurun.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate izinBaslangic = belgeService.parseDate(baslangicStr);
            LocalDate izinBitis = belgeService.parseDate(bitisStr);

            if (izinBaslangic.isAfter(izinBitis)) {
                JOptionPane.showMessageDialog(this, "Başlangıç tarihi bitiş tarihinden sonra olamaz.", "Geçersiz Tarih", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. İzin kaydını veritabanına ekle (IzinDAO kullanarak)
            Izin yeniIzin = new Izin();
            yeniIzin.setStajyerId(currentStajyer.getStajyerId());
            yeniIzin.setIzinBaslangic(izinBaslangic);
            yeniIzin.setIzinBitis(izinBitis);
            yeniIzin.setIzinSebep(izinSebebi);

            // izinDAO.addIzin() metodu artık void döndüğü için doğrudan çağırılır.
            // Eğer bir hata olursa, SQLException fırlatılacak ve aşağıdaki catch bloğu yakalayacaktır.
            izinDAO.addIzin(yeniIzin);
            
            // Eğer buraya gelinirse, izin veritabanına başarıyla eklenmiş demektir.
            // Ayrı bir 'boolean izinEklendi' kontrolüne gerek kalmaz.

            // 2. Belgeyi oluşturmak için gerekli bilgileri topla (BelgeService kullanarak)
            String templatePath = "templates/Izin_Belgesi_Sablonu.docx";
            String outputDirectory = "izin_belgeleri";

            String generatedFilePath = belgeService.createIzinBelgesi(currentStajyer, izinSebebi, izinBaslangic, izinBitis, templatePath, outputDirectory);

            File generatedFile = new File(generatedFilePath);
            if (generatedFile.exists()) {
                JOptionPane.showMessageDialog(this, "İzin kaydı veritabanına eklendi ve izin belgesi başarıyla oluşturuldu:\n" + generatedFilePath, "İşlem Başarılı", JOptionPane.INFORMATION_MESSAGE);

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(generatedFile);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Belge oluşturuldu ancak dosya bulunamadı: " + generatedFilePath, "Hata", JOptionPane.ERROR_MESSAGE);
            }

            // İzin formu alanlarını temizle
            txtdayoffstart.setText("");
            txtdayoffend.setText("");
            txtdayoffcause.setText("");

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Lütfen tarihleri 'gg.aa.yyyy' formatında girin.", "Geçersiz Tarih Formatı", JOptionPane.WARNING_MESSAGE);
            LOGGER.log(Level.WARNING, "Tarih formatı hatası: {0}", e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "İzin belgesi oluşturulurken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "İzin belgesi oluşturma hatası", e);
        } catch (SQLException e) { // IzinDAO.addIzin() buradan fırlatılan SQLException'ı yakalayacak
            JOptionPane.showMessageDialog(this, "Veritabanı hatası oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Veritabanı hatası", e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Beklenmeyen bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Beklenmeyen hata", e);
        }
    
    }//GEN-LAST:event_adddayoffbuttonActionPerformed

    private void txtdayoffstartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdayoffstartActionPerformed
       
    }//GEN-LAST:event_txtdayoffstartActionPerformed

    private void txtdayoffendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdayoffendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtdayoffendActionPerformed

    private void txtdayoffcauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdayoffcauseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtdayoffcauseActionPerformed

    private void addstajuygunlukbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addstajuygunlukbuttonActionPerformed
         if (currentStajyer == null || currentStajyer.getStajyerId() == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir stajyer seçin veya yeni bir stajyer kaydedin.", "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sehir = txtinterncity.getText().trim();
        String fakulte = txtinternfaculty.getText().trim();
        String ogrenciNo = txtinternno.getText().trim();

        if (sehir.isEmpty() || fakulte.isEmpty() || ogrenciNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen şehir, fakülte ve öğrenci numarasını doldurun.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Staj Uygunluk Belgesi verilerini modele ata
            StajUygunlukBelge stajUygunlukBelge = new StajUygunlukBelge();
            stajUygunlukBelge.setStajyerId(currentStajyer.getStajyerId()); // Mevcut stajyerin ID'si
            stajUygunlukBelge.setSehir(sehir);
            stajUygunlukBelge.setFakulte(fakulte);
            stajUygunlukBelge.setOgrenciNo(ogrenciNo);

            // 2. Veritabanına kaydet
            // Not: Eğer bir stajyer için sadece bir uygunluk belgesi olacaksa,
            // burada önce mevcut bir kaydın olup olmadığını kontrol edip güncelleme yapabilirsiniz.
            // Şimdilik sadece yeni kayıt ekliyoruz.
            stajUygunlukBelgeDAO.addStajUygunlukBelge(stajUygunlukBelge);

            // 3. Belgeyi oluşturmak için BelgeService'i kullan
            String templatePath = "templates/Staj_Uygunluk_Belgesi_Sablonu.docx";
            String outputDirectory = "staj_uygunluk_belgeleri"; // Yeni çıktı dizini

            String generatedFilePath = belgeService.createStajUygunlukBelgesi(currentStajyer, stajUygunlukBelge, templatePath, outputDirectory);

            File generatedFile = new File(generatedFilePath);
            if (generatedFile.exists()) {
                JOptionPane.showMessageDialog(this, "Staj uygunluk belgesi veritabanına eklendi ve başarıyla oluşturuldu:\n" + generatedFilePath, "İşlem Başarılı", JOptionPane.INFORMATION_MESSAGE);

                // Belgeyi otomatik aç
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(generatedFile);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Belge oluşturuldu ancak dosya bulunamadı: " + generatedFilePath, "Hata", JOptionPane.ERROR_MESSAGE);
            }

            // Form alanlarını temizle
            txtinterncity.setText("");
            txtinternfaculty.setText("");
            txtinternno.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Staj uygunluk belgesi kaydederken veritabanı hatası", e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Staj uygunluk belgesi oluşturulurken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Staj uygunluk belgesi oluşturma hatası", e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Beklenmeyen bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Staj uygunluk belgesi oluşturma sırasında beklenmeyen hata", e);
        }
    
    }//GEN-LAST:event_addstajuygunlukbuttonActionPerformed

    private void verticalScrollBarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_verticalScrollBarAdjustmentValueChanged
      int scrollValue = verticalScrollBar.getValue();

        this.setLocation(0, -scrollValue); 
    }//GEN-LAST:event_verticalScrollBarAdjustmentValueChanged

    private void txtinterncityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtinterncityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtinterncityActionPerformed

    private void jTextField12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField12ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        SwingUtilities.getWindowAncestor(this).dispose();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField13ActionPerformed

    private void txtBusinessDaysInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusinessDaysInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBusinessDaysInputActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBusinessDaysInputbutton;
    private javax.swing.JCheckBox addSaturdayCheckbox;
    private javax.swing.JButton adddayoffbutton;
    private javax.swing.JButton addstajuygunlukbutton;
    private javax.swing.JButton calculateWorkdayButton;
    private javax.swing.JPanel cikisEvrakIconPanel;
    private javax.swing.JScrollPane cikisEvrakScrollPane;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
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
    private javax.swing.JPanel stajUygunlukPanel;
    private javax.swing.JTextField txtBusinessDaysInput;
    private javax.swing.JTextField txtdayoffcause;
    private javax.swing.JTextField txtdayoffend;
    private javax.swing.JTextField txtdayoffstart;
    private javax.swing.JTextField txtinterncity;
    private javax.swing.JTextField txtinternfaculty;
    private javax.swing.JTextField txtinternno;
    private javax.swing.JScrollBar verticalScrollBar;
    // End of variables declaration//GEN-END:variables



}
