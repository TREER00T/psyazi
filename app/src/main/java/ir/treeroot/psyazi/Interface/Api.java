package ir.treeroot.psyazi.Interface;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.treeroot.psyazi.model.AddPost;
import ir.treeroot.psyazi.model.Message;
import ir.treeroot.psyazi.model.Users;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {


    //Request Login From Server
    @GET("login.php")
    Call<Users> loginAccount(
            @Query("username") String username,
            @Query("password") String password);

    //Request signUp From Server
    @GET("register.php")
    Call<Users> register(
            @Query("username") String username,
            @Query("password") String password,
            @Query("pnumber") String pnumber,
            @Query("aliasname") String aliasname);


    //Request Get Data From Server
    @GET("getDataPost.php")
    Call<List<AddPost>> GetData();


    //Request Set Change AliasName
    @POST("AliASName.php")
    Call<Users> aliasName(
            @Query("username") String username,
            @Query("aliasname") String aliasname);


    //Request Set Change AliasName
    @GET("setaliasname.php")
    Call<Users> getAliasName(
            @Query("username") String username);


    //Request Update Row In Local
    @GET("updateData.php")
    Call<List<AddPost>> UpdateData();


    //Request Get Data From Server
    @SerializedName("username")
    @GET("profileload.php")
    Call<Users> Get_Profile(
            @Query("username") String username);

    @GET("getMSG.php")
    Call<List<Message>> getMessage();


}












