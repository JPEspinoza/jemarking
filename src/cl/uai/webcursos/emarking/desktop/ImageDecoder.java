// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import com.google.zxing.Result;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.jhlabs.image.MedianFilter;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Binarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.albertoborsetta.formscanner.api.FormField;

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import com.albertoborsetta.formscanner.api.FormTemplate;
import com.albertoborsetta.formscanner.api.FormField;
import com.albertoborsetta.formscanner.api.FormGroup;
import com.albertoborsetta.formscanner.api.FormQuestion;
import com.albertoborsetta.formscanner.api.commons.Constants;
import java.util.TreeMap;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.ImageObserver;
import java.awt.Image;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import java.awt.image.BufferedImage;
import com.google.zxing.qrcode.QRCodeReader;
import java.io.File;
import org.apache.log4j.Logger;

public class ImageDecoder implements Runnable
{
    private static Logger logger;
    private int filenumber;
    private File tempdir;
    private boolean doubleside;
    private QrDecodingResult qrResult;
    private QRCodeReader reader;
    private BufferedImage image;
    private BufferedImage backimage;
    private BufferedImage anonymous;
    private BufferedImage backanonymous;
    private boolean rotated;
    private boolean success;
    private BufferedImage qr;
    private Moodle moodle;
    
    static {
        ImageDecoder.logger = Logger.getLogger(ImageDecoder.class);
    }
    
    public QrDecodingResult getQrResult() {
        return this.qrResult;
    }
    
    public boolean isDoubleside() {
        return this.doubleside;
    }
    
    public BufferedImage getBackimage() {
        return this.backimage;
    }
    
    public BufferedImage getBackanonymous() {
        return this.backanonymous;
    }
    
    public ImageDecoder(final BufferedImage _img, final BufferedImage _back, final int _filenumber, final File _tmpdir, final Moodle _moodle) {
        this.filenumber = 0;
        this.doubleside = false;
        this.reader = null;
        this.image = null;
        this.backimage = null;
        this.anonymous = null;
        this.backanonymous = null;
        this.rotated = false;
        this.success = false;
        this.image = _img;
        this.backimage = _back;
        this.reader = new QRCodeReader();
        this.filenumber = _filenumber;
        this.tempdir = _tmpdir;
        this.moodle = _moodle;
        if (this.backimage != null) {
            this.doubleside = true;
        }
    }
    
