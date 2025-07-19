package com.sts.stajyertakipsistem.model;

import java.io.Serializable; // Serializable eklendi

public class Referans implements Serializable {
    private String referansId; // Yeni eklendi
    private String adSoyad;
    private String telefonNo;
    private String kurum;

    public Referans() {
        // Boş constructor, JSON serileştirme veya ORM kullanımı için gerekli olabilir.
    }

    public Referans(String referansId, String adSoyad, String telefonNo, String kurum) {
        this.referansId = referansId;
        this.adSoyad = adSoyad;
        this.telefonNo = telefonNo;
        this.kurum = kurum;
    }

    // Getter ve Setter metotları
    public String getReferansId() {
        return referansId;
    }

    public void setReferansId(String referansId) {
        this.referansId = referansId;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }

    public String getTelefonNo() {
        return telefonNo;
    }

    public void setTelefonNo(String telefonNo) {
        this.telefonNo = telefonNo;
    }

    public String getKurum() {
        return kurum;
    }

    public void setKurum(String kurum) {
        this.kurum = kurum;
    }

    @Override
    public String toString() {
        return adSoyad + " (" + kurum + ", ID: " + referansId + ")";
    }
}