package br.safewalk.infra;

import br.safewalk.domain.PerfilUsuario;
import br.safewalk.domain.Usuario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepositoryArquivo implements UsuarioRepository {
    private final Path arquivo;
    private final Map<String, Usuario> cache = new LinkedHashMap<>();

    public UsuarioRepositoryArquivo(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    private void carregar() {
        if (!Files.exists(arquivo)) return;
        try {
            for (String linha : Files.readAllLines(arquivo)) {
                if (linha.isBlank()) continue;
                String[] p = linha.split(";", -1);
                Usuario u = new Usuario(p[0], p[1], p[2], PerfilUsuario.valueOf(p[3]), LocalDate.parse(p[4]));
                cache.put(u.getLogin(), u);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void persistir() {
        try {
            StringBuilder sb = new StringBuilder();
            for (Usuario u : cache.values()) {
                sb.append(u.getLogin()).append(';')
                  .append(u.getSenhaHash()).append(';')
                  .append(u.getNome()).append(';')
                  .append(u.getPerfil().name()).append(';')
                  .append(u.getCriadoEm()).append('\n');
            }
            if (arquivo.getParent() != null) Files.createDirectories(arquivo.getParent());
            Files.writeString(arquivo, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void salvar(Usuario u) {
        cache.put(u.getLogin(), u);
        persistir();
    }

    @Override
    public Optional<Usuario> buscar(String login) {
        return Optional.ofNullable(cache.get(login));
    }

    @Override
    public List<Usuario> listar() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public boolean remover(String login) {
        boolean r = cache.remove(login) != null;
        if (r) persistir();
        return r;
    }
}
