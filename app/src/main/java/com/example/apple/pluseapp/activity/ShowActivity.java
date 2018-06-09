package com.example.apple.pluseapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.apple.pluseapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
      LineChart mLineChart=findViewById(R.id.chart);
//显示边界
        mLineChart.setDrawBorders(false);
        //设置数据

        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            entries.add(new Entry(i, (float) (Math.random()) * 80));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "脉搏波形");
        LineData data = new LineData(lineDataSet);
        mLineChart.setData(data);
    }
}
