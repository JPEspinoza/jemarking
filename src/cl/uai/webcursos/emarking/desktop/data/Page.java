// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.io.File;

public class Page
{
    private int row;
    private Student student;
    private Course course;
    private String problem;
    private String filename;
    private int pagenumber;
    private Moodle moodle;
    private boolean rotated;
    
    public boolean isRotated() {
        return this.rotated;
    }
    
    public void setRotated(final boolean rotated) {
        this.rotated = rotated;
    }
    
    public Page(final Moodle _moodle) {
        this.student = null;
        this.course = null;
        this.problem = null;
        this.filename = null;
        this.pagenumber = -1;
        this.moodle = null;
        this.rotated = false;
        this.moodle = _moodle;
    }
    
    public int getPagenumber() {
        return this.pagenumber;
    }
    
    public void setPagenumber(final int pagenumber) {
        this.pagenumber = pagenumber;
    }
    
    public String getProblem() {
        return this.problem;
    }
    
    public void setProblem(final String problem) {
        this.problem = problem;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public void setRow(final int row) {
        this.row = row;
    }
    
    public Student getStudent() {
        return this.student;
    }
    
    public void setStudent(final Student student) {
        this.student = student;
    }
    
    public Course getCourse() {
        return this.course;
    }
    
    public void setCourse(final Course course) {
        this.course = course;
    }
    
    @Override
    public String toString() {
        return "Row:" + this.row + " Student:" + this.student + " Page number:" + this.pagenumber + " Course:" + this.course + " Filename:" + this.filename + " Output:" + this.problem;
    }
    
    public File getFile() {
        final File file = new File(String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + this.filename + ".png");
        return file;
    }
    
    public boolean isProblematic() {
        return this.course == null || this.student == null || this.pagenumber <= 0 || this.filename == null;
    }
}
