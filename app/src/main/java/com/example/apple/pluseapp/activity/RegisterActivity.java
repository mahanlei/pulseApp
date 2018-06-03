package com.example.apple.pluseapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.pluseapp.Dao.UserDao;
import com.example.apple.pluseapp.R;
import com.example.apple.pluseapp.util.DBHelper;
import com.example.apple.pluseapp.util.Message;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
private Button confirmBt;
private EditText account;
private EditText age;
private RadioButton radioButtonMale;
private  RadioButton radioButtonFemale;
private EditText password;
private EditText checkPwd;
private Context registerContext;
    UserDao userDao=new UserDao();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerContext=RegisterActivity.this;

        confirmBt=findViewById(R.id.confirmBt);
        age=findViewById(R.id.age);
        radioButtonMale=findViewById(R.id.male);
        radioButtonFemale=findViewById(R.id.female);
        password=findViewById(R.id.password);
        checkPwd=findViewById(R.id.checkPwd);
        account=findViewById(R.id.account);
        confirmBt.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
if(isNull(account.getText().toString())){
    Toast.makeText(getApplicationContext(), "请完善用户名", Toast.LENGTH_SHORT).show();
}
if(isNull(password.getText().toString())){
    Toast.makeText(getApplicationContext(), "请完善密码", Toast.LENGTH_SHORT).show();

}
if(!(checkPwd.getText().toString().equals(password.getText().toString()))){
    Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
}
Message message=userDao.isExist(registerContext,account.getText().toString());
if(message.equals(Message.EXIST)){
    Toast.makeText(getApplicationContext(), "用户名已存在", Toast.LENGTH_SHORT).show();
}else{
   if( doRegister(account.getText().toString(),Integer.valueOf(age.getText().toString()),"nv",password.getText().toString()).equals(Message.REGISTER_SUCCESS));
    Toast.makeText(getApplicationContext(), "注册成功，前往登录", Toast.LENGTH_SHORT).show();
    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
    startActivity(intent);
}

    }
    public  Message doRegister(String userName, int age,String gender,String password){
        SQLiteDatabase db= new DBHelper(registerContext,"mu.db",null,1).getWritableDatabase();

       db.execSQL("insert into userInfo values(?,?,?,?,?,?) ",
                new String[]{userName,String.valueOf(age),gender,password,"未知","未进行测试"});
        return Message.REGISTER_SUCCESS;
    }
    private boolean isNull(String s){
        if(s==null||s.equals("")){
            return true;
        }
        return  false;
    }
}
