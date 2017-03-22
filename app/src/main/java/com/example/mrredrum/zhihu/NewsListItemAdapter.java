package com.example.mrredrum.zhihu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NewsListItemAdapter extends BaseAdapter {
    private List<StoriesEntity> entities;
    private Context context;
    private ImageLoader mImageloader;
    private DisplayImageOptions options;
    private boolean isLight;

    public NewsListItemAdapter(Context context) {
        this.context = context;
        this.entities = new ArrayList<>();
        mImageloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        //isLight = ((MainActivity) context).isLight();
    }

    public void addList(List<StoriesEntity> items) {
        this.entities.addAll(items);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_news, parent, false);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String readSeq = PreUtils.getStringFromDefault(context, "read", "");
        if (readSeq.contains(entities.get(position).getId() + "")) {
            viewHolder.tv_title.setTextColor(context.getResources().getColor(R.color.clicked_tv_textcolor));
        } else {
            viewHolder.tv_title.setTextColor(context.getResources().getColor(isLight ? android.R.color.black : android.R.color.white));
        }

        ((LinearLayout) viewHolder.iv_title.getParent().getParent().getParent()).setBackgroundColor(context.getResources().getColor(isLight ? R.color.light_news_item : R.color.dark_news_item));
        ((FrameLayout) viewHolder.tv_title.getParent().getParent()).setBackgroundResource(isLight ? R.drawable.item_background_selector_light : R.drawable.item_background_selector_dark);
        StoriesEntity entity = entities.get(position);
        viewHolder.tv_title.setText(entity.getTitle());
        if (entity.getImages() != null) {
            viewHolder.iv_title.setVisibility(View.VISIBLE);
            mImageloader.displayImage(entity.getImages().get(0), viewHolder.iv_title, options);
        } else {
            viewHolder.iv_title.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_title;
        ImageView iv_title;
    }
}
