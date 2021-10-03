package ir.treeroot.psyazi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.treeroot.psyazi.Database.Room.DataSource.FavoriteRepository;
import ir.treeroot.psyazi.Database.Room.Local.DatabaseFavorite;
import ir.treeroot.psyazi.Database.Room.Local.FavoriteDateSource;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;
import ir.treeroot.psyazi.Ui.VideoPlayer.VideoPlayerActivity;
import ir.treeroot.psyazi.model.AddPost;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Ui.Comment.CommentActivity;
import ir.treeroot.psyazi.Utils.Link;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<AddPost> data;
    public static FavoriteRepository favoriteRepository;
    public static DatabaseFavorite databaseFavorite;

    public PostAdapter(Context context, List<AddPost> data) {

        this.context = context;
        this.data = data;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

        return new MyViewHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        AddPost addPost = data.get(position);

        holder.title.setText(addPost.getTitle());
        holder.aliasname_admin.setText(addPost.getAliasname());

        holder.comment_post.setOnClickListener(v -> {

            Intent i = new Intent(context, CommentActivity.class);
            i.putExtra(Link.Id_post, addPost.getPostid());
            i.putExtra("text", addPost.getText());
            i.putExtra("title", addPost.getTitle());
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        });


        String format = addPost.getFormat();

        try {

            if (format != null) {

                holder.img.setVisibility(View.VISIBLE);
                holder.linearLayout_play.setVisibility(View.VISIBLE);

                if (format.equals("MP4")) {

                    holder.img.setVisibility(View.GONE);
                    holder.video_post.setVideoURI(Uri.parse(addPost.getImg()));

                    holder.linearLayout_play.setOnClickListener(v -> {

                        Intent i=new Intent(context, VideoPlayerActivity.class);
                        i.putExtra(Link.Key_i_video_player,addPost.getImg());
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
                            .load(addPost.getImg()).into(holder.img);

                }

            }

        } catch (NullPointerException ignored) { }


        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3")).setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setBaseAlpha(1).setDropoff(50).build();

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        Picasso.get()
                .load(addPost.getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.profile_image);

        databaseFavorite = DatabaseFavorite.getInstance(context);
        favoriteRepository = FavoriteRepository.getInstance(FavoriteDateSource.getInstance(databaseFavorite.favoriteDao()));


        if (favoriteRepository.itemFavId(Integer.parseInt(addPost.getPostid())) == 1) {

            holder.bookmark.setImageResource(R.drawable.bookmark_full);

        } else {

            holder.bookmark.setImageResource(R.drawable.bookmark_null);

        }

        holder.bookmark.setOnClickListener(v -> {

            if (favoriteRepository.itemFavId(Integer.parseInt(addPost.getPostid())) != 1) {

                holder.bookmark.setImageResource(R.drawable.bookmark_full);
                Favorite favorite = new Favorite();
                favorite.postid = addPost.getPostid();
                favorite.img = addPost.getImg();
                favorite.text = addPost.getText();
                favorite.title = addPost.getTitle();
                favorite.image = addPost.getImage();
                favorite.aliasname = addPost.getAliasname();
                favorite.format = addPost.getFormat();
                favoriteRepository.InsertItem(favorite);

            } else {

                holder.bookmark.setImageResource(R.drawable.bookmark_null);
                Favorite favorite = new Favorite();
                favorite.postid = addPost.getPostid();
                favorite.img = addPost.getImg();
                favorite.text = addPost.getText();
                favorite.title = addPost.getTitle();
                favorite.image = addPost.getImage();
                favorite.aliasname = addPost.getAliasname();
                favorite.format = addPost.getFormat();
                favoriteRepository.DeleteItem(favorite);

            }

        });

    }



    @Override
    public int getItemCount() {
        return data.size();
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
