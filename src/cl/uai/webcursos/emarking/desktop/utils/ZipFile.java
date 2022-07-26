// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.utils;

import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerEvent;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerListener;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import java.io.File;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import java.util.List;
import org.apache.log4j.Logger;

public class ZipFile implements Runnable
{
    private static Logger logger;
    int datalimit;
    List<String> fileList;
    private static String SOURCE_FOLDER;
    private Moodle moodle;
    private List<File> zipfiles;
    private EventListenerList listenerList;
    
    static {
        ZipFile.logger = Logger.getLogger(ZipFile.class);
        ZipFile.SOURCE_FOLDER = "X:\\Reports";
    }
    
    public int getDatalimit() {
        return this.datalimit / 1024 / 1024;
    }
    
    public void setDatalimit(final int datalimit) {
        this.datalimit = datalimit * 1024 * 1024;
    }
    
    public ZipFile(final Moodle moodle) {
        this.datalimit = 104857600;
        this.listenerList = null;
        this.moodle = moodle;
        this.fileList = new ArrayList<String>();
        ZipFile.SOURCE_FOLDER = this.moodle.getQr().getTempdirStringPath();
        this.listenerList = new EventListenerList();
        this.zipfiles = new ArrayList<File>();
        this.setDatalimit(moodle.getMaxZipSize());
    }
    
    public void addProgressListener(final MoodleWorkerListener l) {
        this.listenerList.add(MoodleWorkerListener.class, l);
    }
    
    public void removeProgressListener(final MoodleWorkerListener l) {
        this.listenerList.remove(MoodleWorkerListener.class, l);
    }
    
    protected void fireProgressStarted(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.processStarted(e);
        }
    }
    
    protected void fireProgressFinished(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.processFinished(e);
        }
    }
    
    protected void fireFileAdded(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.stepPerformed(e);
        }
    }
    
    private List<File> zipIt(final String zipFile) {
        final byte[] buffer = new byte[1024];
        final List<File> zips = new ArrayList<File>();
        try {
            int currentfile = 1;
            FileOutputStream fos = new FileOutputStream(String.valueOf(zipFile) + currentfile + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            zips.add(new File(String.valueOf(zipFile) + currentfile + ".zip"));
            String laststudent = "";
            int studentdatasize = 0;
            int accumulateddata = 0;
            int currentimage = 0;
            for (final String file : this.fileList) {
                String zipfilename = String.valueOf(zipFile) + currentfile + ".zip";
                ++currentimage;
                if (!file.equals("answers.txt")) {
                    final String[] parts = file.split("-");
                    if (parts.length != 3) {
                        ZipFile.logger.error("Invalid file in directory " + file);
                    }
                    if (!laststudent.equals(parts[0])) {
                        laststudent = parts[0];
                        accumulateddata += studentdatasize;
                        studentdatasize = 0;
                        if (accumulateddata > this.datalimit) {
                            ++currentfile;
                            accumulateddata = 0;
                            zos.closeEntry();
                            zos.close();
                            zipfilename = String.valueOf(zipFile) + currentfile + ".zip";
                            ZipFile.logger.debug("New file created " + zipfilename);
                            fos = new FileOutputStream(zipfilename);
                            zos = new ZipOutputStream(fos);
                            zips.add(new File(zipfilename));
                        }
                    }
                }
                final ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);
                final FileInputStream in = new FileInputStream(String.valueOf(ZipFile.SOURCE_FOLDER) + File.separator + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                    studentdatasize += len;
                }
                in.close();
                final MoodleWorkerEvent e = new MoodleWorkerEvent(this, currentimage, this.fileList.size(), file);
                this.fireFileAdded(e);
            }
            zos.closeEntry();
            zos.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return zips;
    }
    
    private void generateFileList(final File node) {
        if (node.isFile()) {
            this.fileList.add(this.generateZipEntry(node.toString()));
        }
        if (node.isDirectory()) {
            final String[] subNote = node.list();
            String[] array;
            for (int length = (array = subNote).length, i = 0; i < length; ++i) {
                final String filename = array[i];
                this.generateFileList(new File(node, filename));
            }
        }
    }
    
    public List<File> getZipFiles() {
        return this.zipfiles;
    }
    
    private String generateZipEntry(final String file) {
        return file.substring(ZipFile.SOURCE_FOLDER.length() + 1, file.length());
    }
    
    @Override
    public void run() {
        if (this.moodle.isAnswerSheets()) {
            Path path = Paths.get(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/answers.txt", new String[0]);
            ZipFile.logger.info("Saving answers in json format to " + path.toString());
            try {
                Files.write(path, this.moodle.getStudentOMRAnswers().getBytes(), new OpenOption[0]);
            }
            catch (IOException e1) {
                ZipFile.logger.error("Error writing answers in json");
                e1.printStackTrace();
            }
            path = Paths.get(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/answers.csv", new String[0]);
            ZipFile.logger.info("Saving answers in CSV format to " + path.toString());
            try {
                Files.write(path, this.moodle.getStudentOMRAnswersCSV().getBytes(), new OpenOption[0]);
            }
            catch (IOException e1) {
                ZipFile.logger.error("Error writing answers in CSV");
                e1.printStackTrace();
            }
        }
        this.generateFileList(new File(this.moodle.getQr().getTempdirStringPath()));
        ZipFile.logger.debug("Files to include in zip:" + this.fileList.size());
        MoodleWorkerEvent e2 = new MoodleWorkerEvent(this, 0, this.fileList.size(), "");
        this.fireProgressStarted(e2);
        File zipfile = null;
        try {
            zipfile = File.createTempFile("emarking", ".zip");
            this.zipfiles = this.zipIt(zipfile.getAbsolutePath());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            ZipFile.logger.error(ex.getMessage());
        }
        ZipFile.logger.debug("Zip process finished");
        e2 = new MoodleWorkerEvent(this, this.fileList.size(), this.fileList.size(), "");
        this.fireProgressFinished(e2);
    }
    
    public int unZipIt(final String zipFile) {
        final byte[] buffer = new byte[1024];
        int totalFiles = 0;
        try {
            final File folder = this.moodle.getQr().getTempdir();
            if (!folder.exists()) {
                folder.mkdir();
            }
            final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry(), ++totalFiles) {
                final String fileName = ze.getName();
                final File newFile = new File(this.moodle.getQr().getTempdir() + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                final FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zis.closeEntry();
            zis.close();
            ZipFile.logger.info("Done");
            return totalFiles;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return totalFiles;
        }
    }
}
