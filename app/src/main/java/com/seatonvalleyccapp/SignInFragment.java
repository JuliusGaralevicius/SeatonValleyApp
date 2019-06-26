package com.seatonvalleyccapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by julius on 15/02/2018.
 */
public class SignInFragment extends Fragment implements InfoRequestListener {
    private FirebaseAuth firebaseAuth;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mLogin;
    private ProgressBar mProgress;
    private OnChangeFragmentCallback callback;
    private TextView mRegister;
    private CheckBox mSaveLoginCheckBox;
    private SharedPreferences mLoginPreference;
    private SharedPreferences.Editor mLoginPreferenceEditor;
    private boolean isEmailSaved;

    private String lastEmail;
    private String lastPassword;

    public SignInFragment() {
        super();
    }

    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        // Add fragments to the stack and then detach them from the UI
        try {
            Fragment fragment = NewsFragment.class.newInstance();
            Fragment fragment1 = TwitterFragment.class.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_login, fragment)
                    .add(R.id.fl_login, fragment1)
                    .commit();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .detach(fragment)
                    .detach(fragment1)
                    .commit();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mSaveLoginCheckBox = (CheckBox) fragmentView.findViewById(R.id.cb_remember_email);
        mLoginPreference = getActivity().getSharedPreferences("loginPreference", Context.MODE_PRIVATE);
        mLoginPreferenceEditor = mLoginPreference.edit();
        mEmail = (TextInputEditText) fragmentView.findViewById(R.id.et_signin_email);
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastEmail = mEmail.getText().toString();
                System.err.println(lastEmail);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPassword = (TextInputEditText) fragmentView.findViewById(R.id.et_signin_password);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastPassword = mPassword.getText().toString();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRegister = (TextView) fragmentView.findViewById(R.id.tv_signin_no_account);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    callback.changeFragment(1);
            }
        });
        mLogin = (Button) fragmentView.findViewById(R.id.btn_signin_login);
        mLogin.setClickable(true);
        mProgress = (ProgressBar) fragmentView.findViewById(R.id.pb_signin_progress);
        mProgress.setVisibility(View.INVISIBLE);

        // add remembered email in text box
        isEmailSaved = mLoginPreference.getBoolean("saveLogin", false);
        if (isEmailSaved) {
            mEmail.setText(mLoginPreference.getString("email", ""));
            mSaveLoginCheckBox.setChecked(true);
        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLogin();
                onLoginClick();
            }
        });
        //loginWithData("juliux222@yahoo.com", "852456");
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void saveLogin() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);

        if (mSaveLoginCheckBox.isChecked()) {
            mLoginPreferenceEditor.putBoolean("saveLogin", true);
            mLoginPreferenceEditor.putString("email", mEmail.getText().toString());
            mLoginPreferenceEditor.commit();
        } else {
            mLoginPreferenceEditor.clear();
            mLoginPreferenceEditor.commit();
        }
    }

    private void onLoginClick() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        loginWithData(email, password);
    }
    private void askForAdminDetails(final String email, final String password){
        Toast.makeText(getContext(), "Please enter missing details", Toast.LENGTH_SHORT).show();

    }
    private void loginWithData(String email, String password) {

        mProgress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkAdminNoData(firebaseAuth.getCurrentUser().getUid());
                } else {
                    Toast.makeText(getContext(), "login failed", Toast.LENGTH_SHORT).show();
                }
                mProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void checkAdminNoData(final String uid) {
        String currentEmail = firebaseAuth.getCurrentUser().getEmail();
        FirebaseDatabase.getInstance().getReference("admins").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if user is an admin check if admin has full data
                if (dataSnapshot!=null){
                    FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // if first login, ask for data
                            if (!dataSnapshot.hasChild("postcode")){
                                firebaseAuth.signOut();
                                callback.changeFragment(1);
                                Toast.makeText(getContext(), "First time admin login detected, please fill in details", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                LoginIfNotBanned(uid);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    LoginIfNotBanned(uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void LoginIfNotBanned(String uid) {
        FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                if (!data.status.equals("banned")) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "account is banned", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        callback = (OnChangeFragmentCallback) context;
        super.onAttach(context);
    }

    @Override
    public String OnEmailRequest() {
        return lastEmail;
    }

    @Override
    public String OnPasswordRequest() {
        return lastPassword;
    }
}
