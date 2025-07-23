package com.sts.stajyertakipsistem.model;

import java.io.Serializable;

public class Referans implements Serializable {

    private int referansId;
    private String adSoyad;
    private String telefonNo;
    private String kurum;

    public Referans() {
    }

    public Referans(int referansId, String adSoyad, String telefonNo, String kurum) {
        this.referansId = referansId;
        this.adSoyad = adSoyad;
        this.telefonNo = telefonNo;
        this.kurum = kurum;
    }

    public int getReferansId() { return referansId; }
    public void setReferansId(int referansId) { this.referansId = referansId; }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    public String getTelefonNo() { return telefonNo; }
    public void setTelefonNo(String telefonNo) { this.telefonNo = telefonNo; }

    public String getKurum() { return kurum; }
    public void setKurum(String kurum) { this.kurum = kurum; }

    @Override
    public String toString() {
        
        
        return adSoyad;
    }
}