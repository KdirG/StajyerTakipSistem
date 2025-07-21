package com.sts.stajyertakipsistem.model;

public class Evrak {
    private int evrakId; // Değişiklik: String -> int
    private String dosyaYolu;

    public Evrak() {
    }

    // Parametre türü int olarak değiştirildi
    public Evrak(int evrakId, String dosyaYolu) {
        this.evrakId = evrakId;
        this.dosyaYolu = dosyaYolu;
    }

    // Getter/Setter türleri int olarak değiştirildi
    public int getEvrakId() { return evrakId; }
    public void setEvrakId(int evrakId) { this.evrakId = evrakId; }

    public String getDosyaYolu() { return dosyaYolu; }
    public void setDosyaYolu(String dosyaYolu) { this.dosyaYolu = dosyaYolu; }
}