package com.example.mrredrum.zhihu;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

public class MainFragment extends Fragment {
    private boolean isLoading = false;
    private Handler handler = new Handler();
    private Activity mActivity;
    private NewsListItemAdapter mAdapter;

    /*model*/
    private Before before;
    private Latest latest;
    private String date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected void initData() {
        loadFirst();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    /**/
    private void loadFirst() {
        isLoading = true;
        if (HttpUtils.isNetworkConnected(mActivity)) {
            HttpUtils.get(Constant.LATESTNEWS, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    SQLiteDatabase db = ((MainActivity) mActivity).getCacheDbHelper().getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + Constant.LATEST_COLUMN + ",' " + responseString + "')");
                    db.close();
                    parseLatestJson(responseString);
                }
            });
        } else {
            SQLiteDatabase db = ((MainActivity) mActivity).getCacheDbHelper().getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + Constant.LATEST_COLUMN, null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseLatestJson(json);
            } else {
                isLoading = false;
            }
            cursor.close();
            db.close();
        }
    }

    private void parseLatestJson(String responseString) {
        Gson gson = new Gson();
        latest = gson.fromJson(responseString, Latest.class);
        date = latest.getDate();
        //kanner.setTopEntities(latest.getTop_stories());
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<StoriesEntity> storiesEntities = latest.getStories();
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle("今日热闻");
                storiesEntities.add(0, topic);
                mAdapter.addList(storiesEntities);
                isLoading = false;
            }
        });
    }

    private void parseBeforeJson(String responseString) {
        Gson gson = new Gson();
        before = gson.fromJson(responseString, Before.class);
        if (before == null) {
            isLoading = false;
            return;
        }
        date = before.getDate();
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<StoriesEntity> storiesEntities = before.getStories();
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle(convertDate(date));
                storiesEntities.add(0, topic);
                mAdapter.addList(storiesEntities);
                isLoading = false;
            }
        });
    }

    private String convertDate(String date) {
        String result = date.substring(0, 4);
        result += "年";
        result += date.substring(4, 6);
        result += "月";
        result += date.substring(6, 8);
        result += "日";
        return result;
    }
}
