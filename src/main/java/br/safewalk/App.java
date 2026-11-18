package br.safewalk;

import br.safewalk.infra.OcorrenciaRepository;
import br.safewalk.infra.OcorrenciaRepositoryArquivo;
import br.safewalk.infra.RotaRepository;
import br.safewalk.infra.RotaRepositoryArquivo;
import br.safewalk.infra.UsuarioRepository;
import br.safewalk.infra.UsuarioRepositoryArquivo;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.service.OcorrenciaService;
import br.safewalk.service.RelatorioService;
import br.safewalk.service.RotaService;
import br.safewalk.ui.EntradaConsole;
import br.safewalk.ui.MenuOcorrencia;
import br.safewalk.ui.MenuPrincipal;
import br.safewalk.ui.MenuRelatorio;
import br.safewalk.ui.MenuRota;
import br.safewalk.ui.MenuUsuario;

import java.nio.file.Path;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Path base = Path.of("dados");
        UsuarioRepository ur = new UsuarioRepositoryArquivo(base.resolve("usuarios.csv"));
        RotaRepository rr = new RotaRepositoryArquivo(base.resolve("rotas.csv"));
        OcorrenciaRepository or = new OcorrenciaRepositoryArquivo(base.resolve("ocorrencias.csv"));

        AutenticacaoService auth = new AutenticacaoService(ur);
        RotaService rs = new RotaService(rr);
        OcorrenciaService os = new OcorrenciaService(or);
        RelatorioService rel = new RelatorioService(or);

        try (Scanner sc = new Scanner(System.in)) {
            EntradaConsole in = new EntradaConsole(sc);
            MenuUsuario mu = new MenuUsuario(in, auth);
            MenuRota mr = new MenuRota(in, rs, os, auth);
            MenuOcorrencia mo = new MenuOcorrencia(in, os, auth);
            MenuRelatorio mrel = new MenuRelatorio(in, rel);
            MenuPrincipal mp = new MenuPrincipal(in, auth, mu, mr, mo, mrel);

            System.out.println("== SafeWalk ==");
            mp.rodar();
        }
    }
}
