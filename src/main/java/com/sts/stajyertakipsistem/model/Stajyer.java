package com.sts.stajyertakipsistem.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public class Stajyer implements Serializable {

    private int stajyerId; // Değişiklik: String -> int
    private String adSoyad;
    private String adres;
    private String telefonNo;
    private String ibanNo;
    private LocalDate dogumTarihi;
    private Okul okul;
    private Referans referans;
    private String tcKimlik;
    private Evrak girisEvrak;
    private Evrak cikisEvrak;
    private String bolum;
    private int sinif;
    private LocalDate stajBaslangicTarihi;
    private LocalDate stajBitisTarihi;
    private long hesaplananIsGunu; // YENİ EKLENECEK ALAN

    public Stajyer() {}

    // Constructor parametresi int olarak güncellendi ve hesaplananIsGunu eklendi
    public Stajyer(int stajyerId, String adSoyad, String adres, String telefonNo, String ibanNo,
                   LocalDate dogumTarihi, LocalDate stajBaslangicTarihi, LocalDate stajBitisTarihi, Okul okul, Referans referans, String tcKimlik,
                   Evrak girisEvrak, Evrak cikisEvrak,
                   String bolum, int sinif, long hesaplananIsGunu) { // <<-- hesaplananIsGunu parametresi eklendi
        this.stajyerId = stajyerId;
        this.adSoyad = adSoyad;
        this.adres = adres;
        this.telefonNo = telefonNo;
        this.ibanNo = ibanNo;
        this.dogumTarihi = dogumTarihi;
        this.okul = okul;
        this.referans = referans;
        this.tcKimlik = tcKimlik;
        this.girisEvrak = girisEvrak;
        this.cikisEvrak = cikisEvrak;
        this.bolum = bolum;
        this.sinif = sinif;
        this.stajBaslangicTarihi = stajBaslangicTarihi;
        this.stajBitisTarihi = stajBitisTarihi;
        this.hesaplananIsGunu = hesaplananIsGunu; // <<-- atama yapıldı
    }

    public int getYas() {
        if (dogumTarihi == null) return 0;
        return Period.between(dogumTarihi, LocalDate.now()).getYears();
    }

    // Getter ve Setter'lar int olarak güncellendi
    public int getStajyerId() { return stajyerId; }
    public void setStajyerId(int stajyerId) { this.stajyerId = stajyerId; }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }
    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }
    public String getTelefonNo() { return telefonNo; }
    public void setTelefonNo(String telefonNo) { this.telefonNo = telefonNo; }
    public String getIbanNo() { return ibanNo; }
    public void setIbanNo(String ibanNo) { this.ibanNo = ibanNo; }
    public LocalDate getDogumTarihi() { return dogumTarihi; }
    public void setDogumTarihi(LocalDate dogumTarihi) { this.dogumTarihi = dogumTarihi; }
    public Okul getOkul() { return okul; }
    public void setOkul(Okul okul) { this.okul = okul; }
    public Referans getReferans() { return referans; }
    public void setReferans(Referans referans) { this.referans = referans; }
    public String getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(String tcKimlik) { this.tcKimlik = tcKimlik; }
    public Evrak getGirisEvrak() { return girisEvrak; }
    public void setGirisEvrak(Evrak girisEvrak) { this.girisEvrak = girisEvrak; }
    public Evrak getCikisEvrak() { return cikisEvrak; }
    public void setCikisEvrak(Evrak cikisEvrak) { this.cikisEvrak = cikisEvrak; }
    public String getBolum() { return bolum; }
    public void setBolum(String bolum) { this.bolum = bolum; }
    public int getSinif() { return sinif; }
    public void setSinif(int sinif) { this.sinif = sinif; }
    public LocalDate getStajBaslangicTarihi() { return stajBaslangicTarihi; }
    public void setStajBaslangicTarihi(LocalDate stajBaslangicTarihi) { this.stajBaslangicTarihi = stajBaslangicTarihi; }
    public LocalDate getStajBitisTarihi() { return stajBitisTarihi; }
    public void setStajBitisTarihi(LocalDate stajBitisTarihi) { this.stajBitisTarihi = stajBitisTarihi; }

   
    public long getHesaplananIsGunu() {
        return hesaplananIsGunu;
    }

    public void setHesaplananIsGunu(long hesaplananIsGunu) {
        this.hesaplananIsGunu = hesaplananIsGunu;
    }
    
    
    public String getStajyerTuru() {
        return this.okul != null ? this.okul.getOkulTuru() : "";
    }

    public String getBolumForDisplay() {
        if (getStajyerTuru().equalsIgnoreCase("LISE")) {
            return "Alan: " + this.bolum;
        } else {
            return "Bölüm: " + this.bolum;
        }
    }

    @Override
    public String toString() {
        return "Stajyer{" +
               "stajyerId=" + stajyerId +
               ", adSoyad='" + adSoyad + '\'' +
               ", adres='" + adres + '\'' +
               ", telefonNo='" + telefonNo + '\'' +
               ", ibanNo='" + ibanNo + '\'' +
               ", dogumTarihi=" + dogumTarihi +
               ", okul=" + okul +
               ", referans=" + referans +
               ", tcKimlik='" + tcKimlik + '\'' +
               ", girisEvrak=" + girisEvrak +
               ", cikisEvrak=" + cikisEvrak +
               ", bolum='" + bolum + '\'' +
               ", sinif=" + sinif +
               ", stajBaslangicTarihi=" + stajBaslangicTarihi +
               ", stajBitisTarihi=" + stajBitisTarihi +
               ", hesaplananIsGunu=" + hesaplananIsGunu + // <<-- toString'e eklendi
               '}';
    }
}