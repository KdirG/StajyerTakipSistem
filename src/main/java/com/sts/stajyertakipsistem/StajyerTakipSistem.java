package com.sts.stajyertakipsistem;

import com.sts.stajyertakipsistem.GUI.StajyerListForm; // StajyerListForm'u import ediyoruz
// import javax.swing.JFrame; // Bu satırı artık main metodunuz için ihtiyacınız yoksa kaldırabilirsiniz.
import javax.swing.SwingUtilities;

/**
 *
 * @author kadir
 */
public class StajyerTakipSistem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Bir JFrame oluşturmak yerine, doğrudan StajyerListForm nesnesini oluşturuyoruz
            // Çünkü StajyerListForm'un kendisi zaten bir JFrame.
            StajyerListForm stajyerListForm = new StajyerListForm();
            
            // Oluşturduğumuz StajyerListForm JFrame'ini görünür yapıyoruz.
            stajyerListForm.setVisible(true);

            // Eski kodunuzda olan ve hataya neden olan 'mainFrame' oluşturma ve 'add()' çağırma satırlarını SİLDİK.
            // Örneğin:
            // JFrame mainFrame = new JFrame("..."); // BU SATIRI SİLİN
            // mainFrame.add(stajyerListForm);       // VE BU SATIRI SİLİN
            // mainFrame.pack();                     // BU SATIRLAR ARTIK StajyerListForm'un constructor'ında OLMALI
            // mainFrame.setLocationRelativeTo(null); // BU SATIRLAR ARTIK StajyerListForm'un constructor'ında OLMALI
        });
    }
}