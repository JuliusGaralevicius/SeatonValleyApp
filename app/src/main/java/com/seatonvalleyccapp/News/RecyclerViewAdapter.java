package com.seatonvalleyccapp.News;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.seatonvalleyccapp.R;

import java.util.ArrayList;

/**
 * Created by V on 26/03/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<NewsItem> newsItemList;

    public RecyclerViewAdapter(Context context, ArrayList<NewsItem> newsItemList) {
        this.context = context;
        this.newsItemList = newsItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.cardview_news_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.title.setText(newsItemList.get(position).getTitle());
        holder.shortDescription.setText(newsItemList.get(position).getShortDescription());
        holder.thumbnail.setImageDrawable(newsItemList.get(position).getThumbnail());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                // Open news page in browser
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(newsItemList.get(position).getUrl()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView title;
        TextView shortDescription;
        CardView cardView;

        public MyViewHolder(View itemView){
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.iv_news_thumbnail);
            title = (TextView) itemView.findViewById(R.id.tv_news_title);
            shortDescription = (TextView) itemView.findViewById(R.id.tv_news_description);
            cardView = (CardView) itemView.findViewById(R.id.cv_id);
        }
    }

}