package com.cse118.tanabesuganodiagramslider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    Spinner diagramDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        diagramDropdown = findViewById(R.id.select_diagram);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.diagrams_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diagramDropdown.setAdapter(adapter);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 10),
                new DataPoint(8, 20),
                new DataPoint(15, 30),
                new DataPoint(25, 40),
                new DataPoint(35, 50)
        });
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 50),
                new DataPoint(10, 60),
                new DataPoint(20, 70),
                new DataPoint(25, 75),
                new DataPoint(35, 75)
        });
        graph.addSeries(series);
        graph.addSeries(series2);
    }
}
