package br.safewalk.service;

import br.safewalk.domain.Ocorrencia;
import br.safewalk.domain.Periodo;
import br.safewalk.domain.TipoOcorrencia;
import br.safewalk.domain.Usuario;
import br.safewalk.infra.OcorrenciaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OcorrenciaService {
    private final OcorrenciaRepository repo;

    public OcorrenciaService(OcorrenciaRepository repo) {
        this.repo = repo;
    }

    public Ocorrencia registrar(Usuario autor, String trechoId, TipoOcorrencia tipo, LocalDate data, String descricao) {
        if (trechoId == null || trechoId.isBlank()) throw new IllegalArgumentException("trecho obrigatorio");
        if (data == null) throw new IllegalArgumentException("data obrigatoria");
        if (data.isAfter(LocalDate.now())) throw new IllegalArgumentException("data no futuro");
        String id = UUID.randomUUID().toString().substring(0, 8);
        Ocorrencia o = new Ocorrencia(id, trechoId, tipo, data, descricao, autor.getLogin());
        repo.salvar(o);
        return o;
    }

    public List<Ocorrencia> listar() {
        return repo.listar();
    }

    public List<Ocorrencia> filtrar(TipoOcorrencia tipo, Periodo periodo) {
        return repo.listar().stream()
                .filter(o -> tipo == null || o.getTipo() == tipo)
                .filter(o -> periodo == null || periodo.contem(o.getData()))
                .toList();
    }

    public boolean excluir(String id, Usuario u) {
        Ocorrencia o = repo.buscar(id).orElse(null);
        if (o == null) return false;
        if (!o.getReportadoPor().equals(u.getLogin()) && !u.podeModerar()) {
            throw new SecurityException("sem permissao");
        }
        return repo.remover(id);
    }
}
