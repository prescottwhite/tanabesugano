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
        Diagram d2 = new Diagram(mGraph, "d2", this);

        setUpRadioButtons(d2);
        
        mRatioEditText = findViewById(R.id.editRatio);

        mSeekBar = findViewById(R.id.seek_x);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGraph.removeSeries(mSeek_series);
                mSeek_series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(progress, 0),
                        new DataPoint(progress, 75)
                });
                mRatioEditText.setText(Integer.toString(progress));
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
        for(int i = 0; i < diagram.getLength(); i++) {
            RadioButton newButton = new RadioButton(this);
            newButton.setText(diagram.getLineName(i));
            newButton.setTextColor(mLineColors[i]);
            mRgLineChoice.addView(newButton);
        }
    }
}
