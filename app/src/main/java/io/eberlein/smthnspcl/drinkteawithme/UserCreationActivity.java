package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserCreationActivity extends AppCompatActivity {

    @BindView(R.id.usernameEditText)
    EditText username;
    @BindView(R.id.passwordEditText)
    EditText password;
    @BindView(R.id.repeatedPasswordEditText)
    EditText rpassword;

    @OnClick(R.id.createAccountButton)
    public void create() {
        String u = username.getText().toString();
        String p = password.getText().toString();
        String r = rpassword.getText().toString();
        if (u.isEmpty() || p.isEmpty() || r.isEmpty()) {
            Toast.makeText(this, "username / password fields are empty", Toast.LENGTH_SHORT).show();
        } else if (!p.equals(r)) {
            Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
        } else if (p.length() < 6) { // todo check if upper / lower etc
            Toast.makeText(this, "password should be more than 6 chars", Toast.LENGTH_SHORT).show();
        } else {
            new API().createUser(new User(u, p), new onSuccess(), new onFailure());
        }
    }

    @OnClick(R.id.backToLoginButton)
    public void login() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercreation);
        ButterKnife.bind(this);
    }

    class onSuccess extends API.on {
        public void execute() {
            Toast.makeText(getApplicationContext(), "user created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    class onFailure extends API.on {
        @Override
        public void execute() {
            Toast.makeText(getApplicationContext(), "username taken", Toast.LENGTH_SHORT).show();
            username.setText("");
        }
    }
}
