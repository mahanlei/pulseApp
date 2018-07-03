package com.example.apple.pluseapp.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.pluseapp.R;
import com.example.apple.pluseapp.util.DeviceListAdapter;
import com.example.apple.pluseapp.util.MyDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_BT_ENABLE_CODE = 1;
    private static final String TAG = "showActivity";
    private BluetoothAdapter mBluetooth;//蓝牙适配器
    List<BluetoothDevice> mDevices;
    ArrayList<String> mArrayAdapter = new ArrayList<>();
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 连接对象的名称
    private final String NAME = "LGL";
    // 这里本身即是服务端也是客户端，需要如下类
    private BluetoothSocket clientSocket;
    private static boolean mbConectOk = false;
    private static InputStream misIn = null;
    private BluetoothDevice device;
    // 输出流_客户端需要往服务端输出
    private OutputStream os;
    private InputStream inputStream;
    //线程类的实例
//    private AcceptThread ac;
    ConnectedThread ct;
    private int count = 0;
    int index = 0;

    LineChart mLineChart;
    private List<float[]> allData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        findViewById(R.id.connBt).setOnClickListener(this);
        mLineChart = findViewById(R.id.chart);
//显示边界
        mLineChart.setNoDataText("暂无数据");
        mLineChart.setDrawBorders(false);

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

    //点击连接按钮
    @Override
    public void onClick(View view) {
        mArrayAdapter.clear();
        initBlueTooth();
        findPairDevices();
        //发现新设备
        final BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // 收到的广播类型
                String action = intent.getAction();
                // 发现设备的广播
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // 从intent中获取设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 判断是否配对过
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        if (!isExistDevice(device)) {
//                            mDevices.add(device);
                            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                    }
//                    Log.e(TAG, "add:" + device.getName());
                    // 搜索完成
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                    mAdapter.setItems(mDevices);
//                    mLoadingDialog.dismiss();
                    showToast("搜索完成！");
                } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                }
            }
        };

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        // 搜索完成的广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播
        registerReceiver(receiver, filter);
        showListDialog();


    }

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

    //搜索已配对设备
    private void findPairDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetooth.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


    private boolean isExistDevice(BluetoothDevice device) {
        String[] items = mArrayAdapter.toArray(new String[0]);
        for (String item : items) {
            if (item.split("\n")[1].equals(device.getName())) {
                return true;
            }
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(ShowActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void showListDialog() {

        final String[] items = mArrayAdapter.toArray(new String[0]);
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(ShowActivity.this);
        listDialog.setTitle("搜索到的设备");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do
                Toast.makeText(ShowActivity.this,
                        "成功连接" + items[which].split("\n")[1],
                        Toast.LENGTH_SHORT).show();
                // 主动连接蓝牙
                try {
                    // 判断是否在搜索,如果在搜索，就取消搜索
                    if (mBluetooth.isDiscovering()) {
                        mBluetooth.cancelDiscovery();
                    }

                    // 获得远程设备
                    BluetoothDevice device = mBluetooth.getRemoteDevice(items[which].split("\n")[1]);
                    Log.i("连接设备", "device:" + device);

                    clientSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    // 连接
                    clientSocket.connect();
                    ct = new ConnectedThread(clientSocket);
                    ct.start();
                    LineData data=new LineData();
                    mLineChart.setData(data);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    }, 1000,1000);
                }catch (Exception e) {

                }


    }
});
        listDialog.show();

    }

    @Override
    public void onBackPressed() {
       ct.cancel();
        super.onBackPressed();
    }

    class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            while (true) {
                try {
                    int i = 0;
                    while ((i = mmInStream.read(buffer)) != -1) {
                        String str = new String(buffer);
//                        Log.i("收到的数据", str.substring(0, 4));
                        saveData(Integer.valueOf(str.substring(0, 4)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }

    public void saveData(int data){
        int length=allData.size();
        allData.add(new float[]{new Float(length++), new Float(data)});

    }
    private void addEntry() {
        LineData data = mLineChart.getData();
        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            Log.i("addEntry", "count:"+count);
            //在450个数据之后开始删除已经没有显示在折线图范围的数据 后面的数据还是从右向左出现  当然也可以不用删除，这样可以滑动折线图浏览以前的数据
//            if (count > 45) {
//                set.removeFirst();
//                data.addEntry( new Entry((float) allData.get(index)[0], (float) allData.get(index)[1]), 0);
//            } else {
                data.addEntry(new Entry((float) allData.get(index)[0], (float) allData.get(index)[1]), 0);
//            }
            count++;
            index++;
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mLineChart.notifyDataSetChanged();

            // 折线图最多显示的数量
//            mLineChart.setVisibleXRangeMaximum(45);
            mLineChart.moveViewToX(data.getEntryCount());
        }
    }
//    public void addEntry(float yValue){
//        LineData data=mLineChart.getLineData();
//
//        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
//        // 本例只有一个，那么就是第0条折线
//        ILineDataSet dataSet=data.getDataSetByIndex(0);
//
//        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。
//        if (dataSet==null){
//            dataSet= createSet();
//            data.addDataSet(dataSet);
//        }
//
//        // 先添加一个x坐标轴的值
//        // 因为是从0开始，data.getXValCount()每次返回的总是全部x坐标轴上总数量，所以不必多此一举的加1
////        data.addXValue(data.getXValCount()+"");
////        data.addXValue(xValue);
////        data.a
//        //如果异常则为红色
//
//
//        // set.getEntryCount()获得的是所有统计图表上的数据点总量，
//        // 如从0开始一样的数组下标，那么不必多次一举的加1
//        Entry entry=new Entry(yValue,dataSet.getEntryCount());
//        // 往linedata里面添加点。注意：addentry的第二个参数即代表折线的下标索引。
//        // 因为本例只有一个统计折线，那么就是第一个，其下标为0.
//        // 如果同一张统计图表中存在若干条统计折线，那么必须分清是针对哪一条（依据下标索引）统计折线添加。
//        data.addEntry(entry,0);
//
//        // 像ListView那样的通知数据更新
//        mLineChart.notifyDataSetChanged();
//
//        // 当前统计图表中最多在x轴坐标线上显示的总量
//        mLineChart.setVisibleXRangeMaximum(10);
//        // 将坐标移动到最新
//        // 此代码将刷新图表的绘图
//        mLineChart.moveViewToX(data.getEntryCount()-5);
//
//    }


}
