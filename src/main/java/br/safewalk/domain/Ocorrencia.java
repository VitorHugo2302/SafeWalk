package br.safewalk.domain;

import java.time.LocalDate;

public class Ocorrencia {
    private String id;
    private String trechoId;
    private TipoOcorrencia tipo;
    private LocalDate data;
    private String descricao;
    private String reportadoPor;
    private double lat;
    private double lng;

    public Ocorrencia(String id, String trechoId, TipoOcorrencia tipo, LocalDate data,
                      String descricao, String reportadoPor, double lat, double lng) {
        this.id = id;
        this.trechoId = trechoId == null ? "" : trechoId;
        this.tipo = tipo;
        this.data = data;
        this.descricao = descricao == null ? "" : descricao;
        this.reportadoPor = reportadoPor;
        this.lat = lat;
        this.lng = lng;
    }

    public Ocorrencia(String id, String trechoId, TipoOcorrencia tipo, LocalDate data,
                      String descricao, String reportadoPor) {
        this(id, trechoId, tipo, data, descricao, reportadoPor, 0, 0);
    }

    public String getId() { return id; }
    public String getTrechoId() { return trechoId; }
    public TipoOcorrencia getTipo() { return tipo; }
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }
    public String getReportadoPor() { return reportadoPor; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}
