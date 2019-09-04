package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class ProfileFragment extends Fragment {
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.sessionCount)
    TextView sessionCount;
    @BindView(R.id.friendCount)
    TextView friendCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        FirebaseFirestore.getInstance().collection(USERS).document(Static.hash(FirebaseAuth.getInstance().getCurrentUser().getEmail())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User u = new User(snapshot);
                username.setText(u.getDisplayName());
                email.setText(u.getEmail());
                sessionCount.setText(String.valueOf(u.getSessionCount()));
                friendCount.setText(String.valueOf(u.getFriends().size()));
            }
        });
        return v;
    }


}
