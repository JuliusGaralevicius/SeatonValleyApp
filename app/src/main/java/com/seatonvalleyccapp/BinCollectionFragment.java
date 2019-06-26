package com.seatonvalleyccapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.seatonvalleyccapp.News.CalendarHighlighter;

import java.util.HashMap;


public class BinCollectionFragment extends Fragment {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    MaterialCalendarView calendarView;
    String postcode;
    DatabaseReference schedules;
    String uid;

    public BinCollectionFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bin_collection, container, false);
        calendarView =  v.findViewById(R.id.cv_bin_collection_calendar);
        calendarView.state().edit().setMinimumDate(CalendarDay.from(2017, 12, 31)).setMaximumDate(CalendarDay.from(2018, 11, 31)).commit();
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
;
        db.getReference("users").child(uid).child("postcode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postcode = dataSnapshot.getValue(String.class);
                String postCodeShort = substring(postcode);

                db.getReference("timetables").child("2018")
                        .child(postCodeShort).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<Integer, Integer> schedule = new HashMap<>();
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String weekOfYear = child.getKey();
                            int weekOfYearInt = Integer.parseInt(weekOfYear);
                            Integer dayOfWeek = child.getValue(Integer.class);
                            schedule.put(weekOfYearInt, dayOfWeek+1);
                        }
                        CalendarHighlighter h = new CalendarHighlighter(schedule);
                        calendarView.addDecorator(h);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }
    private String substring(String original){
        String sub="";
        for (int i =0; i<original.length(); i++){
            char c = original.charAt(i);
            sub+=c;
            if (c == ' '){
                sub+= original.charAt(i+1);
                break;
            }
        }
        return sub;
    }

}
