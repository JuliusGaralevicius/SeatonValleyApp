package com.seatonvalleyccapp;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.RatingBar;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RewardCallback {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private UserData userData;
    private RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        setNavigationHeader(navigationView);
        MenuItem initialItem = navigationView.getMenu().getItem(0);
        initialItem.setChecked(true);
        onNavigationItemSelected(initialItem);

    }

    private void setNavigationHeader(NavigationView navigationView) {
        View v = navigationView.getHeaderView(0);
        final TextView title = (TextView) v.findViewById(R.id.tv_navigation_header_title);
        TextView subtitle = (TextView) v.findViewById(R.id.tv_navigation_header_subtitle);
        ratingBar = (RatingBar) v.findViewById(R.id.rb_rating_bar);
        subtitle.setText(firebaseUser.getEmail());

        firebaseDatabase.getReference("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userData = dataSnapshot.getValue(UserData.class);

                title.setText(userData.getName());
                RewardUtils utils = new RewardUtils();
                utils.checkReward(firebaseUser.getUid(), RewardUtils.RewardType.DAILY_LOGIN, MainActivity.this);
                utils.checkReward(firebaseUser.getUid(), RewardUtils.RewardType.ISSUE_RESOLVED, MainActivity.this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("stars").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer stars = dataSnapshot.getValue(Integer.class);
                ratingBar.setRating(stars.intValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            firebaseAuth.signOut();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Class fragmentClass = null;
        Fragment fragment = null;
        String title = "";

        if (id == R.id.nav_report) {
            fragmentClass = MapFragment.class;
            title = "Report a Problem";
        } else if (id == R.id.nav_bincollection) {
            fragmentClass = BinCollectionFragment.class;
            title = "Bin Collection";
        } else if (id == R.id.nav_contact) {
            fragmentClass = ContactFragment.class;
            title = "Contact Us";
        } else if (id == R.id.nav_twitter) {
            fragmentClass = TwitterFragment.class;
            title = "Twitter Feed";
        } else if (id == R.id.nav_about) {
            title = "About";
            fragmentClass = AboutFragment.class;
        } else if (id == R.id.nav_news) {
            fragmentClass = NewsFragment.class;
            title = "News";
        }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        getSupportActionBar().setTitle(title);
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnRewardEarned(RewardInformation info) {
        if (info.dailyReward){

            DialogFragment fragment = new RewardFragment();

            Bundle extras = new Bundle();
            extras.putInt("progress", info.dailyPoints);
            extras.putString("rewardType", RewardFragment.types[0]);

            fragment.setArguments(extras);
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        }
        if (info.resolvedReward){
            DialogFragment fragment = new RewardFragment();

            Bundle extras = new Bundle();
            extras.putInt("progress", info.resolvedPoints);
            extras.putString("rewardType", RewardFragment.types[1]);

            fragment.setArguments(extras);
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        }

        if (info.starEarned){
            DialogFragment fragment = new StarRewardFragment();

            Bundle extras = new Bundle();
            extras.putInt("stars", info.newStars);

            fragment.setArguments(extras);
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        }

    }
}