    private BufferedImage createAnonymousVersion(final BufferedImage image) {
        float anonymousPercentage = (float)this.moodle.getAnonymousPercentage();
        final float anonymousPercentageFirstPage = (float)this.moodle.getAnonymousPercentageCustomPage();
        if (this.qrResult.isSuccess() && this.qrResult.getExampage() == this.moodle.getAnonymousCustomPage()) {
            anonymousPercentage = anonymousPercentageFirstPage;
        }
        if (anonymousPercentage == 0.0f) {
            anonymousPercentage = 10.0f;
        }
        final int cropHeight = (int)Math.max(image.getHeight() * (anonymousPercentage / 100.0f), 1.0f);
        final BufferedImage anonymousimage = new BufferedImage(image.getWidth(), image.getHeight(), 3);
        final Graphics g = anonymousimage.getGraphics();
        g.drawImage(image, 0, cropHeight, anonymousimage.getWidth(), anonymousimage.getHeight(), 0, cropHeight, image.getWidth(), image.getHeight(), null);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, anonymousimage.getWidth(), cropHeight);
        g.dispose();
        return anonymousimage;
    }
    
    public BufferedImage getAnonymous() {
        return this.anonymous;
    }
    
    public int getFilenumber() {
        return this.filenumber;
    }
    
    public BufferedImage getImage() {
        return this.image;
    }
    
    public BufferedImage getQr() {
        return this.qr;
    }
    
    public boolean isRotated() {
        return this.rotated;
    }
    
    public boolean isSuccess() {
        return this.success;
    }
    
    private BufferedImage rotateImage180(BufferedImage image) {
        final AffineTransform tx = AffineTransform.getScaleInstance(-1.0, -1.0);
        tx.translate(-image.getWidth(null), -image.getHeight(null));
        final AffineTransformOp op = new AffineTransformOp(tx, 1);
        image = op.filter(image, null);
        return image;
    }
    
    private BufferedImage extractTopRightCornerForQR(final BufferedImage image) {
        final BufferedImage subimage = image.getSubimage(image.getWidth() - image.getWidth() / 4, 0, image.getWidth() / 4, image.getHeight() / 4);
        return subimage;
    }
    
    @Override
    public void run() {
        this.qrResult = this.decodeQR(this.image, this.filenumber);
        if (!this.qrResult.isSuccess() && this.doubleside) {
            final QrDecodingResult qrresultback = this.decodeQR(this.backimage, this.filenumber);
            if (qrresultback.isSuccess()) {
                final BufferedImage tmp = this.image;
                this.image = this.backimage;
                this.backimage = tmp;
                this.qrResult = qrresultback;
            }
        }
        if (this.qrResult.isSuccess() && this.qrResult.isRotated()) {
            this.image = this.rotateImage180(this.image);
            if (this.doubleside) {
                this.backimage = this.rotateImage180(this.backimage);
            }
        }
        this.anonymous = this.createAnonymousVersion(this.image);
        if (this.doubleside) {
            this.backanonymous = this.createAnonymousVersion(this.backimage);
        }
        this.success = this.qrResult.isSuccess();
        this.rotated = this.qrResult.isRotated();
        if (this.success && this.qrResult.isAnswersheet() && this.moodle.getOMRTemplate() != null) {
            final TreeMap<String, String> answers = new TreeMap<String, String>();
            FormTemplate formTemplate = null;
            try {
                final File omrtemplatefile = new File(this.moodle.getOMRTemplate());
                formTemplate = new FormTemplate(omrtemplatefile);
                final FormTemplate filledForm = new FormTemplate(this.qrResult.getFilename(), formTemplate);

                HashMap<String, Integer> crop = new HashMap<String, Integer>();
                crop.put("TOP", 0);
                crop.put("BOTTOM", 0);
                crop.put("LEFT", 0);
                crop.put("RIGHT", 0);

                filledForm.findCorners(anonymous,
                        this.moodle.getOMRthreshold(),
                        this.moodle.getOMRdensity(),
                        Constants.CornerType.ROUND,
                        crop
                );
                filledForm.findPoints(this.anonymous, this.moodle.getOMRthreshold(), this.moodle.getOMRdensity(), this.moodle.getOMRshapeSize());

                // now we have to extract the answers from the filledform
                // we can get "groups", those groups inside have fields, which is what was being used before
                for(String group : filledForm.getGroups().keySet()) {
                    FormGroup fg = filledForm.getGroup(group);

                    for (String field : fg.getFields().keySet()) {
                        FormQuestion ff = fg.getFields().get(field);
                        answers.put(field, ff.getValues());
                    }
                }

                this.qrResult.setAnswers(answers);
            }
            catch (Exception e) {
                ImageDecoder.logger.error("Problem with the OMR template");
                e.printStackTrace();
            }
        }
        try {
            ImageIO.write(this.image, "png", new File(String.valueOf(this.tempdir.getAbsolutePath()) + "/" + this.qrResult.getFilename() + ".png"));
            ImageIO.write(this.anonymous, "png", new File(String.valueOf(this.tempdir.getAbsolutePath()) + "/" + this.qrResult.getFilename() + "_a.png"));
            if (this.doubleside) {
                ImageIO.write(this.backimage, "png", new File(String.valueOf(this.tempdir.getAbsolutePath()) + "/" + this.qrResult.getBackfilename() + ".png"));
                ImageIO.write(this.backanonymous, "png", new File(String.valueOf(this.tempdir.getAbsolutePath()) + "/" + this.qrResult.getBackfilename() + "_a.png"));
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    private BinaryBitmap getBitmapFromBufferedImage(final BufferedImage qrcorner) {
        final LuminanceSource source = new BufferedImageLuminanceSource(qrcorner);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return bitmap;
    }
    
    private QrDecodingResult decodeQR(final BufferedImage image, final int filenumber) {
        BufferedImage qrcorner = this.extractTopRightCornerForQR(image);
        final QrDecodingResult decodingresult = new QrDecodingResult();
        try {
            Result result = null;
            Exception decodeException = null;
            final int maxattempts = 3;
            int attempt = 1;
            while (attempt <= maxattempts) {
                try {
                    final BinaryBitmap bitmap = this.getBitmapFromBufferedImage(qrcorner);
                    result = this.reader.decode(bitmap);
                    decodeException = null;
                    break;
                }
                catch (Exception e) {
                    decodeException = e;
                    ImageDecoder.logger.debug("Attempt " + attempt);
                    ++attempt;
                    final MedianFilter filter = new MedianFilter();
                    final BufferedImage newqrcorner = new BufferedImage(qrcorner.getWidth(), qrcorner.getHeight(), 1);
                    filter.filter(qrcorner, newqrcorner);
                    qrcorner = newqrcorner;
                }
            }
            if (decodeException != null) {
                throw decodeException;
            }
            decodingresult.setOutput(result.getText().replace(" ", "").trim());
            decodingresult.setFilename(decodingresult.getOutput());
            if (decodingresult.getFilename().length() == 0) {
                decodingresult.setFilename("ERROR-EMPTYQR-" + (filenumber + 1));
            }
            else {
                final String[] parts = decodingresult.getFilename().split("-");
                if (parts.length == 5 && parts[4].trim().contains("BB")) {
                    decodingresult.setAttemptId(Integer.parseInt(parts[3]));
                    decodingresult.setAnswersheet(true);
                }
                if (parts.length == 4 && parts[3].trim().contains("R")) {
                    decodingresult.setRotated(true);
                }
                if (parts.length >= 3) {
                    decodingresult.setUserid(Integer.parseInt(parts[0]));
                    decodingresult.setCourseid(Integer.parseInt(parts[1]));
                    decodingresult.setExampage(Integer.parseInt(parts[2]));
                    decodingresult.setFilename(String.valueOf(decodingresult.getUserid()) + "-" + decodingresult.getCourseid() + "-" + decodingresult.getExampage());
                    decodingresult.setSuccess(true);
                }
                else {
                    ImageDecoder.logger.error("QR contains invalid information");
                    decodingresult.setFilename("ERROR-INVALIDPARTSQR-" + (filenumber + 1));
                }
            }
        }
        catch (NotFoundException e2) {
            decodingresult.setFilename("ERROR-NOTFOUND-" + (filenumber + 1));
        }
        catch (ChecksumException e3) {
            decodingresult.setFilename("ERROR-CHECKSUM-" + (filenumber + 1));
        }
        catch (FormatException e4) {
            decodingresult.setFilename("ERROR-CHECKSUM-" + (filenumber + 1));
        }
        catch (Exception e5) {
            decodingresult.setFilename("ERROR-NULL-" + (filenumber + 1));
        }
        decodingresult.setBackfilename(String.valueOf(decodingresult.getFilename()) + "b");
        return decodingresult;
    }
}
