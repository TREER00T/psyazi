package ir.treeroot.psyazi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.treeroot.psyazi.Database.SQLite.DatabaseSqlite;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.model.AddPost;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.Utils.ConnectNetwork;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.adapter.PostAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyazi.Utils.Link.MyPref;

public class PostFragment extends Fragment {

    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView recyclerView;
    SearchView search;
    RequestQueue requestQueue;
    List<AddPost> addPosts = new ArrayList<>();
    SwipeRefreshLayout refresh;
    PostAdapter adapter;
    LinearLayout search_bar;
    Api request;
    LottieAnimationView animationView;
    DatabaseSqlite db;
    String username;
    SharedPreferences shPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        init(v);

        Refresh();

        Search();

        deleteLocalData();
        GetData();
        return v;

    }

    private void Refresh() {

        refresh.setOnRefreshListener(() -> {

            GetData();
            //noinspection deprecation
            new Handler().postDelayed(() -> refresh.setRefreshing(false), 1000);
            animationView.setVisibility(View.GONE);

        });

    }

    private void Search() {

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @SuppressWarnings("CollectionAddedToSelf")
            @Override
            public boolean onQueryTextSubmit(String query) {

                findPost(query);
                adapter = new PostAdapter(getContext(), addPosts);
                recyclerView.setAdapter(adapter);
                addPosts.removeAll(addPosts);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

    }

    public void init(View v) {

        search = v.findViewById(R.id.searchview);
        recyclerView = v.findViewById(R.id.recyclerview);
        search_bar = v.findViewById(R.id.search_bar);
        shimmerFrameLayout = v.findViewById(R.id.shimmer_layout);
        refresh = v.findViewById(R.id.refresh);
        animationView = v.findViewById(R.id.animationView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setFadingEdgeLength(80);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        requestQueue = Volley.newRequestQueue(requireActivity());
        request = APIClient.getApiClient(Link.url).create(Api.class);
        db = new DatabaseSqlite(getActivity());
        shPref = requireActivity().getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        username = shPref.getString("username", "");

    }


    public void GetData() {

        shimmerFrameLayout.startShimmer();

        if (!ConnectNetwork.isOnline(requireActivity())) {

            new StyleableToast
                    .Builder(requireActivity())
                    .text("اینترنت خود را چک کنید")
                    .textColor(Color.WHITE)
                    .backgroundColor(Color.parseColor("#FFFA3C3C"))
                    .font(R.font.isans)
                    .show();

        }

        if (db.ExitsPostTable()) {

            List<AddPost> addPosts = db.postList();
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new PostAdapter(getContext(), addPosts);
            recyclerView.setAdapter(adapter);
            recyclerView.setFadingEdgeLength(80);
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.stopShimmer();

        }

        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NonNull Call<List<AddPost>> call, @NonNull Response<List<AddPost>> response) {

                recyclerView.setVisibility(View.VISIBLE);
                List<AddPost> list = response.body();

                adapter = new PostAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);

                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                db.InsertPost(list);

            }

            @Override
            public void onFailure(@NonNull Call<List<AddPost>> call, @NonNull Throwable t) {

            }

        });

    }

    public void deleteLocalData() {

        request.UpdateData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<AddPost>> call, @NotNull Response<List<AddPost>> response) {

                List<AddPost> list = response.body();

                for (int i = 0; i < list.size(); i++) {
                    AddPost data = list.get(i);
                    db.deleteCourse(data);
                }

            }

            @Override
            public void onFailure(@NotNull Call<List<AddPost>> call, @NotNull Throwable t) {

            }

        });

    }

    public void findPost(String url) {

        String LINK = Link.URL_GET_Search + url;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LINK,

                null, response -> {

            try {

                JSONArray jsonArray = response.getJSONArray("search");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String titles = jsonObject.getString("title");
                    String aliasname = jsonObject.getString("aliasname");
                    String img = jsonObject.getString("img");
                    String image = jsonObject.getString("image");
                    String text = jsonObject.getString("text");
                    String id = jsonObject.getString("id");
                    String postid = jsonObject.getString("postid");
                    String format = jsonObject.getString("format");


                    AddPost addPost = new AddPost();

                    addPost.setTitle(titles);
                    addPost.setAliasname(aliasname);
                    addPost.setImg(img);
                    addPost.setImage(image);
                    addPost.setText(text);
                    addPost.setId(id);
                    addPost.setPostid(postid);
                    addPost.setFormat(format);
                    addPosts.add(addPost);
                    animationView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    adapter = new PostAdapter(getContext(), addPosts);
                    recyclerView.setAdapter(adapter);

                }

                if (jsonArray.toString().equals("[]")) {

                    animationView.setVisibility(View.VISIBLE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> { });

        requestQueue.add(jsonObjectRequest);

    }

}