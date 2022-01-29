package com.example.pruebaupax.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebaupax.Elementos.popUpGenerico;
import com.example.pruebaupax.SQLite.DaoSQLite;
import com.example.pruebaupax.SQLite.dbPeliculas;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityHelper {

    final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;


    String TAG = "Permisos";

    AppCompatActivity activity;
    Context context;
    DaoSQLite sqlHelper;
    SQLiteDatabase database;
    public dbPeliculas PeliculasDB;
    public popUpGenerico pop;

    public void InicializarActividad(AppCompatActivity activity, Context context) {
        try {
            this.activity = activity;
            this.context = context;
            pop = new popUpGenerico(context);
            PeliculasDB = new dbPeliculas(context);
            sqlHelper = new DaoSQLite(context);
            database = sqlHelper.getWritableDatabase();
            database.close();
            //pedirPermisos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Metodo para pedir permisos
    public boolean pedirPermisos() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int writeExternalPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessNetworkState = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int cameraPermission = ContextCompat.checkSelfPermission(activity,Manifest.permission.CAMERA);


        List<String> listaPermisos = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.SEND_SMS);
        }
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (accessNetworkState != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.CAMERA);
        }
        if (!listaPermisos.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listaPermisos.toArray(new String[listaPermisos.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    //Metodo a llamar cuando la peticion de permisos termino
    public boolean onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {


                // Inicializar Map para agregar permisos
                Map<String, Integer> perms = new HashMap<>();


                // Agregar permisos al mapa
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);


                //Revisar estado de los permisos
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Permisos aceptados");


                    } else {
                        Log.d(TAG, "Faltan permisos");

                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            pop.dialogoDefault("Hay permisos pendientes de aceptar", "¿Desea aceptarlos?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            pedirPermisos();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:

                                            break;
                                    }
                                }
                            }, null);
                        }

                        else {
                            Toast.makeText(context, "Ve a configuraciones y habilita los permisos requeridos", Toast.LENGTH_LONG)
                                    .show();
                            //
                        }
                    }
                }
                break;
            }
            default:
                Log.e(TAG,"Request code desconocido");
                break;
        }
        return false;
    }


    //Metodo para revisar si la red esta disponible
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_CELLULAR");
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_WIFI");
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_ETHERNET");
                    return true;
                }
            }
        }

        return false;

    }



    public Uri BitmapAUri(Context context, Bitmap inImage,String nombre) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, nombre, null);
        return Uri.parse(path);
    }

}
