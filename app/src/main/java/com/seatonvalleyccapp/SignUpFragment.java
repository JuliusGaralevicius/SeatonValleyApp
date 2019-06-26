package com.seatonvalleyccapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by julius on 15/02/2018.
 */

public class SignUpFragment extends Fragment {

    private InfoRequestListener listener;

    private TextInputEditText mName;
    private TextInputEditText mSurname;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private TextInputEditText mPostcode;
    private Button mRegister;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ProgressBar mProgress;
    private Set<String> postcodes;

    private boolean finishedLoading;

    public SignUpFragment(){
        super();
    }
    public static SignUpFragment newInstance(InfoRequestListener listener){
        SignUpFragment fragment = new SignUpFragment();
        fragment.listener = listener;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        finishedLoading = false;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mName = (TextInputEditText) fragmentView.findViewById(R.id.et_register_name);
        mSurname = (TextInputEditText) fragmentView.findViewById(R.id.et_register_surname);
        mEmail = (TextInputEditText) fragmentView.findViewById(R.id.et_register_email);
        mPassword = (TextInputEditText) fragmentView.findViewById(R.id.et_register_password);
        mRegister = (Button) fragmentView.findViewById(R.id.btn_register);
        mProgress = (ProgressBar) fragmentView.findViewById(R.id.pb_register_load);
        mPostcode = (TextInputEditText) fragmentView.findViewById(R.id.et_register_postcode);
        mProgress.setVisibility(View.INVISIBLE);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryRegister();
            }
        });
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String email = listener.OnEmailRequest();
                String password = listener.OnPasswordRequest();
                System.err.println("Focus changed" +email + password);
                if (email!=null){
                    mEmail.setText(email);
                }
                if (password != null) {
                    mPassword.setText(password);
                }
            }
        });
        firebaseDatabase.getReference("postcodes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postcodes = new HashSet<>();
                for (DataSnapshot s: dataSnapshot.getChildren()){
                    postcodes.add(s.getKey());
                }
                finishedLoading = true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    private void tryRegister(){
        String name = mName.getText().toString().trim();
        String surname = mSurname.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String postcode = mPostcode.getText().toString().replaceAll("\\s+", "").toUpperCase();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        if (name.length()<1){
            Toast.makeText(getContext(), "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (surname.length()<1){
            Toast.makeText(getContext(), "Enter your surname", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.length()<6){
            Toast.makeText(getContext(), "Email must be at least 6 characters", Toast.LENGTH_LONG).show();
            return;
        }
        if (finishedLoading){
            if (!postcodeExists(postcode)){
                Toast.makeText(getContext(), "Your postcode does not belong to Seaton Valley", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                postcode = insertSpace(postcode);
            }
        }
        if (!finishedLoading){
            Toast.makeText(getContext(), "Please wait for servers to load and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6){
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            return;
        }

        registerWithData(name, surname, email, password, postcode);
    }

    private String insertSpace(String postcode) {
        int length = postcode.length();
        return postcode.substring(0, length-3) + " " + postcode.substring(length-3, length);
    }

    private boolean postcodeExists(String postcode) {
        String postcodeShort = postcode.replaceAll("\\s+", "");
        return postcodes.contains(postcodeShort);
    }
    private void registerWithData(final String name, final String surname, final String email, final String password, final String postcode){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final String uid = authResult.getUser().getUid();
                firebaseDatabase.getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            System.err.println("DATA EXISTS");
                            UserData oldData = dataSnapshot.getValue(UserData.class);
                            UserData newData = new UserData(name, surname,  email, System.currentTimeMillis(), oldData.stars, oldData.progress, oldData.postsAvailable, postcode, oldData.markerUsed, oldData.status);
                            newData.lastLoginReward = oldData.lastLoginReward;
                            firebaseDatabase.getReference("users").child(uid).setValue(newData);
                            Toast.makeText(getContext(), "User data updated, you can now log in", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            UserData newData = new UserData(name, surname,  email, System.currentTimeMillis(), 1, 0, 1, postcode, 0, "");
                            firebaseDatabase.getReference("users").child(uid).setValue(newData);
                            Toast.makeText(getContext(), "User data updated, you can now log in", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        UserData data = new UserData(name, surname,  email, System.currentTimeMillis(), 1, 0, 1, postcode, 0, "");
                        if (task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();
                            data.setName(name);
                            data.setSurname(surname);
                            data.setEmail(email);

                            firebaseDatabase.getReference("users").child(uid).setValue(data);
                            signInWithData(email, password);
                        }
                        else {
                            Toast.makeText(getContext(), "Sign up was not successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void signInWithData(final String email, final String password){
        mProgress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mProgress.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    startActivity(intent);
                }
                else {
                    mProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
