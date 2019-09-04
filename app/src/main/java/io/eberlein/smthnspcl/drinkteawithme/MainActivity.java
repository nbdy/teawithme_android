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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.Arrays;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 420;

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

    public void setupFriendSessionCountListeners(User u) {
        CollectionReference cr = FirebaseFirestore.getInstance().collection(USERS);
        for (String fr : u.getFriends().values()) {
            cr.document(fr).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (snapshot != null && snapshot.exists()) {
                            User f = new User(snapshot);

                        }
                    }
                }
            });
        }
    }

    private void precheck(FirebaseUser user) {
        firebaseUserHash = Static.hash(user.getEmail());
        CollectionReference cr = FirebaseFirestore.getInstance().collection(USERS);
        DocumentReference ur = cr.document(firebaseUserHash);
        ur.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u;
                if (!documentSnapshot.exists())
                    u = new User(user.getDisplayName(), user.getEmail(), getResources().getString(R.string.never_documented));
                else u = new User(documentSnapshot);
                u.setOnline();
                fragmentManager.beginTransaction().replace(R.id.content, new HomeFragment(firebaseUserHash)).commit();
                setupFriendSessionCountListeners(u);
            }
        });
        getIntentData();
    }

    private void getIntentData() {
        Uri data = getIntent().getData();
        if (data != null) {
            String uh = data.getHost();
            if (uh.equals(firebaseUserHash)) {
                Toast.makeText(this, getResources().getString(R.string.i_hope_you_are_your_own_friend), Toast.LENGTH_LONG).show();
            } else {
                String c = data.getPathSegments().get(0);
                if (c.equals("friend_accepted")) {
                    CollectionReference ur = FirebaseFirestore.getInstance().collection(USERS);
                    DocumentReference u = ur.document(firebaseUserHash);
                    DocumentReference o = ur.document(uh);
                    u.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User cu = new User(documentSnapshot);
                            o.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User ru = new User(documentSnapshot);
                                    Toast.makeText(getApplicationContext(), "added " + ru.getDisplayName() + "to your friends ", Toast.LENGTH_LONG).show();
                                    ru.addFriend(firebaseUserHash);
                                    o.set(ru);
                                }
                            });
                            cu.addFriend(uh);
                            u.set(cu);
                        }
                    });
                }
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
            new XPopup.Builder(this).asConfirm(getResources().getString(R.string.do_you_really_want_to_leave), getResources().getString(R.string.you_are_about_to_leave_the_app), new OnConfirmListener() {
                @Override
                public void onConfirm() {
                    finish();
                }
            });
        }
    }

    private void invite() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, "drink some tea with me: https://teawth.me/invite/" + firebaseUserHash);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getResources().getString(R.string.invite_someone)));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment f = null;

        if (id == R.id.nav_home) {
            f = new HomeFragment(firebaseUserHash);
        } else if (id == R.id.nav_profile) {
            f = new ProfileFragment();
        } else if (id == R.id.nav_history) {
            f = new HistoryFragment();
        } else if (id == R.id.nav_friends) {
            f = new FriendsFragment(firebaseUserHash);
        } else if (id == R.id.nav_tools) {
            f = new SettingsFragment(this, firebaseUserHash);
        } else if (id == R.id.nav_share) {
            invite();
        }

        if (f != null) fragmentManager.beginTransaction().replace(R.id.content, f).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setOffline() {
        if (firebaseUserHash != null) {
            FirebaseFirestore.getInstance().collection(USERS).document(firebaseUserHash).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    new User(snapshot).setOffline();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        setOffline();
        super.onPause();
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
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
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
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    response.getError().printStackTrace();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.known_unknowns), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
