package com.sts.stajyertakipsistem.service; 

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import com.sts.stajyertakipsistem.model.Stajyer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private void replacePlaceholdersInParagraph(XWPFParagraph p, Map<String, String> placeholders) {
        String text = p.getText();
        boolean changed = false;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();

            if (text.contains(placeholder)) {
                text = text.replace(placeholder, value);
                changed = true;
            }
        }

        if (changed) {
            List<XWPFRun> runs = p.getRuns();
            for (int i = runs.size() - 1; i >= 0; i--) {
                p.removeRun(i);
            }
            XWPFRun run = p.createRun();
            run.setText(text);
        }
    }

    public String createIzinBelgesi(Stajyer stajyer, String izinSebebi, LocalDate izinBaslangic, LocalDate izinBitis,
                                   String templatePath, String outputDirectory) throws IOException {

        java.nio.file.Path dir = java.nio.file.Paths.get(outputDirectory);
        if (!java.nio.file.Files.exists(dir)) {
            java.nio.file.Files.createDirectories(dir);
        }

        String fileName = "İzin_Belgesi_" + stajyer.getAdSoyad().replace(" ", "_") + "_" + LocalDate.now().format(formatter) + ".docx";
        String outputPath = dir.resolve(fileName).toString();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("[BUGUN_TARIH]", LocalDate.now().format(formatter));
        placeholders.put("[AD_SOYAD]", stajyer.getAdSoyad() != null ? stajyer.getAdSoyad() : "");
        placeholders.put("[TC_KIMLIK]", stajyer.getTcKimlik() != null ? stajyer.getTcKimlik() : "");
        placeholders.put("[TELEFON_NO]", stajyer.getTelefonNo() != null ? stajyer.getTelefonNo() : "");
        placeholders.put("[ADRES]", stajyer.getAdres() != null ? stajyer.getAdres() : "");
        placeholders.put("[OKUL_ADI]", stajyer.getOkul() != null && stajyer.getOkul().getOkulAdi() != null ? stajyer.getOkul().getOkulAdi() : "");
        placeholders.put("[BOLUM]", stajyer.getBolum() != null ? stajyer.getBolum() : "");
        placeholders.put("[SINIF]", String.valueOf(stajyer.getSinif()));
        placeholders.put("[DOGUM_TARIHI]", stajyer.getDogumTarihi() != null ? stajyer.getDogumTarihi().format(formatter) : "");
        placeholders.put("[BASLANGIC_TARIHI]", stajyer.getStajBaslangicTarihi() != null ? stajyer.getStajBaslangicTarihi().format(formatter) : "");
        placeholders.put("[BITIS_TARIHI]", stajyer.getStajBitisTarihi() != null ? stajyer.getStajBitisTarihi().format(formatter) : "");
        placeholders.put("[IBAN_NO]", stajyer.getIbanNo() != null ? stajyer.getIbanNo() : "");
        placeholders.put("[REFERANS_AD_SOYAD]", stajyer.getReferans() != null && stajyer.getReferans().getAdSoyad() != null ? stajyer.getReferans().getAdSoyad() : "");
        placeholders.put("[REFERANS_TELEFON]", stajyer.getReferans() != null && stajyer.getReferans().getTelefonNo() != null ? stajyer.getReferans().getTelefonNo() : "");
        placeholders.put("[REFERANS_KURUM]", stajyer.getReferans() != null && stajyer.getReferans().getKurum() != null ? stajyer.getReferans().getKurum() : "");

        placeholders.put("[IZIN_SEBEBI]", izinSebebi != null ? izinSebebi : "");
        placeholders.put("[IZIN_BASLANGIC_TARIHI]", izinBaslangic != null ? izinBaslangic.format(formatter) : "");
        placeholders.put("[IZIN_BITIS_TARIHI]", izinBitis != null ? izinBitis.format(formatter) : "");

        createDocumentFromTemplate(templatePath, outputPath, placeholders);
        return outputPath;
    }

    public LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, formatter);
    }
}
