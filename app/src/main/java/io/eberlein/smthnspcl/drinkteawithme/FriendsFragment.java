package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsFragment extends Fragment {
    private User user;

    @BindView(R.id.usernameEditText)
    EditText username;

    FriendsFragment(User user) {
        this.user = user;
    }

    @OnClick(R.id.addButton)
    void addButtonClicked() {
        String otherUser = username.getText().toString();
        if (otherUser.isEmpty()) {
            Toast.makeText(getContext(), "no username specified", Toast.LENGTH_SHORT).show();
        } else {
            new API(getContext()).addUser(user, new User(otherUser), new onAddSuccess(), new onAddFailure(), new onAddError());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    class onAddSuccess extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getContext(), "sent friend request", Toast.LENGTH_SHORT).show();
        }
    }

    class onAddFailure extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getContext(), "user does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    class onAddError extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getContext(), "could not send request to server", Toast.LENGTH_SHORT).show();
        }
    }
}
