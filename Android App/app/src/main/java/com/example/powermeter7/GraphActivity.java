package com.example.powermeter7;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class GraphActivity extends AppCompatActivity {
    private static final String TAG = "GraphActivity";
    private LineChart energyChart;
    private Spinner timePeriodSpinner;

    private static final String[] TIME_PERIODS = {"Daily", "Weekly", "Monthly"};
    private Map<String, Double> energyData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        energyChart = findViewById(R.id.energyChart);
        Button backToMainBtn = findViewById(R.id.backToMainBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        timePeriodSpinner = findViewById(R.id.timePeriodSpinner);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TIME_PERIODS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePeriodSpinner.setAdapter(adapter);
        timePeriodSpinner.setSelection(0);
        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                updateChart(TIME_PERIODS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateChart("Daily");
            }
        });

        // Firebase listener to update chart in real time
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("powerData").child("energyLog");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                energyData.clear();
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                    String date = entrySnapshot.getKey();
                    Double energy = entrySnapshot.getValue(Double.class);
                    if (energy != null) {
                        energyData.put(date, energy);
                        Log.d(TAG, "Loaded data: " + date + " -> " + energy + " kWh");
                    }
                }
                updateChart(timePeriodSpinner.getSelectedItem().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GraphActivity.this, "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation
        backToMainBtn.setOnClickListener(v -> {
            startActivity(new Intent(GraphActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(GraphActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });
    }

    private void updateChart(String timePeriod) {
        List<Entry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        if (timePeriod.equals("Daily")) {
            cal.add(Calendar.DAY_OF_YEAR, -6);
            for (int i = 0; i < 7; i++) {
                String date = sdf.format(cal.getTime());
                String label = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(cal.getTime());
                xAxisLabels.add(label);
                entries.add(new Entry(i, energyData.getOrDefault(date, 0.0).floatValue()));
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
        } else if (timePeriod.equals("Weekly")) {
            cal.add(Calendar.WEEK_OF_YEAR, -3);
            for (int i = 0; i < 4; i++) {
                Calendar start = (Calendar) cal.clone();
                start.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.DAY_OF_YEAR, 6);

                double total = 0;
                Calendar current = (Calendar) start.clone();
                while (!current.after(end)) {
                    String date = sdf.format(current.getTime());
                    total += energyData.getOrDefault(date, 0.0);
                    current.add(Calendar.DAY_OF_MONTH, 1);
                }

                xAxisLabels.add("Week " + start.get(Calendar.WEEK_OF_YEAR));
                entries.add(new Entry(i, (float) total));
                cal.add(Calendar.WEEK_OF_YEAR, 1);
            }
        } else if (timePeriod.equals("Monthly")) {
            cal.add(Calendar.MONTH, -5);
            for (int i = 0; i < 6; i++) {
                Calendar start = (Calendar) cal.clone();
                start.set(Calendar.DAY_OF_MONTH, 1);
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.MONTH, 1);
                end.add(Calendar.DAY_OF_MONTH, -1);

                double total = 0;
                Calendar current = (Calendar) start.clone();
                while (!current.after(end)) {
                    String date = sdf.format(current.getTime());
                    total += energyData.getOrDefault(date, 0.0);
                    current.add(Calendar.DAY_OF_MONTH, 1);
                }

                xAxisLabels.add(new SimpleDateFormat("MMM yy", Locale.getDefault()).format(start.getTime()));
                entries.add(new Entry(i, (float) total));
                cal.add(Calendar.MONTH, 1);
            }
        }

        LineDataSet set = new LineDataSet(entries, "Energy Usage (" + timePeriod + ")");
        set.setColor(Color.CYAN);
        set.setCircleColor(Color.CYAN);
        set.setValueTextColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setDrawValues(false);
        set.setCircleRadius(4f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(set);
        energyChart.setData(data);

        XAxis xAxis = energyChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setLabelCount(xAxisLabels.size());

        energyChart.getAxisLeft().setTextColor(Color.WHITE);
        energyChart.getAxisRight().setEnabled(false);
        energyChart.getLegend().setTextColor(Color.CYAN);
        energyChart.getDescription().setEnabled(false);
        energyChart.setBackgroundColor(Color.TRANSPARENT);
        energyChart.animateX(500);
        energyChart.invalidate();
    }
}
