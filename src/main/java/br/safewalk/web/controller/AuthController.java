package br.safewalk.web.controller;

import br.safewalk.domain.PerfilUsuario;
import br.safewalk.domain.Usuario;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.web.dto.Dtos.CadastroRequest;
import br.safewalk.web.dto.Dtos.LoginRequest;
import br.safewalk.web.dto.Dtos.Mensagem;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AutenticacaoService auth;

    public AuthController(AutenticacaoService auth) {
        this.auth = auth;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroRequest req) {
        try {
            Usuario u = auth.cadastrar(req.login(), req.senha(), req.nome(), PerfilUsuario.COMUM);
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Mensagem(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        if (!auth.validar(req.login(), req.senha())) {
            return ResponseEntity.status(401).body(new Mensagem("credenciais invalidas"));
        }
        session.setAttribute("login", req.login());
        return auth.buscarUsuario(req.login())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(new Mensagem("usuario sumiu")));
    }

    @PostMapping("/sair")
    public ResponseEntity<?> sair(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new Mensagem("ok"));
    }

    @GetMapping("/sessao")
    public ResponseEntity<?> sessao(HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login == null) return ResponseEntity.status(401).body(new Mensagem("nao autenticado"));
        return auth.buscarUsuario(login)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(new Mensagem("sessao expirada")));
    }
}
