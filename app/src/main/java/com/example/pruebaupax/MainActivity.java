package com.example.pruebaupax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pruebaupax.Fragmentos.FragmentoMapa;
import com.example.pruebaupax.Helpers.activityHelper;
import com.example.pruebaupax.Interfaces.API_Peliculas;
import com.example.pruebaupax.Modelos.Pelicula;
import com.example.pruebaupax.Modelos.PeliculaRespuesta;
import com.example.pruebaupax.Recyclers.PeliculaAdapter;
import com.example.pruebaupax.Servicios.ServicioUbicacion;
import com.example.pruebaupax.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    PeliculaAdapter peliculaAdapter;
    Retrofit retrofit;
    StorageReference storageRef;
    activityHelper helper;
    boolean aptoParaCargar;
    private static String URL = "https://api.themoviedb.org/";
    private int Pagina = 1;
    private final String APIKEY = "8832d958565992c10efa490119fa973b";
    private static String Lenguaje = "es-MX";
    FragmentoMapa mapa;

    Intent servicioUbicacion;


    int Tomar_Multiples_Imagenes = 20;
    int CAPTURAR_FOTO = 18;
    String imageEncoded;
    List<String> imagesEncodedList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("Ubicaciones");
            InicializarVariables();

        }catch (Exception e){
            helper.pop.popUpListener(getCurrentFocus(),e.getMessage(),false,null);
        }

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            aptoParaCargar=true;
            if (helper.isNetworkAvailable()){
                find();
            }else {
                peliculaAdapter.AgregarListaOffline(helper.PeliculasDB.queryPeliculas().getResults());
            }
        }catch (Exception e){
            helper.pop.popUpListener(getCurrentFocus(),e.getMessage(),false,null);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                    if (helper.isNetworkAvailable()){
                        ConectarServicio();
                        peliculaAdapter.LimpiarAdaptador();
                        Pagina=1;
                        find();
                        Log.i("BroadcastInternetTag", "Conectado");

                    }else {
                        if (servicioActivo(ServicioUbicacion.class)){
                            stopService(servicioUbicacion);
                        }
                        Log.i("BroadcastInternetTag", "Desconectado");
                    }
                }
            }
        }, intentFilter);
        ConectarServicio();
    }


    //Metodo para buscar peliculas en la API
    private void find(){

        API_Peliculas apiPeliculas = retrofit.create(API_Peliculas.class);

        Call<PeliculaRespuesta> call = apiPeliculas.getAllMovies(APIKEY,Lenguaje,Pagina);
        call.enqueue(new Callback<PeliculaRespuesta>() {
            @Override
            public void onResponse(Call<PeliculaRespuesta> call, Response<PeliculaRespuesta> response) {
                try {
                    if (response.isSuccessful()){
                        aptoParaCargar=true;
                       // Toast.makeText(MainActivity.this, (String.valueOf(response.code())), Toast.LENGTH_SHORT).show();
                        PeliculaRespuesta respuesta = response.body();
                        ArrayList<Pelicula> peliculas = respuesta.getResults();
                        peliculaAdapter.AgregarLista(peliculas);
                        for (Pelicula p: peliculas){
                            helper.PeliculasDB.insertarPelicula(p);
                            //Log.i("Pelicula",p.getTitle());
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    helper.pop.popUpListener(getCurrentFocus(),e.getMessage(),false,null);
                    //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("try",e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PeliculaRespuesta> call, Throwable t) {
                helper.pop.popUpListener(getCurrentFocus(),t.getMessage(),false,null);
                //Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure",t.getMessage());

            }
        });
    }


    private void InicializarVariables(){
        helper = new activityHelper();
        helper.InicializarActividad(MainActivity.this,MainActivity.this);
        helper.pedirPermisos();
        servicioUbicacion = new Intent(this, ServicioUbicacion.class);
        retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        storageRef = FirebaseStorage.getInstance().getReference();

        peliculaAdapter = new PeliculaAdapter(MainActivity.this);
        binding.recycler.setAdapter(peliculaAdapter);
        binding.recycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,3);
        binding.recycler.setLayoutManager(gridLayoutManager);

        mapa = FragmentoMapa.newInstance("","");
        binding.recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0){
                    int visibleItemCount= gridLayoutManager.getChildCount();
                    int totalIteamCount = gridLayoutManager.getItemCount();
                    int pastVisibleItems= gridLayoutManager.findFirstVisibleItemPosition();

                    if (aptoParaCargar){
                        if (helper.isNetworkAvailable()){
                            if ((visibleItemCount+pastVisibleItems)>=totalIteamCount){
                                aptoParaCargar=false;
                                Pagina+=1;
                                find();
                            }
                        }
                    }

                }
            }
        });

        binding.floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.isNetworkAvailable()){
                    if (getSupportFragmentManager().findFragmentByTag("FragmentoMapa")==null){
                        getSupportFragmentManager().beginTransaction().add(R.id.Pantalla_Principal,mapa,"FragmentoMapa").addToBackStack("").commit();
                    }else {
                        getSupportFragmentManager().popBackStack();
                    }
                }else {
                    helper.pop.popUpListener(getCurrentFocus(),"No puede acceder al mapa sin conección a internet",false,null);
                }

               // Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_LONG).show();
            }
        });


        binding.floatingImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Selecciona una imagen"), Tomar_Multiples_Imagenes);
            }
        });

        binding.TomarFotografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURAR_FOTO);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==Tomar_Multiples_Imagenes){
            SubirFotosDeGaleria(requestCode,resultCode,data);
        }

        if (requestCode == CAPTURAR_FOTO)
        {
            if(data!=null) {

                Bitmap fotoTomada = (Bitmap) data.getExtras().get("data");
                Uri uri = helper.BitmapAUri(MainActivity.this,fotoTomada,data.getDataString());
                StorageReference reference = storageRef.child("Imagenes").child(uri.getLastPathSegment());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        helper.pop.popUpListener(getCurrentFocus(), "Imagen subida satisfactoriamente", true, null);

                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            helper.onRequestPermissionResult(requestCode,permissions,grantResults);
        }catch (Exception e){
            helper.pop.popUpListener(getCurrentFocus(),e.getMessage(),false,null);
        }
    }


    //Metodo para saber si algun servicio esta activo
    private boolean servicioActivo(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Servicio","Activo");
                return true;
            }
        }
        Log.i("Servicio","inactivo");
        return false;
    }



    //Metodo para conectar servicio si es que no esta conectado
    private void ConectarServicio(){
        try {
            if (helper.isNetworkAvailable()){
                if (!servicioActivo(ServicioUbicacion.class)){

                    startService(servicioUbicacion);
                    bindService(servicioUbicacion, new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            //retrieve an instance of the service here from the IBinder returned
                            //from the onBind method to communicate with
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                        }
                    }, Context.BIND_AUTO_CREATE);
                }
            }
        }catch (Exception e){
            helper.pop.popUpListener(getCurrentFocus(),e.getMessage(),false,null);
        }
    }


    private void SubirFotosDeGaleria(int requestCode, int resultCode, Intent data){
        try {

            if (requestCode == Tomar_Multiples_Imagenes && resultCode == RESULT_OK
                    && null != data) {


                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri uri=data.getData();
                    StorageReference reference = storageRef.child("Imagenes").child(uri.getLastPathSegment());

                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            helper.pop.popUpListener(getCurrentFocus(),"Imagen subida satisfactoriamente",true,null);

                        }
                    });

                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
                        for (int i = 0; i < clipData.getItemCount(); i++) {

                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uriArrayList.add(uri);

                            StorageReference reference = storageRef.child("Imagenes").child(uri.getLastPathSegment());

                            if (i==clipData.getItemCount()-1){
                                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        helper.pop.popUpListener(getCurrentFocus(),"Imagenes subidas satisfactoriamente",true,null);

                                    }
                                });
                            }
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.i("LOG_TAG", "Imagenes seleccionadas" + uriArrayList.size());

                    }
                }
            } else {

                Toast.makeText(this, "No escogiste una imagen",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            helper.pop.popUpListener(getCurrentFocus(),e.getMessage(),false,null);

        }

    }
}