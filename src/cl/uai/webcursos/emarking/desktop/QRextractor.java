// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import org.ghost4j.GhostscriptException;
import java.io.OutputStream;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptLoggerOutputStream;
import org.apache.log4j.Level;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.event.EventListenerList;
import java.util.Map;
import java.util.TreeMap;
import java.io.File;
import org.apache.log4j.Logger;

public class QRextractor implements Runnable
{
    private FileType fileType;
    private static Logger logger;
    private File tempdir;
    private int totalpages;
    private int maxpages;
    private int threads;
    private int step;
    private int resolution;
    private boolean doubleside;
    private String pdffile;
    private TreeMap<Integer, Map<Integer, String>> decodedpages;
    private TreeMap<Integer, String> errorpages;
    private EventListenerList listenerList;
    private Moodle moodle;
    
    static {
        QRextractor.logger = Logger.getLogger(QRextractor.class);
    }
    
    public FileType getFileType() {
        return this.fileType;
    }
    
    public void setFileType(final FileType fileType) {
        this.fileType = fileType;
    }
    
    public int getResolution() {
        return this.resolution;
    }
    
    public void setResolution(final int resolution) {
        this.resolution = resolution;
    }
    
    public QRextractor(final Moodle _moodle) {
        this.tempdir = null;
        this.totalpages = 0;
        this.maxpages = Integer.MAX_VALUE;
        this.threads = 4;
        this.step = 32;
        this.resolution = 100;
        this.doubleside = false;
        this.pdffile = null;
        this.decodedpages = null;
        this.errorpages = null;
        this.listenerList = null;
        this.listenerList = new EventListenerList();
        this.decodedpages = new TreeMap<Integer, Map<Integer, String>>();
        this.errorpages = new TreeMap<Integer, String>();
        this.moodle = _moodle;
    }
    
    void addPageProcessedListener(final PageProcessedListener l) {
        this.listenerList.add(PageProcessedListener.class, l);
    }
    
    void removePageProcessedListener(final PageProcessedListener l) {
        this.listenerList.remove(PageProcessedListener.class, l);
    }
    
