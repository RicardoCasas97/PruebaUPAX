package com.example.pruebaupax.Recyclers;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pruebaupax.Modelos.Pelicula;
import com.example.pruebaupax.R;

import java.util.ArrayList;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaAdapter.ViewHolder> {

    private ArrayList<Pelicula> peliculaArrayList;
    private Context context;

    public PeliculaAdapter(Context context) {
        peliculaArrayList = new ArrayList<>();
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pelicula_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Pelicula p = peliculaArrayList.get(position);
        holder.originalTitle.setText(p.getOriginalTitle());
        if (p.getAdult()){
            holder.overview.setText(context.getString(R.string.si));
        }else {
            holder.overview.setText(context.getString(R.string.no));
        }
        holder.release_date.setText(p.getReleaseDate());
        holder.vote_average.setText(p.getVoteAverage().toString());
        Glide.with(context).load("https://image.tmdb.org/t/p/w500/"+p.getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imagen);

    }

    public void AgregarLista(ArrayList<Pelicula> peliculaArrayList){
        this.peliculaArrayList.addAll(peliculaArrayList);
        notifyDataSetChanged();
    }

    public void LimpiarAdaptador(){
        peliculaArrayList.clear();
        notifyDataSetChanged();
    }


    public void AgregarListaOffline(ArrayList<Pelicula> peliculaArrayList){
        this.peliculaArrayList.clear();
        this.peliculaArrayList.addAll(peliculaArrayList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return peliculaArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagen;
        TextView originalTitle,overview,release_date,vote_average;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen= itemView.findViewById(R.id.Poster);
            originalTitle= itemView.findViewById(R.id.originalTitle);
            overview= itemView.findViewById(R.id.adults);
            release_date= itemView.findViewById(R.id.release_date);
            vote_average= itemView.findViewById(R.id.vote_average);
        }
    }
}
