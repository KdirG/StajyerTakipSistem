/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sts.stajyertakipsistem.db;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String JDBC_DRIVER = "org.firebirdsql.jdbc.FBDriver";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "masterkey";

    private static String DB_URL;

    static {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Firebird JDBC sürücüsü başarıyla yüklendi.");

            // Uygulamanın çalıştığı dizin (currentWorkingDir) genellikle EXE'nin bulunduğu dist klasörünün yolu olacaktır.
            String currentWorkingDir = System.getProperty("user.dir");
            System.out.println("Uygulamanın çalıştığı dizin (currentWorkingDir): " + currentWorkingDir);

            // currentWorkingDir'den (dist) bir üst dizine çık (yani StajyerTakipSistem proje kök dizinine)
            Path parentDir = Paths.get(currentWorkingDir).getParent();
            if (parentDir == null) {
                // Eğer currentWorkingDir zaten dosya sisteminin kökü ise, bu path işlemi sorun çıkarır
                System.err.println("HATA: Projenin kök dizinine ulaşılamadı. currentWorkingDir: " + currentWorkingDir);
                throw new Exception("Proje kök dizini belirlenemedi."); 
            }
            
            String projectRootPath = parentDir.toAbsolutePath().toString(); // Mutlak yolu alalım
            System.out.println("Bulunan Proje Kök Dizini (projectRootPath): " + projectRootPath);


            // Veritabanı dosyasının tam yolunu oluştur: projectRootPath/data/STAJ25.FDB
            String dbFilePath = projectRootPath + File.separator + "data" + File.separator + "STAJ25.FDB";
            
            File dbFile = new File(dbFilePath);
            if (!dbFile.exists()) {
                System.err.println("HATA: Veritabanı dosyası bulunamadı.");
                System.err.println("Aranan yol: " + dbFile.getAbsolutePath()); // Hata mesajında bu yolu görmeliyiz
                // Eğer bu hata devam ederse, veritabanı dosyasının yerini kontrol edin.
                // System.exit(1); 
            } else {
                System.out.println("Veritabanı dosyası bulundu: " + dbFilePath);
            }

            // Firebird JDBC URL'sini oluştur. Firebird path'i "/" bekler.
            DB_URL = "jdbc:firebirdsql://localhost:3050/" + dbFilePath.replace("\\", "/"); 
            
            System.out.println("Oluşturulan DB URL: " + DB_URL);

        } catch (ClassNotFoundException e) {
            System.err.println("HATA: Firebird JDBC sürücüsü bulunamadı.");
            e.printStackTrace();
        } catch (Exception e) { // Path işlemleri için genel bir hata yakalayıcı ekledik
            System.err.println("Veritabanı yolu oluşturulurken beklenmeyen bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null) {
            throw new SQLException("Veritabanı URL'si oluşturulamadı. Sürücü veya dosya hatasını kontrol edin.");
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Veritabanı bağlantısı kapatılırken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}