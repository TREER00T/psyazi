package ir.treeroot.psyazi.Ui.Favorite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.treeroot.psyazi.Database.Room.DataSource.FavoriteRepository;
import ir.treeroot.psyazi.Database.Room.Local.DatabaseFavorite;
import ir.treeroot.psyazi.Database.Room.Local.FavoriteDateSource;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.adapter.FavoritePostAdapter;
import ir.treeroot.psyazi.model.AddPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CompositeDisposable compositeDisposable;
    FavoriteRepository favoriteRepository;
    DatabaseFavorite databaseFavorite;
    Api request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);


        init();

        deleteLocalData();

        RoomDb();

    }

    private void RoomDb() {

        databaseFavorite = DatabaseFavorite.getInstance(this);

        favoriteRepository = FavoriteRepository.getInstance(FavoriteDateSource.getInstance(databaseFavorite.favoriteDao()));

        compositeDisposable = new CompositeDisposable();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        compositeDisposable.add(favoriteRepository.getALlFavoriteItem()

                .observeOn(AndroidSchedulers.mainThread())

                .subscribeOn(Schedulers.io()).subscribe(addPosts -> {

                    FavoritePostAdapter adapter = new FavoritePostAdapter(getApplicationContext(), addPosts);

                    recyclerView.setAdapter(adapter);

                }));

    }

    public void deleteLocalData() {

        request.UpdateData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NonNull Call<List<AddPost>> call, @NonNull Response<List<AddPost>> response) {

                List<AddPost> list = response.body();

                for (int i = 0; i < list.size(); i++) {

                    Favorite favorite = new Favorite();

                    int data = Integer.parseInt(favorite.postid = list.get(i).getRow());

                    favoriteRepository.DeleteOrderBy(data);

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<AddPost>> call, @NonNull Throwable t) {

            }
        });

    }

    private void init() {

        recyclerView = findViewById(R.id.recyclerview_favorite);

        findViewById(R.id.back_favorite).setOnClickListener(v -> finish());

        request = APIClient.getApiClient(Link.url).create(Api.class);

    }

}