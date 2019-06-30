package com.test.fan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Fragment> fragments;
    private int prePos;

    // Constant
    private static final String[] TAGS = {"home", "s2t", "reading", "history", "setting"};
    private static final String PRE = "PREPOS";
    private static final int HOME = 0;
    private static final int S2T = 1;
    private static final int READING = 2;
    private static final int HISTORY = 3;
    private static final int SETTING = 4;
    private static final int READ_WRITE_PERM = 2333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //判断是否已经登录过
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        boolean isSignedIn = sp.getBoolean("isSignedIn", false);
        if(!isSignedIn)
        {
            goToLoginActivity();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragments = new ArrayList<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        if (!(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                !(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    READ_WRITE_PERM);
        }
        else {
            // Initialize the fragments
            if(savedInstanceState == null) {
                prePos = 0;
                fragments = new ArrayList<>();
                fragments.add(new HomeFragment());
                fragments.add(new S2TFragment());
                fragments.add(new ReadingFragment());
                fragments.add(new HistoryFragment());
                fragments.add(new SettingFragment());
            }
            else {
                prePos = savedInstanceState.getInt(PRE);
                fragments = new ArrayList<>();
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAGS[HOME]);
                S2TFragment s2TFragment = (S2TFragment) getSupportFragmentManager().findFragmentByTag(TAGS[S2T]);
                ReadingFragment readingFragment = (ReadingFragment) getSupportFragmentManager().findFragmentByTag(TAGS[READING]);
                HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(TAGS[HISTORY]);
                SettingFragment settingFragment = (SettingFragment) getSupportFragmentManager().findFragmentByTag(TAGS[SETTING]);
                fragments.add(homeFragment);
                fragments.add(s2TFragment);
                fragments.add(readingFragment);
                fragments.add(historyFragment);
                fragments.add(settingFragment);
            }
            setDefaultFragment(prePos);
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_WRITE_PERM) {
            boolean granted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                fragments = new ArrayList<>();
                fragments.add(new HomeFragment());
                fragments.add(new S2TFragment());
                fragments.add(new ReadingFragment());
                fragments.add(new HistoryFragment());
                fragments.add(new SettingFragment());
                setDefaultFragment(HOME);
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Tips")
                        .setMessage("没有给予一定的权限，程序将会终结")
                        .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            switchFragment(HOME);
        }
        else if (id == R.id.nav_s2t) {
            switchFragment(S2T);
        }
        else if (id == R.id.nav_reading) {
            switchFragment(READING);
        }
        else if (id == R.id.nav_history) {
            switchFragment(HISTORY);
        }
        else if (id == R.id.nav_setting) {
            switchFragment(SETTING);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PRE, prePos);
    }

    private void setDefaultFragment(int pos){
        Fragment fragment = fragments.get(pos);
        if(fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragments.get(prePos), TAGS[pos]).commit();
        }
    }

    private void switchFragment(int pos) {
        Fragment currentFragment = fragments.get(pos);
        Fragment previousFragment = fragments.get(prePos);
        getSupportFragmentManager().beginTransaction().hide(previousFragment).commit();
        if(currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, currentFragment, TAGS[pos]).commit();
        }
        prePos = pos;
    }
}