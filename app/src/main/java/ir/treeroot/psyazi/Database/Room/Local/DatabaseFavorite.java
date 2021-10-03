package ir.treeroot.psyazi.Database.Room.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;

@Database(entities = {Favorite.class},version = 4,exportSchema = false)
public abstract class DatabaseFavorite extends RoomDatabase{

    public abstract FavoriteDao favoriteDao();
    public static DatabaseFavorite instance;

    public static DatabaseFavorite getInstance(Context c){

        if (instance==null){

            instance = Room.databaseBuilder(c, DatabaseFavorite.class ,"Favorite")
                    .allowMainThreadQueries()
                    .build();

        }

        return instance;

    }

}
