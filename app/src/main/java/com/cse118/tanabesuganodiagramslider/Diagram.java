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
    private GraphView mGraph;
    private String mDiagramName;
    private Context mContext;

    private int mLength;
    private int[] mLineColors;

    private String[] mLineNames;
    private treeClass[] treeMapArray;

    public Diagram(GraphView graph, String diagramName, Context context) {
        mGraph = graph;
        mDiagramName = diagramName;
        mContext = context;
        mLineColors = mContext.getResources().getIntArray(R.array.lineColors);
        createGraph();
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
        try (
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(mContext.getAssets().open(
                                "databases/" + mDiagramName + ".csv")))
        ) {
            String line = "";

            // Get first line of csv and store in firstLineCSV
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

        }

        LineGraphSeries<DataPoint>[] lineGraphSeries = new LineGraphSeries[treeMapArray.length];
        for (int i = 0; i < treeMapArray.length; i++) {
            DataPoint[] dataPoints = new DataPoint[treeMapArray[0].getSize()];
            double[][] points = treeMapArray[i].getAllKeyVals();

            for (int j = 0; j < dataPoints.length; j++) {
                dataPoints[j] = new DataPoint(points[j][0], points[j][1]);
            }
            lineGraphSeries[i] = new LineGraphSeries<>(dataPoints);
        }

        for (int i = 0; i < lineGraphSeries.length; i++) {
            mGraph.addSeries(lineGraphSeries[i]);
            int colorIndex = i % mLineColors.length;
            lineGraphSeries[i].setColor(mLineColors[colorIndex]);
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
}
