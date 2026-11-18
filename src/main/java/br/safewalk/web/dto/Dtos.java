package br.safewalk.web.dto;

import java.util.List;

public final class Dtos {

    private Dtos() {}

    public record LoginRequest(String login, String senha) {}

    public record CadastroRequest(String login, String senha, String nome) {}

    public record TrechoRequest(String nomeVia, String bairro, double distanciaKm, double lat, double lng) {}

    public record NovaRotaRequest(String nome, String horario, List<TrechoRequest> trechos) {}

    public record NovaOcorrenciaRequest(String trechoId, String tipo, String data, String descricao) {}

    public record Mensagem(String mensagem) {}
}
