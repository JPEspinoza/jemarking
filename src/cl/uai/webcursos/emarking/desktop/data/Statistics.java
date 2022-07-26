// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.util.Arrays;

public class Statistics
{
    double[] data;
    double size;
    
    public Statistics(final double[] data) {
        this.data = data;
        this.size = data.length;
    }
    
    double getMean() {
        double sum = 0.0;
        double[] data;
        for (int length = (data = this.data).length, i = 0; i < length; ++i) {
            final double a = data[i];
            sum += a;
        }
        return sum / this.size;
    }
    
    double getVariance() {
        final double mean = this.getMean();
        double temp = 0.0;
        double[] data;
        for (int length = (data = this.data).length, i = 0; i < length; ++i) {
            final double a = data[i];
            temp += (mean - a) * (mean - a);
        }
        return temp / this.size;
    }
    
    double getStdDev() {
        return Math.sqrt(this.getVariance());
    }
    
    public double median() {
        final double[] b = new double[this.data.length];
        System.arraycopy(this.data, 0, b, 0, b.length);
        Arrays.sort(b);
        if (this.data.length % 2 == 0) {
            return (b[b.length / 2 - 1] + b[b.length / 2]) / 2.0;
        }
        return b[b.length / 2];
    }
}
