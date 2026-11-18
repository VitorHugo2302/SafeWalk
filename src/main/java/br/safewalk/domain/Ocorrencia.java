package br.safewalk.domain;

import java.time.LocalDate;

public class Ocorrencia {
    private String id;
    private String trechoId;
    private TipoOcorrencia tipo;
    private LocalDate data;
    private String descricao;
    private String reportadoPor;

    public Ocorrencia(String id, String trechoId, TipoOcorrencia tipo, LocalDate data, String descricao, String reportadoPor) {
        this.id = id;
        this.trechoId = trechoId;
        this.tipo = tipo;
        this.data = data;
        this.descricao = descricao == null ? "" : descricao;
        this.reportadoPor = reportadoPor;
    }

    public String getId() { return id; }
    public String getTrechoId() { return trechoId; }
    public TipoOcorrencia getTipo() { return tipo; }
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }
    public String getReportadoPor() { return reportadoPor; }
}
