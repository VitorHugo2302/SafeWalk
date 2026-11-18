package br.safewalk.domain;

import java.util.Objects;

public class Trecho {
    private String id;
    private String nomeVia;
    private String bairro;
    private double distanciaKm;
    private double lat;
    private double lng;

    public Trecho(String id, String nomeVia, String bairro, double distanciaKm, double lat, double lng) {
        if (distanciaKm <= 0) throw new IllegalArgumentException("distancia precisa ser positiva");
        this.id = id;
        this.nomeVia = nomeVia;
        this.bairro = bairro;
        this.distanciaKm = distanciaKm;
        this.lat = lat;
        this.lng = lng;
    }

    public Trecho(String id, String nomeVia, String bairro, double distanciaKm) {
        this(id, nomeVia, bairro, distanciaKm, 0, 0);
    }

    public String getId() { return id; }
    public String getNomeVia() { return nomeVia; }
    public String getBairro() { return bairro; }
    public double getDistanciaKm() { return distanciaKm; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trecho)) return false;
        return Objects.equals(id, ((Trecho) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
