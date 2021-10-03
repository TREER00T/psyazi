package ir.treeroot.psyazi.Ui.About;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


import java.util.List;

import ir.treeroot.psyazi.Database.SQLite.DatabaseSqlite;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.model.AddPost;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Utils.Link;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class About extends AppCompatActivity {

    TextView about;
    Api request;
    DatabaseSqlite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();

        GetAbout();

    }

    public void init() {

        about = findViewById(R.id.abouts);

        findViewById(R.id.back_about).setOnClickListener(v -> finish());
        db = new DatabaseSqlite(this);

        request = APIClient.getApiClient(Link.url).create(Api.class);

    }


    private void GetAbout() {

        if(db.ExitsPostTable()){

            List<AddPost> addPosts = db.postList();
            String abouts = addPosts.get(0).getAbout();
            about.setText(abouts);

        }

        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NonNull Call<List<AddPost>> call, @NonNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();
                assert addPosts != null;
                String abouts = addPosts.get(0).getAbout();
                about.setText(abouts);
                db.InsertPost(addPosts);

            }

            @Override
            public void onFailure(@NonNull Call<List<AddPost>> call, @NonNull Throwable t) {
            }

        });

    }

}