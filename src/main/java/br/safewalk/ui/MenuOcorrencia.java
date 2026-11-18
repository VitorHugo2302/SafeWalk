package br.safewalk.ui;

import br.safewalk.domain.Ocorrencia;
import br.safewalk.domain.Periodo;
import br.safewalk.domain.TipoOcorrencia;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.service.OcorrenciaService;

import java.time.LocalDate;
import java.util.List;

public class MenuOcorrencia {
    private final EntradaConsole in;
    private final OcorrenciaService svc;
    private final AutenticacaoService auth;

    public MenuOcorrencia(EntradaConsole in, OcorrenciaService svc, AutenticacaoService auth) {
        this.in = in;
        this.svc = svc;
        this.auth = auth;
    }

    public void registrar() {
        String trecho = in.texto("id do trecho");
        System.out.println("tipos: 1-ASSALTO 2-FURTO 3-AGRESSAO 4-ILUMINACAO_RUIM 5-ACIDENTE_TRANSITO 6-VIA_DETERIORADA");
        int t = in.inteiro("tipo");
        if (t < 1 || t > 6) {
            System.out.println("tipo invalido");
            return;
        }
        TipoOcorrencia tipo = TipoOcorrencia.values()[t - 1];
        LocalDate d = in.data("data");
        String desc = in.texto("descricao");
        try {
            Ocorrencia o = svc.registrar(auth.atual(), trecho, tipo, d, desc);
            System.out.println("registrada: " + o.getId());
        } catch (Exception e) {
            System.out.println("erro: " + e.getMessage());
        }
    }

    public void listarFiltrado() {
        String filtro = in.texto("tipo (vazio=todos)").toUpperCase();
        TipoOcorrencia tipo = null;
        if (!filtro.isBlank()) {
            try { tipo = TipoOcorrencia.valueOf(filtro); }
            catch (Exception e) { System.out.println("tipo invalido - mostrando todos"); }
        }
        String p = in.texto("filtrar periodo? (s/n)");
        Periodo per = null;
        if (p.equalsIgnoreCase("s")) {
            LocalDate ini = in.data("inicio");
            LocalDate fim = in.data("fim");
            try { per = new Periodo(ini, fim); }
            catch (Exception e) { System.out.println("periodo invalido - ignorando"); }
        }
        List<Ocorrencia> ls = svc.filtrar(tipo, per);
        if (ls.isEmpty()) {
            System.out.println("vazio");
            return;
        }
        for (Ocorrencia o : ls) {
            System.out.printf("%s | %s | trecho %s | %s | %s%n",
                    o.getId(), o.getTipo(), o.getTrechoId(), o.getData(), o.getDescricao());
        }
    }

    public void excluir() {
        String id = in.texto("id da ocorrencia");
        try {
            if (svc.excluir(id, auth.atual())) System.out.println("excluida");
            else System.out.println("nao encontrada");
        } catch (SecurityException e) {
            System.out.println("sem permissao");
        }
    }
}
