// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Student
{
    private int id;
    private String fullname;
    private String idnumber;
    private int rownumber;
    private int pages;
    private TreeMap<String, String> answers;
    private int attemptid;
    
    public Student() {
        this.pages = 0;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getFullname() {
        return this.fullname;
    }
    
    public void setFullname(final String fullname) {
        this.fullname = fullname;
    }
    
    public String getIdnumber() {
        return this.idnumber;
    }
    
    public void setIdnumber(final String idnumber) {
        this.idnumber = idnumber;
    }
    
    @Override
    public String toString() {
        return this.getFullname();
    }
    
    public int getRownumber() {
        return this.rownumber;
    }
    
    public void setRownumber(final int rownumber) {
        this.rownumber = rownumber;
    }
    
    public void addPage(final Page p) {
        this.setPages(this.getPages() + 1);
    }
    
    public int getPages() {
        return this.pages;
    }
    
    public void setPages(final int pages) {
        this.pages = pages;
    }
    
    public void removePage(final Page current) {
        this.setPages(this.getPages() - 1);
    }
    
    public TreeMap<String, String> getAnswers() {
        return this.answers;
    }
    
    public void setAnswers(final TreeMap<String, String> answers) {
        this.answers = answers;
    }
    
    public String getAnswersValues() {
        String output = "";
        if (this.answers == null) {
            return output;
        }
        for (final Map.Entry<String, String> entry : this.answers.entrySet()) {
            output = String.valueOf(output) + entry.getKey() + ":" + entry.getValue() + ";";
        }
        return output;
    }
    
    public int getAttemptid() {
        return this.attemptid;
    }
    
    public void setAttemptid(final int attemptid) {
        this.attemptid = attemptid;
    }
}
