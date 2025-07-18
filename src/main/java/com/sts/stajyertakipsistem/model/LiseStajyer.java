package com.sts.stajyertakipsistem.model;

import java.time.LocalDate;

public class LiseStajyer extends Stajyer {
    private String alan;

    public LiseStajyer(String stajyerId, String adSoyad, String adres, String telefonNo, String ibanNo,
                       LocalDate dogumTarihi, Okul okul, Referans referans, long tcKimlik,
                       GirisEvrak girisEvrak, CikisEvrak cikisEvrak, String alan) {
        // Stajyer sınıfının güncel constructor'ı 'stajyerId' almadığı için bu kaldırıldı.
        // Eğer Stajyer sınıfına 'stajyerId' eklemeyecekseniz, bu parametreyi burada da kaldırın.
        // Şimdilik, Stajyer'in adSoyad ile başlayan constructor'ına göre düzenlenmiştir.
        // Eğer stajyerId'yi Stajyer sınıfında tutacaksanız, Stajyer sınıfına 'private String stajyerId;' eklemeli ve uygun constructor'ı oluşturmalısınız.
        super(adSoyad, adres, telefonNo, ibanNo, dogumTarihi, okul, referans,
              tcKimlik, girisEvrak, cikisEvrak); // Stajyer constructor'ına uygun hale getirildi
        this.alan = alan;
        // Eğer stajyerId'yi LiseStajyer sınıfında tutacaksanız, burada bir 'private String stajyerId;' alanı tanımlayıp atamanız gerekir.
        // this.stajyerId = stajyerId; // Eğer LiseStajyer'e özel bir stajyerId tutacaksanız
    }

    public String getAlan() {
        return alan;
    }

    public void setAlan(String alan) {
        this.alan = alan;
    }
}