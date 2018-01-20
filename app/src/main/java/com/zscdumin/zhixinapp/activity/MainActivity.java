package com.zscdumin.zhixinapp.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.fragment.MeFragment;
import com.zscdumin.zhixinapp.fragment.ParentFragment;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_layout)
    NavigationView nav_layout;
    //Fragment
    private ParentFragment newsListFragment = null;
    private MeFragment meFragment = null;
    private FragmentTransaction transaction;
    private Fragment fragment;

    private ImageView user_icon_iv = null;
    private TextView user_name_tv = null;
    private TextView user_phone_tv = null;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x11:
                    user_icon_iv.setImageBitmap((Bitmap) msg.obj);
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.fragment_main);
        ButterKnife.bind(this);
        PgyUpdateManager.register(this);
        transaction = getSupportFragmentManager().beginTransaction();
        //初始是新闻页面
        newsListFragment = new ParentFragment();
        transaction.add(R.id.fl_content, newsListFragment, "newsListFragment").commit();
        nav_layout.setNavigationItemSelectedListener(this);

        View view = nav_layout.inflateHeaderView(R.layout.nav_header_main);
        user_name_tv = view.findViewById(R.id.user_name);
        user_phone_tv = view.findViewById(R.id.user_phone);
        user_icon_iv = view.findViewById(R.id.user_icon);

        /**
         * 获取用户数据
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String login_type = bundle.getString("login_type");
        if (login_type.equals("with_face")) {
            String user_name = bundle.getString("user_name");
            String user_icon_url = bundle.getString("user_icon");
            String user_phone = bundle.getString("phone_num");
            user_name_tv.setText("用户:【" + user_name + "】");
            user_phone_tv.setText("手机:【" + user_phone + "】");
            setBitmapByUrl(user_icon_url);
        }
        else if (login_type.equals("with_account")){
            user_name_tv.setText("用户:【杜敏】");
            user_phone_tv.setText("手机:【18979429542】");
        }
    }

    public void setBitmapByUrl(final String url) {
        new Thread() {
            @Override
            public void run() {
                //获取okHttp对象get请求,
                try {
                    OkHttpClient client = new OkHttpClient();
                    //获取请求对象
                    Request request = new Request.Builder().url(url).build();
                    //获取响应体
                    ResponseBody body = client.newCall(request).execute().body();
                    //获取流
                    InputStream in = body.byteStream();
                    //转化为bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    //使用Hanlder发送消息
                    Message msg = Message.obtain();
                    msg.what = 0x11;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_news: //新闻菜单
                item.setChecked(true);
                transaction = getSupportFragmentManager().beginTransaction();
                fragment = getSupportFragmentManager().findFragmentByTag("newsListFragment");

                if (fragment != null) {
                    transaction.show(newsListFragment);
                } else {
                    transaction.add(R.id.fl_content, newsListFragment, "newsListFragment");
                }

                if (meFragment != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("meFragment"));
                }
                //提交事务
                transaction.commit();
                //关闭抽屉菜单
                drawer.closeDrawers();
                break;

            case R.id.nav_me:
                item.setChecked(true);
                transaction = getSupportFragmentManager().beginTransaction();
                if (meFragment != null) {
                    transaction.show(meFragment);
                } else {
                    meFragment = new MeFragment();
                    transaction.add(R.id.fl_content, meFragment, "meFragment");
                }

                if (newsListFragment != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("newsListFragment"));
                }

                transaction.commit();
                //关闭抽屉菜单
                drawer.closeDrawers();
                break;
            case R.id.nav_tuling:
                intent = new Intent(this, TulingActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_weather:
                intent = new Intent(this, WeatherMainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_version_update:
                checkAppUpdate();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setTitle("主人,确定要退出吗?")
                    .setIcon(R.drawable.exit)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "向右滑弹出抽屉菜单!", Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PgyCrashManager.unregister();
        System.exit(0);
    }
    //版本更新代码
    private void checkAppUpdate() {
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        new AlertDialog.Builder(MainActivity.this)

                                .setIcon(R.drawable.update_bg)
                                .setTitle("发现新版本,立即更新?")
                                .setMessage(appBean.getReleaseNote())
                                .setNegativeButton(
                                        "确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                startDownloadTask(
                                                        MainActivity.this,
                                                        appBean.getDownloadURL());

                                            }
                                        }).show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        Toast.makeText(MainActivity.this,"当前应用已经是最新版了！",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}