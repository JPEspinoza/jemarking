// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import javax.json.JsonObject;
import javax.json.JsonArray;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Iterator;
import java.io.FileInputStream;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.net.URLEncoder;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerEvent;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerListener;
import java.io.File;
import java.util.List;
import cl.uai.webcursos.emarking.desktop.data.Activity;
import javax.swing.event.EventListenerList;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import org.apache.log4j.Logger;

public class UploadWorker implements Runnable
{
    private static Logger logger;
    private Moodle moodle;
    private int courseId;
    private EventListenerList listenerList;
    private Activity activity;
    private boolean merge;
    private String newactivityname;
    private List<File> filesToUpload;
    
    static {
        UploadWorker.logger = Logger.getLogger(UploadWorker.class);
    }
    
    void addProcessingListener(final MoodleWorkerListener l) {
        this.listenerList.add(MoodleWorkerListener.class, l);
    }
    
    protected void fireUploading(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.stepPerformed(e);
        }
    }
    
    protected void fireUploadFinished(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.processFinished(e);
        }
    }
    
    protected void fireUploadStarted(final MoodleWorkerEvent e) {
        final MoodleWorkerListener[] ls = this.listenerList.getListeners(MoodleWorkerListener.class);
        MoodleWorkerListener[] array;
        for (int length = (array = ls).length, i = 0; i < length; ++i) {
            final MoodleWorkerListener l = array[i];
            l.processStarted(e);
        }
    }
    
    void removeProcessingListener(final MoodleWorkerListener l) {
        this.listenerList.remove(MoodleWorkerListener.class, l);
    }
    
    public UploadWorker(final Moodle moodle, final Activity activity, final boolean merge, final String newactivityname, final List<File> filesToUpload, final int courseId) {
        this.moodle = null;
        this.courseId = 0;
        this.listenerList = null;
        this.listenerList = new EventListenerList();
        this.moodle = moodle;
        this.activity = activity;
        this.merge = merge;
        this.newactivityname = newactivityname;
        this.filesToUpload = filesToUpload;
        this.courseId = courseId;
    }
    
    @Override
    public void run() {
        UploadWorker.logger.debug("Starting upload worker");
        UploadWorker.logger.debug("Files to upload: " + this.filesToUpload.size());
        int totalBytes = 0;
        for (final File fileToUpload : this.filesToUpload) {
            if (this.activity != null) {
                UploadWorker.logger.debug("Activity: " + this.activity.getId() + " " + this.activity.getName());
            }
            UploadWorker.logger.debug("New activity name:" + this.newactivityname);
            UploadWorker.logger.debug("Merge:" + this.merge);
            final String CrLf = "\r\n";
            final String mergestring = this.merge ? "1" : "0";
            String uploadUrl = String.valueOf(this.moodle.getUrl()) + this.moodle.getMoodleAjaxUrl() + "?action=upload" + "&course=" + this.courseId + "&merge=" + mergestring + "&username=" + this.moodle.getUsername() + "&password=" + this.moodle.getPassword();
            String cleanNewActivityName;
            try {
                cleanNewActivityName = URLEncoder.encode(this.newactivityname, "UTF-8");
            }
            catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fatal error, could not encode using UTF-8. Check your java installation.");
                return;
            }
            if (this.activity != null) {
                uploadUrl = String.valueOf(uploadUrl) + "&nmid=" + this.activity.getId();
            }
            else {
                uploadUrl = String.valueOf(uploadUrl) + "&nmid=-666&name=" + cleanNewActivityName;
            }
            URLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;
            final StringBuffer response = new StringBuffer();
            try {
                final URL url = new URL(uploadUrl);
                UploadWorker.logger.debug("url:" + url);
                conn = url.openConnection();
                conn.setDoOutput(true);
                final InputStream imgIs = new FileInputStream(fileToUpload);
                final byte[] imgData = new byte[imgIs.available()];
                imgIs.read(imgData);
                imgIs.close();
                String message1 = "";
                message1 = String.valueOf(message1) + "-----------------------------4664151417711" + CrLf;
                message1 = String.valueOf(message1) + "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + fileToUpload.getName() + "\"" + CrLf;
                message1 = String.valueOf(message1) + "Content-Type: application/zip" + CrLf;
                message1 = String.valueOf(message1) + CrLf;
                String message2 = "";
                message2 = String.valueOf(message2) + CrLf + "-----------------------------4664151417711--" + CrLf;
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
                conn.setRequestProperty("Content-Length", String.valueOf(message1.length() + message2.length() + imgData.length));
                os = conn.getOutputStream();
                os.write(message1.getBytes());
                totalBytes += imgData.length;
                int index = 0;
                int size = 1024;
                do {
                    if (index + size > imgData.length) {
                        size = imgData.length - index;
                    }
                    os.write(imgData, index, size);
                    os.flush();
                    index += size;
                    final MoodleWorkerEvent e2 = new MoodleWorkerEvent(this, imgData.length, index, null);
                    this.fireUploading(e2);
                } while (index < imgData.length && !Thread.currentThread().isInterrupted());
                os.write(message2.getBytes());
                os.flush();
                is = conn.getInputStream();
                final char buff = '\u0200';
                final byte[] data = new byte[buff];
                int len;
                do {
                    len = is.read(data);
                    if (len > 0) {
                        response.append(new String(data, 0, len));
                    }
                } while (len > 0);
            }
            catch (Exception e3) {
                e3.printStackTrace();
                return;
            }
            finally {
                try {
                    os.close();
                }
                catch (Exception ex) {}
                try {
                    is.close();
                }
                catch (Exception ex2) {}
            }
            try {
                os.close();
            }
            catch (Exception ex3) {}
            try {
                is.close();
            }
            catch (Exception ex4) {}
            UploadWorker.logger.debug(response.toString());
            JsonArray jarr;
            try {
                jarr = this.moodle.parseMoodleResponse(response.toString());
            }
            catch (Exception e4) {
                e4.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fatal error processing server response after uploading. Check the logs and notify administrator.");
                return;
            }
            final JsonObject jobj = jarr.getJsonObject(0);
            final int id = Integer.parseInt(jobj.getString("id"));
            final String name = jobj.getString("name");
            (this.activity = new Activity()).setId(id);
            this.activity.setName(name);
            UploadWorker.logger.debug("Just added emarking id:" + id + " name:" + name);
        }
        final MoodleWorkerEvent e5 = new MoodleWorkerEvent(this, totalBytes, totalBytes, null);
        this.fireUploadFinished(e5);
    }
}
