package com.sts.stajyertakipsistem.model;

import java.time.LocalDate;

public class Izin {
    private int izinId;
    private int stajyerId; // Foreign key to STAJYERLER table
    private LocalDate izinBaslangic;
    private LocalDate izinBitis;
    private String izinSebep;

    // Constructors
    public Izin() {
    }

    public Izin(int izinId, int stajyerId, LocalDate izinBaslangic, LocalDate izinBitis, String izinSebep) {
        this.izinId = izinId;
        this.stajyerId = stajyerId;
        this.izinBaslangic = izinBaslangic;
        this.izinBitis = izinBitis;
        this.izinSebep = izinSebep;
    }

    // Getters and Setters
    public int getIzinId() {
        return izinId;
    }

    public void setIzinId(int izinId) {
        this.izinId = izinId;
    }

    public int getStajyerId() {
        return stajyerId;
    }

    public void setStajyerId(int stajyerId) {
        this.stajyerId = stajyerId;
    }

    public LocalDate getIzinBaslangic() {
        return izinBaslangic;
    }

    public void setIzinBaslangic(LocalDate izinBaslangic) {
        this.izinBaslangic = izinBaslangic;
    }

    public LocalDate getIzinBitis() {
        return izinBitis;
    }

    public void setIzinBitis(LocalDate izinBitis) {
        this.izinBitis = izinBitis;
    }

    public String getIzinSebep() {
        return izinSebep;
    }

    public void setIzinSebep(String izinSebep) {
        this.izinSebep = izinSebep;
    }
}