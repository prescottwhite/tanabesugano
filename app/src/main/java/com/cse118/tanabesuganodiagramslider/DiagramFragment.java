package com.cse118.tanabesuganodiagramslider;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

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

    private int mGroundState;
    private int mGroundState2;
    private Boolean mIsShowAllStates;
    private Boolean mIsShowOtherGround;

    private Context mContext;
    private Diagram mDiagram;
    private int[] mLineColors;
    private int mPrimaryColor;
    private int mSecondaryColor;

    private EditText mEditXVal;
    private EditText mEditYVal;
    private GraphView mGraph;
    private SeekBar mSeekBar;
    private LinearLayout mHidden;
    private RadioGroup mChoices;
    private Switch mToggleGround;
    private ToggleButton mToggleSpin;


    private LineGraphSeries<DataPoint> mVirturalRuler;
    private LineGraphSeries<DataPoint> mCalculateRuler;

    private int mProgress;


    private final SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //mGraph.removeSeries(mVirturalRuler);

                mProgress = progress;
                Double progressX = convertX(mProgress);
                mEditXVal.setText("" + progressX);
                generateY();
                //generateRatio(progress);
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

    private final RadioGroup.OnCheckedChangeListener mLineChoiceChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            generateY();
        }
    };

    private final CompoundButton.OnCheckedChangeListener mGroundToggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mIsShowOtherGround = b;
            int temp = mGroundState2;
            mGroundState2 = mGroundState;
            mGroundState = temp;
            generateGraph(mDiagram);
        }
    };

    private final CompoundButton.OnCheckedChangeListener mSpinToggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mIsShowAllStates = b;
            generateGraph(mDiagram);
            setUpRadioButtons(mDiagram);
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
        mGroundState = mDiagram.getGroundState();
        mGroundState2 = mDiagram.getGroudState2();
        mIsShowAllStates = false;
        mIsShowOtherGround = false;

        mLineColors = view.getResources().getIntArray(R.array.lineColors);
        mPrimaryColor = ContextCompat.getColor(mContext, R.color.colorBlack);
        mSecondaryColor = ContextCompat.getColor(mContext, R.color.colorGrey);

        mEditXVal = view.findViewById(R.id.fragment_diagram_et_DeltaOverB);
        mEditYVal = view.findViewById(R.id.fragment_diagram_et_y);
        mGraph = view.findViewById(R.id.graph);
        mSeekBar = view.findViewById(R.id.seek_x);
        mHidden = view.findViewById(R.id.ll_main_hidden);
        mChoices = view.findViewById(R.id.rg_main_choices);
        mSeekBar = view.findViewById(R.id.seek_x);
        mToggleGround = view.findViewById(R.id.toggle_diagram_ground);
        mToggleSpin = view.findViewById(R.id.toggle_diagram_spin);

        setEditTextButtonXVal(mEditYVal);
        setEditTextButtonYVal(mEditXVal);


        generateGraph(mDiagram);
        setUpRadioButtons(mDiagram);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mChoices.setOnCheckedChangeListener(mLineChoiceChangeListener);
        mToggleGround.setOnCheckedChangeListener(mGroundToggleListener);
        mToggleSpin.setOnCheckedChangeListener(mSpinToggleListener);

        mProgress = -1;

        return view;
    }

    private void generateGraph(Diagram diagram){
        mGraph.removeAllSeries();
        // Draw Lines
        for (int i = 0; i < diagram.getLength(); i++) {
            Diagram.LineMap lineMap = diagram.getLineMap(i);
            if (shouldDraw(i)) {
                DataPoint[] dataPoints = lineMap.getDataPoints();
                LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(dataPoints);
                mGraph.addSeries(lineGraphSeries);

                if (lineMap.getStateNumber() == mGroundState2) {
                    lineGraphSeries.setColor(Color.GRAY);
                } else {
                    int colorIndex = i % mLineColors.length;
                    lineGraphSeries.setColor(mLineColors[colorIndex]);
                }
            }
        }

        // Set Viewport
        Viewport viewport = mGraph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(DIAGRAM_MAX_X);

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(DIAGRAM_MAX_Y);

        mVirturalRuler = new LineGraphSeries<>(
                new DataPoint[]{});
        mGraph.addSeries(mVirturalRuler);
        mVirturalRuler.setColor(mPrimaryColor);
        mVirturalRuler.setThickness(RULER_THICKNESS);

        mCalculateRuler = new LineGraphSeries<>();
        mGraph.addSeries(mCalculateRuler);
        mCalculateRuler.setColor(mSecondaryColor);
        mCalculateRuler.setThickness(RULER_THICKNESS);
    }

    private void setUpRadioButtons(Diagram diagram) {
        mChoices.removeAllViewsInLayout();
        for (int i = 0; i < diagram.getLength(); i++) {
            if (shouldDraw(i)) {
                RadioButton newButton = new RadioButton(mContext);
                newButton.setText(diagram.getLineName(i));
                newButton.setId(i);
                int colorIndex = i % mLineColors.length;
                newButton.setTextColor(mLineColors[colorIndex]);
                mChoices.addView(newButton);
            }
        }
    }

    private Boolean shouldDraw(int i) {
        int state = mDiagram.getLineMap(i).getStateNumber();
        return mIsShowAllStates || state == mGroundState || state == mGroundState2;
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

    private double[] getNearKeyValue(double key, int line) {
        double[] pair = new double[2];
        Diagram.LineMap lineMap = mDiagram.getLineMap(line);
        Map.Entry<Double, Double> entry;

        try {
            entry = lineMap.ceilingEntry(key);
        }
        catch (NullPointerException e) {
            entry = lineMap.floorEntry(key);
        }

        pair[0] = entry.getKey();
        pair[1] = entry.getValue();

        return pair;
    }

    private double convertX(int raw) {
        return (double) raw / 10;
    }

    private void setEditTextButtonYVal(final EditText setup)
    {
        setup.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0)
                {
                    generateYGivenX(Double.parseDouble(setup.getText().toString()));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void setEditTextButtonXVal(final EditText setup)
    {
        setup.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0)
                {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
    }
    /*
    private void generateXGivenY(int y)
    {
        Double progressX = convertX(x);

        int lineIndex = mChoices.getCheckedRadioButtonId();
        Log.i(LOG_TAG, "calculating based on radio button id: " + lineIndex);
        if (lineIndex >= 0) {

            double[] kvPair = getNearKeyValue(progressX, lineIndex);
            mEditYVal.setText(Double.toString(kvPair[1]));
            mEditXVal.setText("" + progressX);

            mCalculateRuler.resetData(new DataPoint[]{
                    new DataPoint(kvPair[0], 0),
                    new DataPoint(kvPair[0], kvPair[1])
            });
            mVirturalRuler.resetData(new DataPoint[]{
                    new DataPoint(progressX, kvPair[1]),
                    new DataPoint(progressX, DIAGRAM_MAX_Y)});
        } else {
            mVirturalRuler.resetData(new DataPoint[]{
                    new DataPoint(progressX, 0),
                    new DataPoint(progressX, DIAGRAM_MAX_Y)});
        }
    }*/
    private void generateYGivenX(Double x)
    {

        int lineIndex = mChoices.getCheckedRadioButtonId();
        Log.i(LOG_TAG, "calculating based on radio button id: " + lineIndex);
        if (lineIndex >= 0) {

            double[] kvPair = getNearKeyValue(x, lineIndex);
            mEditYVal.setText("" + kvPair[1]);

            mCalculateRuler.resetData(new DataPoint[]{
                    new DataPoint(kvPair[0], 0),
                    new DataPoint(kvPair[0], kvPair[1])
            });
            mVirturalRuler.resetData(new DataPoint[]{
                    new DataPoint(x, kvPair[1]),
                    new DataPoint(x, DIAGRAM_MAX_Y)});
        } else {
            mVirturalRuler.resetData(new DataPoint[]{
                    new DataPoint(x, 0),
                    new DataPoint(x, DIAGRAM_MAX_Y)});
        }
    }

    private void generateY() {
        // TODO: run this onCLick for radio group
        // TODO: grey out  progress bar
        Double progressX = convertX(mProgress);

        int lineIndex = mChoices.getCheckedRadioButtonId();
        Log.i(LOG_TAG, "calculating based on radio button id: " + lineIndex);
        if (lineIndex >= 0) {

            double[] kvPair = getNearKeyValue(progressX, lineIndex);

            mEditYVal.setText(Double.toString(kvPair[1]));
            mEditXVal.setText("" + progressX);

            mCalculateRuler.resetData(new DataPoint[]{
                    new DataPoint(kvPair[0], 0),
                    new DataPoint(kvPair[0], kvPair[1])
            });
            mVirturalRuler.resetData(new DataPoint[]{
                    new DataPoint(progressX, kvPair[1]),
                    new DataPoint(progressX, DIAGRAM_MAX_Y)});
        } else {
            mVirturalRuler.resetData(new DataPoint[]{
                    new DataPoint(progressX, 0),
                    new DataPoint(progressX, DIAGRAM_MAX_Y)});
        }
    }
}
