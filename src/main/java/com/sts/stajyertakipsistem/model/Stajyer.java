package com.sts.stajyertakipsistem.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public class Stajyer implements Serializable {
   
    private String stajyerId; 
    private String adSoyad;
    private String adres;
    private String telefonNo;
    private String ibanNo;
    private LocalDate dogumTarihi;
    private Okul okul;
    private Referans referans;
    private long tcKimlik;
    private GirisEvrak girisEvrak;
    private CikisEvrak cikisEvrak;
    

    public Stajyer(String adSoyad, String adres, String telefonNo, String ibanNo,
                   LocalDate dogumTarihi, Okul okul, Referans referans, long tcKimlik,
                   GirisEvrak girisEvrak, CikisEvrak cikisEvrak) {
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
        this.stajyerId = null; 
    }

    // İkinci Constructor (Tüm parametrelerle birlikte stajyerId ve staj tarihleri/durum)
    public Stajyer(String stajyerId, String adSoyad, String adres, String telefonNo, String ibanNo,
                   LocalDate dogumTarihi, Okul okul, Referans referans, long tcKimlik,
                   GirisEvrak girisEvrak, CikisEvrak cikisEvrak
                   ) {
        
        this.stajyerId = stajyerId; // stajyerId'yi ata
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
        }


    
    public int getYas() {
        if (dogumTarihi == null) return 0;
        return Period.between(dogumTarihi, LocalDate.now()).getYears();
    }

    // Getter ve Setter metotları
    // stajyerId değişkeni eklendiği için getter/setter'ı da eklendi
    public String getStajyerId() { return stajyerId; }
    public void setStajyerId(String stajyerId) { this.stajyerId = stajyerId; }

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

    public long getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(long tcKimlik) { this.tcKimlik = tcKimlik; }

    public GirisEvrak getGirisEvrak() { return girisEvrak; }
    public void setGirisEvrak(GirisEvrak girisEvrak) { this.girisEvrak = girisEvrak; }

    public CikisEvrak getCikisEvrak() { return cikisEvrak; }
    public void setCikisEvrak(CikisEvrak cikisEvrak) { this.cikisEvrak = cikisEvrak; }

   
    
}