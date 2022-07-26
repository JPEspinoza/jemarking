// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.awt.EventQueue;
import java.awt.Component;
import javax.swing.UIManager;
import org.apache.log4j.PropertyConfigurator;
import java.util.ResourceBundle;
import java.util.Locale;
import org.apache.log4j.Logger;

public class eMarking
{
    private static Logger logger;
    private static boolean IS_MAC;
    
    static {
        eMarking.logger = Logger.getLogger(eMarking.class);
        eMarking.IS_MAC = false;
    }
    
    public static void main(final String[] args) {
        final Locale locale = Locale.getDefault();
        EmarkingDesktop.lang = ResourceBundle.getBundle("cl.uai.webcursos.emarking.desktop.lang", locale);
        PropertyConfigurator.configure("log4j.properties");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final String lcOSName = System.getProperty("os.name").toLowerCase();
        eMarking.IS_MAC = lcOSName.startsWith("mac os x");
        if (eMarking.IS_MAC) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "eMarking");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        eMarking.logger.info("Initializing eMarking desktop");
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final EmarkingDesktop window = new EmarkingDesktop();
                    window.getFrame().setLocationRelativeTo(null);
                    window.getFrame().setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
