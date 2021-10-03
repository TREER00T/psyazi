package ir.treeroot.psyazi.Ui.Chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.treeroot.psyazi.Database.SQLite.DatabaseSqlite;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Utils.BottomSheetNavigationFragment;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.adapter.ChatBoxAdapter;
import ir.treeroot.psyazi.model.AddPost;
import ir.treeroot.psyazi.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyazi.Utils.Link.MyPref;
import static ir.treeroot.psyazi.Utils.Link.url_chat;

public class ChatActivity extends AppCompatActivity {

    String usernameFrom, usernameTo;
    EditText editText;
    ImageView bc_chat, attach_file, btn;
    RecyclerView rec_chat;
    List<Message> MessageList = new ArrayList<>();
    ChatBoxAdapter adapter;
    Handler handler;
    Thread thread;
    DatabaseSqlite db;
    TextView aliasnameAdminChat, is_typing;
    CircleImageView admin_img_chat;
    Socket socket;
    Api request, requestChat;
    SharedPreferences shPref;

    {
        try {
            socket = IO.socket(url_chat);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        Get_admin_username();

        GetProfileAdmin();

        ButtonClick();

        SocketIo();

    }

    private void SocketIo() {

        socket.emit("nickname", usernameFrom);

        socket.connect();
        socket.on("message", handlerInComingMessage);
        socket.on("SelectData", handlerInComingMessageSelect);
        socket.on("isTyping", HandlerIsTyping);
        socket.on("StopTyping", HandlerStopTyping);

    }

    private void ButtonClick() {

        btn.setOnClickListener(v1 -> {

            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeZone = new SimpleDateFormat("HH:mm");
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat groupBy = new SimpleDateFormat("dd MMMM yyyy");
            String dataTime = timeZone.format(calendar.getTime());
            String groupByTime = groupBy.format(calendar.getTime());
            String message = editText.getText().toString();
            sendMessage(message, dataTime, groupByTime);
            editText.setText("");

        });

    }

    public void init() {

        btn = findViewById(R.id.send_msg);
        editText = findViewById(R.id.edittext_msg);
        rec_chat = findViewById(R.id.rec_chat);
        bc_chat = findViewById(R.id.bc_chat);
        attach_file = findViewById(R.id.attach_file);
        aliasnameAdminChat = findViewById(R.id.aliasname_admin_chat);
        admin_img_chat = findViewById(R.id.img_admin_chat);
        is_typing = findViewById(R.id.is_typing);

        findViewById(R.id.back_chat).setOnClickListener(v -> finish());

        request = APIClient.getApiClient(Link.url).create(Api.class);
        requestChat = APIClient.getApiClient(url_chat).create(Api.class);

        editText.addTextChangedListener(ChatEditTextWatcher);
        editText.addTextChangedListener(IsTyping);
        db = new DatabaseSqlite(this);
        aliasnameAdminChat.setSelected(true);


        Glide.with(this)
                .load("https://treeroot.ir/data/wallpaper/1.png").into(bc_chat);

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        usernameFrom = shPref.getString("username", "");

        handler = new Handler();

        attach_file.setOnClickListener(v -> {

            BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetNavigationFragment.newInstance();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

        });

    }

    private void Get_admin_username() {

        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NonNull Call<List<AddPost>> call, @NonNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();
                usernameTo = addPosts.get(0).getUsername();
                sendUser(usernameTo);

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
                String message, usernameId, time_zone, groupByTime;

                try {

                    message = jsonObject.getString("message");
                    time_zone = jsonObject.getString("timezone");
                    usernameId = jsonObject.getString("from");
                    groupByTime = jsonObject.getString("groupByTime");


                    String format;
                    if (usernameId.equals(usernameFrom)) {

                        format = "green";

                        Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                        MessageList.add(m);

                    } else {

                        format = "white";

                        Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                        MessageList.add(m);

                    }

                    adapter = new ChatBoxAdapter(MessageList);

                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    if (MessageList.size() == 0) {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size()), 0);

                    } else {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size() - 1), 0);

                    }


                    rec_chat.setAdapter(adapter);
                    rec_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };

    public Emitter.Listener handlerInComingMessageSelect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message, usernameId, time_zone, groupByTime;

                try {

                    message = jsonObject.getString("message");
                    time_zone = jsonObject.getString("timezone");
                    usernameId = jsonObject.getString("from");
                    groupByTime = jsonObject.getString("groupByTime");

                    String format;
                    if (usernameId.equals(usernameFrom)) {

                        format = "green";

                        Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                        MessageList.add(m);

                    } else {

                        format = "white";

                        Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                        MessageList.add(m);

                    }

                    adapter = new ChatBoxAdapter(MessageList);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    if (MessageList.size() == 0) {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size()), 0);


                    } else {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size() - 1), 0);

                    }


                    rec_chat.setAdapter(adapter);
                    rec_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };

    private void sendMessage(String message, String time_zone, String groupByTime) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("from", usernameFrom);
            postData.put("to", usernameTo);
            postData.put("message", message);
            postData.put("timezone", time_zone);
            postData.put("groupByTime", groupByTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("message", postData);

    }


    public void sendUser(String usernameToId) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("from", usernameFrom);
            postData.put("to", usernameToId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("GetData", postData);

    }

    public Emitter.Listener HandlerIsTyping = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message;

                try {

                    message = jsonObject.getString("message");
                    is_typing.setText(message);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };


    public Emitter.Listener HandlerStopTyping = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message;

                try {

                    message = jsonObject.getString("message");
                    is_typing.setText(message);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };


    private final TextWatcher ChatEditTextWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!editText.getText().toString().isEmpty() && !(editText.length() < 1)) {


                btn.setVisibility(View.VISIBLE);
                attach_file.setVisibility(View.GONE);

            } else {


                btn.setVisibility(View.GONE);
                attach_file.setVisibility(View.VISIBLE);

            }

        }


        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    private final TextWatcher IsTyping = new TextWatcher() {

        JSONObject after;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            JSONObject postData = new JSONObject();

            try {

                postData.put("to", usernameTo);
                postData.put("message", "در حال نوشتن ...");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("isTyping", postData);

        }


        @Override
        public void afterTextChanged(Editable s) {

            after = new JSONObject();

            try {

                after.put("to", usernameTo);
                after.put("message", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            thread = new Thread(() -> {

                try {

                    Thread.sleep(1500);
                    handler.post(() -> socket.emit("StopTyping", after));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

            thread.start();

        }

    };


    private void GetProfileAdmin() {

        if (db.ExitsPostTable()) {

            List<AddPost> addPosts = db.postList();
            String aliasnames = addPosts.get(0).getAliasname();

            Picasso.get()
                    .load(addPosts.get(0).getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(admin_img_chat);

            aliasnameAdminChat.setText(aliasnames);

        }

        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NonNull Call<List<AddPost>> call, @NonNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();

                Picasso.get()
                        .load(addPosts.get(0).getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(admin_img_chat);

                String aliasname = addPosts.get(0).getAliasname();
                aliasnameAdminChat.setText(aliasname);
                db.InsertPost(addPosts);

            }

            @Override
            public void onFailure(@NonNull Call<List<AddPost>> call, @NonNull Throwable t) {

            }

        });

    }

}