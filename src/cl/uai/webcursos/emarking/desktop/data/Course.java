// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

public class Course
{
    private int id;
    private String shortname;
    private String fullname;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getShortname() {
        return this.shortname;
    }
    
    public void setShortname(final String shortname) {
        this.shortname = shortname;
    }
    
    public String getFullname() {
        return this.fullname;
    }
    
    public void setFullname(final String fullname) {
        this.fullname = fullname;
    }
    
    @Override
    public String toString() {
        return this.getFullname();
    }
}
