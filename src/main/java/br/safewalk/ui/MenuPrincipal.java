package br.safewalk.ui;

import br.safewalk.service.AutenticacaoService;

public class MenuPrincipal {
    private final EntradaConsole in;
    private final AutenticacaoService auth;
    private final MenuUsuario mu;

    public MenuPrincipal(EntradaConsole in, AutenticacaoService auth, MenuUsuario mu) {
        this.in = in;
        this.auth = auth;
        this.mu = mu;
    }

    public void rodar() {
        while (true) {
            if (!auth.autenticado()) {
                System.out.println("\n[1] entrar  [2] cadastrar  [0] sair");
                int op = in.inteiro("op");
                if (op == 1) mu.entrar();
                else if (op == 2) mu.cadastrar();
                else if (op == 0) return;
                else System.out.println("opcao invalida");
            } else {
                System.out.println("\n== SafeWalk | " + auth.atual().getLogin() + " ==");
                System.out.println("bem-vindo, " + auth.atual().getNome());
                System.out.println("[0] sair da sessao");
                int op = in.inteiro("op");
                if (op == 0) auth.sair();
            }
        }
    }
}
