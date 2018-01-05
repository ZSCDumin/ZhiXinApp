package com.zscdumin.zhixinapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zscdumin.zhixinapp.R;
import com.zscdumin.zhixinapp.bean.NewsBean;

import java.util.ArrayList;

/**
 * Created by luo-pc on 2016/5/15.
 */
public class NewsListAdapter extends RecyclerView.Adapter {

    //列表元素
    private static final int TYPE_ITEM = 0;
    //脚布局
    private static final int TYPE_FOOTER = 1;

    public final String TAG = "NewsListAdapter";
    private ArrayList<NewsBean> newsList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    //是否显示脚布局
    private boolean showFooter = true;

    public void setData(ArrayList<NewsBean> newsList) {
        this.newsList = newsList;
        this.notifyDataSetChanged();
    }

    public NewsListAdapter(Context context) {
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_news_item, parent, false);
            ItemViewHolder vh = new ItemViewHolder(root);
            return vh;
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_listview_footer, null);

            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.tv_title.setText(newsList.get(position).getTitle());

            vh.tv_desc.setText(newsList.get(position).getDigest());

            //加载图片
            Glide.with(context).load(newsList.get(position).getImgsrc())
                    .error(R.drawable.ic_image_loadfail).crossFade().into(vh.iv_image);
        }

    }

    @Override
    public int getItemViewType(int position) {
        //将最后一个元素设置成脚布局
        if (!showFooter) {
            return TYPE_ITEM;
        }
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }

    }

    public void isShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
    }

    public boolean isShowFooter() {
        return this.showFooter;
    }

    @Override
    public int getItemCount() {
        int ishow = showFooter ? 1 : 0;
        if (newsList == null) {
            return ishow;
        }
        return newsList.size() + ishow;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_title;
        private TextView tv_desc;
        private ImageView iv_image;

        public ItemViewHolder(View root) {
            super(root);
            iv_image = (ImageView) root.findViewById(R.id.iv_image);
            tv_title = (TextView) root.findViewById(R.id.tv_title);
            tv_desc = (TextView) root.findViewById(R.id.tv_desc);

            root.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, this.getLayoutPosition());
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


}
