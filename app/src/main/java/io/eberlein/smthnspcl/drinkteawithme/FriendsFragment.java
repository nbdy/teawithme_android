package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsFragment extends Fragment {
    private User user;

    public FriendsFragment(User user) {
        this.user = user;
    }

    @OnClick(R.id.inviteButton)
    public void inviteButtonClicked() {
        new API().inviteUser(user, new onSuccess(), new onFailure());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    class onSuccess extends API.on {
        @Override
        public void execute(String data) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "drink some tea with me: " + data);
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, "invite someone"));
        }
    }

    class onFailure extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getContext(), "could not generate invitation url", Toast.LENGTH_SHORT).show();
        }
    }
}
