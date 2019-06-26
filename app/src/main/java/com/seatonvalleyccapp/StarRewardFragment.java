package com.seatonvalleyccapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class StarRewardFragment extends DialogFragment {
    private static String arg0 = "stars";
    private int numStars;

    private TextView message;
    private Button confirmButton;
    private RatingBar bar;
    public StarRewardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle data = getArguments();
        numStars = data.getInt(arg0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_star_reward, container, false);
        message = v.findViewById(R.id.tv_star_reward_c_message);
        message.setText("Congratulations! You have earned an extra star");
        bar =  v.findViewById(R.id.rb_star_reward_stars);
        bar.setRating(numStars);
        confirmButton = (Button) v.findViewById(R.id.btn_star_reward_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

}
