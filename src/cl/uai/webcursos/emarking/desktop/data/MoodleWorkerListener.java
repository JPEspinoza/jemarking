// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.util.EventListener;

public interface MoodleWorkerListener extends EventListener
{
    void processStarted(final MoodleWorkerEvent p0);
    
    void stepPerformed(final MoodleWorkerEvent p0);
    
    void processFinished(final MoodleWorkerEvent p0);
}
