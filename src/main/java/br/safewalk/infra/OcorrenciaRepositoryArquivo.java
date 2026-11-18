package br.safewalk.infra;

import br.safewalk.domain.Ocorrencia;
import br.safewalk.domain.TipoOcorrencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OcorrenciaRepositoryArquivo implements OcorrenciaRepository {
    private final Path arquivo;
    private final Map<String, Ocorrencia> cache = new LinkedHashMap<>();

    public OcorrenciaRepositoryArquivo(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    private void carregar() {
        if (!Files.exists(arquivo)) return;
        try {
            for (String linha : Files.readAllLines(arquivo)) {
                if (linha.isBlank()) continue;
                String[] p = linha.split(";", -1);
                Ocorrencia o = new Ocorrencia(
                        p[0], p[1],
                        TipoOcorrencia.valueOf(p[2]),
                        LocalDate.parse(p[3]),
                        p[4], p[5]);
                cache.put(o.getId(), o);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void persistir() {
        try {
            StringBuilder sb = new StringBuilder();
            for (Ocorrencia o : cache.values()) {
                sb.append(o.getId()).append(';')
                  .append(o.getTrechoId()).append(';')
                  .append(o.getTipo().name()).append(';')
                  .append(o.getData()).append(';')
                  .append(o.getDescricao().replace(';', ',').replace('\n', ' ')).append(';')
                  .append(o.getReportadoPor()).append('\n');
            }
            if (arquivo.getParent() != null) Files.createDirectories(arquivo.getParent());
            Files.writeString(arquivo, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void salvar(Ocorrencia o) {
        cache.put(o.getId(), o);
        persistir();
    }

    @Override
    public Optional<Ocorrencia> buscar(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Ocorrencia> listar() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public boolean remover(String id) {
        boolean r = cache.remove(id) != null;
        if (r) persistir();
        return r;
    }
}
