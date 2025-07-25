package com.sts.stajyertakipsistem.model;

public class StajUygunlukBelge {
    private int belgeId;
    private int stajyerId; 
    private String sehir;
    private String fakulte;
    private String ogrenciNo;

    // Constructorlar
    public StajUygunlukBelge() {
    }

    public StajUygunlukBelge(int belgeId, int stajyerId, String sehir, String fakulte, String ogrenciNo) {
        this.belgeId = belgeId;
        this.stajyerId = stajyerId;
        this.sehir = sehir;
        this.fakulte = fakulte;
        this.ogrenciNo = ogrenciNo;
    }

    // Getter ve Setter Metotları

    public int getBelgeId() {
        return belgeId;
    }

    public void setBelgeId(int belgeId) {
        this.belgeId = belgeId;
    }

    public int getStajyerId() {
        return stajyerId;
    }

    public void setStajyerId(int stajyerId) {
        this.stajyerId = stajyerId;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getFakulte() {
        return fakulte;
    }

    public void setFakulte(String fakulte) {
        this.fakulte = fakulte;
    }

    public String getOgrenciNo() {
        return ogrenciNo;
    }

    public void setOgrenciNo(String ogrenciNo) {
        this.ogrenciNo = ogrenciNo;
    }
}