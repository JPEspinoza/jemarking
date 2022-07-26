// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.util.TreeMap;

public class QrDecodingResult
{
    private int userid;
    private int courseid;
    private int exampage;
    private boolean answersheet;
    private String output;
    private String filename;
    private boolean rotated;
    private boolean flipped;
    private boolean success;
    private String backfilename;
    private TreeMap<String, String> answers;
    private int attemptid;
    
    public QrDecodingResult() {
        this.userid = 0;
        this.courseid = 0;
        this.exampage = 0;
        this.answersheet = false;
        this.output = null;
        this.filename = null;
        this.rotated = false;
        this.flipped = false;
        this.success = false;
        this.answers = null;
    }
    
    public boolean isSuccess() {
        return this.success;
    }
    
    public void setSuccess(final boolean success) {
        this.success = success;
    }
    
    public boolean isRotated() {
        return this.rotated;
    }
    
    public void setRotated(final boolean rotated) {
        this.rotated = rotated;
    }
    
    public boolean isFlipped() {
        return this.flipped;
    }
    
    public void setFlipped(final boolean flipped) {
        this.flipped = flipped;
    }
    
    public int getCourseid() {
        return this.courseid;
    }
    
    public void setUserid(final int userid) {
        this.userid = userid;
    }
    
    public void setCourseid(final int courseid) {
        this.courseid = courseid;
    }
    
    public void setExampage(final int exampage) {
        this.exampage = exampage;
    }
    
    public void setOutput(final String output) {
        this.output = output;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public int getExampage() {
        return this.exampage;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public String getOutput() {
        return this.output;
    }
    
    public int getUserid() {
        return this.userid;
    }
    
    public String getBackfilename() {
        return this.backfilename;
    }
    
    public void setBackfilename(final String backfilename) {
        this.backfilename = backfilename;
    }
    
    public boolean isAnswersheet() {
        return this.answersheet;
    }
    
    public void setAnswersheet(final boolean answersheet) {
        this.answersheet = answersheet;
    }
    
    public TreeMap<String, String> getAnswers() {
        return this.answers;
    }
    
    public void setAnswers(final TreeMap<String, String> answers) {
        this.answers = answers;
    }
    
    public int getAttemptId() {
        return this.attemptid;
    }
    
    public void setAttemptId(final int id) {
        this.attemptid = id;
    }
}
