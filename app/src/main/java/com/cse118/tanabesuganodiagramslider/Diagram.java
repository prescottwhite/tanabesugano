package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.TreeMap;

public class Diagram {
    private static final String LOG_TAG = Diagram.class.getSimpleName();
    private static final int MAX_DIAGRAMS = 7;

    private final Context mContext;
    private int mLength; // Dependent on the number of state titles in the first line of the CSV

    private String[] mLineNames;
    private LineMap[] mLineMaps;


    public Diagram(int diagramIndex, Context context) {
        mContext = context;
        readFile(diagramIndex);
        Log.i(LOG_TAG, "Created new Diagram at index: " + diagramIndex);
    }

    private void readFile(int diagramIndex) {
        if (diagramIndex >= MAX_DIAGRAMS) {
            Log.e(LOG_TAG, "Diagram index out of bounds, the app is probably going to crash");
        }
        String diagramFilename = mContext.getResources().getStringArray(R.array.diagrams_filenames)[diagramIndex];

        try (
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(mContext.getAssets().open(
                                diagramFilename)))
        ) {
            // Parse the first line in the csv into names
            String line = buffer.readLine();
            String[] tokens = line.split(",");
            mLineNames = Arrays.copyOfRange(tokens, 1, tokens.length);
            mLength = mLineNames.length;

            // Create and initialize the array based on the number of lines to be drawn
            mLineMaps = new LineMap[mLength];
            for (int i = 0; i < mLength; i++) {
                mLineMaps[i] = new LineMap();
            }

            // Read from each csv line into the lineMaps
            while ((line = buffer.readLine()) != null) {
                tokens = line.split(",");
                try {
                    Double xValue = Double.parseDouble(tokens[0]);
                    for (int i = 0; i < mLength; i++) {
                        try {
                            Double yValue = Double.parseDouble(tokens[i + 1]);
                            mLineMaps[i].put(xValue, yValue);
                        } catch (NumberFormatException e) {
                            Log.w(LOG_TAG, "CSV is missing a Y value", e);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.e(LOG_TAG, "CSV is missing a X value", e);
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, diagramFilename + " failed to open", e);
        }
    }


    public String getLineName(int i) {
        return mLineNames[i];
    }

    public int getLength() {
        return mLength;
    }

    public LineMap[] getTreeMap() {
        return mLineMaps;
    }

    public LineMap getLineMap(int i) {
        return mLineMaps[i];
    }

    public class LineMap extends TreeMap<Double, Double> {
        public DataPoint[] getDataPoints() {
            DataPoint[] dataPoints = new DataPoint[this.size()];
            int i = 0;
            for (Double j : this.keySet()) {
                dataPoints[i] = new DataPoint(j, this.get(j));
                i++;
            }
            return dataPoints;
        }
    }
}
