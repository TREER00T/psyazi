package ir.treeroot.psyazi.Database.Room.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;


public interface IFavoriteDateSource {

    Flowable<List<Favorite>> getALlFavoriteItem();

    void InsertItem(Favorite...favorites);

    void DeleteItem(Favorite favorites);

    int DeleteOrderBy(int favorites);

    int itemFavId(int itemFavId);

}
