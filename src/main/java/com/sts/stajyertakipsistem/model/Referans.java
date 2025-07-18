/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sts.stajyertakipsistem.model;




public class Referans{
	private String adSoyad;
    private String telefonNo;
    private String kurum;
	
public Referans(String adSoyad, String telefonNo, String kurum) {
        this.adSoyad = adSoyad;
        this.telefonNo = telefonNo;
        this.kurum=kurum;
    }
    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    public String getTelefonNo() { return telefonNo; }
    public void setTelefonNo(String telefonNo) { this.telefonNo = telefonNo; }
  
    public String getKurum() { return kurum; }
    public void setKurum(String kurum) { this.kurum = kurum; }
	
}
