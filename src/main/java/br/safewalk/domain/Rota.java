package br.safewalk.domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rota {
    private String id;
    private String dono;
    private String nome;
    private List<Trecho> trechos;
    private LocalTime horarioPrevisto;

    public Rota(String id, String dono, String nome, LocalTime horarioPrevisto) {
        this.id = id;
        this.dono = dono;
        this.nome = nome;
        this.horarioPrevisto = horarioPrevisto;
        this.trechos = new ArrayList<>();
    }

    public void adicionarTrecho(Trecho t) {
        trechos.add(t);
    }

    public double distanciaTotal() {
        double total = 0;
        for (Trecho t : trechos) total += t.getDistanciaKm();
        return total;
    }

    public boolean ehNoturna() {
        int h = horarioPrevisto.getHour();
        return h >= 19 || h < 6;
    }

    public String getId() { return id; }
    public String getDono() { return dono; }
    public String getNome() { return nome; }
    public LocalTime getHorarioPrevisto() { return horarioPrevisto; }

    public List<Trecho> getTrechos() {
        return Collections.unmodifiableList(trechos);
    }

    public void setNome(String nome) { this.nome = nome; }
    public void setHorarioPrevisto(LocalTime h) { this.horarioPrevisto = h; }
}
