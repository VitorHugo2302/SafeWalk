package br.safewalk.domain;

public enum TipoOcorrencia {
    ASSALTO(10.0),
    FURTO(6.0),
    AGRESSAO(8.0),
    ILUMINACAO_RUIM(2.5),
    ACIDENTE_TRANSITO(4.0),
    VIA_DETERIORADA(1.5);

    private final double pesoBase;

    TipoOcorrencia(double pesoBase) {
        this.pesoBase = pesoBase;
    }

    public double getPesoBase() {
        return pesoBase;
    }
}
