package br.safewalk.web.controller;

import br.safewalk.domain.Periodo;
import br.safewalk.service.RelatorioService;
import br.safewalk.web.dto.Dtos.Mensagem;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService svc;

    public RelatorioController(RelatorioService svc) {
        this.svc = svc;
    }

    @GetMapping("/contagem")
    public ResponseEntity<?> contagem(@RequestParam(required = false) String inicio,
                                      @RequestParam(required = false) String fim,
                                      HttpSession session) {
        if (session.getAttribute("login") == null) {
            return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        }
        Periodo p = null;
        if (inicio != null && fim != null) {
            try { p = new Periodo(LocalDate.parse(inicio), LocalDate.parse(fim)); } catch (Exception ignored) {}
        }
        Map<String, Integer> mapa = svc.contagemPorTipo(p);
        return ResponseEntity.ok(mapa);
    }

    @GetMapping("/exportar")
    public ResponseEntity<?> exportar(HttpSession session) throws IOException {
        if (session.getAttribute("login") == null) {
            return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        }
        Path destino = Path.of("dados", "relatorio-" + System.currentTimeMillis() + ".csv");
        svc.exportarCsv(null, destino);
        byte[] bytes = Files.readAllBytes(destino);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=ocorrencias.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
