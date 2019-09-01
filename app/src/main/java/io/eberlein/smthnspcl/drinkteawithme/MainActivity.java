package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.smthnspcl.drinkteawithme.Static.DISPLAY_NAME;
import static io.eberlein.smthnspcl.drinkteawithme.Static.FRIENDS;
import static io.eberlein.smthnspcl.drinkteawithme.Static.LAST_SESSION;
import static io.eberlein.smthnspcl.drinkteawithme.Static.ONLINE;
import static io.eberlein.smthnspcl.drinkteawithme.Static.SESSION_COUNT;
import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 420;
    private static final String TAG = "{ MAIN }";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @OnClick(R.id.logout)
    public void logout() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    private FragmentManager fragmentManager;

    private FirebaseAuth mAuth;
    private String firebaseUserHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void precheck(FirebaseUser user) {
        firebaseUserHash = Static.hash(user.getEmail());
        CollectionReference cr = FirebaseFirestore.getInstance().collection(USERS);
        DocumentReference ur = cr.document(firebaseUserHash);
        ur.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    Log.i(TAG, documentSnapshot.getId() + " does not exist");
                    HashMap<String, Object> u = new HashMap<>();
                    u.put(DISPLAY_NAME, user.getDisplayName());
                    u.put(ONLINE, true);
                    u.put(LAST_SESSION, getResources().getString(R.string.never_documented));
                    u.put(SESSION_COUNT, "0");
                    ur.set(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fragmentManager.beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                        }
                    });
                    //ur.collection(FRIENDS).
                } else {
                    Log.i(TAG, documentSnapshot.getId() + " does exist");
                    fragmentManager.beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                }
            }
        });
        getIntentData();
    }

    private void getIntentData() {
        Uri data = getIntent().getData();
        if (data != null) {
            String uh = data.getHost();
            String c = data.getPathSegments().get(0);
            if (c.equals("friend_accepted")) {
                Toast.makeText(this, "this would add some user to your friendslist", Toast.LENGTH_LONG).show();
                CollectionReference cr = FirebaseFirestore.getInstance().collection(USERS);
                DocumentReference ur = cr.document(firebaseUserHash);
                ur.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //documentSnapshot.get(FRIENDS, )
                    }
                });
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        precheck(user);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.headerUsernameLabel)).setText(user.getDisplayName());
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

    private void invite() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, "drink some tea with me: https://teawth.me/invite/" + firebaseUserHash);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, "invite someone"));
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
            f = new ProfileFragment();
        } else if (id == R.id.nav_history) {
            f = new HistoryFragment();
        } else if (id == R.id.nav_friends) {
            f = new FriendsFragment();
        } else if (id == R.id.nav_tools) {
            f = new SettingsFragment();
        } else if (id == R.id.nav_share) {
            invite();
        }

        if (f != null) fragmentManager.beginTransaction().replace(R.id.content, f).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) updateUI(auth.getCurrentUser());
        else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.BlackTheme)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build()
                            ))
                            .setTosAndPrivacyPolicyUrls(
                                    "https://teawth.me/tos",
                                    "https://teawth.me/privacy"
                            )
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) updateUI(FirebaseAuth.getInstance().getCurrentUser());
            else {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "sign in cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), "no internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "known unknowns", Toast.LENGTH_SHORT).show();
                Log.e("[BOII]", "Sign-in error: ", response.getError());
            }
        }
    }
}
