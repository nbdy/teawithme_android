package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class HomeFragment extends Fragment {
    private String TAG = "{ HOME }";
    @BindView(R.id.lastTeaSession)
    TextView lastTeaSession;
    @BindView(R.id.sessionCount)
    TextView sessionCount;
    @BindView(R.id.sessionCountSuffixLabel)
    TextView sessionCountSuffix;

    private DocumentReference user;
    private String userHash;

    HomeFragment(String userHash) {
        this.userHash = userHash;
    }

    @OnClick(R.id.teaImageView)
    void teaImageViewTapped() {
        Toast.makeText(getContext(), "you are drinking tea now", Toast.LENGTH_SHORT).show();
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                new User(snapshot).setLastSession(Static.getCurrentTimestamp());
            }
        });
        // todo cooldown time
        // todo get time between last session and new session and save to avg session time
    }

    private void init() {
        user = FirebaseFirestore.getInstance().collection(USERS).document(userHash);
        user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (snapshot != null && snapshot.exists()) {
                        updateUI(new User(snapshot));
                    }
                }
            }
        });
    }

    private void updateUI(User user) {
        lastTeaSession.setText(user.getLastSession());
        Integer sc = user.getSessionCount();
        if (sc == 1) sessionCountSuffix.setText(R.string.session);
        else sessionCountSuffix.setText(R.string.sessions);
        sessionCount.setText(String.valueOf(sc));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }
}
