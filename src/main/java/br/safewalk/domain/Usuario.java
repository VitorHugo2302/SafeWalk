package br.safewalk.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Usuario {
    private String login;
    private String senhaHash;
    private String nome;
    private PerfilUsuario perfil;
    private LocalDate criadoEm;

    public Usuario(String login, String senhaHash, String nome, PerfilUsuario perfil) {
        this(login, senhaHash, nome, perfil, LocalDate.now());
    }

    public Usuario(String login, String senhaHash, String nome, PerfilUsuario perfil, LocalDate criadoEm) {
        this.login = login;
        this.senhaHash = senhaHash;
        this.nome = nome;
        this.perfil = perfil;
        this.criadoEm = criadoEm;
    }

    public String getLogin() { return login; }
    public String getSenhaHash() { return senhaHash; }
    public String getNome() { return nome; }
    public PerfilUsuario getPerfil() { return perfil; }
    public LocalDate getCriadoEm() { return criadoEm; }

    public void setNome(String nome) { this.nome = nome; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    public boolean podeModerar() {
        return perfil == PerfilUsuario.MODERADOR || perfil == PerfilUsuario.ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        return Objects.equals(login, ((Usuario) o).login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
