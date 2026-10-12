package br.safewalk;

import br.safewalk.infra.UsuarioRepository;
import br.safewalk.infra.UsuarioRepositoryArquivo;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.ui.EntradaConsole;
import br.safewalk.ui.MenuPrincipal;
import br.safewalk.ui.MenuUsuario;

import java.nio.file.Path;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Path base = Path.of("dados");
        UsuarioRepository ur = new UsuarioRepositoryArquivo(base.resolve("usuarios.csv"));
        AutenticacaoService auth = new AutenticacaoService(ur);

        try (Scanner sc = new Scanner(System.in)) {
            EntradaConsole in = new EntradaConsole(sc);
            MenuUsuario mu = new MenuUsuario(in, auth);
            MenuPrincipal mp = new MenuPrincipal(in, auth, mu);

            System.out.println("== SafeWalk ==");
            mp.rodar();
        }
    }
}
