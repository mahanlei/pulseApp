package com.example.apple.pluseapp.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.apple.pluseapp.R;
import com.example.apple.pluseapp.util.BlueToothUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity{
    public static final int REQUEST_BT_ENABLE_CODE = 1;
    private static final String TAG="showActivity";
    private BluetoothAdapter mBluetooth;//蓝牙适配器
    BlueToothUtils blueToothUtils;
    LineChart mLineChart;
    private List<float[]> allData=new ArrayList<>();
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
//        List<Entry> entries = new ArrayList<>();
//
//        for (int i = 0; i < data.size(); i++) {
//            entries.add(new Entry(data.get(i)[0], (float)data.get(i)[1]));
//        }
        //一个LineDataSet就是一条线
        LineData data=new LineData();
        mLineChart.setData(data);
        final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<allData.size();i++) {
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
//        LineDataSet lineDataSet = new LineDataSet(entries, "脉搏波形");
//        lineDataSet.setDrawCircles(false);//图表上的数据点是否用小圆圈表示
//        LineData data = new LineData(lineDataSet);
//
//        mLineChart.setData(data);


    }
    private int i = 0;
    int  index=0;
    private void  addEntry() {
        LineData data = mLineChart.getData();
        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //在450个数据之后开始删除已经没有显示在折线图范围的数据 后面的数据还是从右向左出现  当然也可以不用删除，这样可以滑动折线图浏览以前的数据
            if (i > 450) {

                set.removeFirst();
                data.addEntry(new Entry(allData.get(index)[0], (float)allData.get(index)[1]),0);
            } else {

                data.addEntry(new Entry(allData.get(index)[0], (float)allData.get(index)[1]),0);
            }
            i++;
            index++;
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mLineChart.notifyDataSetChanged();

            // 折线图最多显示的数量
            mLineChart.setVisibleXRangeMaximum(450);
            mLineChart.moveViewToX(data.getEntryCount());
        }
    }
        private LineDataSet createSet() {

            LineDataSet set = new LineDataSet(null, "脉搏波形");
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(Color.parseColor("#FDC328"));
            set.setDrawCircles(false);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setValueTextColor(Color.WHITE);
            set.setValueTextSize(9f);
            set.setDrawValues(false);
            return set;
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
allData.add(new float[]{new Float(temp[0]), new Float(temp[1])});

        }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
