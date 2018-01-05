package com.zscdumin.zhixinapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.activity.NewsActivity;
import com.zscdumin.zhixinapp.adapter.NewsListAdapter;
import com.zscdumin.zhixinapp.bean.NewsBean;
import com.zscdumin.zhixinapp.utils.FileUtils;
import com.zscdumin.zhixinapp.utils.HttpUtils;
import com.zscdumin.zhixinapp.utils.JsonUtils;
import com.zscdumin.zhixinapp.utils.Urls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


/**
 * Created by luo-pc on 2016/5/14.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String keyword;
    private String TAG = "NewsListFragment";

    private ArrayList<NewsBean> newsList = null;
    private RecyclerView recycle_news;
    private NewsListAdapter newsListAdapter;
    private Context context;
    //private ArrayList<NewsBean> newsList = null;
    private LinearLayoutManager layoutManager;

    //在重新创建fragment时加载缓存数据
    private int count = 0;

    //页数
    int pageIndex = 0;
    private SwipeRefreshLayout sr_refresh;


    public void setKeyword(String keyword) {
        this.keyword = keyword;

        if (count != 0) {
            new DownloadTask().execute(getUrl());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, null);
        context = getContext();
        layoutManager = new LinearLayoutManager(context);
        newsListAdapter = new NewsListAdapter(context);
        initView(view);


        newsListAdapter.setOnItemClickListener(onItemClickListener);
        recycle_news.setLayoutManager(layoutManager);
        recycle_news.setAdapter(newsListAdapter);

        sr_refresh.setColorSchemeResources(R.color.primary, R.color.primary_dark,
                R.color.primary_light, R.color.accent);
        sr_refresh.setOnRefreshListener(this);

        recycle_news.addOnScrollListener(onScrollListener);

        if (context != null) {
            File cacheFile = FileUtils.getDisCacheDir(context, "NewsBean" + keyword);
            if (cacheFile.exists()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile));
                    ArrayList<NewsBean> list = (ArrayList<NewsBean>) ois.readObject();
                    newsListAdapter.setData(list);
                    Log.i(TAG, list.size() + " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new DownloadTask().execute(getUrl());

        return view;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //SCROLL_STATE_IDLE
            //The RecyclerView is not currently scrolling.
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == newsListAdapter.getItemCount()
                    && newsListAdapter.isShowFooter()) {
                //加载更多新闻
                pageIndex += Urls.PAZE_SIZE;
                new UpdateTask().execute(getUrl());
            }

        }
    };


    //拼接url
    private String getUrl() {
        StringBuilder sb = new StringBuilder();
        switch (keyword) {
            //头条
            case Urls.TOP_ID:
                sb.append(Urls.TOP_URL).append(Urls.TOP_ID);
                break;
            //NBA
            case Urls.NBA_ID:
                sb.append(Urls.COMMON_URL).append(Urls.NBA_ID);
                break;
            //汽车
            case Urls.CAR_ID:
                sb.append(Urls.COMMON_URL).append(Urls.CAR_ID);
                break;
            //笑话
            case Urls.JOKE_ID:
                sb.append(Urls.COMMON_URL).append(Urls.JOKE_ID);
                break;
            default:
                sb.append(Urls.TOP_URL).append(Urls.TOP_ID);
                break;
        }

        sb.append("/").append(pageIndex).append(Urls.END_URL);
        return sb.toString();
    }


    private NewsListAdapter.OnItemClickListener onItemClickListener = new NewsListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), NewsActivity.class);
            intent.putExtra("url", newsList.get(position).getUrl());

            startActivity(intent);
        }
    };


    private void initView(View view) {
        recycle_news = (RecyclerView) view.findViewById(R.id.recycle_news);
        sr_refresh = (SwipeRefreshLayout) view.findViewById(R.id.sr_refresh);
    }


    @Override
    public void onRefresh() {
        //刷新置pageIndex为0获取最新数据
        pageIndex = 0;

        if (newsList != null) {
            newsList.clear();
        }

        new DownloadTask().execute(getUrl());
//        newsListAdapter.notifyDataSetChanged();

    }

    /**
     * 请求新闻信息
     */
    class DownloadTask extends AsyncTask<String, Integer, ArrayList<NewsBean>> {

        private ObjectOutputStream oos;

        @Override
        protected ArrayList<NewsBean> doInBackground(String... params) {
            try {
                String infoUrl = params[0];
                HttpUtils.getJsonString(infoUrl, new HttpUtils.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        newsList = JsonUtils.readJsonNewsBean(response, keyword);

                        if (count == 0) {
                            if (context != null) {
                                File cacheFile = FileUtils.getDisCacheDir(context, "NewsBean" + keyword);

                                try {
                                    oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
                                    oos.writeObject(newsList);
                                    count++;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (oos != null) {
                                        try {
                                            oos.close();
                                        }catch (IOException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
                return newsList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsBean> newsList) {
            if (newsList == null) {
                return;
            } else {
                NewsListFragment.this.newsList = newsList;
                newsListAdapter.setData(newsList);
//                recycle_news.setLayoutManager(layoutManager);
//                recycle_news.setAdapter(newsListAdapter);
                newsListAdapter.notifyDataSetChanged();

            }
            sr_refresh.setRefreshing(false);

        }
    }

    /**
     * 加载更多
     */
    class UpdateTask extends AsyncTask<String, Integer, ArrayList<NewsBean>> {

        private ArrayList<NewsBean> updateNewsList;

        @Override
        protected ArrayList<NewsBean> doInBackground(String... params) {
            try {
                String infoUrl = params[0];
                HttpUtils.getJsonString(infoUrl, new HttpUtils.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        updateNewsList = JsonUtils.readJsonNewsBean(response, keyword);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });

                if (updateNewsList.size() <= 0) {
                    newsListAdapter.isShowFooter(false);
                }


                for (NewsBean i : updateNewsList) {
                    newsList.add(i);
                }

                return updateNewsList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsBean> updateNewsList) {
            if (updateNewsList == null) {
                Toast.makeText(getContext(), "请求数据失败", Toast.LENGTH_SHORT).show();
            } else {
//                newsListAdapter.isShowFooter(false);
                newsListAdapter.setData(NewsListFragment.this.newsList);
            }
        }
    }
}