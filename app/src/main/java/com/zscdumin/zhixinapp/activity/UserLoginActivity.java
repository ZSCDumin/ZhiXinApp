package com.zscdumin.zhixinapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    @BindView(R.id.user_account)
    EditText userAccount;
    @BindView(R.id.user_password)
    EditText userPassword;
    private boolean isSuccess = false;

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
                if (isLoginSucess()) {//登录成功
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.add_new_user:
                Intent intent = new Intent(this, UserRegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    public boolean isLoginSucess() {
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
                    if (account.equals(user_account) && password.equals(user_password)) {
                        flag = 1;
                        userAccount.setText("");
                        userPassword.setText("");
                        Toast.makeText(UserLoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        isSuccess = true;
                        break;
                    }
                }
                if (flag == 1) {
                    isSuccess = false;
                    Toast.makeText(UserLoginActivity.this, "登录失败,账号或密码错误！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                isSuccess = false;
                Toast.makeText(UserLoginActivity.this, "登录失败,账号或密码错误！", Toast.LENGTH_SHORT).show();
            }
        });
        return isSuccess;
    }
}