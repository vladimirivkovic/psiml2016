package com.example.root.myapplication;

/**
 * Created by root on 22.7.16..
 */
public class PredictionEngine {
    public static float sum(float[] seq) {
        float sum = 0;

        for (float x:seq) {
            sum += x;
        }

        return sum;
    }

    public static float mean(float[] seq) {
        float sum = 0;

        for (float x:seq) {
            sum += x;
        }

        return sum/seq.length;
    }

    public static float std(float[] seq, float mean) {
        double sum = 0;

        for (float x:seq) {
            sum += (x - mean)*(x - mean);
        }

        return (float) Math.sqrt(sum/seq.length);
    }

    public static float var(float mean, float std) {
        return std/mean;
    }

    public static float peakToPeak(float[] seq) {
        float min = seq[0], max = seq[0];

        for (float x:seq) {
            if (x < min) {
                min = x;
            }
            if (x > max) {
                max = x;
            }
        }

        return max - min;
    }

    public static float percentile(float[] seq, int per) {
        float min = seq[0], max = seq[0], up,  cnt = 0;

        for (float x:seq) {
            if (x < min) {
                min = x;
            }
            if (x > max) {
                max = x;
            }
        }

        up = min + (max - min) * per * 0.01f;

        for (float x:seq) {
            if (min<=x && x<=up) {
                cnt++;
            }
        }

        return cnt;
    }

    public static float autocorrelation(float[] seq, float mean) {
        float num = 0, den = 0;

        for (int i=0; i<seq.length-1; i++) {
            num += (seq[i] - mean)*(seq[i+1] - mean);
        }
        for (int i=0; i<seq.length; i++) {
            den += (seq[i] - mean)*(seq[i] - mean);
        }

        return num/den;
    }

    public static float skewness(float[] seq, float mean) {
        float num = 0, den = 0;
        int len = seq.length;

        for (int i=0; i<seq.length; i++) {
            num += (seq[i] - mean)*(seq[i] - mean)*(seq[i] - mean);
            den += (seq[i] - mean)*(seq[i] - mean);
        }

        return (num/len)/(float)Math.pow(den/len, 1.5);
    }

    public static float kurtosis(float[] seq, float mean) {
        float num = 0, den = 0;
        int len = seq.length;

        for (int i=0; i<seq.length; i++) {
            num += (float) Math.pow(seq[i] - mean, 4);
            den += (seq[i] - mean)*(seq[i] - mean);
        }

        return (num/len)/(float)Math.pow(den/len, 3) - 3;
    }

    public static float power(float[] seq) {
        float p = 0;

        for (float x:seq) {
            if (x != 0) {
                p += x * x;
            }
        }

        return p;
    }

    public static float logEnergy(float[] seq) {
        float p = 0;

        for (float x:seq) {
            if (x != 0) {
                p += (float) Math.log(x * x);
            }
        }

        return p;
    }

    public static float max(float[] seq) {
        float max = seq[0];

        for (float x:seq) {
            if (x > max) {
                max = x;
            }
        }

        return max;
    }
}
