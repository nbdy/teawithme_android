package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.eberlein.smthnspcl.drinkteawithme.Static.USERS;

public class SettingsFragment extends Fragment {

    @BindView(R.id.username)
    TextInputEditText username;
    @BindView(R.id.teaTimeSeekbar)
    SeekBar teaTimeSeekbar;
    @BindView(R.id.teaTime)
    TextView teaTime;

    @BindView(R.id.teaVolumeSeekbar)
    SeekBar teaVolumeSeekbar;
    @BindView(R.id.teaVolume)
    TextView teaVolume;

    private DocumentReference user;
    private Context context;

    SettingsFragment(Context ctx, String userHash) {
        this.context = ctx;
        user = FirebaseFirestore.getInstance().collection(USERS).document(userHash);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        teaTimeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                teaTime.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User u = new User(snapshot);
                username.setText(u.getDisplayName());
                teaTime.setText(String.valueOf(u.getTeaTime()));
                teaTimeSeekbar.setProgress(u.getTeaTime());
                teaVolume.setText(String.valueOf(u.getCupSize()));
            }
        });
        teaVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Double x = 1.0 * i / 1000;
                seekBar.setProgress(i);
                teaVolume.setText(String.format(Locale.getDefault(), "%.1f", x));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;
    }

    @Override
    public void onStop() {
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User u = new User(snapshot);
                u.setDisplayName(username.getText().toString());
                u.setTeaTime(Integer.valueOf(teaTime.getText().toString()));
                u.addCupSize(Double.valueOf(teaVolume.getText().toString()) / 1000);
                Toast.makeText(context, context.getResources().getText(R.string.saved_settings), Toast.LENGTH_SHORT).show();
            }
        });
        super.onStop();
    }
}
