package com.cse118.tanabesuganodiagramslider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {

    private Spinner mDiagramDropdown;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Dropdown menu
        mDiagramDropdown = findViewById(R.id.select_diagram);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.diagrams_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiagramDropdown.setAdapter(adapter);


        // Creating Diagram Graph Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment =fragmentManager.findFragmentById(R.id.fragmentcontainer_main_graph);
        if (fragment == null) {
            fragment = new DiagramFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentcontainer_main_graph, fragment)
                    .commit();
        }

    }









}
