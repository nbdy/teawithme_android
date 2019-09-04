package io.eberlein.smthnspcl.drinkteawithme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryFragment extends Fragment {

    public HistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, v);
        return v;
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
