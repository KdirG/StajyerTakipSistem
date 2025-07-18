/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sts.stajyertakipsistem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

   

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:firebirdsql://localhost:3050/C:/Users/kadir/OneDrive/Belgeler/NetBeansProjects/StajyerTakipSistem/data/STAJ.FDB";
    private static final String USER = "SYSDBA"; // Varsayılan Firebird kullanıcı adı
    private static final String PASSWORD = "masterkey"; // Varsayılan Firebird parolası


 static {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            System.out.println("Firebird JDBC sürücüsü başarıyla yüklendi.");
        } catch (ClassNotFoundException e) {
            System.err.println("HATA: Firebird JDBC sürücüsü bulunamadı.");
            e.printStackTrace();
           
        }
    }
  public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD); }
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                // System.out.println("Veritabanı bağlantısı kapatıldı."); // İsteğe bağlı log
            } catch (SQLException e) {
                System.err.println("Veritabanı bağlantısı kapatılırken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }   

}

