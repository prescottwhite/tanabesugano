package com.cse118.tanabesuganodiagramslider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    Spinner mDiagramDropdown;

    GraphView mGraph;
    LineGraphSeries<DataPoint> mSeek_series;

    SeekBar mSeekBar;
    EditText mEditText;
    TextView mTextView;
    RadioButton mRadio1;
    RadioButton mRadio2;
    Switch mSwitch1;
    Switch mSwitch2;
    ToggleButton mToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDiagramDropdown = findViewById(R.id.select_diagram);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.diagrams_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiagramDropdown.setAdapter(adapter);

        mGraph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 10),
                new DataPoint(8, 20),
                new DataPoint(15, 30),
                new DataPoint(25, 40),
                new DataPoint(40, 50)
        });
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 50),
                new DataPoint(10, 60),
                new DataPoint(20, 70),
                new DataPoint(25, 75),
                new DataPoint(40, 75)
        });
        mGraph.addSeries(series);
        mGraph.addSeries(series2);

        mSeekBar = findViewById(R.id.seek_x);

        mTextView = findViewById(R.id.textView);
        mEditText = findViewById(R.id.editText);
        mRadio1 = findViewById(R.id.radioButton);
        mRadio2 = findViewById(R.id.radioButton2);
        mSwitch1 = findViewById(R.id.switch1);
        mSwitch2 = findViewById(R.id.switch2);
        mToggleButton = findViewById(R.id.toggleButton);
        final View[] views = {mDiagramDropdown, mTextView, mEditText, mRadio1, mRadio2, mSwitch1, mSwitch2, mToggleButton};

        SeekBar seekBar = findViewById(R.id.seek_x);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGraph.removeSeries(mSeek_series);
                mSeek_series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(progress, 0),
                        new DataPoint(progress, 75)
                });
                mGraph.addSeries(mSeek_series);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                hideViews(views);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showViews(views);
            }
        });
    }

    void hideViews(View[] views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.INVISIBLE);
        }
    }

    void showViews(View[] views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.VISIBLE);
        }
    }
}
