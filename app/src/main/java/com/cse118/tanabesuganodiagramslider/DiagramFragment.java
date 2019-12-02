package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;


public class DiagramFragment extends Fragment {
    private static final String LOG_TAG = DiagramFragment.class.getSimpleName();
    public static String DIAGRAM_INDEX = "diagram_index";
    private static int DIAGRAM_MAX_X = 40;
    private static int DIAGRAM_MAX_Y = 80;
    private static int RULER_THICKNESS = 15;


    private Context mContext;
    private Diagram mDiagram;
    private int[] mLineColors;
    private int mPrimaryColor;
    private int mSecondaryColor;

    private EditText mEditRatio;
    private GraphView mGraph;
    private SeekBar mSeekBar;
    private LinearLayout mHidden;
    private LinearLayout mRatios;
    private RadioGroup mChoices;

    private LineGraphSeries<DataPoint> mVirtualRuler;
    private LineGraphSeries<DataPoint> mCalculateRuler;

    private int mProgress;


    private final SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //mGraph.removeSeries(mVirturalRuler);

                mProgress = progress;
                generateY();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                hideDetails();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showDetails();
                generateRatios();
            }
        };

    private final RadioGroup.OnCheckedChangeListener mLineChoiceChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            generateY();
            //generateRatios();
        }
    };

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
        mPrimaryColor = ContextCompat.getColor(mContext, R.color.colorBlack);
        mSecondaryColor = ContextCompat.getColor(mContext, R.color.colorGrey);


        mEditRatio = view.findViewById(R.id.editRatio);
        mGraph = view.findViewById(R.id.graph);
        mSeekBar = view.findViewById(R.id.seek_x);
        mHidden = view.findViewById(R.id.ll_main_radioGroup);
        mChoices = view.findViewById(R.id.rg_main_choices);
        mSeekBar = view.findViewById(R.id.seek_x);


        generateGraph(mDiagram);
        setUpRadioButtons(mDiagram);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mChoices.setOnCheckedChangeListener(mLineChoiceChangeListener);

        mProgress = -1;

        return view;
    }

    private void generateGraph(Diagram diagram){
        // Draw Lines
        for (int i = 0; i < diagram.getLength(); i++) {
            DataPoint[] dataPoints = diagram.getLineMap(i).getDataPoints();
            LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(dataPoints);

            mGraph.addSeries(lineGraphSeries);
            int colorIndex = i % mLineColors.length;
            lineGraphSeries.setColor(mLineColors[colorIndex]);
        }

        // Set Viewport
        Viewport viewport = mGraph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(DIAGRAM_MAX_X);

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(DIAGRAM_MAX_Y);

        mVirtualRuler = new LineGraphSeries<>(
                new DataPoint[]{});
        mGraph.addSeries(mVirtualRuler);
        mVirtualRuler.setColor(mPrimaryColor);
        mVirtualRuler.setThickness(RULER_THICKNESS);

        mCalculateRuler = new LineGraphSeries<>();
        mGraph.addSeries(mCalculateRuler);
        mCalculateRuler.setColor(mSecondaryColor);
        mCalculateRuler.setThickness(RULER_THICKNESS);
    }

    private void setUpRadioButtons(Diagram diagram) {
        for (int i = 0; i < diagram.getLength(); i++) {
            RadioButton newButton = new RadioButton(mContext);
            newButton.setText(diagram.getLineName(i));
            newButton.setId(i);
            int colorIndex = i % mLineColors.length;
            newButton.setTextColor(mLineColors[colorIndex]);
            mChoices.addView(newButton);
        }
    }

    private double getRatio(double y2, double y1) {
        double ratio = y2 / y1;

        return Math.floor(ratio * 100) / 100;
    }

    private void generateRatios() {
        int lineIndex = mChoices.getCheckedRadioButtonId();

        if (lineIndex >= 0) {
            double progressX = convertX(mProgress);
            int diagramLength = mDiagram.getLength();

            int arraySize = diagramLength * 2;
            double[] ratios = new double[arraySize];
            String[] ratioExpressions = new String[arraySize];

            double[] kvPairSelected = getNearKeyValue(progressX, lineIndex);
            double[] kvPairOthers;

            String lineNameSelected = mDiagram.getLineName(lineIndex);
            String lineNameOthers = "";

            for (int i = 0; i < diagramLength; i++) {
                if (lineIndex != i) {
                    kvPairOthers = getNearKeyValue(progressX, i);
                    ratios[i] = getRatio(kvPairSelected[1], kvPairOthers[1]);
                    ratios[i + diagramLength] = getRatio(kvPairOthers[1], kvPairSelected[1]);

                    lineNameOthers = mDiagram.getLineName(i);
                }
                if (ratios[i] != 0) {
                    ratioExpressions[i] = lineNameSelected + " / " + lineNameOthers + " = " + ratios[i];
                    ratioExpressions[i + diagramLength] = lineNameOthers + " / " + lineNameSelected + " = " + ratios[i + diagramLength];

                    Log.d("TAG", "" + ratioExpressions[i]);
                    Log.d("TAG", "" + ratioExpressions[i + diagramLength]);
                }
            }

        }
    }

    private void hideDetails() {
        mHidden.setVisibility(View.INVISIBLE);
    }

    private void showDetails() {
        mHidden.setVisibility(View.VISIBLE);
    }

    private double[] getNearKeyValue(double key, int line) {
        double[] pair = new double[2];
        Diagram.LineMap[] treeMap = mDiagram.getLineMaps();
        Map.Entry<Double, Double> entry;

        try {
            entry = treeMap[line].ceilingEntry(key);
        }
        catch (NullPointerException e) {
            entry = treeMap[line].floorEntry(key);
        }

        pair[0] = entry.getKey();
        pair[1] = entry.getValue();

        return pair;
    }

    private double convertX(int raw) {
        return (double) raw / 10;
    }

    private void generateY() {
        // TODO: run this onCLick for radio group
        // TODO: grey out  progress bar
        Double progressX = convertX(mProgress);

        int lineIndex = mChoices.getCheckedRadioButtonId();
        Log.i(LOG_TAG, "calculating based on radio button id: " + lineIndex);
        if (lineIndex >= 0) {

            double[] kvPair = getNearKeyValue(progressX, lineIndex);
            mEditRatio.setText(Double.toString(kvPair[1]));

            mCalculateRuler.resetData(new DataPoint[]{
                    new DataPoint(kvPair[0], 0),
                    new DataPoint(kvPair[0], kvPair[1])
            });
            mVirtualRuler.resetData(new DataPoint[]{
                    new DataPoint(progressX, kvPair[1]),
                    new DataPoint(progressX, DIAGRAM_MAX_Y)});
        } else {
            mVirtualRuler.resetData(new DataPoint[]{
                    new DataPoint(progressX, 0),
                    new DataPoint(progressX, DIAGRAM_MAX_Y)});
        }
    }
}
