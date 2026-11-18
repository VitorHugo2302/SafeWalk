package br.safewalk.ui;

import br.safewalk.domain.Periodo;
import br.safewalk.service.RelatorioService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class MenuRelatorio {
    private final EntradaConsole in;
    private final RelatorioService svc;

    public MenuRelatorio(EntradaConsole in, RelatorioService svc) {
        this.in = in;
        this.svc = svc;
    }

    public void contagem() {
        String p = in.texto("filtrar periodo? (s/n)");
        Periodo per = null;
        if (p.equalsIgnoreCase("s")) {
            LocalDate ini = in.data("inicio");
            LocalDate fim = in.data("fim");
            try { per = new Periodo(ini, fim); }
            catch (Exception e) { System.out.println("periodo invalido - ignorando"); }
        }
        Map<String, Integer> mapa = svc.contagemPorTipo(per);
        if (mapa.isEmpty()) {
            System.out.println("nenhuma ocorrencia");
            return;
        }
        mapa.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    public void exportar() {
        String p = in.texto("arquivo destino (ex: relatorio.csv)");
        try {
            Path caminho = svc.exportarCsv(null, Path.of(p));
            System.out.println("gerado em " + caminho.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("erro: " + e.getMessage());
        }
    }
}
