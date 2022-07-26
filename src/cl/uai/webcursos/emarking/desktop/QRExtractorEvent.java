// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.util.EventObject;

public class QRExtractorEvent extends EventObject
{
    private static final long serialVersionUID = -5628641904021923334L;
    private QrDecodingResult qrresult;
    private boolean backPage;
    
    public QRExtractorEvent(final Object arg0, final QrDecodingResult result, final boolean isBack) {
        super(arg0);
        this.backPage = false;
        this.qrresult = result;
        this.backPage = isBack;
    }
    
    public QrDecodingResult getQrresult() {
        return this.qrresult;
    }
    
    public boolean isBackPage() {
        return this.backPage;
    }
}