    protected void firePageProcessed(final QRExtractorEvent e) {
        final PageProcessedListener[] ls = this.listenerList.getListeners(PageProcessedListener.class);
        PageProcessedListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final PageProcessedListener l = array[i];
            l.processed(e);
        }
    }
    
    protected void fireExtractionFinished(final QRExtractorEvent e) {
        final PageProcessedListener[] ls = this.listenerList.getListeners(PageProcessedListener.class);
        PageProcessedListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final PageProcessedListener l = array[i];
            l.finished(e);
        }
    }
    
    protected void fireExtractionStarted(final QRExtractorEvent e) {
        final PageProcessedListener[] ls = this.listenerList.getListeners(PageProcessedListener.class);
        PageProcessedListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final PageProcessedListener l = array[i];
            l.started(e);
        }
    }
    
    public TreeMap<Integer, Map<Integer, String>> getDecodedpages() {
        return this.decodedpages;
    }
    
    public TreeMap<Integer, String> getErrorpages() {
        return this.errorpages;
    }
    
    public int getMaxpages() {
        return this.maxpages;
    }
    
    public Object[][] getResultsAsData() {
        final Object[][] data = new Object[this.totalpages][2];
        for (int i = 0; i < this.totalpages; ++i) {
            data[i][0] = i + 1;
            data[i][1] = "N/A";
        }
        return data;
    }
    
    public int getStep() {
        return this.step;
    }
    
    public File getTempdir() {
        return this.tempdir;
    }
    
    public int getTotalpages() {
        return this.totalpages;
    }
    
    public boolean isDoubleside() {
        return this.doubleside;
    }
    
    void removeQRdecodeListener(final PageProcessedListener l) {
        this.listenerList.remove(PageProcessedListener.class, l);
    }
    
    @Override
    public void run() {
        QRextractor.logger.debug("Starting Ghost4j");
        QRextractor.logger.debug("Document: " + this.pdffile);
        QRextractor.logger.debug("Total pages: " + this.totalpages);
        QRextractor.logger.debug("Tempdir: " + this.tempdir);
        QRextractor.logger.debug("Resolution: " + this.resolution + "ppp");
        QRextractor.logger.debug("Maxpages: " + this.maxpages);
        QRextractor.logger.debug("Threads: " + this.threads);
        QRextractor.logger.debug("Doubleside: " + this.doubleside);
        QRextractor.logger.debug("Step: " + this.step);
        QRextractor.logger.debug("File type: " + this.fileType);
        long time = System.currentTimeMillis();
        int currentpage = 0;
        if (this.doubleside && this.step % 2 != 0) {
            ++this.step;
        }
        if (this.doubleside && this.maxpages % 2 != 0 && this.maxpages < this.totalpages) {
            ++this.maxpages;
        }
        if (this.totalpages > this.maxpages) {
            this.totalpages = this.maxpages;
        }
        while (this.totalpages > currentpage && currentpage < this.maxpages) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            long timeperstep = System.currentTimeMillis();
            int lastpage = Math.min(currentpage + this.step - 1, this.totalpages - 1);
            QRextractor.logger.debug("Extracting images from " + (currentpage + 1) + " to " + (lastpage + 1));
            List<BufferedImage> images = null;
            try {
                images = this.getImages(currentpage + 1, lastpage + 1, this.pdffile);
                final long timeperstepextract = System.currentTimeMillis() - timeperstep;
                QRextractor.logger.debug("Extraction of " + images.size() + " pages finished in " + timeperstepextract / 1000L + " seconds");
            }
            catch (Exception e3) {
                e3.printStackTrace();
                break;
            }
            lastpage = currentpage + images.size();
            for (int i = 0; i < images.size() && !Thread.currentThread().isInterrupted(); i += this.threads) {
                final ImageDecoder[] decoders = new ImageDecoder[this.threads];
                final Thread[] decoderthreads = new Thread[this.threads];
                for (int j = 0; j < this.threads; ++j) {
                    final int filenumber = currentpage;
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    if (i + j >= images.size()) {
                        break;
                    }
                    final BufferedImage currentpageimage = images.get(i + j);
                    BufferedImage nextpageimage = null;
                    if (this.doubleside) {
                        nextpageimage = ((currentpage <= lastpage && images.size() > i + j + 1) ? images.get(i + j + 1) : null);
                    }
                    decoders[j] = new ImageDecoder(currentpageimage, nextpageimage, filenumber, this.tempdir, this.moodle);
                    (decoderthreads[j] = new Thread(decoders[j])).start();
                    ++currentpage;
                    if (this.doubleside) {
                        ++currentpage;
                        ++i;
                    }
                }
                for (int j = 0; j < this.threads; ++j) {
                    try {
                        if (decoderthreads[j] != null) {
                            decoderthreads[j].join();
                        }
                    }
                    catch (InterruptedException e4) {
                        e4.printStackTrace();
                    }
                }
                for (int j = 0; j < this.threads && !Thread.currentThread().isInterrupted(); ++j) {
                    final ImageDecoder decoder = decoders[j];
                    if (decoder != null) {
                        final QrDecodingResult result = decoder.getQrResult();
                        if (decoder.isSuccess()) {
                            Map<Integer, String> pages = null;
                            if (this.decodedpages.containsKey(result.getUserid())) {
                                pages = this.decodedpages.get(result.getUserid());
                            }
                            else {
                                pages = new Hashtable<Integer, String>();
                                this.decodedpages.put(result.getUserid(), pages);
                            }
                            pages.put(result.getExampage(), result.getFilename());
                            if (this.doubleside) {
                                pages.put(result.getExampage(), result.getBackfilename());
                            }
                        }
                        else {
                            this.errorpages.put(decoder.getFilenumber(), result.getFilename());
                            if (this.doubleside) {
                                this.errorpages.put(decoder.getFilenumber(), result.getBackfilename());
                            }
                        }
                        final QRExtractorEvent e5 = new QRExtractorEvent(this, decoder.getQrResult(), false);
                        this.firePageProcessed(e5);
                        if (this.doubleside) {
                            final QRExtractorEvent e6 = new QRExtractorEvent(this, decoder.getQrResult(), true);
                            this.firePageProcessed(e6);
                        }
                    }
                }
            }
            timeperstep = System.currentTimeMillis() - timeperstep;
            QRextractor.logger.debug("Extraction + Processing finished in " + timeperstep / 1000L + " seconds");
        }
        time = System.currentTimeMillis() - time;
        QRextractor.logger.debug("Total Extraction finished in " + time / 1000L + " seconds");
        QRextractor.logger.debug("Decoded pages:" + this.decodedpages.size());
        QRextractor.logger.debug("Error pages:" + this.errorpages.size());
        final QRExtractorEvent e7 = new QRExtractorEvent(this, null, false);
        this.fireExtractionFinished(e7);
    }
    
    public void setDoubleside(final boolean doubleside) {
        this.doubleside = doubleside;
        this.setStep();
    }
    
    public void setMaxpages(final int maxpages) {
        this.maxpages = maxpages;
    }
    
    public void setTempdir() {
        try {
            this.tempdir = Files.createTempDirectory("emarking", (FileAttribute<?>[])new FileAttribute[0]).toFile();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public String getTempdirStringPath() {
        return this.tempdir.getAbsolutePath();
    }
    
    public void setTotalpages(final int totalpages) {
        this.totalpages = totalpages;
    }
    
    private List<BufferedImage> getImages(final int first, final int last, final String pdffile) throws Exception {
        if (this.fileType == FileType.PDF) {
            final File inputfile = new File(pdffile);
            final File tmpfile = new File("input.pdf");
            if (tmpfile.exists()) {
                tmpfile.delete();
            }
            try {
                FileUtils.copyFile(inputfile, tmpfile);
            }
            catch (IOException e1) {
                e1.printStackTrace();
                throw new Exception("Impossible to copy file");
            }
            final GhostscriptLoggerOutputStream gsloggerOutStream = new GhostscriptLoggerOutputStream(Level.OFF);
            final Ghostscript gs = Ghostscript.getInstance();
            gs.setStdOut(gsloggerOutStream);
            gs.setStdOut(gsloggerOutStream);
            final String[] gsArgs = { "-dSAFER", "-dBATCH", "-dNOPAUSE", "-sDEVICE=pnggray", "-r" + this.resolution, "-dFirstPage=" + first, "-dLastPage=" + last, "-sOutputFile=tmpfigure%d.png", "input.pdf" };
            try {
                gs.initialize(gsArgs);
                gs.exit();
            }
            catch (GhostscriptException e2) {
                QRextractor.logger.error("ERROR: " + e2.getMessage());
                throw new Exception("Impossible to extract images");
            }
            final List<BufferedImage> images = new ArrayList<BufferedImage>();
            for (int i = 1; i <= last - first + 1; ++i) {
                try {
                    final File f = new File("tmpfigure" + i + ".png");
                    images.add(ImageIO.read(f));
                    f.delete();
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return images;
        }
        if (this.fileType == FileType.ZIP) {
            final List<BufferedImage> images2 = new ArrayList<BufferedImage>();
            for (int j = first; j <= last; ++j) {
                final String filename = String.valueOf(this.tempdir.getAbsolutePath()) + "/Prueba_" + j + ".png";
                try {
                    final File f2 = new File(filename);
                    images2.add(ImageIO.read(f2));
                    f2.delete();
                }
                catch (IOException e4) {
                    e4.printStackTrace();
                    QRextractor.logger.error("Could not read file " + filename);
                }
            }
            return images2;
        }
        throw new Exception("Invalid file type");
    }
    
    public String getPdffile() {
        return this.pdffile;
    }
    
    public void setPdffile(final String pdffile) {
        this.pdffile = pdffile;
    }
    
    public int getMaxThreads() {
        return this.threads;
    }
    
    public void setMaxThreads(final int _threads) {
        this.threads = _threads;
        this.setStep();
    }
    
    private void setStep() {
        this.step = (this.doubleside ? (this.threads * 4) : (this.threads * 2));
    }
    
    public enum FileType
    {
        ZIP("ZIP", 0), 
        PDF("PDF", 1);
        
        private FileType(final String name, final int ordinal) {
        }
    }
}
