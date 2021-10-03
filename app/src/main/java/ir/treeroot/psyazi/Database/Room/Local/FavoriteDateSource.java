package ir.treeroot.psyazi.Database.Room.Local;

import java.util.List;

import io.reactivex.Flowable;
import ir.treeroot.psyazi.Database.Room.DataSource.IFavoriteDateSource;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;


public class FavoriteDateSource implements IFavoriteDateSource {

    private final FavoriteDao dao;
    public static FavoriteDateSource iFavoriteDateSource;

    public FavoriteDateSource(FavoriteDao dao) {
        this.dao = dao;
    }

    public static FavoriteDateSource getInstance(FavoriteDao favorite) {

        if (iFavoriteDateSource == null) {

            iFavoriteDateSource = new FavoriteDateSource(favorite);

        }

        return iFavoriteDateSource;

    }

    @Override
    public Flowable<List<Favorite>> getALlFavoriteItem() {
        return dao.getALlFavoriteItem();
    }

    @Override
    public void InsertItem(Favorite... favorites) {
        dao.InsertItem(favorites);
    }

    @Override
    public void DeleteItem(Favorite favorites) {
        dao.DeleteItem(favorites);
    }

    @Override
    public int DeleteOrderBy(int favorites) {
        return dao.DeleteOrderBy(favorites);
    }

    @Override
    public int itemFavId(int itemFavId) {
        return dao.itemFavId(itemFavId);
    }

}
