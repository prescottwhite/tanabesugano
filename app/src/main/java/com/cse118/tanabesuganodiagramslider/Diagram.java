package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class Diagram {
    private GraphView mGraph;
    private String mDiagramName;
    private Context mContext;

    String[] firstLineCSV;

    private hashClass[] hashMapArray;

//    String[][] line1 = new String[202][2];
//    String[][] line2 = new String[202][2];
//    String[][] line3 = new String[202][2];

    public Diagram(GraphView graph, String diagramName, Context context) {
        mGraph = graph;
        mDiagramName = diagramName;
        mContext = context;
        createGraph();
    }

    public class hashClass {
        private HashMap<Double, Double> linesHash = new HashMap<>();

//        public hashClass() {
//            linesHash = new HashMap<Double, Double>();
//        }

        public HashMap getHashMap() {
            return linesHash;
        }

        public int getSize() {
            return linesHash.size();
        }

        public double[][] getAllKeyVals() {
            double[][] allVals = new double[linesHash.size()][2];
            int i = 0;
            for (Double j : linesHash.keySet()) {
                allVals[i][0] = j;
                allVals[i][1] = linesHash.get(j);
                i++;
            }
            return allVals;
        }

        public void putValsInHash(double x, double y) {
            linesHash.put(x, y);
        }
    }

    public void createGraph() {
        try (
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(mContext.getAssets().open(
                                "databases/" + mDiagramName + ".csv")))
        ) {
            String line = "";

            //Get first line of csv and store in firstLineCSV
            line = buffer.readLine();
            String[] tokens = line.split(",");
            firstLineCSV = tokens;

            //Create a new HashMap array based on how many 'y' values are found on each line of csv
            hashMapArray = new hashClass[tokens.length - 1];
            for (int i = 0; i < hashMapArray.length; i++) {
                hashMapArray[i] = new hashClass();
            }

            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
                tokens = line.split(",");

                for (int i = 0; i < tokens.length - 1; i++) {
                    hashMapArray[i].putValsInHash(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[i + 1]));
                }
            }

        } catch (IOException e) {

        }

        LineGraphSeries<DataPoint>[] lineGraphSeries = new LineGraphSeries[hashMapArray.length];
        for (int i = 0; i < hashMapArray.length; i++) {
            double x = 0;
            double y = 0;
            int index = 0;
            DataPoint[] dataPoints = new DataPoint[hashMapArray[0].getSize()];
//            for (Double j : hashMapArray[i].keySet()) {
//                x = j;
//                y = hashMapArray[i].get(j);
//                dataPoints[index] = new DataPoint(x, y);
//                index++;
//            }
            double[][] points = hashMapArray[i].getAllKeyVals();
            for (int j = 0; j < dataPoints.length; j++) {
                dataPoints[j] = new DataPoint(points[j][0], points[j][1]);
                System.out.println("points[" + j + "][] = " + points[j][0] + ", " + points[j][1]);
            }
            lineGraphSeries[i] = new LineGraphSeries<>(dataPoints);
        }

        for (int i = 0; i < lineGraphSeries.length; i++) {
            mGraph.addSeries(lineGraphSeries[i]);
        }

//        DataPoint[] line1Data = new DataPoint[linesHash[0].size()];
//        for (int i = 1; i < line1Data.length + 1; i++) {
//            line1Data[i - 1] = new DataPoint(Double.parseDouble(line1[i][0]), Double.parseDouble(line1[i][1]));
//        }
//        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(line1Data);
//
//        DataPoint[] line2Data = new DataPoint[linesHash[0].size()];
//        for (int i = 1; i < line2Data.length + 1; i++) {
//            line2Data[i - 1] = new DataPoint(Double.parseDouble(line2[i][0]), Double.parseDouble(line2[i][1]));
//        }
//        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(line2Data);
//
//        DataPoint[] line3Data = new DataPoint[linesHash[0].size()];
//        for (int i = 1; i < line3Data.length + 1; i++) {
//            line3Data[i - 1] = new DataPoint(Double.parseDouble(line3[i][0]), Double.parseDouble(line3[i][1]));
//        }
//        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(line3Data);
//
//        mGraph.addSeries(series1);
//        mGraph.addSeries(series2);
//        mGraph.addSeries(series3);
    }
//    HashSet readDGraph = new HashSet();
////        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
////                new DataPoint(0, 50),
////                new DataPoint(10, 60),
////                new DataPoint(20, 70),
////                new DataPoint(25, 75),
////                new DataPoint(40, 75)
////        });
}
