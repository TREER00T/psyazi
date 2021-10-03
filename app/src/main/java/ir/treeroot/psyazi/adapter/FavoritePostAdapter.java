package ir.treeroot.psyazi.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.treeroot.psyazi.Database.Room.DataSource.FavoriteRepository;
import ir.treeroot.psyazi.Database.Room.Local.DatabaseFavorite;
import ir.treeroot.psyazi.Database.Room.Local.FavoriteDateSource;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Ui.Comment.CommentActivity;
import ir.treeroot.psyazi.Ui.VideoPlayer.VideoPlayerActivity;
import ir.treeroot.psyazi.Utils.Link;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FavoritePostAdapter extends RecyclerView.Adapter<FavoritePostAdapter.MyViewHolder> {

    Context context;
    List<Favorite> favorites;
    public static DatabaseFavorite databaseFavorite;
    public static FavoriteRepository favoriteRepository;

    public FavoritePostAdapter(Context context, List<Favorite> favorites) {

        this.context = context;
        this.favorites = favorites;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        Favorite data=favorites.get(position);

        databaseFavorite =DatabaseFavorite.getInstance(context);
        favoriteRepository =FavoriteRepository.getInstance(FavoriteDateSource.getInstance(databaseFavorite.favoriteDao()));

        Picasso.get()
                .load(data.image).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.profile_image);

        String format = data.format;

        try {

            if (format != null) {

                holder.img.setVisibility(View.VISIBLE);
                holder.linearLayout_play.setVisibility(View.VISIBLE);

                if (format.equals("MP4")) {

                    holder.img.setVisibility(View.GONE);
                    holder.video_post.setVideoURI(Uri.parse(data.img));

                    holder.linearLayout_play.setOnClickListener(v -> {

                        Intent i=new Intent(context, VideoPlayerActivity.class);
                        i.putExtra(Link.Key_i_video_player,data.img);
                        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);

                    });

                }

                holder.video_post.setVisibility(View.VISIBLE);
                holder.linearLayout_play.setVisibility(View.VISIBLE);

                if (format.equals("JPG")) {

                    holder.video_post.setVisibility(View.GONE);
                    holder.linearLayout_play.setVisibility(View.GONE);

                    Glide.with(context)
                            .load(data.img).into(holder.img);

                }

            }

        } catch (NullPointerException ignored) { }

        holder.title.setText(data.title);
        holder.aliasname_admin.setText(data.aliasname);

        holder.comment_post.setOnClickListener(v -> {

            Intent i = new Intent(context, CommentActivity.class);
            i.putExtra(Link.Id_post, data.postid);
            i.putExtra("text", data.text);
            i.putExtra("title", data.title);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        });

        databaseFavorite =DatabaseFavorite.getInstance(context);
        favoriteRepository =FavoriteRepository.getInstance(FavoriteDateSource.getInstance(databaseFavorite.favoriteDao()));

        if(favoriteRepository.itemFavId(Integer.parseInt(data.postid))==1){

            holder.bookmark.setImageResource(R.drawable.bookmark_full);

        } else {

            holder.bookmark.setImageResource(R.drawable.bookmark_null);

        }

        holder.bookmark.setOnClickListener(v -> {

            if(favoriteRepository.itemFavId(Integer.parseInt(data.postid))!=1){

                holder.bookmark.setImageResource(R.drawable.bookmark_full);
                Favorite favorite =new Favorite();
                favorite.postid=data.postid;
                favorite.img=data.img;
                favorite.text=data.text;
                favorite.title=data.title;
                favorite.image=data.image;
                favorite.aliasname=data.aliasname;
                favorite.format=data.format;
                favoriteRepository.InsertItem(favorite);

            }else {

                holder.bookmark.setImageResource(R.drawable.bookmark_null);
                Favorite favorite =new Favorite();
                favorite.postid=data.postid;
                favorite.img=data.img;
                favorite.text=data.text;
                favorite.title=data.title;
                favorite.image=data.image;
                favorite.aliasname=data.aliasname;
                favorite.format=data.format;
                favoriteRepository.DeleteItem(favorite);

            }

        });

    }


    @Override
    public int getItemCount() {
        return favorites.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView title, aliasname_admin;
        ImageView img, comment_post, bookmark;
        CircleImageView profile_image;
        VideoView video_post;
        LinearLayout linearLayout_play;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img_post);
            profile_image = itemView.findViewById(R.id.profile_image);
            aliasname_admin = itemView.findViewById(R.id.aliasname_admin);
            comment_post = itemView.findViewById(R.id.comment_post);
            bookmark = itemView.findViewById(R.id.bookmark);
            video_post = itemView.findViewById(R.id.video_post);
            linearLayout_play = itemView.findViewById(R.id.linearLayout_play);

        }

    }

}
