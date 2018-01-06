package com.zscdumin.zhixinapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.bean.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class UserLoginActivity extends AppCompatActivity {

    @BindView(R.id.user_account_login)
    EditText userAccount;
    @BindView(R.id.user_password_login)
    EditText userPassword;
    @BindView(R.id.user_login)
    Button userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "e7edc6aee5407505ad8a1b08cddbd4d8");
        setContentView(R.layout.user_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.user_login, R.id.add_new_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_login:
                isLoginSucess();
                break;
            case R.id.add_new_user:
                Intent intent = new Intent(this, UserRegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void isLoginSucess() {
        if (userAccount.length() > 0 && userPassword.length() > 0) {
            BmobQuery<Person> query = new BmobQuery<Person>();
            query.findObjects(this, new FindListener<Person>() {

                String user_account = userAccount.getText().toString().trim();
                String user_password = userPassword.getText().toString().trim();
                int flag = 0;

                @Override
                public void onSuccess(List<Person> list) {
                    for (int i = 0; i < list.size(); i++) {
                        String account = list.get(i).getUserAccount();
                        String password = list.get(i).getUserPassword();
                        Log.i("登录", account + " " + password);
                        Log.i("登录", user_account + " " + user_password);
                        if (account.equals(user_account) && password.equals(user_password)) {
                            flag = 1;
                            Toast.makeText(UserLoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                    if (flag == 0) {
                        userAccount.setText("");
                        userPassword.setText("");
                        Toast.makeText(UserLoginActivity.this, "登录失败,账号或密码错误！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        } else {
            Toast.makeText(UserLoginActivity.this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
        }
    }
}