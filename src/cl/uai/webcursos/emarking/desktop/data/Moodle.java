// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.io.*;
import java.net.URLConnection;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.net.URLEncoder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.Json;
import javax.json.JsonArray;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Map;
import java.util.Iterator;
import cl.uai.webcursos.emarking.desktop.EmarkingDesktop;
import java.util.Hashtable;
import cl.uai.webcursos.emarking.desktop.QRextractor;
import org.apache.log4j.Logger;

public class Moodle
{
    private static Logger logger;
    public static final String USER_AGENT = "Mozilla/5.0";
    private String moodleUrl;
    private String moodleAjaxUrl;
    private String moodleUsername;
    private String moodlePassword;
    private boolean doubleSide;
    private String maxzipsize;
    private Pages studentPages;
    private QRextractor qrExtractor;
    private String lastfile;
    private String omrTemplate;
    private int threshold;
    private int density;
    private int shapesize;
    private Hashtable<Integer, Student> students;
    private Hashtable<Integer, Course> courses;
    private Hashtable<Integer, Course> usercourses;
    private int anonymousPercentage;
    private int anonymousPercentageCustomPage;
    private int anonymousCustomPage;
    private boolean fakeStudents;
    private boolean answerSheets;
    
    static {
        Moodle.logger = Logger.getLogger(Moodle.class);
    }
    
    public int getAnonymousCustomPage() {
        return this.anonymousCustomPage;
    }
    
    public void setAnonymousCustomPage(final int anonymousCustomPage) {
        this.anonymousCustomPage = anonymousCustomPage;
    }
    
    public int getAnonymousPercentage() {
        return this.anonymousPercentage;
    }
    
    public void setAnonymousPercentage(final int anonymousPercentage) {
        this.anonymousPercentage = anonymousPercentage;
    }
    
    public int getAnonymousPercentageCustomPage() {
        return this.anonymousPercentageCustomPage;
    }
    
    public void setAnonymousPercentageCustomPage(final int anonymousPercentageCustomPage) {
        this.anonymousPercentageCustomPage = anonymousPercentageCustomPage;
    }
    
    public Moodle() {
        this.maxzipsize = "64Mb";
        this.omrTemplate = null;
        this.threshold = 127;
        this.density = 40;
        this.shapesize = 8;
        this.students = new Hashtable<Integer, Student>();
        this.courses = new Hashtable<Integer, Course>();
        this.usercourses = new Hashtable<Integer, Course>();
        this.anonymousPercentage = 10;
        this.anonymousPercentageCustomPage = 10;
        this.anonymousCustomPage = 1;
        this.fakeStudents = false;
        this.answerSheets = false;
        this.qrExtractor = new QRextractor(this);
        this.clearPages();
    }
    
    public void clearPages() {
        this.qrExtractor.setTempdir();
        this.studentPages = new Pages(this);
        this.students = new Hashtable<Integer, Student>();
        this.courses = new Hashtable<Integer, Course>();
    }
    
