package com.example.pruebaupax.Fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pruebaupax.Elementos.popUpGenerico;
import com.example.pruebaupax.Modelos.Ubicacion;
import com.example.pruebaupax.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoMapa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoMapa extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FragmentoMapaTag";

    popUpGenerico pop ;
    ArrayList<Ubicacion> ubicacionArrayList;

    private GoogleMap Map;

    public FragmentoMapa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentoMapa.
     *
     */
    // TODO: Rename and change types and number of parameters



    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Map = googleMap;
            //LatLng sydney = new LatLng(-34, 151);
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
    };



    public static FragmentoMapa newInstance(String param1, String param2) {
        FragmentoMapa fragment = new FragmentoMapa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ubicacionArrayList = obtenerPuntos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmento_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pop= new popUpGenerico(getContext());
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_google);
            assert mapFragment != null;
            mapFragment.getMapAsync(callback);
        }catch (Exception e){
            pop.popUpListener(getView(),e.getMessage(),false,null);
        }
    }


    private ArrayList<Ubicacion> obtenerPuntos(){
        ArrayList<Ubicacion> arrayList= new ArrayList<>();
        db.collection("ubicaciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ubicacion ubicacion = new Ubicacion();
                                ubicacion.setFecha((String) document.get("fecha"));
                                ubicacion.setLatitud((String) document.get("latitud"));
                                ubicacion.setLongitud((String) document.get("longitud"));

                                arrayList.add(ubicacion);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            try {
                                AgregarPuntos(arrayList);
                            }catch (Exception e){
                                pop.popUpListener(getView(),e.getMessage(),false,null);
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return arrayList;
    }

    private void AgregarPuntos(ArrayList<Ubicacion> ubicacionArrayList){
        ArrayList<MarkerOptions> markers = new ArrayList<>();
        if (ubicacionArrayList.size()>0){
            for (Ubicacion u:ubicacionArrayList){
                LatLng coordenadas = new LatLng(Double.parseDouble(u.getLatitud()),Double.parseDouble(u.getLongitud()));
                markers.add(new MarkerOptions().position(coordenadas).title(u.getFecha()));
                //Map.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
            }
            for (MarkerOptions markerOptions : markers){
                if (markerOptions!=null){
                    Map.addMarker(markerOptions);
                }
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLng position;
            for(int i = 0; i < markers.size(); i++){
                position = markers.get(i).getPosition();
                builder.include(new LatLng(position.latitude, position.longitude));
            }
            LatLngBounds bounds = builder.build();
            Map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
        }
    }
}