package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.eberlein.smthnspcl.drinkteawithme.Static.FRIENDS;
import static io.eberlein.smthnspcl.drinkteawithme.Static.ONLINE;
import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class FriendsFragment extends Fragment {
    private final static String TAG = "{ FRIENDS }";

    @BindView(R.id.friendList)
    RecyclerView friendList;
    private DocumentReference userReference;
    private FirebaseUser user;
    private FirestoreRecyclerAdapter<User, UserHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseFirestore.getInstance().collection(USERS).document(Static.hash(user.getEmail()));
        friendList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirestoreRecyclerOptions<User> o = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userReference.collection(FRIENDS).orderBy(ONLINE), User.class).build();
        adapter = new FirestoreRecyclerAdapter<User, UserHolder>(o) {
            @Override
            protected void onBindViewHolder(@NonNull UserHolder friendHolder, int i, @NonNull User f) {
                friendHolder.friendName.setText(f.getDisplayName());
                friendHolder.onlineStatus.setText(f.getOnline() ? "online" : "offline");
                friendHolder.lastOnline.setText(f.getLastOnline());
            }

            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_friend, parent, false));
            }
        };
        friendList.setAdapter(adapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        Log.i(TAG, "started listening");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.i(TAG, "stopped listening");
    }

    class UserHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friendName)
        TextView friendName;
        @BindView(R.id.onlineStatus)
        TextView onlineStatus;
        @BindView(R.id.lastOnline)
        TextView lastOnline;

        UserHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
