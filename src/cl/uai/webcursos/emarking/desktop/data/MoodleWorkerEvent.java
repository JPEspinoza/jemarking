// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.util.EventObject;

public class MoodleWorkerEvent extends EventObject
{
    private int current;
    private int total;
    private Object output;
    private static final long serialVersionUID = 7606489359709233942L;
    
    public int getTotal() {
        return this.total;
    }
    
    public Object getOutput() {
        return this.output;
    }
    
    public int getCurrent() {
        return this.current;
    }
    
    public MoodleWorkerEvent(final Object source, final int current, final int total, final Object output) {
        super(source);
        this.current = current;
        this.total = total;
        this.output = output;
    }
}
