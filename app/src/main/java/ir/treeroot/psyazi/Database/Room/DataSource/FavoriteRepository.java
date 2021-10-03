package ir.treeroot.psyazi.Database.Room.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import ir.treeroot.psyazi.Database.Room.ModelDB.Favorite;

public class FavoriteRepository implements IFavoriteDateSource {

    private final IFavoriteDateSource dateSource;
    public static FavoriteRepository instance;

    public FavoriteRepository(IFavoriteDateSource iFavoriteDateSource) {

        this.dateSource = iFavoriteDateSource;

    }

    public static FavoriteRepository getInstance(IFavoriteDateSource iFavoriteDateSource) {

        if (instance == null) {

            instance = new FavoriteRepository(iFavoriteDateSource);

        }

        return instance;

    }

    @Override
    public Flowable<List<Favorite>> getALlFavoriteItem() {
        return dateSource.getALlFavoriteItem();
    }

    @Override
    public void InsertItem(Favorite... favorites) {
        dateSource.InsertItem(favorites);
    }

    @Override
    public void DeleteItem(Favorite favorites) {
        dateSource.DeleteItem(favorites);
    }

    @Override
    public int DeleteOrderBy(int favorites) {
        return dateSource.DeleteOrderBy(favorites);
    }

    @Override
    public int itemFavId(int itemFavId) {
        return dateSource.itemFavId(itemFavId);
    }
}
