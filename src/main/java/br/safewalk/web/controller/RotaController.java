package br.safewalk.web.controller;

import br.safewalk.domain.IndiceSeguranca;
import br.safewalk.domain.Rota;
import br.safewalk.domain.Trecho;
import br.safewalk.domain.Usuario;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.service.CalculadoraIndice;
import br.safewalk.service.OcorrenciaService;
import br.safewalk.service.RotaService;
import br.safewalk.web.dto.Dtos.Mensagem;
import br.safewalk.web.dto.Dtos.NovaRotaRequest;
import br.safewalk.web.dto.Dtos.TrechoRequest;
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

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rotas")
public class RotaController {

    private final RotaService rotas;
    private final OcorrenciaService ocorrencias;
    private final AutenticacaoService auth;
    private final CalculadoraIndice calc;

    public RotaController(RotaService rotas, OcorrenciaService ocorrencias, AutenticacaoService auth, CalculadoraIndice calc) {
        this.rotas = rotas;
        this.ocorrencias = ocorrencias;
        this.auth = auth;
        this.calc = calc;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody NovaRotaRequest req, HttpSession session) {
        Usuario u = usuario(session);
        if (u == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        if (req.trechos() == null || req.trechos().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensagem("informe ao menos um trecho"));
        }
        try {
            Rota r = rotas.criar(u, req.nome(), LocalTime.parse(req.horario()));
            for (TrechoRequest t : req.trechos()) {
                Trecho tr = new Trecho(UUID.randomUUID().toString().substring(0, 6),
                        t.nomeVia(), t.bairro(), t.distanciaKm(), t.lat(), t.lng());
                rotas.adicionarTrecho(r.getId(), tr);
            }
            return ResponseEntity.ok(rotas.buscar(r.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Mensagem(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(defaultValue = "minhas") String escopo, HttpSession session) {
        Usuario u = usuario(session);
        if (u == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        List<Rota> ls = escopo.equals("todas") ? rotas.listarTodas() : rotas.listarDoUsuario(u.getLogin());
        return ResponseEntity.ok(ls);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String q, HttpSession session) {
        if (usuario(session) == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        return ResponseEntity.ok(rotas.buscarPorNome(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalhe(@PathVariable String id, HttpSession session) {
        if (usuario(session) == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        Rota r = rotas.buscar(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }

    @GetMapping("/{id}/indice")
    public ResponseEntity<?> indice(@PathVariable String id, HttpSession session) {
        if (usuario(session) == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        Rota r = rotas.buscar(id);
        if (r == null) return ResponseEntity.notFound().build();
        IndiceSeguranca idx = calc.calcular(r, ocorrencias.listar());
        return ResponseEntity.ok(idx);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable String id, HttpSession session) {
        Usuario u = usuario(session);
        if (u == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        try {
            boolean ok = rotas.excluir(id, u);
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
