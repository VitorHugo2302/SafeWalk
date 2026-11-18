package br.safewalk.web.controller;

import br.safewalk.domain.Ocorrencia;
import br.safewalk.domain.Periodo;
import br.safewalk.domain.TipoOcorrencia;
import br.safewalk.domain.Usuario;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.service.OcorrenciaService;
import br.safewalk.web.dto.Dtos.Mensagem;
import br.safewalk.web.dto.Dtos.NovaOcorrenciaRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService svc;
    private final AutenticacaoService auth;

    public OcorrenciaController(OcorrenciaService svc, AutenticacaoService auth) {
        this.svc = svc;
        this.auth = auth;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody NovaOcorrenciaRequest req, HttpSession session) {
        Usuario u = usuario(session);
        if (u == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        try {
            Ocorrencia o = svc.registrar(u, req.trechoId(),
                    TipoOcorrencia.valueOf(req.tipo()),
                    LocalDate.parse(req.data()),
                    req.descricao());
            return ResponseEntity.ok(o);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Mensagem(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String tipo,
                                    @RequestParam(required = false) String inicio,
                                    @RequestParam(required = false) String fim,
                                    HttpSession session) {
        if (usuario(session) == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        TipoOcorrencia t = null;
        if (tipo != null && !tipo.isBlank()) {
            try { t = TipoOcorrencia.valueOf(tipo); } catch (Exception ignored) {}
        }
        Periodo p = null;
        if (inicio != null && fim != null) {
            try { p = new Periodo(LocalDate.parse(inicio), LocalDate.parse(fim)); } catch (Exception ignored) {}
        }
        List<Ocorrencia> ls = svc.filtrar(t, p);
        return ResponseEntity.ok(ls);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable String id, HttpSession session) {
        Usuario u = usuario(session);
        if (u == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        try {
            boolean ok = svc.excluir(id, u);
            if (!ok) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(new Mensagem("excluida"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(new Mensagem("sem permissao"));
        }
    }

    private Usuario usuario(HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login == null) return null;
        return auth.buscarUsuario(login).orElse(null);
    }
}
