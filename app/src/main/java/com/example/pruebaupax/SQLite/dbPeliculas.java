package com.example.pruebaupax.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.pruebaupax.Modelos.Pelicula;
import com.example.pruebaupax.Modelos.PeliculaRespuesta;

import java.util.ArrayList;

public class dbPeliculas extends DaoSQLite{
    Context contexto;
    SQLiteDatabase db;
    public dbPeliculas(Context contexto) {
        super(contexto);
        this.contexto= contexto;
    }

    //Metodo para insertar un registro en la base de datos local
    public long insertarPelicula(Pelicula p){
        long id = 0;
        DaoSQLite daoSQLite = new DaoSQLite(contexto);
        SQLiteDatabase db = daoSQLite.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Entradas.id, p.getId());
        values.put(Entradas.originalLanguage,p.getOriginalLanguage());
        values.put(Entradas.originalTitle,p.getOriginalTitle());
        values.put(Entradas.overview,p.getOverview());
        values.put(Entradas.popularity,p.getPopularity());
        values.put(Entradas.posterPath,p.getPosterPath());
        values.put(Entradas.title,p.getTitle());
        values.put(Entradas.video,p.getVideo());
        values.put(Entradas.voteAverage,p.getVoteAverage());
        values.put(Entradas.voteCount,p.getVoteCount());
        values.put(Entradas.adult,p.getAdult());
        values.put(Entradas.backdropPath,p.getBackdropPath());
        values.put(Entradas.releaseDate,p.getReleaseDate());
        try {
            String query= "SELECT * FROM "+Entradas.NombreTabla+" WHERE "+Entradas.id+"="+p.getId();
            Cursor cursor= db.rawQuery(query,null);
            if (cursor==null||!cursor.moveToFirst()){
                id = db.insert(Entradas.NombreTabla,null,values);
            }else {
                db.update(Entradas.NombreTabla,values,Entradas.id+"="+p.getId(),null);
            }
            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
        return id;
    }



    //Metodo para buscar todos los registros en la base de datos local
    public PeliculaRespuesta queryPeliculas(){
        PeliculaRespuesta respuesta= new PeliculaRespuesta();
        ArrayList<Pelicula> peliculaArrayList = new ArrayList<>();
        try {
            DaoSQLite daoSQLite = new DaoSQLite(contexto);
            db = daoSQLite.getWritableDatabase();
            String query= "SELECT * FROM "+Entradas.NombreTabla;
            Cursor cursor= db.rawQuery(query,null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    do {
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                            Pelicula p= new Pelicula();
                            p.setId(Integer.parseInt(cursor.getString(1)));
                            p.setOriginalLanguage(cursor.getString(2));
                            p.setOriginalTitle(cursor.getString(3));
                            p.setOverview(cursor.getString(4));
                            p.setPopularity(Double.parseDouble(cursor.getString(5)));
                            p.setPosterPath(cursor.getString(6));
                            p.setReleaseDate(cursor.getString(7));
                            p.setTitle(cursor.getString(8));
                            p.setVideo(Boolean.parseBoolean(cursor.getString(9)));
                            p.setVoteAverage(Double.parseDouble(cursor.getString(10)));
                            p.setVoteCount(Integer.parseInt(cursor.getString(11)));
                            p.setAdult(Boolean.parseBoolean(cursor.getString(12)));
                            p.setBackdropPath(cursor.getString(13));


                            peliculaArrayList.add(p);
                            //Log.i("Elemento",lista.get(i).toString());
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                respuesta.setResults(peliculaArrayList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return  respuesta;
    }


}
