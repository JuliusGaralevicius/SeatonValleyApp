package com.seatonvalleyccapp.About;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.seatonvalleyccapp.R;

import java.util.List;

/**
 * Created by V on 16/03/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<ProfileIcon> profileIcons;

    public RecyclerViewAdapter(Context context, List<ProfileIcon> profileIcons) {
        this.context = context;
        this.profileIcons = profileIcons;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate custom layout design
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.cardview_about_icon, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // Set values to be displayed on cards
        holder.profilePicture.setImageResource(profileIcons.get(position).getProfilePicture());
        holder.name.setText(profileIcons.get(position).getName());
        holder.role.setText(profileIcons.get(position).getRole());
    }

    @Override
    public int getItemCount() {
        return profileIcons.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        TextView name;
        TextView role;
        CardView card;

        public MyViewHolder(View profileView) {
            super(profileView);

            profilePicture = (ImageView) profileView.findViewById(R.id.iv_profilePicture);
            name = (TextView) profileView.findViewById(R.id.tv_profileName);
            role = (TextView) profileView.findViewById(R.id.tv_profileRole);
            card = (CardView) profileView.findViewById(R.id.cv_card);
        }
    }

}
