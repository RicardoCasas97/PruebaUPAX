package com.example.pruebaupax.Modelos;

public class Ubicacion {
    private String Latitud;
    private String Longitud;
    private String Fecha;

    public String getLatitud() {
        return Latitud;
    }

    public void setLatitud(String latitud) {
        Latitud = latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public void setLongitud(String longitud) {
        Longitud = longitud;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public Ubicacion(String latitud, String longitud, String fecha) {
        Latitud = latitud;
        Longitud = longitud;
        Fecha = fecha;
    }

    public Ubicacion() {
    }
}
