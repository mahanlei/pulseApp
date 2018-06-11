package com.example.apple.pluseapp.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.apple.pluseapp.R;
import com.example.apple.pluseapp.util.BlueToothUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity{
    public static final int REQUEST_BT_ENABLE_CODE = 1;
    private static final String TAG="showActivity";
    private BluetoothAdapter mBluetooth;//蓝牙适配器
    BlueToothUtils blueToothUtils;
    LineChart mLineChart;
    private List<float[]> data=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
//        findViewById(R.id.connBt).setOnClickListener(this);

        mLineChart=findViewById(R.id.chart);
//显示边界
        mLineChart.setNoDataText("暂无数据");
        mLineChart.setDrawBorders(false);
        //设置数据
        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry(data.get(i)[0], (float)data.get(i)[1]));
        }
        //一个LineDataSet就是一条线
LineData data=new LineData();
        mLineChart.setData(data);
        Thread thread=new Thread();
        thread.run();
        LineDataSet lineDataSet = new LineDataSet(entries, "脉搏波形");
        lineDataSet.setDrawCircles(false);//图表上的数据点是否用小圆圈表示
        LineData data = new LineData(lineDataSet);

        mLineChart.setData(data);



    }

//    @Override
//    public void onClick(View view) {
////    initBlueTooth();
////    blueToothUtils.searchDevices();
//        try {
//            readData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<Entry> entries = new ArrayList<>();
//
//        for (int i = 0; i < data.size(); i++) {
//            entries.add(new Entry(data.get(i)[0], (float)data.get(i)[1]));
//        }
//        //一个LineDataSet就是一条线
//
//        LineDataSet lineDataSet = new LineDataSet(entries, "脉搏波形");
//        lineDataSet.setDrawCircles(false);//图表上的数据点是否用小圆圈表示
//        LineData data = new LineData(lineDataSet);
//        mLineChart.setData(data);
//    }

    //开启蓝牙
    private void initBlueTooth() {
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        //1.设备不支持蓝牙，结束应用
        if (mBluetooth == null) {
            Toast.makeText(this, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //2.判断蓝牙是否打开
        if (!mBluetooth.enable()) {
            //没打开请求打开
            Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btEnable, REQUEST_BT_ENABLE_CODE);
        }
    }

    public void readData() throws IOException {
        //得到资源中的Raw数据流
        try {
            InputStream in=getResources().openRawResource(R.raw.scope_0);
        InputStreamReader inputStreamReader = new InputStreamReader(in);

        BufferedReader bufReader = new BufferedReader(inputStreamReader);
       String line;
        while((line = bufReader.readLine())!=null) {
            //按行读取输入流数据
//            Log.i("data1",line);
            String[] temp = line.split(",");
//            Log.i("data",temp[1]);
            //读取数据
data.add(new float[]{new Float(temp[0]), new Float(temp[1])});

        }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
