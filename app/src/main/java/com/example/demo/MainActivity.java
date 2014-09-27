package com.example.demo;

import person.zhou.views.PieChartView;
import person.zhou.views.ProgressView;
import person.zhou.views.ProgressView;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.SeekBar;

import com.example.progressdemo.R;

/**
 * 主页面
 *
 * @author zhou
 *
 */
public class MainActivity extends Activity implements View.OnClickListener {

    ProgressView progress1, progress2;
    SeekBar seekBar1, seekBar2;
    PieChartView pieChart;

    /**
     * UI Color Palette<br>
     */
    final int[] Colors = { 0xff03a9f4, 0xffcddc39, 0xffe91e63, 0xff00bcd4, 0xfff5722, };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        View button1 = findViewById(R.id.button1);
        progress1 = (ProgressView) findViewById(R.id.progress1);
        progress2 = (ProgressView) findViewById(R.id.progress2);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        pieChart = (PieChartView) findViewById(R.id.pie_chart);
        button1.setOnClickListener(this);

        setSizeData();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            progress1.setProgress(seekBar1.getProgress());
            progress2.setProgress(seekBar2.getProgress());
            pieChart.removeCallbacks(action);
            setSizeData();
        }
    }

    Runnable action;
    private void setSizeData() {
        final SparseIntArray pipAndColor = new SparseIntArray(5);
        // 颜色值 百分比
        pieChart.init(Colors);
        pieChart.beginLoad();
        action = new Runnable() {

            @Override
            public void run() {
                int[] sizePercents = { 7, 15, 33, 55, 40 };
                pipAndColor.put(Colors[0], sizePercents[0]);
                pipAndColor.put(Colors[1], sizePercents[1]);
                pipAndColor.put(Colors[2], sizePercents[2]);
                pipAndColor.put(Colors[3], sizePercents[3]);

                pieChart.setData(pipAndColor, true);
            }
        };
        pieChart.postDelayed(action, 5000);

    }
}
