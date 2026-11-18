package br.safewalk.domain;

import java.time.LocalDate;

public class Periodo {
    private final LocalDate inicio;
    private final LocalDate fim;

    public Periodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) throw new IllegalArgumentException("datas obrigatorias");
        if (fim.isBefore(inicio)) throw new IllegalArgumentException("fim antes do inicio");
        this.inicio = inicio;
        this.fim = fim;
    }

    public boolean contem(LocalDate d) {
        return !d.isBefore(inicio) && !d.isAfter(fim);
    }

    public LocalDate getInicio() { return inicio; }
    public LocalDate getFim() { return fim; }
}
