package br.safewalk.infra;

import br.safewalk.domain.Rota;

import java.util.List;
import java.util.Optional;

public interface RotaRepository {
    void salvar(Rota r);
    Optional<Rota> buscar(String id);
    List<Rota> listar();
    boolean remover(String id);
}
