/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sts.stajyertakipsistem.model;

import java.time.LocalDate;

public class Evrak {
    private String evrakId;
    private String dosyayolu;
    private LocalDate stajBaslangicTarihi;
    private LocalDate stajBitisTarihi;

    public Evrak( String evrakId,String dosyayolu,LocalDate stajBaslangicTarihi,LocalDate stajBitisTarihi) {
                this.evrakId=evrakId;
                this.dosyayolu = dosyayolu;
                this.stajBaslangicTarihi=stajBaslangicTarihi;
                this.stajBitisTarihi=stajBitisTarihi;        
    }
    public String getEvrakId() { return evrakId; }
    public void setEvrakId(String evrakId) { this.evrakId = evrakId; }
    
    
    public String getDosyayolu() { return dosyayolu; }
    public void setDosyayolu(String dosyayolu) { this.dosyayolu = dosyayolu; }
    
    public LocalDate getStajBaslangicTarihi() { return stajBaslangicTarihi; }
    public void setStajBaslangicTarihi(LocalDate stajBaslangicTarihi) { this.stajBaslangicTarihi = stajBaslangicTarihi; }
    
    public LocalDate getStajBitisTarihi() { return stajBitisTarihi; }
    public void setStajBitisTarihi(LocalDate stajBitisTarihi) { this.stajBitisTarihi = stajBitisTarihi; }
    
   

    
}
