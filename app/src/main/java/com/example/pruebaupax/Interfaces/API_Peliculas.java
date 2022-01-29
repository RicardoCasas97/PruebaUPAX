package com.example.pruebaupax.Interfaces;

import com.example.pruebaupax.Modelos.PeliculaRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API_Peliculas {

    @GET("3/movie/upcoming")
    Call<PeliculaRespuesta> getAllMovies(@Query("api_key") String api_key,
                                         @Query("language") String language,
                                         @Query("page") int page);
    
}
