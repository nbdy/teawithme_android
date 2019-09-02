package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class FriendsFragment extends Fragment {

    @BindView(R.id.friendList)
    RecyclerView friendList;

    private String userHash;

    FriendsFragment(String userHash) {
        this.userHash = userHash;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference usersReference = FirebaseFirestore.getInstance().collection(USERS);
        friendList.setLayoutManager(new LinearLayoutManager(getContext()));
        UserAdapter adapter = new UserAdapter();
        friendList.setAdapter(adapter);
        usersReference.document(userHash).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                for (String f : new User(snapshot).getFriends().values()) {
                    usersReference.document(f).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            adapter.addUser(new User(snapshot));
                        }
                    });
                }
            }
        });

        return v;
    }

    class UserAdapter extends RecyclerView.Adapter<UserHolder> {
        List<User> users;

        UserAdapter() {
            users = new ArrayList<>();
        }

        @NonNull
        @Override
        public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserHolder(LayoutInflater.from(getContext()).inflate(R.layout.viewholder_friend, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserHolder holder, int position) {
            User u = users.get(position);
            holder.friendName.setText(u.getDisplayName());
            holder.lastOnline.setText(u.getLastOnline());
            holder.onlineStatus.setText(u.getOnline() ? "online" : "offline");
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        void addUser(User u) {
            if (!users.contains(u)) {
                users.add(u);
                notifyItemChanged(users.indexOf(u));
            }
        }
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
