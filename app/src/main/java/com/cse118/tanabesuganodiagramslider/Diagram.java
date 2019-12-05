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
    private LineMap[] mReversePairLineMaps;

    private int mGroundState;
    private int mGroundState2;

    public Diagram(int diagramIndex, Context context) {
        mContext = context;

        mGroundState = -1;
        mGroundState2 = -1;

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
                // Parse names to get stateNumber
                int stateNumber = Character.getNumericValue(mLineNames[i].charAt(0));
                mLineMaps[i] = new LineMap(stateNumber);
            }
            mReversePairLineMaps = new LineMap[mLength];
            for (int i = 0; i < mLength; i++) {
                // Parse names to get stateNumber
                int stateNumber = Character.getNumericValue(mLineNames[i].charAt(0));
                mReversePairLineMaps[i] = new LineMap(stateNumber);
            }

            // Read from each csv line into the lineMaps
            while ((line = buffer.readLine()) != null) {
                tokens = line.split(",");
                try {
                    Double xValue = Double.parseDouble(tokens[0]);
                    for (int i = 1; i < tokens.length; i++) {
                        try {
                            double yValue = Double.parseDouble(tokens[i]);
                            mLineMaps[i - 1].put(xValue, yValue);
                            mReversePairLineMaps[i - 1].put(yValue, xValue);
                            if (xValue > 0 && yValue == 0) {
                                setGroundState(mLineMaps[i - 1].getStateNumber());
                            }
                        } catch (NumberFormatException e) {
                            Log.w(LOG_TAG, "CSV is missing a Y value");
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

    public int getGroundState() {
        return mGroundState;
    }

    private void setGroundState(int stateNumber) {
        if (mGroundState == -1 || mGroundState == stateNumber) {
            mGroundState = stateNumber;
        } else if (mGroundState2 == -1 || mGroundState2 == stateNumber) {
            mGroundState2 = stateNumber;
        } else {
            Log.e(LOG_TAG, stateNumber + " already set all state numbers");
        }
    }

    public int getGroundState2() {
        return mGroundState2;
    }

    public String getLineName(int i) {
        return mLineNames[i];
    }

    public int getLength() {
        return mLength;
    }


    public LineMap getLineMap(int i) {
        return mLineMaps[i];
    }

    public LineMap getReverseLineMap(int i) {
        return mReversePairLineMaps[i];
    }


    public class LineMap extends TreeMap<Double, Double> {
        private final int mStateNumber;

        LineMap(int stateNumber) {
            super();
            mStateNumber = stateNumber;
        }

        public int getStateNumber() {
            return mStateNumber;
        }

        public DataPoint[] getDataPoints() {
            DataPoint[] dataPoints = new DataPoint[this.size()];
            int i = 0;
            for (Double j : this.keySet()) {
                if (this.get(j) != null) {
                    dataPoints[i] = new DataPoint(j, this.get(j));
                    i++;
                } else {
                    Log.e(LOG_TAG, "LineMap getDataPoints has a null value at j = " + j);
                }
            }
            return dataPoints;
        }
    }
}
