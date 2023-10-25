package com.blue.youtubeapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blue.youtubeapi.MainActivity;
import com.blue.youtubeapi.R;
import com.blue.youtubeapi.model.Post;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    Context context;
    ArrayList<Post> postArrayList;

    public PostAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_row, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = postArrayList.get(position);

        holder.txtTitle.setText(post.title);
        holder.txtBody.setText(post.body);
        Glide.with(context).load(post.thumbnailUrl).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public TextView txtBody;
        public ImageView imgView;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtBody = itemView.findViewById(R.id.txtBody);
            imgView = itemView.findViewById(R.id.imgView);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 몇번째 카드뷰를 눌렀는지 확인
                    int index = getAdapterPosition();
                    Post post = postArrayList.get(index);

                    // 유튜브로 연결
                    String url = "https://www.youtube.com/watch?v=" + post.videoId;

                    // 유튜브 웹브라우저 액티비티 실행
                    openWebPage(url);
                }
            });
        }
    }
    // 웹브라우저 액티비티 실행 함수
    public void openWebPage(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
