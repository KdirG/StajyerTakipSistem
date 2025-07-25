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

    
    private void initializeCustomComponents() {
        
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

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel2.setText("STAJYER TAKİP SİSTEMİ");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 47, -1, -1));

        jLabel1.setText("Evrakları Buraya Sürükleyip Bırakabilirsiniz");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 176, -1, -1));
        add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 31, 1006, 10));

        jButton2.setText("Çıkış Yap");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(777, 53, -1, -1));

        jButton1.setText("Kaydet");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(897, 521, -1, -1));

        jLabel18.setText("Giriş Evrakları ");
        add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 476, -1, -1));

        girisEvrakIconPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        girisEvrakIconPanel.setLayout(new javax.swing.BoxLayout(girisEvrakIconPanel, javax.swing.BoxLayout.LINE_AXIS));

        girisEvrakScrollPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        girisEvrakIconPanel.add(girisEvrakScrollPane);

        add(girisEvrakIconPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 233, 203, 243));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Bitiş Tarihi:");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Başlangıç Tarihi:");

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

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
        jTextField12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });

        jLabel13.setText("Bölüm:");

        jLabel14.setText("Sınıf:");

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.CENTER);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(61, 61, 61)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(32, 32, 32)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(45, 45, 45)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(21, 21, 21)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(68, 68, 68)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(41, 41, 41)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(46, 46, 46)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(24, 24, 24)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(8, 8, 8)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(8, 8, 8)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(68, 68, 68)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel3))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel12)
                .addGap(6, 6, 6)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel11))
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel10))
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel9))
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel8))
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel7))
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel6))
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel16))
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel17))
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel5))
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4))
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel13))
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 198, -1, -1));
        add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 513, -1, -1));

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMaximumSize(new java.awt.Dimension(212, 102));
        jPanel2.setLayout(new java.awt.BorderLayout());

        addSaturdayCheckbox.setText("Cumartesi Dahil ");
        addSaturdayCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSaturdayCheckboxActionPerformed(evt);
            }
        });
        jPanel2.add(addSaturdayCheckbox, java.awt.BorderLayout.CENTER);
        jPanel2.add(jLabelBusinessDaysResult, java.awt.BorderLayout.PAGE_START);

        calculateWorkdayButton.setText("Hesapla");
        calculateWorkdayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateWorkdayButtonActionPerformed(evt);
            }
        });
        jPanel2.add(calculateWorkdayButton, java.awt.BorderLayout.PAGE_END);

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 521, -1, -1));
        add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 94, 1006, 10));

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel21.setText("İzin Ekle");

        jLabel23.setText("Bitiş");

        jLabel22.setText("Başlangıç");

        txtdayoffstart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdayoffstartActionPerformed(evt);
            }
        });

        txtdayoffend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdayoffendActionPerformed(evt);
            }
        });

        adddayoffbutton.setText("Ekle");
        adddayoffbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adddayoffbuttonActionPerformed(evt);
            }
        });

        jLabel24.setText("Sebep");

        txtdayoffcause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdayoffcauseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(adddayoffbutton))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtdayoffstart, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jLabel21))
                        .addGap(63, 146, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel24)
                        .addComponent(txtdayoffcause, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel23)
                        .addComponent(txtdayoffend, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtdayoffstart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtdayoffend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtdayoffcause, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(adddayoffbutton)
                .addContainerGap())
        );

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 588, -1, -1));

        jLabel25.setText("Çıkış Evrakları");
        add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(646, 476, -1, -1));

        cikisEvrakIconPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cikisEvrakIconPanel.setLayout(new javax.swing.BoxLayout(cikisEvrakIconPanel, javax.swing.BoxLayout.LINE_AXIS));

        cikisEvrakScrollPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cikisEvrakIconPanel.add(cikisEvrakScrollPane);

        add(cikisEvrakIconPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(646, 233, 203, 243));

        stajUygunlukPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel19.setText("Staj Uygunluk Belgesi Ekle");

        jLabel26.setText("Şehir ");

        txtinterncity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtinterncityActionPerformed(evt);
            }
        });

        jLabel27.setText("Öğrenci No.:");

        addstajuygunlukbutton.setText("Ekle");
        addstajuygunlukbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addstajuygunlukbuttonActionPerformed(evt);
            }
        });

        jLabel28.setText("Fakülte");

        javax.swing.GroupLayout stajUygunlukPanelLayout = new javax.swing.GroupLayout(stajUygunlukPanel);
        stajUygunlukPanel.setLayout(stajUygunlukPanelLayout);
        stajUygunlukPanelLayout.setHorizontalGroup(
            stajUygunlukPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stajUygunlukPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, stajUygunlukPanelLayout.createSequentialGroup()
                .addGroup(stajUygunlukPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtinterncity, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel27)
                    .addComponent(txtinternno))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(stajUygunlukPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, stajUygunlukPanelLayout.createSequentialGroup()
                        .addComponent(addstajuygunlukbutton)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, stajUygunlukPanelLayout.createSequentialGroup()
                        .addComponent(txtinternfaculty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(stajUygunlukPanelLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addContainerGap())))
        );
        stajUygunlukPanelLayout.setVerticalGroup(
            stajUygunlukPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stajUygunlukPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addGroup(stajUygunlukPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel28))
                .addGap(12, 12, 12)
                .addGroup(stajUygunlukPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtinterncity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtinternfaculty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtinternno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addstajuygunlukbutton)
                .addContainerGap())
        );

        add(stajUygunlukPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 588, -1, 216));

        verticalScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                verticalScrollBarAdjustmentValueChanged(evt);
            }
        });
        add(verticalScrollBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 104, -1, 700));
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JTextField txtdayoffcause;
    private javax.swing.JTextField txtdayoffend;
    private javax.swing.JTextField txtdayoffstart;
    private javax.swing.JTextField txtinterncity;
    private javax.swing.JTextField txtinternfaculty;
    private javax.swing.JTextField txtinternno;
    private javax.swing.JScrollBar verticalScrollBar;
    // End of variables declaration//GEN-END:variables



}
