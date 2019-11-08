package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.TreeMap;


public class DiagramFragment extends Fragment {

    private Diagram mDiagram;
    private GraphView mGraph;

    private int[] mLineColors;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagram, container, false);


        mGraph = (GraphView) view.findViewById(R.id.graph);
        mDiagram = new Diagram(mGraph, "d2", view.getContext());
        mLineColors = view.getResources().getIntArray(R.array.lineColors);

        generateGraph();
        mDiagram.mGraph = mGraph;


        return view;
    }

    private void generateGraph(){

        LineGraphSeries<DataPoint>[] lineGraphSeries = new LineGraphSeries[mDiagram.getLength()];
        for (int i = 0; i < mDiagram.getLength(); i++) {
            DataPoint[] dataPoints = new DataPoint[mDiagram.getPoints(0).getSize()];
            double[][] points = mDiagram.getPoints(i).getAllKeyVals();

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


}
