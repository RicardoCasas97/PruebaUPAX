package com.example.pruebaupax.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DaoSQLite extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION= 1;
    public static final String DATABASE_NAME= "AXCPTSqlite";



    //Nombres base de datos local
    public static class Entradas implements BaseColumns {
        public static final String NombreTabla="t_Peliculas";
        public static final String idInterno ="idInterno";
        public static final String id ="id";
        public static final String originalLanguage ="originalLanguage";
        public static  final String originalTitle ="originalTitle";
        public static final String overview ="overview";
        public  static  final String popularity ="popularity";
        public static final String posterPath ="posterPath";
        public static final String releaseDate ="releaseDate";
        public static final String title="title";
        public static final String video="video";
        public static final String voteAverage="voteAverage";
        public static final String voteCount="voteCount";
        public static final String adult="adult";
        public static final String backdropPath="backdropPath";
    }

    public DaoSQLite(Context contexto){
        super(contexto,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override



    //Creación base de datos
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists "+ Entradas.NombreTabla+
                "(" + Entradas.idInterno + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Entradas.id +" TEXT,"
                    + Entradas.originalLanguage +" TEXT,"
                    + Entradas.originalTitle +" TEXT,"
                    + Entradas.overview +" TEXT,"
                    + Entradas.popularity +" TEXT, "
                    + Entradas.posterPath +" TEXT, "
                    + Entradas.title +" TEXT, "
                    + Entradas.video +" TEXT, "
                    + Entradas.voteAverage +" TEXT, "
                    + Entradas.voteCount +" TEXT, "
                    + Entradas.adult +" TEXT, "
                    + Entradas.backdropPath +" TEXT, "
                    + Entradas.releaseDate +" TEXT"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE "+ Entradas.NombreTabla);
            onCreate(db);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
