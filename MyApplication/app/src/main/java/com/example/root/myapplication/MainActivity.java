package com.example.root.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class MainActivity extends Activity implements SensorEventListener {
    private static final int sw = 128;

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float[] seqX = new float[sw];
    private float[] seqY = new float[sw];
    private float[] seqZ = new float[sw];
    private int pos = 0;

    private boolean timerStarted = false;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, act;

    Classifier clf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }

//        try {
//            makeClassifier();
//        } catch (Exception e) {
//            //act.setText("jbg");
//            e.printStackTrace();
//        }
        clf = loadClassifier();

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processData(deltaX, deltaY, deltaZ);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        act = (TextView) findViewById(R.id.act);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
//        if (deltaX < 2)
//            deltaX = 0;
//        if (deltaY < 2)
//            deltaY = 0;

//        if (!timerStarted) {
//            new Timer().scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    //Log.d("nesto", "nesto");
//                    //act.setText("nesto " + new Random().nextDouble()*10);
//                }
//            }, 1000, 1000);
//            timerStarted = true;
//        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }

    private Classifier loadClassifier() {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    getResources().openRawResource(R.raw.rdf));
            Classifier cls = (Classifier) ois.readObject();
            ois.close();

            return cls;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processData(float x, float y, float z) {
        seqX[pos] = x;
        seqY[pos] = y;
        seqZ[pos] = z;

        if (pos == sw-1) {
            String activity;
            double pred = predictActivity(seqX, seqY, seqZ);

            if (pred == 0.0) {
                activity = "running";
            } else if (pred == 1.0) {
                activity = "walking";
            } else if (pred == 2.0) {
                activity = "standing";
            } else if (pred == 3.0) {
                activity = "upstairs";
            } else if (pred == 4.0) {
                activity = "downstairs";
            } else {
                activity = "junk";
            }
            act.setText(activity);

            shiftSequences();
            pos = sw/2;
        } else {
            pos++;
        }
    }

    private void shiftSequences() {
        for (int i=0; i<sw/2; i++) {
            seqX[i] = seqX[sw/2+i];
            seqY[i] = seqY[sw/2+i];
            seqZ[i] = seqZ[sw/2+i];
        }
    }

    private double predictActivity(float[] x, float[] y, float[] z) {
        ArrayList<Attribute> atts = new ArrayList<Attribute>(17);
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("0");
        classVal.add("1");
        classVal.add("2");
        classVal.add("3");
        classVal.add("4");
        for (int i=0; i<51; i++) {
            atts.add(new Attribute("content" + i, (ArrayList<String>) null));
        }
        atts.add(new Attribute("@@class@@",classVal));

        Instances dataRaw = new Instances("TestInstances",atts,0);
        double[] instanceValue1 = new double[dataRaw.numAttributes()];

        float[] X = generateFeatures(x);
        float[] Y = generateFeatures(y);
        float[] Z = generateFeatures(z);

        for (int i=0; i < 17; i++) {
            instanceValue1[i] = X[i];
        }
        for (int i=0; i < 17; i++) {
            instanceValue1[17+i] = Y[i];
        }
        for (int i=0; i < 17; i++) {
            instanceValue1[2*17+i] = Z[i];
        }
        instanceValue1[51] = 0;

        dataRaw.add(new DenseInstance(1.0, instanceValue1));
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        //dataRaw.instance(0).setValue(51, "0");


        try {
            return clf.classifyInstance(dataRaw.instance(0));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private float[] generateFeatures(float[] a) {
        float[] ret = new float[17];

        ret[0] = PredictionEngine.sum(a);
        ret[1] = PredictionEngine.mean(a);
        ret[2] = PredictionEngine.std(a, ret[1]);
        ret[3] = PredictionEngine.var(ret[1], ret[2]);
        ret[4] = PredictionEngine.peakToPeak(a);
        ret[5] = PredictionEngine.percentile(a, 10);
        ret[6] = PredictionEngine.percentile(a, 25);
        ret[7] = PredictionEngine.percentile(a, 50);
        ret[8] = PredictionEngine.percentile(a, 75);
        ret[9] = PredictionEngine.percentile(a, 90);
        ret[10] = ret[8] - ret[6];
        ret[11] = PredictionEngine.autocorrelation(a, ret[1]);
        ret[12] = PredictionEngine.skewness(a, ret[1]);
        ret[13] = PredictionEngine.kurtosis(a, ret[1]);
        ret[14] = PredictionEngine.power(a);
        ret[15] = PredictionEngine.logEnergy(a);
        ret[16] = PredictionEngine.max(a);

        return ret;
    }


//    private void makeClassifier() throws Exception {
//        BufferedReader br = null;
//        int numFolds = 10;
//        br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.p7)));
//
//        Instances trainData = new Instances(br);
//        trainData.setClassIndex(trainData.numAttributes() - 1);
//        br.close();
//        RandomForest rf = new RandomForest();
//
//        //   rf.buildClassifier(trainData);
//        //Evaluation evaluation = new Evaluation(trainData);
//        //evaluation.crossValidateModel(rf, trainData, numFolds, new Random(1));
//
//        RandomForest finalRF = new RandomForest();
//        finalRF.buildClassifier(trainData);
//
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/download");
//        myDir.mkdirs();
//
//        String fname = "rdf.model";
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
//
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            ObjectOutputStream oos = new ObjectOutputStream(out);
//            oos.writeObject(finalRF);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            act.setText("haos");
//            e.printStackTrace();
//        }
//
//
////        ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("download/rdf.model", Context.MODE_PRIVATE));
////        oos.writeObject(finalRF);
////        oos.flush();
////        oos.close();
//    }
}

