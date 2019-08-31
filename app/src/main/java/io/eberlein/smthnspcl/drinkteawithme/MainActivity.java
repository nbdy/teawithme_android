package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private User user;
    private API api;
    private FragmentManager fragmentManager;

    void launchUserLoginActivity() {
        Intent i = new Intent(getApplicationContext(), UserLoginActivity.class);
        startActivity(i);
    }

    void checkIfLoggedIn() {
        user = Paper.book("user").read("username");
        if (user == null) {
            Log.i("[MAIN]", "no user data could be loaded");
            Toast.makeText(this, "no user data saved", Toast.LENGTH_SHORT).show();
            launchUserLoginActivity();
        } else {
            Log.i("[MAIN]", "loaded user: " + user.getUsername());
            Toast.makeText(this, "saved user data loaded", Toast.LENGTH_SHORT).show();
            api.loginUser(user, new onLoginSuccess(), new onLoginFailure(), new onLoginError());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Paper.init(this);
        fragmentManager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        api = new API(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment f = null;

        if (id == R.id.nav_home) {
            f = new HomeFragment();
        } else if (id == R.id.nav_profile) {
            f = new ProfileFragment(user);
        } else if (id == R.id.nav_history) {
            f = new HistoryFragment(user);
        } else if (id == R.id.nav_friends) {
            f = new FriendsFragment(user);
        } else if (id == R.id.nav_tools) {
            f = new SettingsFragment();
        }

        if (f != null) fragmentManager.beginTransaction().replace(R.id.content, f).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfLoggedIn();
    }

    class onLoginSuccess extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "logged in successfully", Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.headerUsernameLabel)).setText(user.getUsername());
            fragmentManager.beginTransaction().replace(R.id.content, new HomeFragment()).commit();
        }
    }

    class onLoginFailure extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "could not login with saved user data", Toast.LENGTH_SHORT).show();
            Paper.book("user").delete("username");
            user = null;
            launchUserLoginActivity();
        }
    }

    class onLoginError extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "there was an error connecting to the server", Toast.LENGTH_SHORT).show();
            user = null;
            launchUserLoginActivity();
        }
    }
}
