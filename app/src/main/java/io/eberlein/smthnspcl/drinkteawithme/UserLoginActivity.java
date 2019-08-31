package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class UserLoginActivity extends AppCompatActivity {

    @BindView(R.id.usernameEditText)
    EditText username;
    @BindView(R.id.passwordEditText)
    EditText password;

    @OnClick(R.id.loginButton)
    public void login() {
        String u = username.getText().toString();
        String p = password.getText().toString();
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "username / password not filled", Toast.LENGTH_SHORT).show();
        } else {
            new API(getApplicationContext()).loginUser(new User(u, p), new onSuccess(), new onFailure(), new onError());
        }
    }

    @OnClick(R.id.createAccountButton)
    public void create() {
        Intent i = new Intent(this, UserCreationActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        ButterKnife.bind(this);
    }

    class onSuccess extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "logged in", Toast.LENGTH_SHORT).show();
            Paper.book("user").write("username", new User(username.getText().toString(), password.getText().toString()));
            finish();
        }
    }

    class onFailure extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "username / password wrong", Toast.LENGTH_SHORT).show();
            password.setText("");
        }
    }

    class onError extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "there was an error connecting to the server", Toast.LENGTH_SHORT).show();
        }
    }
}
