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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class HistoryFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public HistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, v);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SessionAdapter adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);
        FirebaseFirestore.getInstance().collection(USERS).document(Static.hash(FirebaseAuth.getInstance().getCurrentUser().getEmail())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User u = new User(snapshot);
                // for(Session s : u.getSessions()) adapter.addSession(s);
            }
        });
        return v;
    }

    class SessionAdapter extends RecyclerView.Adapter<SessionHolder> {
        private List<Session> sessions;

        SessionAdapter() {
            sessions = new ArrayList<>();
        }

        void addSession(Session session) {
            if (!sessions.contains(session)) {
                sessions.add(session);
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SessionHolder(LayoutInflater.from(getContext()).inflate(R.layout.viewholder_session, parent));
        }

        @Override
        public void onBindViewHolder(@NonNull SessionHolder holder, int position) {
            Session s = sessions.get(position);
            holder.teaName.setText(s.getTeaName());
            holder.teaVolume.setText(String.valueOf(s.getLiter()));
            holder.timestamp.setText(s.getTimestamp());
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }
    }

    class SessionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.teaName)
        TextView teaName;
        @BindView(R.id.teaVolume)
        TextView teaVolume;
        @BindView(R.id.timeStamp)
        TextView timestamp;

        SessionHolder(View v) {
            super(v);
        }
    }
}
