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
    @BindView(R.id.usernameEditText)
    EditText username;

    FriendsFragment() {

    }

    @OnClick(R.id.addButton)
    void addButtonClicked() {
        String otherUser = username.getText().toString();
        if (otherUser.isEmpty()) {
            Toast.makeText(getContext(), "no username specified", Toast.LENGTH_SHORT).show();
        } else {
            // todo
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        return v;
    }
}
