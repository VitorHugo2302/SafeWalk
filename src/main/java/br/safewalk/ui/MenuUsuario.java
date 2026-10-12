package br.safewalk.ui;

import br.safewalk.domain.PerfilUsuario;
import br.safewalk.service.AutenticacaoService;

public class MenuUsuario {
    private final EntradaConsole in;
    private final AutenticacaoService auth;

    public MenuUsuario(EntradaConsole in, AutenticacaoService auth) {
        this.in = in;
        this.auth = auth;
    }

    public void cadastrar() {
        String login = in.texto("login");
        String senha = in.texto("senha");
        String nome = in.texto("nome");
        try {
            auth.cadastrar(login, senha, nome, PerfilUsuario.COMUM);
            System.out.println("cadastrado");
        } catch (Exception e) {
            System.out.println("erro: " + e.getMessage());
        }
    }

    public boolean entrar() {
        String login = in.texto("login");
        String senha = in.texto("senha");
        if (auth.entrar(login, senha)) {
            System.out.println("ola " + auth.atual().getNome());
            return true;
        }
        System.out.println("credenciais invalidas");
        return false;
    }
}
