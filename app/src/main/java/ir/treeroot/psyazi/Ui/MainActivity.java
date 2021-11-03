package ir.treeroot.psyazi.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Ui.fragment.AccountFragment;
import ir.treeroot.psyazi.Ui.fragment.PostFragment;
import ir.treeroot.psyazi.Ui.fragment.ChatListFragment;

import static ir.treeroot.psyazi.Utils.Link.MyPref;
import static ir.treeroot.psyazi.Utils.Link.url_chat;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    String usernameFrom;
    ViewPager viewPager;
    AccountFragment accountFragment;
    ChatListFragment chatListFragment;
    PostFragment postFragment;
    MenuItem menuItem;
    SharedPreferences shPref;
    Socket socket;

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
        setContentView(R.layout.activity_main);
        init();

        SocketIo();
        sendUser(usernameFrom);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        setupViewPager(viewPager);

        bottomNavigationView.setOnItemSelectedListener(

                item -> {

                    switch (item.getItemId()) {

                        case R.id.post:

                            viewPager.setCurrentItem(0);

                            break;

                        case R.id.talk:

                            viewPager.setCurrentItem(1);

                            break;

                        case R.id.account:

                            viewPager.setCurrentItem(2);

                            break;

                    }

                    return false;

                });


    }
    private void SocketIo() {

        socket.connect();

    }
    public void sendUser(String username) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("username", username);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("userOnline", postData);

    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        postFragment = new PostFragment();
        chatListFragment = new ChatListFragment();
        accountFragment = new AccountFragment();

        adapter.addFragment(postFragment);
        adapter.addFragment(chatListFragment);
        adapter.addFragment(accountFragment);

        viewPager.setAdapter(adapter);

    }

    public void init() {

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        usernameFrom = shPref.getString("username", "");
    }


    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        @SuppressWarnings("deprecation")
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        userDisconnected(usernameFrom);


    }
    public void userDisconnected(String username) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("username", username);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("disconnect", postData);

    }

}