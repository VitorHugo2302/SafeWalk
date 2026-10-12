package br.safewalk.service;

import br.safewalk.domain.PerfilUsuario;
import br.safewalk.domain.Usuario;
import br.safewalk.infra.UsuarioRepository;

import java.security.MessageDigest;
import java.util.Optional;

public class AutenticacaoService {
    private final UsuarioRepository repo;
    private Usuario sessao;

    public AutenticacaoService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario cadastrar(String login, String senha, String nome, PerfilUsuario perfil) {
        if (login == null || login.isBlank()) throw new IllegalArgumentException("login vazio");
        if (senha == null || senha.length() < 4) throw new IllegalArgumentException("senha curta demais");
        if (repo.buscar(login).isPresent()) throw new IllegalArgumentException("login ja existe");
        Usuario u = new Usuario(login, hash(senha), nome, perfil);
        repo.salvar(u);
        return u;
    }

    public boolean entrar(String login, String senha) {
        Optional<Usuario> ou = repo.buscar(login);
        if (ou.isEmpty()) return false;
        if (!ou.get().getSenhaHash().equals(hash(senha))) return false;
        sessao = ou.get();
        return true;
    }

    public void sair() {
        sessao = null;
    }

    public Usuario atual() {
        return sessao;
    }

    public boolean autenticado() {
        return sessao != null;
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
