// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.AffineTransformOp;
import java.awt.image.ImageObserver;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import cl.uai.webcursos.emarking.desktop.EmarkingDesktop;
import java.util.Map;
import java.util.Hashtable;
import javax.swing.event.EventListenerList;
import org.apache.log4j.Logger;

public class MoodleWorker implements Runnable
{
    private static Logger logger;
    private Moodle moodle;
    private int[] rows;
    private Action action;
    private EventListenerList listenerList;
    
    static {
        MoodleWorker.logger = Logger.getLogger(MoodleWorker.class);
    }
    
    public MoodleWorker(final Moodle moodle, final int[] rows, final Action action) {
        this.listenerList = null;
        this.listenerList = new EventListenerList();
        this.moodle = moodle;
        this.rows = rows;
        this.action = action;
    }
    
    public void addRowProcessedListener(final MoodleWorkerListener l) {
        this.listenerList.add(MoodleWorkerListener.class, l);
    }
    
    public void removeRowProcessedListener(final MoodleWorkerListener l) {
        this.listenerList.remove(MoodleWorkerListener.class, l);
    }
    
    protected void fireRowProcessingStarted(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.processStarted(e);
        }
    }
    
    protected void fireRowProcessed(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.stepPerformed(e);
        }
    }
    
    protected void fireRowProcessingFinished(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.processFinished(e);
        }
    }
    
    @Override
    public void run() {
        final Map<Integer, Boolean> rowsprocessed = new Hashtable<Integer, Boolean>();
        MoodleWorkerEvent e = new MoodleWorkerEvent(this, 0, this.rows.length, null);
        this.fireRowProcessingStarted(e);
        int current = 0;
        int[] rows;
        for (int length = (rows = this.rows).length, i = 0; i < length; ++i) {
            final int row = rows[i];
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            if (rowsprocessed.containsKey(row)) {
                MoodleWorker.logger.error("This shouldn't happen");
            }
            else {
                switch (this.action) {
                    case FIX_FROM_FOLLOWING: {
                        try {
                            this.moodle.getPages().fixFromFollowing(row);
                            rowsprocessed.put(row, true);
                            e = new MoodleWorkerEvent(this, current, this.rows.length, row);
                            this.fireRowProcessed(e);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            MoodleWorker.logger.error("Something went wrong! Row:" + row);
                        }
                        break;
                    }
                    case FIX_FROM_PREVIOUS: {
                        try {
                            this.moodle.getPages().fixFromPrevious(row);
                            rowsprocessed.put(row, true);
                            e = new MoodleWorkerEvent(this, current, this.rows.length, row);
                            this.fireRowProcessed(e);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            MoodleWorker.logger.error("Something went wrong! Row:" + row);
                        }
                        break;
                    }
                    case ROTATE180: {
                        try {
                            this.rotatePageAndSave(row);
                            rowsprocessed.put(row, true);
                            if (this.moodle.isDoubleside() && row % 2 == 0) {
                                this.rotatePageAndSave(row + 1);
                                rowsprocessed.put(row + 1, true);
                            }
                            e = new MoodleWorkerEvent(this, current, this.rows.length, row);
                            this.fireRowProcessed(e);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            MoodleWorker.logger.error("Something went wrong! Row:" + row);
                        }
                        break;
                    }
                    case ROTATE180ANDFIX: {
                        try {
                            this.rotatePageAndSave(row);
                            rowsprocessed.put(row, true);
                            if (this.moodle.isDoubleside() && row % 2 == 0) {
                                this.rotatePageAndSave(row + 1);
                                rowsprocessed.put(row + 1, true);
                            }
                            this.moodle.getPages().fixFromPrevious(row);
                            e = new MoodleWorkerEvent(this, current, this.rows.length, row);
                            this.fireRowProcessed(e);
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                            MoodleWorker.logger.error("Something went wrong! Row:" + row);
                        }
                        break;
                    }
                    case SWAPFRONTBACK: {
                        try {
                            if (row % 2 == 0) {
                                this.swapFrontBackPages(row);
                                e = new MoodleWorkerEvent(this, current, this.rows.length, row);
                                this.fireRowProcessed(e);
                            }
                            else {
                                MoodleWorker.logger.error("Invalid page for swapping " + row);
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            MoodleWorker.logger.error("Something went wrong! Row:" + row);
                        }
                        break;
                    }
                    case ROTATE180ANDFIXFROMFOLLOWING: {
                        try {
                            this.rotatePageAndSave(row);
                            rowsprocessed.put(row, true);
                            if (this.moodle.isDoubleside() && row % 2 == 0) {
                                this.rotatePageAndSave(row + 1);
                                rowsprocessed.put(row + 1, true);
                            }
                            this.moodle.getPages().fixFromFollowing(row);
                            e = new MoodleWorkerEvent(this, current, this.rows.length, row);
                            this.fireRowProcessed(e);
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                            MoodleWorker.logger.error("Something went wrong! Row:" + row);
                        }
                        break;
                    }
                }
                ++current;
            }
        }
        e = new MoodleWorkerEvent(this, this.rows.length, this.rows.length, null);
        this.fireRowProcessingFinished(e);
    }
    
    private void swapFrontBackPages(final int row) throws Exception {
        if (!this.moodle.getQr().isDoubleside()) {
            throw new Exception("This can not be done in single side scanning");
        }
        if (this.moodle.getQr().isDoubleside() && row % 2 != 0) {
            throw new Exception(EmarkingDesktop.lang.getString("onlyevenrowsdoubleside"));
        }
        final Page current = this.moodle.getPages().get(row);
        final Page next = this.moodle.getPages().get(row + 1);
        if (current == null || next == null) {
            throw new Exception("Invalid pages in swap operation");
        }
        final File currentFile = new File(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + current.getFilename() + ".png");
        final File nextFile = new File(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + next.getFilename() + ".png");
        final File tempFile = File.createTempFile("emarking", ".png");
        final File currentFileAnonymous = new File(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + current.getFilename() + "_a.png");
        final File nextFileAnonymous = new File(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + next.getFilename() + "_a.png");
        final File tempFileAnonymous = File.createTempFile("emarking_a", ".png");
        if (!currentFile.exists() || !nextFile.exists() || !currentFile.exists() || !nextFile.exists()) {
            throw new Exception("Invalid files for swap operation");
        }
        boolean result = true;
        result &= nextFile.renameTo(tempFile);
        result &= currentFile.renameTo(nextFile);
        result &= tempFile.renameTo(currentFile);
        MoodleWorker.logger.debug("Exchanged " + nextFile + " and " + currentFile);
        result &= nextFileAnonymous.renameTo(tempFileAnonymous);
        result &= currentFileAnonymous.renameTo(nextFileAnonymous);
        result &= tempFileAnonymous.renameTo(currentFileAnonymous);
        MoodleWorker.logger.debug("Exchanged " + nextFileAnonymous + " and " + currentFileAnonymous);
        if (!result) {
            throw new Exception("Fatal error renaming files in swap operation");
        }
    }
    
    private void rotatePageAndSave(final int row) throws Exception {
        final Page p = this.moodle.getPages().get(row);
        final File file = p.getFile();
        BufferedImage image = ImageIO.read(file);
        image = this.rotateImage180static(image);
        ImageIO.write(image, "png", file);
        final File fileAnonymous = new File(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + p.getFilename() + "_a.png");
        final BufferedImage anonymousImage = this.createAnonymousVersionStatic(image);
        ImageIO.write(anonymousImage, "png", fileAnonymous);
    }
    
    private BufferedImage rotateImage180static(BufferedImage image) {
        final AffineTransform tx = AffineTransform.getScaleInstance(-1.0, -1.0);
        tx.translate(-image.getWidth(null), -image.getHeight(null));
        final AffineTransformOp op = new AffineTransformOp(tx, 1);
        image = op.filter(image, null);
        return image;
    }
    
    private BufferedImage createAnonymousVersionStatic(final BufferedImage image) {
        final int cropHeight = (int)(image.getHeight() / 10.0f);
        final BufferedImage anonymousimage = new BufferedImage(image.getWidth(), image.getHeight(), 3);
        final Graphics g = anonymousimage.getGraphics();
        g.drawImage(image, 0, cropHeight, anonymousimage.getWidth(), anonymousimage.getHeight(), 0, cropHeight, image.getWidth(), image.getHeight(), null);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, anonymousimage.getWidth(), cropHeight);
        g.dispose();
        return anonymousimage;
    }
    
    public int getTotalRows() {
        return this.rows.length;
    }
    
    public enum Action
    {
        FIX_FROM_PREVIOUS("FIX_FROM_PREVIOUS", 0), 
        FIX_FROM_FOLLOWING("FIX_FROM_FOLLOWING", 1), 
        ROTATE180("ROTATE180", 2), 
        ROTATE180ANDFIX("ROTATE180ANDFIX", 3), 
        SWAPFRONTBACK("SWAPFRONTBACK", 4), 
        ROTATE180ANDFIXFROMFOLLOWING("ROTATE180ANDFIXFROMFOLLOWING", 5);
        
        private Action(final String name, final int ordinal) {
        }
    }
}
