package com.example.apple.pluseapp.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.apple.pluseapp.R;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener{
private Button startButton;
private MenuItem userItem;
private MenuItem cloudItem;
private Context homePageContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
startButton=findViewById(R.id.startBt);
userItem=findViewById(R.id.user);
cloudItem=findViewById(R.id.cloud);
startButton.setOnClickListener(this);

    }
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
           // Inflate the menu; this adds items to the action bar if it is present.
                 //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
                getMenuInflater().inflate(R.menu.action_bar, menu);
                return true;
             }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                 switch (item.getItemId()) {
                     case R.id.user:
                         Toast.makeText(this, "用户信息", Toast.LENGTH_SHORT).show();
                            break;
                     case R.id.cloud:
                             Toast.makeText(this, "云平台大数据", Toast.LENGTH_SHORT).show();
                             break;

                     default:
                             break;
                    }
                 return super.onOptionsItemSelected(item);
             }
    @Override
    public void onClick(View view) {

    }
}
