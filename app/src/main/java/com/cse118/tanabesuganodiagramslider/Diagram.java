package com.cse118.tanabesuganodiagramslider;

import android.content.Context;

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
    private treeClass[] treeMapArray;


    public Diagram(int diagramIndex, Context context) {
        mDiagramIndex = diagramIndex;

        mContext = context;
        createGraph();

        mDiagram = this;
    }

    public class treeClass {
        private TreeMap<Double, Double> lineMap = new TreeMap<>();

        public TreeMap<Double, Double> getTreeMap() {
            return lineMap;
        }

        public int getSize() {
            return lineMap.size();
        }

        public double[][] getAllKeyVals() {
            double[][] allVals = new double[lineMap.size()][2];
            int i = 0;
            for (Double j : lineMap.keySet()) {
                allVals[i][0] = j;
                allVals[i][1] = lineMap.get(j);
                i++;
            }
            return allVals;
        }

        public void putValsInTree(double x, double y) {
            lineMap.put(x, y);
        }
    }

    public void createGraph() {
        int diagramIndex = 0;
        String diagramFilename = mContext.getResources().getStringArray(R.array.diagrams_filenames)[diagramIndex];

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


            // Create a new treeMap array based on how many 'y' values are found on each line of csv
            treeMapArray = new treeClass[tokens.length - 1];
            for (int i = 0; i < treeMapArray.length; i++) {
                treeMapArray[i] = new treeClass();
            }

            while ((line = buffer.readLine()) != null) {
                tokens = line.split(",");

                for (int i = 0; i < tokens.length - 1; i++) {
                    treeMapArray[i].putValsInTree(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[i + 1]));
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

    public treeClass[] getTreeMap() {
        return treeMapArray;
    }

    public treeClass getPoints(int i){ return treeMapArray[i];}

}
