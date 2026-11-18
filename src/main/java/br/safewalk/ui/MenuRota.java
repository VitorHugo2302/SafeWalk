package br.safewalk.ui;

import br.safewalk.domain.IndiceSeguranca;
import br.safewalk.domain.Rota;
import br.safewalk.domain.Trecho;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.service.CalculadoraIndice;
import br.safewalk.service.OcorrenciaService;
import br.safewalk.service.RotaService;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class MenuRota {
    private final EntradaConsole in;
    private final RotaService rotas;
    private final OcorrenciaService ocorrencias;
    private final AutenticacaoService auth;
    private final CalculadoraIndice calc;

    public MenuRota(EntradaConsole in, RotaService rotas, OcorrenciaService ocorrencias, AutenticacaoService auth) {
        this.in = in;
        this.rotas = rotas;
        this.ocorrencias = ocorrencias;
        this.auth = auth;
        this.calc = new CalculadoraIndice();
    }

    public void criar() {
        String nome = in.texto("nome da rota");
        LocalTime h = in.hora("horario previsto");
        Rota r = rotas.criar(auth.atual(), nome, h);
        int n = in.inteiro("quantos trechos");
        for (int i = 0; i < n; i++) {
            System.out.println("-- trecho " + (i + 1));
            String via = in.texto("rua/avenida");
            String bairro = in.texto("bairro");
            double km = in.decimal("distancia (km)");
            try {
                Trecho t = new Trecho(UUID.randomUUID().toString().substring(0, 6), via, bairro, km);
                rotas.adicionarTrecho(r.getId(), t);
            } catch (Exception e) {
                System.out.println("erro: " + e.getMessage());
                i--;
            }
        }
        System.out.println("rota " + r.getId() + " criada");
    }

    public void listarMinhas() {
        List<Rota> ls = rotas.listarDoUsuario(auth.atual().getLogin());
        if (ls.isEmpty()) {
            System.out.println("nenhuma rota");
            return;
        }
        for (Rota r : ls) {
            System.out.printf("%s | %s | %.2fkm | %s%n",
                    r.getId(), r.getNome(), r.distanciaTotal(), r.getHorarioPrevisto());
            for (Trecho t : r.getTrechos()) {
                System.out.printf("   - [%s] %s / %s (%.2fkm)%n", t.getId(), t.getNomeVia(), t.getBairro(), t.getDistanciaKm());
            }
        }
    }

    public void buscar() {
        String termo = in.texto("buscar por nome");
        List<Rota> ls = rotas.buscarPorNome(termo);
        if (ls.isEmpty()) {
            System.out.println("nada encontrado");
            return;
        }
        for (Rota r : ls) {
            System.out.println(r.getId() + " - " + r.getNome() + " (" + r.getDono() + ")");
        }
    }

    public void avaliar() {
        String id = in.texto("id da rota");
        Rota r = rotas.buscar(id);
        if (r == null) {
            System.out.println("rota nao encontrada");
            return;
        }
        IndiceSeguranca idx = calc.calcular(r, ocorrencias.listar());
        System.out.printf("score %.1f - classificacao %s%n", idx.getScore(), idx.getClassificacao());
    }

    public void excluir() {
        String id = in.texto("id da rota");
        try {
            if (rotas.excluir(id, auth.atual())) System.out.println("excluida");
            else System.out.println("nao encontrada");
        } catch (SecurityException e) {
            System.out.println("sem permissao");
        }
    }
}
