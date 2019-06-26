package com.seatonvalleyccapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seatonvalleyccapp.About.ProfileIcon;
import com.seatonvalleyccapp.About.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    private List<ProfileIcon> profileIcons;
    private View view;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        profileIcons = new ArrayList<>();

        // Add team members with appropriate values
        profileIcons.add(new ProfileIcon(R.drawable.julius,"Julius Garalevicius","Lead developer"));
        profileIcons.add(new ProfileIcon(R.drawable.zain,"Zain Khizar","Front-End Developer"));
        profileIcons.add(new ProfileIcon(R.drawable.vasilios,"Vasilios Papaefstathiou","Back-End Developer"));
        profileIcons.add(new ProfileIcon(R.drawable.adam,"Adam Wilson","Project Manager/Web Developer"));
        profileIcons.add(new ProfileIcon(R.drawable.anthony,"Anthony Beavis","Documentation/Testing"));
        profileIcons.add(new ProfileIcon(R.drawable.mehdi,"Mehdi Habbouly","Web Developer"));
        profileIcons.add(new ProfileIcon(R.drawable.jonathan,"Jonathan Faran","Documentation/Testing"));
        profileIcons.add(new ProfileIcon(R.drawable.matt,"Matt Alton","Researcher/Web Developer"));
        profileIcons.add(new ProfileIcon(R.drawable.ben,"Ben Bridgman","Researcher/Web Developer"));

        // Configure recycler view
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv_about);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(),profileIcons);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        rv.setAdapter(adapter);


        return view;
    }

}
