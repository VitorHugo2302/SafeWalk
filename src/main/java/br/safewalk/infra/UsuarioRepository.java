package br.safewalk.infra;

import br.safewalk.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    void salvar(Usuario u);
    Optional<Usuario> buscar(String login);
    List<Usuario> listar();
    boolean remover(String login);
}
