package com.zscdumin.zhixinapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.fragment.MeFragment;
import com.zscdumin.zhixinapp.fragment.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_layout)
    NavigationView nav_layout;
    private final String TAG = "MainActivity";

    //Fragment
    private ParentFragment newsListFragment = null;
    private MeFragment meFragment = null;
    private FragmentTransaction transaction;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.fragment_main);
        ButterKnife.bind(this);
        transaction = getSupportFragmentManager().beginTransaction();
        //初始是新闻页面
        newsListFragment = new ParentFragment();
        transaction.add(R.id.fl_content, newsListFragment, "newsListFragment").commit();
        nav_layout.setNavigationItemSelectedListener(this);
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
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}