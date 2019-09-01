package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.ChangeEventListener;
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

    @BindView(R.id.friendList)
    RecyclerView friendList;
    private DocumentReference userReference;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseFirestore.getInstance().collection(USERS).document(Static.hash(user.getEmail()));
        friendList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirestoreRecyclerOptions<Friend> o = new FirestoreRecyclerOptions.Builder<Friend>()
                .setQuery(userReference.collection(FRIENDS).orderBy(ONLINE), Friend.class)
                .setLifecycleOwner(this).build();
        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Friend, FriendHolder>(o) {

            @Override
            protected void onBindViewHolder(@NonNull FriendHolder friendHolder, int i, @NonNull Friend f) {
                friendHolder.friendName.setText(f.displayName);
                friendHolder.onlineStatus.setText(f.online ? "online" : "offline");
                friendHolder.lastOnline.setText(f.lastOnline);
            }

            @NonNull
            @Override
            public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FriendHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_friend, parent, false));
            }
        };
        friendList.setAdapter(adapter);
        return v;
    }

    class Friend {
        Boolean online;
        String displayName;
        String lastOnline;
        String created;
        String sessionCount;
        String lastSession;
    }

    class FriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friendName)
        TextView friendName;
        @BindView(R.id.onlineStatus)
        TextView onlineStatus;
        @BindView(R.id.lastOnline)
        TextView lastOnline;

        FriendHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
