package com.seatonvalleyccapp;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class RewardFragment extends DialogFragment {
    private static final String arg0 = "progress";
    private static final String arg2 = "rewardType";
    public static final String[] types = {"daily login", "resolved issue(s)"};
    private String messageText;

    private TextView message;
    private Button confirmButton;
    public RewardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle data = getArguments();
        int progress = data.getInt(arg0);
        String rewardType = data.getString(arg2);
        messageText = "You have earned " +(progress) +" progress points for " +rewardType;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reward, container, false);
        message = (TextView) v.findViewById(R.id.tv_reward_c_message);
        message.setText(messageText);
        confirmButton = (Button) v.findViewById(R.id.btn_reward_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

}
