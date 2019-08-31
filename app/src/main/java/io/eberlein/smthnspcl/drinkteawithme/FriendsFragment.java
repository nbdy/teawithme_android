package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Intent;
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

    public FriendsFragment(User user) {
        this.user = user;
    }

    @OnClick(R.id.addButton)
    public void addButtonClicked() {
        String otherUser = username.getText().toString();
        if (otherUser.isEmpty()) {
            Toast.makeText(getContext(), "no username specified", Toast.LENGTH_SHORT).show();
        } else {
            new API(getContext()).addUser(user, new User(otherUser), new onAddSuccess(), new onAddFailure(), new onAddError());
        }

    }

    @OnClick(R.id.inviteButton)
    public void inviteButtonClicked() {
        new API(getContext()).inviteUser(user, new onInviteSuccess(), new onInviteFailure(), new onInviteError());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    class onInviteSuccess extends API.on {
        @Override
        public void execute(String data) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "drink some tea with me: " + data);
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, "invite someone"));
        }
    }

    class onInviteFailure extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getContext(), "could not generate invitation url", Toast.LENGTH_SHORT).show();
        }
    }

    class onInviteError extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getContext(), "there was an error connecting to the server", Toast.LENGTH_SHORT).show();
        }
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
