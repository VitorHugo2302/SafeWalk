package br.safewalk.infra;

import br.safewalk.domain.Rota;
import br.safewalk.domain.Trecho;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RotaRepositoryArquivo implements RotaRepository {
    private final Path arquivo;
    private final Map<String, Rota> cache = new LinkedHashMap<>();

    public RotaRepositoryArquivo(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    private void carregar() {
        if (!Files.exists(arquivo)) return;
        try {
            Rota atual = null;
            for (String linha : Files.readAllLines(arquivo)) {
                if (linha.isBlank()) continue;
                if (linha.startsWith("R;")) {
                    String[] p = linha.split(";", -1);
                    atual = new Rota(p[1], p[2], p[3], LocalTime.parse(p[4]));
                    cache.put(atual.getId(), atual);
                } else if (linha.startsWith("T;") && atual != null) {
                    String[] p = linha.split(";", -1);
                    double lat = p.length > 5 ? Double.parseDouble(p[5]) : 0;
                    double lng = p.length > 6 ? Double.parseDouble(p[6]) : 0;
                    atual.adicionarTrecho(new Trecho(p[1], p[2], p[3], Double.parseDouble(p[4]), lat, lng));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void persistir() {
        try {
            StringBuilder sb = new StringBuilder();
            for (Rota r : cache.values()) {
                sb.append("R;").append(r.getId()).append(';')
                  .append(r.getDono()).append(';')
                  .append(r.getNome()).append(';')
                  .append(r.getHorarioPrevisto()).append('\n');
                for (Trecho t : r.getTrechos()) {
                    sb.append("T;").append(t.getId()).append(';')
                      .append(t.getNomeVia()).append(';')
                      .append(t.getBairro()).append(';')
                      .append(t.getDistanciaKm()).append(';')
                      .append(t.getLat()).append(';')
                      .append(t.getLng()).append('\n');
                }
            }
            if (arquivo.getParent() != null) Files.createDirectories(arquivo.getParent());
            Files.writeString(arquivo, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void salvar(Rota r) {
        cache.put(r.getId(), r);
        persistir();
    }

    @Override
    public Optional<Rota> buscar(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Rota> listar() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public boolean remover(String id) {
        boolean r = cache.remove(id) != null;
        if (r) persistir();
        return r;
    }
}
