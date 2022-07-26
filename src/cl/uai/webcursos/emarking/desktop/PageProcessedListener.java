// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.util.EventListener;

public interface PageProcessedListener extends EventListener
{
    void started(final QRExtractorEvent p0);
    
    void finished(final QRExtractorEvent p0);
    
    void processed(final QRExtractorEvent p0);
}
