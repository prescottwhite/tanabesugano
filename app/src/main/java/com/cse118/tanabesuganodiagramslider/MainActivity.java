package com.cse118.tanabesuganodiagramslider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;

import static java.lang.Double.NaN;

public class MainActivity extends AppCompatActivity {

    Spinner mDiagramDropdown;

    GraphView mGraph;
    LineGraphSeries<DataPoint> mSeek_series;

    private int[] mLineColors;

    LinearLayout mHidden;
    SeekBar mSeekBar;
    EditText mRatioEditText;
    RadioGroup mRgLineChoice;

    Switch mSwitch1;
    Switch mSwitch2;
    ToggleButton mToggleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Line Color List
        mLineColors = this.getResources().getIntArray(R.array.lineColors);


        // Dropdown menu
        mDiagramDropdown = findViewById(R.id.select_diagram);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.diagrams_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiagramDropdown.setAdapter(adapter);


        mHidden = findViewById(R.id.ll_main_hidden);
        mRgLineChoice = findViewById(R.id.rg_main_choices);

        mSwitch1 = findViewById(R.id.switch1);
        mSwitch2 = findViewById(R.id.switch2);
        mToggleButton = findViewById(R.id.toggleButton);

        // Creating graph
        mGraph = (GraphView) findViewById(R.id.graph);
        final Diagram d2 = new Diagram(mGraph, "d2", this);

        setUpRadioButtons(d2);

        mRatioEditText = findViewById(R.id.editRatio);

        mSeekBar = findViewById(R.id.seek_x);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGraph.removeSeries(mSeek_series);

                // These need to be selectable
                int line1 = 0;
                int line2 = 2;

                // Divide progress by 10 and cast to double
                double xKey = convertX(progress);

                // Find closest key, value pair for a given key and line
                double[] keyVal1 = getNearKeyValue(xKey, line1, d2);
                double[] keyVal2 = getNearKeyValue(xKey, line2, d2);

                // Find ratio of line2 over line1
                double ratio;
                if (keyVal1[1] != 0) {
                    ratio = getRatio(keyVal2[1], keyVal1[1]);
                }
                else {
                    ratio = 0.0;
                }
                if (Double.isNaN(ratio)) {
                    ratio = 0.0;
                }

                mSeek_series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(keyVal2[0], 0),
                        new DataPoint(keyVal2[0], keyVal2[1])
                });
                mRatioEditText.setText(Double.toString(ratio));
                mGraph.addSeries(mSeek_series);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                hideViews();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showViews();
            }
        });

    }

    void hideViews() {
        mHidden.setVisibility(View.INVISIBLE);
    }

    void showViews() {
        mHidden.setVisibility(View.VISIBLE);

    }

    void setUpRadioButtons(Diagram diagram) {
        for (int i = 0; i < diagram.getLength(); i++) {
            RadioButton newButton = new RadioButton(this);
            newButton.setText(diagram.getLineName(i));
            newButton.setTextColor(mLineColors[i]);
            mRgLineChoice.addView(newButton);
        }
    }

    double getRatio(double y2, double y1) {
        double ratio = y2 / y1;

        return Math.floor(ratio * 100) / 100;
    }

    double[] getNearKeyValue(double key, int line, Diagram diagram) {
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

    double convertX(int raw) {
        return (double) raw / 10;
    }
}
