package br.safewalk.service;

import br.safewalk.domain.Rota;
import br.safewalk.domain.Trecho;
import br.safewalk.domain.Usuario;
import br.safewalk.infra.RotaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class RotaService {
    private final RotaRepository repo;

    public RotaService(RotaRepository repo) {
        this.repo = repo;
    }

    public Rota criar(Usuario dono, String nome, LocalTime horario) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("nome obrigatorio");
        Rota r = new Rota(UUID.randomUUID().toString().substring(0, 8), dono.getLogin(), nome, horario);
        repo.salvar(r);
        return r;
    }

    public void adicionarTrecho(String rotaId, Trecho t) {
        Rota r = repo.buscar(rotaId).orElseThrow(() -> new IllegalArgumentException("rota nao encontrada"));
        r.adicionarTrecho(t);
        repo.salvar(r);
    }

    public List<Rota> listarDoUsuario(String login) {
        return repo.listar().stream().filter(r -> r.getDono().equals(login)).toList();
    }

    public List<Rota> listarTodas() {
        return repo.listar();
    }

    public boolean excluir(String id, Usuario solicitante) {
        Rota r = repo.buscar(id).orElse(null);
        if (r == null) return false;
        if (!r.getDono().equals(solicitante.getLogin()) && !solicitante.podeModerar()) {
            throw new SecurityException("sem permissao");
        }
        return repo.remover(id);
    }

    public List<Rota> buscarPorNome(String termo) {
        String t = termo.toLowerCase();
        return repo.listar().stream().filter(r -> r.getNome().toLowerCase().contains(t)).toList();
    }

    public Rota buscar(String id) {
        return repo.buscar(id).orElse(null);
    }
}
