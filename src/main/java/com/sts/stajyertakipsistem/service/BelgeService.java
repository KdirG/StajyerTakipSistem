package com.sts.stajyertakipsistem.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.model.StajUygunlukBelge; // Yeni model import edildi

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BelgeService {

    private static final Logger LOGGER = Logger.getLogger(BelgeService.class.getName());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public BelgeService() {
        // Çıkış dizinlerini oluştur (Eğer zaten yoksa)
        try {
            Files.createDirectories(Paths.get("izin_belgeleri"));
            Files.createDirectories(Paths.get("staj_uygunluk_belgeleri"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Belge çıktı dizinleri oluşturulurken hata oluştu.", e);
        }
    }

    /**
     * Verilen şablonu kullanarak bir Word belgesi oluşturur ve placeholder'ları doldurur.
     *
     * @param templatePath Şablon .docx dosyasının yolu.
     * @param outputPath Oluşturulan belgenin kaydedileceği tam yol.
     * @param placeholders Doldurulacak placeholder'ları ve değerlerini içeren Map.
     * @throws IOException Eğer dosya okuma/yazma hatası olursa.
     */
    public void createDocumentFromTemplate(String templatePath, String outputPath, Map<String, String> placeholders) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis);
             FileOutputStream fos = new FileOutputStream(outputPath)) {

            for (XWPFParagraph p : document.getParagraphs()) {
                replacePlaceholdersInParagraph(p, placeholders);
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            replacePlaceholdersInParagraph(p, placeholders);
                        }
                    }
                }
            }
            document.write(fos);
            LOGGER.log(Level.INFO, "Belge başarıyla oluşturuldu: {0}", outputPath);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Belge oluşturulurken hata oluştu.", e);
            throw e;
        }
    }

    /**
     * Bir paragraf içindeki placeholder'ları değiştirir.
     * Bu metot, placeholder'ların birden fazla run'a bölünebileceği senaryoları ele almaz.
     * Basit senaryolar için yeterlidir.
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph p, Map<String, String> placeholders) {
    // 1. Paragraftaki tüm run'ların metinlerini birleştir
    StringBuilder paragraphText = new StringBuilder();
    for (XWPFRun r : p.getRuns()) {
        paragraphText.append(r.getText(0) == null ? "" : r.getText(0));
    }

    String fullText = paragraphText.toString();
    boolean changed = false;

    // 2. Birleştirilmiş metin üzerinde placeholder'ları değiştir
    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
        String placeholder = entry.getKey();
        String value = entry.getValue();

        if (fullText.contains(placeholder)) {
            fullText = fullText.replace(placeholder, value);
            changed = true;
        }
    }

    // 3. Eğer değişiklik yapıldıysa, paragrafın mevcut run'larını temizle ve yeni run ekle
    if (changed) {
        // Mevcut run'ları temizle
        List<XWPFRun> runs = p.getRuns();
        for (int i = runs.size() - 1; i >= 0; i--) {
            p.removeRun(i);
        }

        // Değiştirilmiş metni içeren yeni bir run ekle
        XWPFRun newRun = p.createRun();
        newRun.setText(fullText, 0);

        // İsteğe bağlı: Orijinal run'ların formatını korumaya çalış (basit bir yaklaşım)
        // Eğer ilk run'ın formatını korumak isterseniz:
        // if (!runs.isEmpty()) {
        //    XWPFRun firstRun = runs.get(0);
        //    newRun.setFontFamily(firstRun.getFontFamily());
        //    newRun.setFontSize(firstRun.getFontSize());
        //    newRun.setBold(firstRun.isBold());
        //    newRun.setItalic(firstRun.isItalic());
        //    newRun.setColor(firstRun.getColor());
        //    // Daha fazla format özelliği kopyalayabilirsiniz
        // }
    }
}
    /**
     * İzin Belgesini oluşturur.
     * Şablon dosyasındaki placeholder'ları ilgili verilerle doldurur.
     *
     * @param stajyer Stajyer bilgileri nesnesi.
     * @param izinSebebi İzin sebebi metni.
     * @param izinBaslangic İzin başlangıç tarihi.
     * @param izinBitis İzin bitiş tarihi.
     * @param templatePath İzin belgesi şablonunun yolu.
     * @param outputDirectory Oluşturulan izin belgesinin kaydedileceği dizin.
     * @return Oluşturulan izin belgesi dosyasının tam yolu.
     * @throws IOException Dosya okuma/yazma sırasında bir hata oluşursa.
     */
    public String createIzinBelgesi(Stajyer stajyer, String izinSebebi, LocalDate izinBaslangic, LocalDate izinBitis,
                                   String templatePath, String outputDirectory) throws IOException {

        Path dir = Paths.get(outputDirectory);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String fileName = "İzin_Belgesi_" + stajyer.getAdSoyad().replace(" ", "_") + "_" + LocalDate.now().format(formatter) + ".docx";
        String outputPath = dir.resolve(fileName).toString();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("[BUGUN_TARIH]", LocalDate.now().format(formatter));
        placeholders.put("[AD_SOYAD]", stajyer.getAdSoyad() != null ? stajyer.getAdSoyad() : "");
        placeholders.put("[TC_KIMLIK]", stajyer.getTcKimlik() != null ? stajyer.getTcKimlik() : "");
        placeholders.put("[TELEFON_NO]", stajyer.getTelefonNo() != null ? stajyer.getTelefonNo() : "");
        placeholders.put("[ADRES]", stajyer.getAdres() != null ? stajyer.getAdres() : "");
        
        // OKUL_ADI bilgisi için kontrol
        placeholders.put("[OKUL_ADI]", stajyer.getOkul() != null && stajyer.getOkul().getOkulAdi() != null ? stajyer.getOkul().getOkulAdi() : "");

        placeholders.put("[BOLUM]", stajyer.getBolum() != null ? stajyer.getBolum() : "");
        // Diğer stajyer bilgileri placeholderları...
        placeholders.put("[SINIF]", String.valueOf(stajyer.getSinif()));
        placeholders.put("[DOGUM_TARIHI]", stajyer.getDogumTarihi() != null ? stajyer.getDogumTarihi().format(formatter) : "");
        placeholders.put("[BASLANGIC_TARIHI]", stajyer.getStajBaslangicTarihi() != null ? stajyer.getStajBaslangicTarihi().format(formatter) : "");
        placeholders.put("[BITIS_TARIHI]", stajyer.getStajBitisTarihi() != null ? stajyer.getStajBitisTarihi().format(formatter) : "");
        placeholders.put("[IBAN_NO]", stajyer.getIbanNo() != null ? stajyer.getIbanNo() : "");
        placeholders.put("[REFERANS_AD_SOYAD]", stajyer.getReferans() != null && stajyer.getReferans().getAdSoyad() != null ? stajyer.getReferans().getAdSoyad() : "");
        placeholders.put("[REFERANS_TELEFON]", stajyer.getReferans() != null && stajyer.getReferans().getTelefonNo() != null ? stajyer.getReferans().getTelefonNo() : "");
        placeholders.put("[REFERANS_KURUM]", stajyer.getReferans() != null && stajyer.getReferans().getKurum() != null ? stajyer.getReferans().getKurum() : "");


        // İzin belgesi için placeholder'lar
        placeholders.put("[IZIN_SEBEBI]", izinSebebi != null ? izinSebebi : "");
        placeholders.put("[IZIN_BASLANGIC]", izinBaslangic != null ? izinBaslangic.format(formatter) : "");
        placeholders.put("[IZIN_BITIS]", izinBitis != null ? izinBitis.format(formatter) : "");
        placeholders.put("[BUGUNUN_TARIHI]", LocalDate.now().format(formatter));

        createDocumentFromTemplate(templatePath, outputPath, placeholders);
        return outputPath;
    }

    /**
     * Staj Uygunluk Belgesini oluşturur.
     * Şablon dosyasındaki placeholder'ları ilgili verilerle doldurur.
     *
     * @param stajyer Stajyer bilgileri nesnesi.
     * @param belgeData StajUygunlukBelge nesnesindeki ek belge verileri.
     * @param templatePath Staj uygunluk belgesi şablonunun yolu.
     * @param outputDirectory Oluşturulan uygunluk belgesinin kaydedileceği dizin.
     * @return Oluşturulan uygunluk belgesi dosyasının tam yolu.
     * @throws IOException Dosya okuma/yazma sırasında bir hata oluşursa.
     */
    public String createStajUygunlukBelgesi(Stajyer stajyer, StajUygunlukBelge belgeData,
                                          String templatePath, String outputDirectory) throws IOException {

            LOGGER.log(Level.INFO, "Belge oluşturuluyor. Stajyer ID: {0}", stajyer.getStajyerId());
    LOGGER.log(Level.INFO, "Stajyer Ad Soyad: {0}", stajyer.getAdSoyad());
    LOGGER.log(Level.INFO, "Stajyer Okul Adı: {0}", stajyer.getOkul() != null ? stajyer.getOkul().getOkulAdi() : "Okul Adı Yok");
    LOGGER.log(Level.INFO, "Stajyer Bölüm: {0}", stajyer.getBolum() != null ? stajyer.getBolum() : "Bölüm Yok");
    LOGGER.log(Level.INFO, "Belge Öğrenci No: {0}", belgeData.getOgrenciNo() != null ? belgeData.getOgrenciNo() : "Öğrenci No Yok");

    Path dir = Paths.get(outputDirectory);
    if (!Files.exists(dir)) {
        Files.createDirectories(dir);
    }


        String fileName = "Staj_Uygunluk_Belgesi_" + stajyer.getTcKimlik() + "_" + LocalDate.now().format(formatter) + ".docx";
        String outputPath = dir.resolve(fileName).toString();

        Map<String, String> placeholders = new HashMap<>();
        
        // Şablondaki placeholder'lara göre Stajyer ve StajUygunlukBelge objelerinden verileri çek
        // OKUL_ADI bilgisi için kontrol
        placeholders.put("[OKUL_ADI]", stajyer.getOkul() != null && stajyer.getOkul().getOkulAdi() != null ? stajyer.getOkul().getOkulAdi() : "");
        
        placeholders.put("[FAKULTE]", belgeData.getFakulte() != null ? belgeData.getFakulte() : "");
        placeholders.put("[BOLUM]", stajyer.getBolum() != null ? stajyer.getBolum() : "");
        placeholders.put("[SEHIR]", belgeData.getSehir() != null ? belgeData.getSehir() : "");
        placeholders.put("[OGRENCI_NO]", belgeData.getOgrenciNo() != null ? belgeData.getOgrenciNo() : "");
        placeholders.put("[TC_KIMLIK]", stajyer.getTcKimlik() != null ? stajyer.getTcKimlik() : "");
        placeholders.put("[AD_SOYAD]", stajyer.getAdSoyad() != null ? stajyer.getAdSoyad() : "");
        placeholders.put("[BUGUNUN_TARIHI]", LocalDate.now().format(formatter)); // Belgenin oluşturulduğu tarih
        
        createDocumentFromTemplate(templatePath, outputPath, placeholders);
        return outputPath;
    }

    /**
     * Verilen tarih stringini dd.MM.yyyy formatında LocalDate objesine dönüştürür.
     *
     * @param dateString Dönüştürülecek tarih stringi.
     * @return Dönüştürülmüş LocalDate objesi.
     * @throws DateTimeParseException Eğer tarih stringi belirtilen formatla eşleşmezse.
     */
    public LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, formatter);
    }
}