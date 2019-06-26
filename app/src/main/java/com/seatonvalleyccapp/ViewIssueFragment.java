package com.seatonvalleyccapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by julius on 23/03/2018.
 */

public class ViewIssueFragment extends DialogFragment implements View.OnClickListener {
    // expected argument
    private static final String ARG0 = "markerID";

    private String markerID;
    private MarkerData markerData;
    private UserData posterData;

    private ImageButton mExit;
    private ImageView mIssueImage;
    private TextView mPosterInformation;
    private TextView mDescription;

    private DatabaseReference markersDatabase;
    private DatabaseReference usersDatabase;
    private StorageReference photosStorage;

    private BottomSheetBehavior behavior;

    boolean isActive = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme);
        markerID = getArguments().getString(ARG0);
        View v = inflater.inflate(R.layout.fragment_view_issue, container, false);
        initUIFields(v);
        initFirebaseFields();
        setFields();
        return v;
    }

    private void setFields() {
        getMarkerData();
    }

    private void getMarkerData() {
        markersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                markerData = dataSnapshot.getValue(MarkerData.class);
                getUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getUserData() {
        String userID = markerData.getOwnerID();
        usersDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posterData = dataSnapshot.getValue(UserData.class);
                String text = "User does not exists";
                if (posterData!=null)
                {
                    text = posterData.getName();
                }
                text+= "\n" + TimeUtils.getUKDate(markerData.getDatePosted());
                mPosterInformation.setText(text);
                setDescription();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setDescription() {
        mDescription.setText(markerData.getDescription());
        setImage();
    }

    private void setImage() {
        final long TEN_MEGABYTES = 1024 * 1024 * 10;
                photosStorage.child(markerID).getBytes(TEN_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        if (isActive)
                        Glide.with(getActivity().getApplicationContext()).load(bytes).fitCenter().into(mIssueImage);
                        mIssueImage.setOnClickListener(ViewIssueFragment.this);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Could not load picture", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initFirebaseFields() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        markersDatabase = database.getReference("markers").child(markerID);
        usersDatabase = database.getReference("users");
        photosStorage = FirebaseStorage.getInstance().getReference("photos");
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
        isActive = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isActive = false;
    }

    private void initUIFields(View v) {
        mExit = (ImageButton) v.findViewById(R.id.btn_view_issue_exit);

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mIssueImage = (ImageView) v.findViewById(R.id.iv_view_issue_image);
        mPosterInformation = (TextView) v.findViewById(R.id.tv_view_issue_user_info);
        mDescription = (TextView) v.findViewById(R.id.tv_view_issue_description);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_view_issue_image:



                Intent i = new Intent(getActivity(), FullScreenImageActivity.class);
                Bitmap bitmap = ((GlideBitmapDrawable) mIssueImage.getDrawable()).getBitmap();

               // mIssueImage.buildDrawingCache();
                GlobalVariables.currentBitmap = bitmap;
                startActivity(i);
                break;
        }
    }
}