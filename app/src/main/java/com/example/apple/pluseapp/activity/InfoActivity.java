package com.example.apple.pluseapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.apple.pluseapp.R;
import com.example.apple.pluseapp.util.DBHelper;
import com.example.apple.pluseapp.util.Message;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{
private TextView userName;
private TextView userAge;
private TextView userGender;
private TextView healthRank;
private TextView healthReport;
private Button logoutBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        userName=findViewById(R.id.userName);
        userAge=findViewById(R.id.userAge);
        userGender=findViewById(R.id.userGender);
        healthRank=findViewById(R.id.healthRank);
        healthReport=findViewById(R.id.heathReport);
logoutBt=findViewById(R.id.logoutBt);
logoutBt.setOnClickListener(this);

        Intent intent=getIntent();
        String nameString=intent.getStringExtra("userName");
        userName.setText(nameString);
        List<String> info=getInfo(InfoActivity.this,nameString);
        userAge.setText(info.get(0)+"岁");
        userGender.setText(info.get(1));
        healthRank.setText(info.get(2));
        healthReport.setText(info.get(3));

    }
    public List<String> getInfo(Context context,String userName){
        int userAge;
       String userGender;
         String healthRank;
         String healthReport;
         List<String> result=new ArrayList<>();
        SQLiteDatabase db= new DBHelper(context,"mu.db",null,1).getWritableDatabase();

        Cursor cursor=db.rawQuery("SELECT * FROM userInfo WHERE userName = ?",
                new String[]{userName});
        if(cursor.moveToFirst())
        {
result.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("age"))));
result.add(cursor.getString(cursor.getColumnIndex("gender")));
result.add(cursor.getString(cursor.getColumnIndex("rank")));
result.add(cursor.getString(cursor.getColumnIndex("detail")));
        }
        cursor.close();
        return result;
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(InfoActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
