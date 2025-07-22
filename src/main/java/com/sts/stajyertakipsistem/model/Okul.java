package com.sts.stajyertakipsistem.model;

public class Okul {
    private int okulId;
    private String okulAdi;
    private String okulTuru;

    public Okul() {
    }

    public Okul(int okulId, String okulAdi, String okulTuru) {
        this.okulId = okulId;
        this.okulAdi = okulAdi;
        this.okulTuru = okulTuru;
    }

    public int getOkulId() {
        return okulId;
    }

    public void setOkulId(int okulId) {
        this.okulId = okulId;
    }

    public String getOkulAdi() {
        return okulAdi;
    }

    public void setOkulAdi(String okulAdi) {
        this.okulAdi = okulAdi;
    }

    public String getOkulTuru() {
        return okulTuru;
    }

    public void setOkulTuru(String okulTuru) {
        this.okulTuru = okulTuru;
    }

    @Override
    public String toString() {
        // Tabloda okulun adının görünmesini istediğinizi varsayıyorum
        return okulAdi;
    }
}