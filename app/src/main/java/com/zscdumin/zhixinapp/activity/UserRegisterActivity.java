package com.zscdumin.zhixinapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.bean.Person;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;


public class UserRegisterActivity extends AppCompatActivity {

    @BindView(R.id.user_account_register)
    EditText userAccount;
    @BindView(R.id.user_password_register)
    EditText userPassword;
    @BindView(R.id.user_register)
    Button userRegister;

    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.user_register)
    public void onViewClicked() {
        final String user_account = userAccount.getText().toString().trim();
        String user_password = userPassword.getText().toString().trim();

        //判断用户名和密码是否为空,如果为空则不能进去。
        if (user_account.length() > 0 && user_password.length() > 0) {
            person = new Person();
            person.setUserAccount(user_account);
            person.setUserPassword(user_password);
            //插入方法
            person.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    userAccount.setText("");
                    userPassword.setText("");
                    Toast.makeText(UserRegisterActivity.this, "恭喜您,注册成功！", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UserRegisterActivity.this,UserLoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(int code, String msg) {
                    // TODO Auto-generated method stub
                    Toast.makeText(UserRegisterActivity.this, "很抱歉,您注册失败！：" + msg, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UserRegisterActivity.this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
        }
    }

}