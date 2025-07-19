package com.sts.stajyertakipsistem.model;

import java.io.Serializable;
import java.time.LocalDate;


public abstract class Evrak implements Serializable {
    private String evrakId;
    private String dosyayolu;
    

  
    public Evrak(String evrakId, String dosyayolu
                ) {
        this.evrakId = evrakId;
        this.dosyayolu = dosyayolu;
       
    }
   
    public Evrak() {
    }

   
    public String getEvrakId() { return evrakId; }
    public void setEvrakId(String evrakId) { this.evrakId = evrakId; }

    public String getDosyayolu() { return dosyayolu; }
    public void setDosyayolu(String dosyayolu) { this.dosyayolu = dosyayolu; }

    

    public abstract String getEvrakTuruAciklama();

   
    public boolean isValid() {
        return dosyayolu != null && !dosyayolu.trim().isEmpty();
        
    }
}