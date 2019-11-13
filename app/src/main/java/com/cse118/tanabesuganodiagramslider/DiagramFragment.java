package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;
import java.util.TreeMap;


public class DiagramFragment extends Fragment {
    public final static String DIAGRAM_INDEX = "diagram_index";


    private Context mContext;
    private Diagram mDiagram;
    private int[] mLineColors;

    private EditText mEditRatio;
    private GraphView mGraph;
    private SeekBar mSeekBar;
    private LinearLayout mHidden;
    private RadioGroup mChoices;


    private LineGraphSeries<DataPoint> mSeek_series;

    public static DiagramFragment newInstance(int diagramIndex) {
        DiagramFragment fragment = new DiagramFragment();
        Bundle args = new Bundle();
        args.putInt(DIAGRAM_INDEX, diagramIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagram, container, false);
        mContext = view.getContext();

        int diagramIndex = getArguments().getInt(DIAGRAM_INDEX, 0);
        mDiagram = new Diagram(diagramIndex, mContext);

        mLineColors = view.getResources().getIntArray(R.array.lineColors);

        mEditRatio = view.findViewById(R.id.editRatio);
        mGraph = view.findViewById(R.id.graph);
        mSeekBar = view.findViewById(R.id.seek_x);
        mHidden = view.findViewById(R.id.ll_main_hidden);
        mChoices = view.findViewById(R.id.rg_main_choices);
        mSeekBar = view.findViewById(R.id.seek_x);

        generateGraph(mDiagram);
        setUpRadioButtons(mDiagram);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);

        return view;
    }

    private final SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGraph.removeSeries(mSeek_series);

                // These need to be selectable
                int line1 = 0;
                int line2 = 2;

                // Divide progress by 10 and cast to double
                double xKey = convertX(progress);

                // Find closest key, value pair for a given key and line
                double[] keyVal1 = getNearKeyValue(xKey, line1, mDiagram);
                double[] keyVal2 = getNearKeyValue(xKey, line2, mDiagram);

                // Find ratio of line2 over line1
                double ratio;
                ratio = getRatio(keyVal2[1], keyVal1[1]);
                if (keyVal1[1] == 0) {
                    mEditRatio.setText("--.--");
                }
                else if (Double.isNaN(ratio)){
                    mEditRatio.setText("--.--");
                }
                else {
                    mEditRatio.setText(Double.toString(ratio));
                }

                mSeek_series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(keyVal2[0], 0),
                        new DataPoint(keyVal2[0], keyVal2[1])
                });

                mGraph.addSeries(mSeek_series);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                hideDetails();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showDetails();
            }
        };

    private void generateGraph(Diagram diagram){

        LineGraphSeries<DataPoint>[] lineGraphSeries = new LineGraphSeries[diagram.getLength()];
        for (int i = 0; i < diagram.getLength(); i++) {
            DataPoint[] dataPoints = new DataPoint[diagram.getPoints(0).getSize()];
            double[][] points = diagram.getPoints(i).getAllKeyVals();

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

    private void setUpRadioButtons(Diagram diagram) {

        for (int i = 0; i < diagram.getLength(); i++) {
            RadioButton newButton = new RadioButton(mContext);
            newButton.setText(diagram.getLineName(i));
            newButton.setTextColor(mLineColors[i%mLineColors.length]);
            mChoices.addView(newButton);
        }
    }

    private void hideDetails() {
        mHidden.setVisibility(View.INVISIBLE);
    }

    private void showDetails() {
        mHidden.setVisibility(View.VISIBLE);
    }

    private double getRatio(double y2, double y1) {
        double ratio = y2 / y1;

        return Math.floor(ratio * 100) / 100;
    }

    private double[] getNearKeyValue(double key, int line, Diagram diagram) {
        double[] pair = new double[2];
        Diagram.treeClass[] treeMap = diagram.getTreeMap();
        Map.Entry<Double, Double> entry;

        try {
            entry = treeMap[line].getTreeMap().ceilingEntry(key);
        }
        catch (NullPointerException e) {
            entry = treeMap[line].getTreeMap().floorEntry(key);
        }

        pair[0] = entry.getKey();
        pair[1] = entry.getValue();

        return pair;
    }

    private double convertX(int raw) {
        return (double) raw / 10;
    }
}
