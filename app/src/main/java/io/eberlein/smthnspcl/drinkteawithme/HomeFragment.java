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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.smthnspcl.drinkteawithme.Static.LAST_SESSION;
import static io.eberlein.smthnspcl.drinkteawithme.Static.SESSION_COUNT;

public class HomeFragment extends Fragment {
    private String TAG = "{ HOME }";
    @BindView(R.id.lastTeaSession)
    TextView lastTeaSession;
    @BindView(R.id.sessionCount)
    TextView sessionCount;
    @BindView(R.id.sessionCountSuffixLabel)
    TextView sessionCountSuffix;

    private DocumentReference sessionDocument;
    private FirebaseUser user;

    @OnClick(R.id.teaImageView)
    void teaImageViewTapped() {
        Toast.makeText(getContext(), "you are drinking tea now", Toast.LENGTH_SHORT).show();
        sessionDocument.update(LAST_SESSION, Static.getCurrentTimestamp());
        sessionDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                sessionDocument.update(SESSION_COUNT, String.valueOf(Long.valueOf(documentSnapshot.get(SESSION_COUNT, String.class)) + 1));
            }
        });
        updateUI();
        // todo cooldown time
        // todo get time between last session and new session and save to avg session time
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        sessionDocument = FirebaseFirestore.getInstance().collection("users").document(Static.hash(user.getEmail()));
        updateUI();
    }

    private void updateUI() {
        sessionDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                lastTeaSession.setText(documentSnapshot.get(LAST_SESSION, String.class));
                String sc = documentSnapshot.get(SESSION_COUNT, String.class);
                if (sc.equals("1")) sessionCountSuffix.setText(R.string.session);
                else sessionCountSuffix.setText(R.string.sessions);
                sessionCount.setText(sc);
            }
        });
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
