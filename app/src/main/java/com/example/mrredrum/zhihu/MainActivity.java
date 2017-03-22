package com.example.mrredrum.zhihu;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    private CacheDbHelper dbHelper;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new CacheDbHelper(this, 1);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.light_toolbar));
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }
}
