package ir.treeroot.psyazi.Database.Room.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;


@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM Favorite")
    Flowable<List<Favorite>> getALlFavoriteItem();

    @Query("DELETE FROM Favorite WHERE postid = :postid")
    int DeleteOrderBy(int postid);

    @Insert
    void InsertItem(Favorite...favorites);

    @Delete
    void DeleteItem(Favorite favorites);

    @Query("SELECT EXISTS(SELECT 1 FROM Favorite WHERE postid =:itemFavId)")
    int itemFavId(int itemFavId);

}
