package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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

    private Context mContext;
    private Diagram mDiagram;
    private int[] mLineColors;

    private EditText mEditRatio;
    private EditText mEditXVal;
    private GraphView mGraph;
    private SeekBar mSeekBar;
    private LinearLayout mHidden;
    private RadioGroup mChoices;

    private static final int firstIndexArray = 0;
    private static final int secondIndexArray = 1;

    private final String diagramName = "d2";

    private LineGraphSeries<DataPoint> mSeek_series;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagram, container, false);
        mContext = view.getContext();

        mDiagram = new Diagram(diagramName, mContext);
        mLineColors = view.getResources().getIntArray(R.array.lineColors);

        mEditRatio = view.findViewById(R.id.fragment_diagram_et_editRatio);
        mEditXVal = view.findViewById(R.id.fragment_diagram_et_enterX);
        mGraph = view.findViewById(R.id.fragment_diagram_gphvw_graph);
        mSeekBar = view.findViewById(R.id.fragment_diagram_skbr_seek_x);
        mHidden = view.findViewById(R.id.fragment_diagram_ll_main_hidden);
        mChoices = view.findViewById(R.id.fragment_diagram_rg_main_choices);


        generateGraph(mDiagram);
        setUpRadioButtons(mDiagram);

        //setEditTextButton(mEditRatio);
        setEditTextButtonXVal(mEditXVal);

        mSeekBar = view.findViewById(R.id.fragment_diagram_skbr_seek_x);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);

        return view;
    }

    private void setEditTextButtonXVal(final EditText setup)
    {
        setup.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0)
                {
                    mGraph.removeSeries(mSeek_series);

                    // These need to be selectable
                    int line1 = 0;
                    int line2 = 2;


                    // Find closest key, value pair for a given key and line
                    double[] keyVal1 = getNearKeyValue(Double.parseDouble(setup.getText().toString()), line1, mDiagram);
                    double[] keyVal2 = getNearKeyValue(Double.parseDouble(setup.getText().toString()), line2, mDiagram);

                    // Find ratio of line2 over line1
                    double ratio;
                    ratio = getRatio(keyVal2[1], keyVal1[1]);
                    if (keyVal1[secondIndexArray] == 0) {
                        mEditRatio.setText("N/A");
                    }
                    else if (Double.isNaN(ratio)){
                        mEditRatio.setText("N/A");
                    }
                    else {
                        mEditRatio.setText(Double.toString(ratio));
                    }

                    mSeek_series = new LineGraphSeries<>(new DataPoint[]{
                            new DataPoint(keyVal2[firstIndexArray], 0),
                            new DataPoint(keyVal2[firstIndexArray], keyVal2[secondIndexArray])
                    });

                    mGraph.addSeries(mSeek_series);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    /***
     * Sets up the Scrollable bar for the slider to change the graph value
     */
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
                if (keyVal1[secondIndexArray] == 0) {
                    mEditRatio.setText("N/A");
                }
                else if (Double.isNaN(ratio)){
                    mEditRatio.setText("N/A");
                }
                else {
                    mEditRatio.setText(Double.toString(ratio));
                }

                mSeek_series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(keyVal2[firstIndexArray], 0),
                        new DataPoint(keyVal2[firstIndexArray], keyVal2[secondIndexArray])
                });

                mGraph.addSeries(mSeek_series);

                //Sets the x value
                mEditXVal.setText("" + xKey);
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
        for (int count = 0; count < diagram.getLength(); count++) {
            DataPoint[] dataPoints = new DataPoint[diagram.getPoints(0).getSize()];
            double[][] points = diagram.getPoints(count).getAllKeyVals();

            for (int countOne = 0; countOne < dataPoints.length; countOne++) {
                dataPoints[countOne] = new DataPoint(points[countOne][firstIndexArray], points[countOne][secondIndexArray]);
            }
            lineGraphSeries[count] = new LineGraphSeries<>(dataPoints);
        }

        for (int count = 0; count < lineGraphSeries.length; count++) {
            mGraph.addSeries(lineGraphSeries[count]);
            int colorIndex = count % mLineColors.length;
            lineGraphSeries[count].setColor(mLineColors[colorIndex]);
        }
    }

    private void setUpRadioButtons(Diagram diagram) {
        for (int count = 0; count < diagram.getLength(); count++) {
            RadioButton newButton = new RadioButton(mContext);
            newButton.setText(diagram.getLineName(count));
            newButton.setTextColor(mLineColors[count]);
            mChoices.addView(newButton);
        }
    }

    private void hideDetails() {
        int childCount = mHidden.getChildCount();
        for (int count = 0; count < childCount; count++) {
            View v = mHidden.getChildAt(count);
            if(v!=mGraph && v!= mSeekBar)
            {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showDetails() {
        int childCount = mHidden.getChildCount();
        for (int count = 0; count < childCount; count++) {
            View v = mHidden.getChildAt(count);
            v.setVisibility(View.VISIBLE);
        }
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

        pair[firstIndexArray] = entry.getKey();
        pair[secondIndexArray] = entry.getValue();

        return pair;
    }

    private double convertX(int raw) {
        return (double) raw / 10;
    }
}
