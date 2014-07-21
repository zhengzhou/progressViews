package com.example.demo;

import person.zhou.view.PieChartView;
import person.zhou.view.ProgressView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.example.progressdemo.R;
import com.yiutil.tools.Logger;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.Debug(true);
        setContentView(R.layout.main_layout);
        View button1 = findViewById(R.id.button1);
        progress1 = (ProgressView) findViewById(R.id.progress1);
        progress2 = (ProgressView) findViewById(R.id.progress2);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        pieChart = (PieChartView) findViewById(R.id.pie_chart);
        button1.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            progress1.setProgress(seekBar1.getProgress());
            progress2.setProgress(seekBar2.getProgress());
        }
    }
}
