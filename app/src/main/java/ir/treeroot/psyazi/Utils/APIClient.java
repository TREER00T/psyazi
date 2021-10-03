package ir.treeroot.psyazi.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {

    public static Retrofit retrofit = null;

    public static Retrofit getApiClient(String url){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit==null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }

        return  retrofit;

    }

}
