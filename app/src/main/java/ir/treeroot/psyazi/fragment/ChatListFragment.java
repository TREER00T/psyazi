package ir.treeroot.psyazi.fragment;

import static ir.treeroot.psyazi.Utils.Link.url_chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.treeroot.psyazi.Database.SQLite.DatabaseSqlite;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Ui.Chats.ChatActivity;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.adapter.ChatBoxAdapter;
import ir.treeroot.psyazi.model.AddPost;
import ir.treeroot.psyazi.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    Api request;
    TextView aliasnameAdmin;
    CircleImageView admin_img;
    View online;
    DatabaseSqlite db;
    Handler handler;
    Socket socket;
    RelativeLayout relativeLayout_chat;

    {
        try {
            socket = IO.socket(url_chat);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        init(v);
        SocketIo();

        GetProfileAdmin();


        return v;

    }

    private void SocketIo() {
        socket.connect();

        socket.on("adminOffline", handlerInComingMessage);
    }


    private void init(View v) {

        admin_img = v.findViewById(R.id.admin_img);
        aliasnameAdmin = v.findViewById(R.id.aliasnameAdmin);
        relativeLayout_chat = v.findViewById(R.id.relativeLayout_chat);
        online = v.findViewById(R.id.online);
        aliasnameAdmin.setSelected(true);
        relativeLayout_chat.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), ChatActivity.class)));

        request = APIClient.getApiClient(Link.url).create(Api.class);
        db = new DatabaseSqlite(getActivity());

        handler = new Handler();
    }



    private void GetProfileAdmin() {

        if (db.ExitsPostTable()) {

            List<AddPost> addPosts = db.postList();
            String aliasnames = addPosts.get(0).getAliasname();

            Picasso.get()
                    .load(addPosts.get(0).getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(admin_img);

            aliasnameAdmin.setText(aliasnames);

        }

        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NonNull Call<List<AddPost>> call, @NonNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();
                Picasso.get()
                        .load(addPosts.get(0).getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(admin_img);

                String aliasname = addPosts.get(0).getAliasname();
                aliasnameAdmin.setText(aliasname);

                db.InsertPost(addPosts);

            }

            @Override
            public void onFailure(@NonNull Call<List<AddPost>> call, @NonNull Throwable t) {
            }

        });

    }


    public Emitter.Listener handlerInComingMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                boolean status;

                try {

                    status = jsonObject.getBoolean("status");


                    if (!status) {
                        online.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };


}