package com.example.pruebaupax.Servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.pruebaupax.Elementos.creaNotificacion;
import com.example.pruebaupax.Modelos.Ubicacion;
import com.example.pruebaupax.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServicioUbicacion extends Service {

    private DatabaseReference mDatabase;
    LocationManager manager;
    creaNotificacion notificacion;
    FirebaseFirestore db;
    String TAG = "FirebaeLOG";


    public ServicioUbicacion() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = FirebaseFirestore.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return flags;
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, locationListener);
        return START_NOT_STICKY;

    }


    public void IngresarUbicacion(String latitud, String longitud, String fecha) {
        //Ubicacion ubicacion = new Ubicacion(latitud, longitud, fecha);
        //mDatabase.child("Ubicaciones").child(fecha).setValue(ubicacion);

        try {
            AgregarDataFireStore(latitud,longitud,fecha);
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    public void AgregarDataFireStore(String latitud,String longitud,String fecha){
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("latitud", latitud);
        ubicacion.put("longitud", longitud);
        ubicacion.put("fecha", fecha);



        db.collection("ubicaciones")
                .add(ubicacion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        notificacion = new creaNotificacion(getApplicationContext(),"Ubicación notificada a Firebase",getApplicationContext().getString(R.string.ubicacionNotLargo),getApplicationContext().getString(R.string.ubicacionNotLargo));
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
                double latitud = location.getLatitude (); // longitud
                double longitud = location.getLongitude (); // latitud
                double altitud = location.getAltitude (); // altitud
            Date currentTime = Calendar.getInstance().getTime();
            IngresarUbicacion(String.valueOf(latitud),String.valueOf(longitud),currentTime.toString());

        }
    };



}