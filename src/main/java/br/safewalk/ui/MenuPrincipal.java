package br.safewalk.ui;

import br.safewalk.service.AutenticacaoService;

public class MenuPrincipal {
    private final EntradaConsole in;
    private final AutenticacaoService auth;
    private final MenuUsuario mu;
    private final MenuRota mr;
    private final MenuOcorrencia mo;
    private final MenuRelatorio mrel;

    public MenuPrincipal(EntradaConsole in,
                         AutenticacaoService auth,
                         MenuUsuario mu,
                         MenuRota mr,
                         MenuOcorrencia mo,
                         MenuRelatorio mrel) {
        this.in = in;
        this.auth = auth;
        this.mu = mu;
        this.mr = mr;
        this.mo = mo;
        this.mrel = mrel;
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
                System.out.println("[1] criar rota   [2] minhas rotas   [3] buscar rota");
                System.out.println("[4] avaliar seguranca   [5] excluir rota");
                System.out.println("[6] registrar ocorrencia   [7] listar ocorrencias   [8] excluir ocorrencia");
                System.out.println("[9] relatorio contagem   [10] exportar csv");
                System.out.println("[0] sair da sessao");
                int op = in.inteiro("op");
                switch (op) {
                    case 1 -> mr.criar();
                    case 2 -> mr.listarMinhas();
                    case 3 -> mr.buscar();
                    case 4 -> mr.avaliar();
                    case 5 -> mr.excluir();
                    case 6 -> mo.registrar();
                    case 7 -> mo.listarFiltrado();
                    case 8 -> mo.excluir();
                    case 9 -> mrel.contagem();
                    case 10 -> mrel.exportar();
                    case 0 -> auth.sair();
                    default -> System.out.println("opcao invalida");
                }
            }
        }
    }
}
