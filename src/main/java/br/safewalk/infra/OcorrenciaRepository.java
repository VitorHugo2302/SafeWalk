package br.safewalk.infra;

import br.safewalk.domain.Ocorrencia;

import java.util.List;
import java.util.Optional;

public interface OcorrenciaRepository {
    void salvar(Ocorrencia o);
    Optional<Ocorrencia> buscar(String id);
    List<Ocorrencia> listar();
    boolean remover(String id);
}
