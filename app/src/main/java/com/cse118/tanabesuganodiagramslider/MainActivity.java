package com.cse118.tanabesuganodiagramslider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Spinner mDiagramDropdown;
    private Button mGoBtn;

    private DiagramFragment mDiagramFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoBtn = findViewById(R.id.btn_main_go);
        mGoBtn.setOnClickListener(mGoButtonClickListener);

        // Dropdown menu
        mDiagramDropdown = findViewById(R.id.select_diagram);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.diagrams_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiagramDropdown.setAdapter(adapter);

        mFragmentManager = getSupportFragmentManager();


        int diagram_index = mDiagramDropdown.getSelectedItemPosition();
        buildFragment(diagram_index);

    }

    private final View.OnClickListener mGoButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int diagram_index = mDiagramDropdown.getSelectedItemPosition();
            buildFragment(diagram_index);
        }
    };

    private void buildFragment(int diagram_index) {
        Fragment fragment = DiagramFragment.newInstance(diagram_index);
        mFragmentManager.beginTransaction()
                .replace(R.id.fragmentcontainer_main_graph, fragment)
                .commit();
    }
}
