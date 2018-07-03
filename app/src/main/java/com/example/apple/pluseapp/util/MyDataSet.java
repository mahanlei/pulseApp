package com.example.apple.pluseapp.util;

import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import android.graphics.Color;
import java.util.List;

public class MyDataSet extends LineDataSet{
    private int start;      //最小值
    private int end;   //最大值
    public MyDataSet(List<Entry> yVals, String label, int start, int end) {
        super(yVals, label);
        this.start=start;
        this.end=end;
    }

//    @Override
//    public int getCircleColor(int index) {
//        //如果超出范围则返回红色
//        if (getEntryForXIndex(index).getVal()<start||getEntryForXIndex(index).getVal()>end){
//            return 0xFFff0000;
//        }else return super.getCircleColor(index);
//    }
}
