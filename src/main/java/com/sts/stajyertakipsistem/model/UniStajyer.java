package com.sts.stajyertakipsistem.model;

import java.time.LocalDate;


public class UniStajyer extends Stajyer {
    private String bolum;
    private int sinif;

    public UniStajyer(String stajyerId, String adSoyad, String adres, String telefonNo, String ibanNo,
                      LocalDate dogumTarihi, Okul okul, Referans referans, long tcKimlik,
                      GirisEvrak girisEvrak, CikisEvrak cikisEvrak,
                      String bolum, int sinif) {
        
        super(stajyerId, adSoyad, adres, telefonNo, ibanNo, dogumTarihi,
              okul, referans, tcKimlik, girisEvrak, cikisEvrak);
        this.bolum = bolum;
        this.sinif = sinif;
    }

   
    public String getBolum() {
        return bolum;
    }

    public void setBolum(String bolum) {
        this.bolum = bolum;
    }

 
    public int getSinif() {
        return sinif;
    }

 
    public void setSinif(int sinif) {
        this.sinif = sinif;
    }
}