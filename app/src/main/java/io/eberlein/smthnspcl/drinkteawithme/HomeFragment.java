package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class HomeFragment extends Fragment {
    @BindView(R.id.lastTeaSession)
    TextView lastTeaSession;
    @BindView(R.id.sessionCount)
    TextView sessionCount;
    private String LAST_SESSION = "lastSession";
    private String SESSION_COUNT = "sessionCount";
    private String BOOK = "home";
    private String DATETIMEFORMAT = "EEEE, d MMMM yyyy HH:mm:ss";

    @OnClick(R.id.teaImageView)
    void teaImageViewTapped() {
        Toast.makeText(getContext(), "you are drinking tea now", Toast.LENGTH_SHORT).show();
        Paper.book(BOOK).write(LAST_SESSION, DateFormat.format(DATETIMEFORMAT, Calendar.getInstance().getTime()));
        Paper.book(BOOK).write(SESSION_COUNT, String.valueOf(Integer.valueOf(Paper.book(BOOK).read(SESSION_COUNT)) + 1));
        updateUI(); // todo check cooldown time
    }

    private void preCheck() {
        if (Paper.book(BOOK).read(LAST_SESSION).toString().isEmpty())
            Paper.book(BOOK).write(LAST_SESSION, "never documented");
        if (Paper.book(BOOK).read(SESSION_COUNT).toString().isEmpty())
            Paper.book(BOOK).write(SESSION_COUNT, "0");
    }

    private void setLastTeaSession() {
        lastTeaSession.setText(Paper.book(BOOK).read(LAST_SESSION));
    }

    private void setSessionCount() {
        sessionCount.setText(Paper.book(BOOK).read(SESSION_COUNT));
    }

    private void updateUI() {
        setLastTeaSession();
        setSessionCount();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, v);
        preCheck();
        updateUI();
        return v;
    }
}
