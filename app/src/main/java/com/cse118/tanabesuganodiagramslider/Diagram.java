package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.TreeMap;

public class Diagram {

    private String mDiagramName;
    private Context mContext;
    private static Diagram mDiagram;

    private int mDiagramIndex;
    private int mLength;
    private String[] mDiagramPaths;

    private String[] mLineNames;
    private LineMap[] treeMapArray;


    public Diagram(int diagramIndex, Context context) {
        mDiagramIndex = diagramIndex;

        mContext = context;
        createGraph();

        Toast.makeText(context, Integer.toString(diagramIndex) , Toast.LENGTH_LONG).show();


        if(diagramIndex > 6) {
            // TODO: Clean up this error
            Toast.makeText(context, "Warning, Diagram index out of bounds", Toast.LENGTH_LONG);
            mDiagramIndex = 0;
        }
        mDiagram = this;
    }

    public class LineMap extends TreeMap<Double,Double>{
        public LineMap() {
            super();
        }

        public DataPoint[] getDataPoints() {
            DataPoint[] dataPoints = new DataPoint[this.size()];
            int i = 0;
            for(Double j : this.keySet()) {
                dataPoints[i] = new DataPoint(j, this.get(j));
                i++;
            }
            return dataPoints;
        }
    }

    private void createGraph() {
        String diagramFilename = mContext.getResources().getStringArray(R.array.diagrams_filenames)[mDiagramIndex];

        try (
                BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open(
                            diagramFilename)))
        ) {
            String line = "";

            // Get first line of csv and store in firstLineCSV
            // Parse into mLineNames
            line = buffer.readLine();
            String[] tokens = line.split(",");
            mLineNames = Arrays.copyOfRange(tokens, 1, tokens.length);
            mLength = mLineNames.length;


            // Create a new treeMap array based on the number of electron states (lines to be drawn)
            treeMapArray = new LineMap[mLength];
            for (int i = 0; i < mLength; i++) {
                treeMapArray[i] = new LineMap();
            }

            while ((line = buffer.readLine()) != null) {
                tokens = line.split(",");

                try {
                    Double xValue = Double.parseDouble(tokens[0]);
                    for (int i = 0; i < mLength; i++) {

                        try {
                            Double yValue = Double.parseDouble(tokens[i + 1]);
                            treeMapArray[i].put(xValue, yValue);
                        } catch (NumberFormatException e) {
                            Log.w("Diagram", "CSV is missing an y value", e);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.e("Diagram", "CSV is missing an x value", e);
                }
            }

        } catch (IOException e) {
            // TODO: Something at least
        }
    }

    public String getLineName(int i) {
        return mLineNames[i];
    }

    public int getLength(){
        return mLength;
    }

    public LineMap[] getTreeMap() {
        return treeMapArray;
    }

    public LineMap getLineMap(int i){ return treeMapArray[i];}

}
