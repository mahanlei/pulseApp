package com.example.apple.pluseapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.pluseapp.Dao.UserDao;
import com.example.apple.pluseapp.R;
import com.example.apple.pluseapp.util.DBHelper;
import com.example.apple.pluseapp.util.Message;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginBt;
    private TextView toRegister;
    private EditText username;
    private EditText password;
    private Context loginContext;
    UserDao userDao=new UserDao();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        loginContext = LoginActivity.this;
        username =  findViewById(R.id.account);
        password =  findViewById(R.id.password);
        loginBt = findViewById(R.id.loginBt);
        loginBt.setOnClickListener(this);
        toRegister = findViewById(R.id.newRegister);
        toRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBt:
//                Toast.makeText(getApplicationContext(), "密码错误",
//                        Toast.LENGTH_SHORT).show();
if(userDao.isExist(loginContext,username.getText().toString()).equals(Message.EXIST)){
if(isRight(username.getText().toString(),password.getText().toString()).equals(Message.RIGHT)){
    //进行跳转到主页
Intent intent=new Intent(LoginActivity.this,HomePageActivity.class);
intent.putExtra("userName",username.getText().toString());
startActivity(intent);
}else {
    //显示密码错误
    Toast.makeText(getApplicationContext(), "密码错误",
            Toast.LENGTH_SHORT).show();
}

}else {
    //显示用户名不存在
    Toast.makeText(getApplicationContext(), "用户名不存在",
            Toast.LENGTH_SHORT).show();
}
                break;
            case R.id.newRegister:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

//    public Message isExist(String userName) {
//        SQLiteDatabase db= new DBHelper(loginContext,"mu.db",null,1).getWritableDatabase();
//
//    Cursor cursor=db.rawQuery("SELECT * FROM userInfo WHERE userName = ?",
//            new String[]{userName});
//    if(cursor.moveToFirst())
//    {
//        return Message.EXIST;
//    }
//    cursor.close();
//    return Message.NOT_EXIST;
//}
    public Message isRight(String userName, String password) {
        SQLiteDatabase db= new DBHelper(loginContext,"mu.db",null,1).getWritableDatabase();

        Cursor cursor=db.rawQuery("SELECT password FROM userInfo WHERE userName = ?",
                new String[]{userName});
        if(cursor.moveToFirst())
        {
            String password1=cursor.getString(cursor.getColumnIndex("password"));
            if(password1.equals(password)){
                return Message.RIGHT;
            }

        }
        cursor.close();
        return Message.NOT_RIGHT;
    }
}
