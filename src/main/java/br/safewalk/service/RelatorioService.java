package br.safewalk.service;

import br.safewalk.domain.Ocorrencia;
import br.safewalk.domain.Periodo;
import br.safewalk.infra.OcorrenciaRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {
    private final OcorrenciaRepository repo;

    public RelatorioService(OcorrenciaRepository repo) {
        this.repo = repo;
    }

    public Map<String, Integer> contagemPorTipo(Periodo p) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        List<Ocorrencia> lista = repo.listar();
        for (Ocorrencia o : lista) {
            if (p != null && !p.contem(o.getData())) continue;
            mapa.merge(o.getTipo().name(), 1, Integer::sum);
        }
        return mapa;
    }

    public Path exportarCsv(Periodo p, Path destino) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("id;trecho;tipo;data;autor;descricao\n");
        for (Ocorrencia o : repo.listar()) {
            if (p != null && !p.contem(o.getData())) continue;
            sb.append(o.getId()).append(';')
              .append(o.getTrechoId()).append(';')
              .append(o.getTipo().name()).append(';')
              .append(o.getData()).append(';')
              .append(o.getReportadoPor()).append(';')
              .append(o.getDescricao().replace(';', ',').replace('\n', ' '))
              .append('\n');
        }
        if (destino.getParent() != null) Files.createDirectories(destino.getParent());
        Files.writeString(destino, sb.toString());
        return destino;
    }
}
