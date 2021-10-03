package ir.treeroot.psyazi.Database.Room.ModelDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favorite")
public class Favorite {

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "postid")
    public String postid = null;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "img")
    public String img;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "aliasname")
    public String aliasname;

    @ColumnInfo(name = "format")
    public String format;

}