    public boolean connect() {
        try {
            this.retrieveUserCourses();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Hashtable<Integer, Course> getCourses() {
        return this.courses;
    }
    
    public String getLastfile() {
        return this.lastfile;
    }
    
    public int getMaxthreads() {
        return this.qrExtractor.getMaxThreads();
    }
    
    public int getMaxThreads() {
        return this.qrExtractor.getMaxThreads();
    }
    
    public String getMaxzipsize() {
        return this.maxzipsize;
    }
    
    public int getMaxZipSize() {
        int datasize = 0;
        if (this.maxzipsize.equals(EmarkingDesktop.lang.getString("nosplit"))) {
            datasize = Integer.MAX_VALUE;
        }
        else {
            datasize = Integer.parseInt(this.maxzipsize.toLowerCase().replaceAll("mb", ""));
        }
        return datasize;
    }
    
    public String getMaxZipSizeString() {
        return this.maxzipsize;
    }
    
    public String getMoodleAjaxUrl() {
        return this.moodleAjaxUrl;
    }
    
    public int getOMRdensity() {
        return this.density;
    }
    
    public int getOMRshapeSize() {
        return this.shapesize;
    }
    
    public String getOMRTemplate() {
        return this.omrTemplate;
    }
    
    public int getOMRthreshold() {
        return this.threshold;
    }
    
    public Pages getPages() {
        return this.studentPages;
    }
    
    public String getPassword() {
        return this.moodlePassword;
    }
    
    public QRextractor getQr() {
        return this.qrExtractor;
    }
    
    public QRextractor getQrExtractor() {
        return this.qrExtractor;
    }
    
    public Student getStudentByRowNumber(final int row) {
        for (final Student st : this.students.values()) {
            if (st.getRownumber() == row) {
                return st;
            }
        }
        return null;
    }
    
    public Hashtable<Integer, Student> getStudents() {
        return this.students;
    }
    
    public String getStudentOMRAnswers() {
        final StringBuilder string = new StringBuilder();
        string.append("{\n\"students\" : [\n");
        for (final Map.Entry<Integer, Student> entry : this.students.entrySet()) {
            string.append("\t{ \"userid\" : " + entry.getKey() + ",\n");
            string.append("\t\"attemptid\" : " + entry.getValue().getAttemptid() + ",\n");
            if (entry.getValue().getAnswers() != null) {
                string.append("\t\"answers\" : [\n");
                for (final Map.Entry<String, String> questionEntry : entry.getValue().getAnswers().entrySet()) {
                    string.append("\t\t{\"question\" : \"" + questionEntry.getKey() + "\",");
                    string.append("\t\t\"value\" : \"" + questionEntry.getValue() + "\"},\n");
                }
                string.append("\t\t]\n");
            }
            string.append("\t},\n");
        }
        string.append("]\n}");
        return string.toString();
    }
    
    public String getStudentOMRAnswersCSV() {
        final StringBuilder string = new StringBuilder();
        string.append("userid,attemptid,");
        final StringBuilder studentsstring = new StringBuilder();
        int i = 0;
        for (final Map.Entry<Integer, Student> entry : this.students.entrySet()) {
            studentsstring.append(entry.getKey() + ",");
            studentsstring.append(String.valueOf(entry.getValue().getAttemptid()) + ",");
            if (entry.getValue().getAnswers() != null) {
                for (final Map.Entry<String, String> questionEntry : entry.getValue().getAnswers().entrySet()) {
                    if (i == 0) {
                        string.append(String.valueOf(questionEntry.getKey()) + ",");
                    }
                    studentsstring.append(String.valueOf(questionEntry.getValue()) + ",");
                }
            }
            studentsstring.append("\n");
            ++i;
        }
        string.append("\n");
        return String.valueOf(string.toString()) + studentsstring.toString();
    }
    
    public String getUrl() {
        return this.moodleUrl;
    }
    
    public String getUsername() {
        return this.moodleUsername;
    }
    
    public boolean isDoubleside() {
        return this.doubleSide;
    }
    
    public void loadProperties() {
        final Properties p = new Properties();
        final File f = new File("moodle.properties");
        Label_0054: {
            if (f.exists()) {
                try {
                    p.load(new FileInputStream(f));
                    break Label_0054;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.setMoodleAjaxUrl("mod/emarking/ajax/d.php");
        }
        if (p.containsKey("moodleurl")) {
            this.setUrl(p.getProperty("moodleurl"));
        }
        if (p.containsKey("username")) {
            this.setUsername(p.getProperty("username"));
        }
        if (p.containsKey("filename")) {
            this.setLastfile(p.getProperty("filename"));
        }
        if (p.containsKey("doubleside")) {
            this.setDoubleside(p.getProperty("doubleside").equals("true"));
        }
        if (p.containsKey("maxthreads")) {
            this.setMaxthreads(Integer.parseInt(p.getProperty("maxthreads")));
        }
        if (p.containsKey("resolution")) {
            this.setResolution(Integer.parseInt(p.getProperty("resolution")));
        }
        if (p.containsKey("maxzipsize")) {
            this.setMaxzipsize(p.getProperty("maxzipsize"));
        }
        if (p.containsKey("ajaxurl")) {
            this.setMoodleAjaxUrl(p.getProperty("ajaxurl"));
        }
        if (p.containsKey("omrtemplate")) {
            this.setOMRTemplate(p.getProperty("omrtemplate"));
        }
        if (p.containsKey("threshold")) {
            this.setThreshold(Integer.parseInt(p.getProperty("threshold")));
        }
        if (p.containsKey("density")) {
            this.setDensity(Integer.parseInt(p.getProperty("density")));
        }
        if (p.containsKey("shapesize")) {
            this.setShapeSize(Integer.parseInt(p.getProperty("shapesize")));
        }
        if (p.containsKey("anonymouspercentage")) {
            this.setAnonymousPercentage(Integer.parseInt(p.getProperty("anonymouspercentage")));
        }
        if (p.containsKey("anonymouspercentagecustompage")) {
            this.setAnonymousPercentageCustomPage(Integer.parseInt(p.getProperty("anonymouspercentagecustompage")));
        }
        if (p.containsKey("anonymouscustompage")) {
            this.setAnonymousCustomPage(Integer.parseInt(p.getProperty("anonymouscustompage")));
        }
    }
    
    private String makeMoodleRequest(final String urlpostfix) throws Exception {
        final URL obj = new URL(String.valueOf(this.moodleUrl) + urlpostfix);
        final HttpURLConnection con = (HttpURLConnection)obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        final int responseCode = con.getResponseCode();
        Moodle.logger.debug("Sending 'GET' request to URL : " + this.moodleUrl + urlpostfix.replaceAll("password=.*", "password=xxxx&"));
        Moodle.logger.debug("Response Code : " + responseCode);
        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        final StringBuffer response = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
    
    public JsonArray parseMoodleResponse(final String response) throws Exception {
        JsonReader jsonreader = Json.createReader(new StringReader(response));
        final JsonObject jobj = jsonreader.readObject();
        final String error = jobj.getString("error");
        Moodle.logger.debug("Error code:" + error);
        if (error.trim().equals("")) {
            jsonreader = Json.createReader(new StringReader(jobj.get("values").toString()));
            final JsonArray jarr = jsonreader.readArray();
            return jarr;
        }
        Moodle.logger.error(error);
        throw new Exception(error);
    }
    
    public void retrieveCourseFromId(final int courseid) throws Exception {
        if (this.fakeStudents) {
            this.retrieveUserCourses();
            return;
        }
        final String response = this.makeMoodleRequest(String.valueOf(this.getMoodleAjaxUrl()) + "?action=courseinfo&course=" + courseid + "&username=" + this.moodleUsername + "&password=" + this.moodlePassword);
        final JsonArray jarr = this.parseMoodleResponse(response);
        final JsonObject job = jarr.getJsonObject(0);
        final int id = Integer.parseInt(job.getString("id"));
        final String shortname = job.getString("shortname");
        final String fullname = job.getString("fullname");
        final Course st = new Course();
        st.setId(id);
        st.setShortname(shortname);
        st.setFullname(fullname);
        this.courses.put(id, st);
    }
    
    private void retrieveUserCourses() throws Exception {
        this.usercourses = new Hashtable<Integer, Course>();
        if (this.fakeStudents) {
            this.courses = new Hashtable<Integer, Course>();
            for (int i = 0; i < 6; ++i) {
                final int id = i;
                final String shortname = "course-" + i;
                final String fullname = "Fake course " + i;
                final Course course = new Course();
                course.setId(i);
                course.setShortname(shortname);
                course.setFullname(fullname);
                this.usercourses.put(id, course);
                this.courses.put(id, course);
                Moodle.logger.debug("id:" + id + " shortname:" + shortname + " fullname:" + fullname);
            }
            return;
        }
        final String response = this.makeMoodleRequest(String.valueOf(this.getMoodleAjaxUrl()) + "?action=courses&username=" + this.moodleUsername + "&password=" + this.moodlePassword);
        final JsonArray jarr = this.parseMoodleResponse(response);
        this.usercourses = new Hashtable<Integer, Course>();
        for (int j = 0; j < jarr.size(); ++j) {
            try {
                final JsonObject job = jarr.getJsonObject(j);
                final int id2 = Integer.parseInt(job.getString("id"));
                final String shortname2 = job.getString("shortname");
                final String fullname2 = job.getString("fullname");
                final Course st = new Course();
                st.setId(id2);
                st.setShortname(shortname2);
                st.setFullname(fullname2);
                this.usercourses.put(id2, st);
                Moodle.logger.debug("id:" + id2 + " shortname:" + shortname2 + " fullname:" + fullname2);
            }
            catch (Exception e) {
                Moodle.logger.error(e.getMessage());
            }
        }
    }
    
    public Hashtable<Integer, Activity> retrieveEmarkingActivities(final Hashtable<Integer, Course> courses) {
        final Hashtable<Integer, Activity> output = new Hashtable<Integer, Activity>();
        for (final int courseid : courses.keySet()) {
            try {
                final Hashtable<Integer, Activity> outputCourse = this.retrieveEmarkingActivities(courseid);
                output.putAll(outputCourse);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output;
    }
    
    private Hashtable<Integer, Activity> retrieveEmarkingActivities(final int courseid) throws Exception {
        final String response = this.makeMoodleRequest(String.valueOf(this.getMoodleAjaxUrl()) + "?action=activities&course=" + courseid + "&username=" + this.moodleUsername + "&password=" + this.moodlePassword);
        final Hashtable<Integer, Activity> activities = new Hashtable<Integer, Activity>();
        final JsonArray jarr = this.parseMoodleResponse(response);
        for (int i = 0; i < jarr.size(); ++i) {
            try {
                final JsonObject job = jarr.getJsonObject(i);
                final int id = Integer.parseInt(job.getString("id"));
                final String name = job.getString("name");
                final Activity st = new Activity();
                st.setId(id);
                st.setName(name);
                activities.put(id, st);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return activities;
    }
    
    public void retrieveStudents(final int courseId) throws Exception {
        if (this.students == null) {
            this.students = new Hashtable<Integer, Student>();
        }
        if (this.fakeStudents) {
            for (int i = 0; i < 100; ++i) {
                try {
                    final int id = i + 1;
                    final String idnumber = new StringBuilder(String.valueOf(id)).toString();
                    final String studentname = String.valueOf(id) + ", " + EmarkingDesktop.lang.getString("student");
                    final Student st = new Student();
                    st.setId(id);
                    st.setIdnumber(idnumber);
                    st.setFullname(studentname);
                    if (!this.students.containsKey(id)) {
                        st.setRownumber(this.students.keySet().size());
                        this.students.put(id, st);
                    }
                    Moodle.logger.debug("id:" + id + " student:" + studentname + " idnumber:" + idnumber);
                }
                catch (Exception e) {
                    Moodle.logger.error(e.getMessage());
                }
            }
            return;
        }
        final String response = this.makeMoodleRequest(String.valueOf(this.getMoodleAjaxUrl()) + "?action=students&course=" + courseId + "&username=" + this.moodleUsername + "&password=" + this.moodlePassword);
        final JsonArray jarr = this.parseMoodleResponse(response);
        for (int j = 0; j < jarr.size(); ++j) {
            try {
                final JsonObject job = jarr.getJsonObject(j);
                final int id2 = Integer.parseInt(job.getString("id"));
                final String idnumber2 = job.getString("idnumber");
                final String studentname2 = String.valueOf(job.getString("lastname")) + ", " + job.getString("firstname");
                final Student st2 = new Student();
                st2.setId(id2);
                st2.setIdnumber(idnumber2);
                st2.setFullname(studentname2);
                if (!this.students.containsKey(id2)) {
                    st2.setRownumber(this.students.keySet().size());
                    this.students.put(id2, st2);
                }
                Moodle.logger.debug("id:" + id2 + " student:" + studentname2 + " idnumber:" + idnumber2);
            }
            catch (Exception e2) {
                Moodle.logger.error(e2.getMessage());
            }
        }
    }
    
    public void saveProperties() {
        final Properties p = new Properties();
        final File f = new File("moodle.properties");
        p.setProperty("moodleurl", this.moodleUrl);
        p.setProperty("username", this.moodleUsername);
        p.setProperty("filename", this.lastfile);
        p.setProperty("doubleside", this.doubleSide ? "true" : "false");
        p.setProperty("maxthreads", Integer.toString(this.qrExtractor.getMaxThreads()));
        p.setProperty("resolution", Integer.toString(this.qrExtractor.getResolution()));
        p.setProperty("maxzipsize", this.maxzipsize);
        p.setProperty("ajaxurl", this.moodleAjaxUrl);
        p.setProperty("omrtemplate", (this.omrTemplate == null) ? "" : this.omrTemplate);
        p.setProperty("threshold", Integer.toString(this.threshold));
        p.setProperty("density", Integer.toString(this.density));
        p.setProperty("shapesize", Integer.toString(this.shapesize));
        p.setProperty("anonymouspercentage", Integer.toString(this.anonymousPercentage));
        p.setProperty("anonymouspercentagecustompage", Integer.toString(this.anonymousPercentageCustomPage));
        p.setProperty("anonymouscustompage", Integer.toString(this.anonymousCustomPage));
        try {
            p.store(new FileOutputStream(f), "eMarking for Moodle");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Course[] searchCourses(final String q) throws Exception {
        final String response = this.makeMoodleRequest(String.valueOf(this.getMoodleAjaxUrl()) + "?action=coursesearch&username=" + this.moodleUsername + "&password=" + this.moodlePassword + "&q=" + q);
        final JsonArray jarr = this.parseMoodleResponse(response);
        final Course[] courses = new Course[jarr.size()];
        for (int i = 0; i < jarr.size(); ++i) {
            try {
                final JsonObject job = jarr.getJsonObject(i);
                final int id = Integer.parseInt(job.getString("id"));
                final String shortname = job.getString("shortname");
                final String fullname = job.getString("fullname");
                final Course st = new Course();
                st.setId(id);
                st.setShortname(shortname);
                st.setFullname(fullname);
                courses[i] = st;
                Moodle.logger.debug("id:" + id + " shortname:" + shortname + " fullname:" + fullname);
            }
            catch (Exception e) {
                Moodle.logger.error(e.getMessage());
                return null;
            }
        }
        return courses;
    }
    
    public void setDensity(final int density) {
        this.density = density;
    }
    
    public void setDoubleside(final boolean doubleside) {
        this.doubleSide = doubleside;
    }
    
    public void setLastfile(final String lastfile) {
        this.lastfile = lastfile;
    }
    
    public void setMaxthreads(final int maxthreads) {
        this.qrExtractor.setMaxThreads(maxthreads);
    }
    
    public void setMaxzipsize(final String maxzipsize) {
        this.maxzipsize = maxzipsize;
    }
    
    public void setMoodleAjaxUrl(final String moodleAjaxUrl) {
        this.moodleAjaxUrl = moodleAjaxUrl;
    }
    
    public void setOMRTemplate(final String text) {
        final File f = new File(text);
        if (f.exists() && !f.isDirectory()) {
            this.omrTemplate = text;
        }
        else {
            this.omrTemplate = null;
        }
    }
    
    public void setPassword(final String password) {
        this.moodlePassword = password;
    }
    
    public void setResolution(final int resolution) {
        this.qrExtractor.setResolution(resolution);
    }
    
    public void setShapeSize(final int shapeSize) {
        this.shapesize = shapeSize;
    }
    
    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }
    
    public void setUrl(final String url) {
        this.moodleUrl = url;
    }
    
    public void setUsername(final String username) {
        this.moodleUsername = username;
    }
    
    public boolean uploadFile(final File fileToUpload, final Activity activity, final String newactivityname, final boolean merge, final int courseId) throws Exception {
        final String CrLf = "\r\n";
        final String mergestring = merge ? "1" : "0";
        String uploadUrl = String.valueOf(this.moodleUrl) + this.getMoodleAjaxUrl() + "?action=upload" + "&course=" + courseId + "&merge=" + mergestring + "&username=" + this.moodleUsername + "&password=" + this.moodlePassword;
        if (activity != null) {
            uploadUrl = String.valueOf(uploadUrl) + "&nmid=" + activity.getId();
        }
        else {
            uploadUrl = String.valueOf(uploadUrl) + "&nmid=-666&name=" + URLEncoder.encode(newactivityname, "UTF-8");
        }
        URLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;
        final StringBuffer response = new StringBuffer();
        try {
            final URL url = new URL(uploadUrl);
            Moodle.logger.debug("url:" + url);
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
            int index = 0;
            int size = 1024;
            do {
                if (index + size > imgData.length) {
                    size = imgData.length - index;
                }
                os.write(imgData, index, size);
                index += size;
            } while (index < imgData.length);
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
        catch (Exception e) {
            e.printStackTrace();
            return false;
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
        Moodle.logger.debug(response.toString());
        final JsonArray jarr = this.parseMoodleResponse(response.toString());
        final JsonObject jobj = jarr.getJsonObject(0);
        final int id = Integer.parseInt(jobj.getString("id"));
        final String name = jobj.getString("name");
        Moodle.logger.debug("Just added emarking id:" + id + " name:" + name);
        JOptionPane.showMessageDialog(null, EmarkingDesktop.lang.getString("uploadsuccessfull"));
        return true;
    }
    
    public boolean isAnswerSheets() {
        return this.answerSheets;
    }
    
    public void setAnswerSheets(final boolean answerSheets) {
        this.answerSheets = answerSheets;
    }
    
    public boolean isFakeStudents() {
        return this.fakeStudents;
    }
    
    public void setFakeStudents(final boolean fakeStudents) {
        this.fakeStudents = fakeStudents;
    }

    /**
     * @param password string to encode
     * @return encode password
     */
    public String encodeString(String password){

        try {
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return password;
    }

    /**
     Java runtime version **/
    public String javaRuntimeVersion() {
        return System.getProperty("java.runtime.version");
    }
}
