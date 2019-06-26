package com.seatonvalleyccapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {
    private TextView address;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_contact, container, false);

        address = (TextView) fragmentView.findViewById(R.id.tv_contact_addressText);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addMarkerOnMap();
            }
        });

        return  fragmentView;

    }

    private void addMarkerOnMap(){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:55.073276,-1.526665?q=55.073276,-1.526665(Seaton Valley Council)"));
        startActivity(i);
    }


}
