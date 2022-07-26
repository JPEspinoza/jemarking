// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.io.IOException;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class eMarkingTechnologySamples
{
    private static Logger logger;
    
    static {
        eMarkingTechnologySamples.logger = Logger.getLogger(eMarkingTechnologySamples.class);
    }
    
    public static void main(final String[] args) throws IOException, InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        eMarkingTechnologySamples.logger.info("Executing Image decoder");
        final File samplesdir = new File("samples");
        File[] listFiles;
        for (int length = (listFiles = samplesdir.listFiles()).length, i = 0; i < length; ++i) {
            final File f = listFiles[i];
            if (!f.isDirectory()) {
                if (f.getAbsolutePath().endsWith(".png")) {
                    eMarkingTechnologySamples.logger.info(f.getAbsolutePath());
                    BufferedImage img = null;
                    img = ImageIO.read(f);
                    final BufferedImage imgback = new BufferedImage(img.getWidth(), img.getHeight(), 1);
                    final ImageDecoder decoder = new ImageDecoder(img, imgback, 1, Files.createTempDirectory("emarking", (FileAttribute<?>[])new FileAttribute[0]).toFile(), null);
                    decoder.run();
                    eMarkingTechnologySamples.logger.info("Success:" + decoder.isSuccess());
                    eMarkingTechnologySamples.logger.info("Courseid:" + decoder.getQrResult().getCourseid());
                    eMarkingTechnologySamples.logger.info("Userid:" + decoder.getQrResult().getUserid());
                    eMarkingTechnologySamples.logger.info("Page:" + decoder.getQrResult().getExampage());
                }
            }
        }
        eMarkingTechnologySamples.logger.info("Done");
    }
}
