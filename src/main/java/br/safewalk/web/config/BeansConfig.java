package br.safewalk.web.config;

import br.safewalk.infra.OcorrenciaRepository;
import br.safewalk.infra.OcorrenciaRepositoryArquivo;
import br.safewalk.infra.RotaRepository;
import br.safewalk.infra.RotaRepositoryArquivo;
import br.safewalk.infra.UsuarioRepository;
import br.safewalk.infra.UsuarioRepositoryArquivo;
import br.safewalk.service.AutenticacaoService;
import br.safewalk.service.CalculadoraIndice;
import br.safewalk.service.OcorrenciaService;
import br.safewalk.service.RelatorioService;
import br.safewalk.service.RotaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class BeansConfig {

    private static final Path BASE = Path.of("dados");

    @Bean
    public UsuarioRepository usuarioRepository() {
        return new UsuarioRepositoryArquivo(BASE.resolve("usuarios.csv"));
    }

    @Bean
    public RotaRepository rotaRepository() {
        return new RotaRepositoryArquivo(BASE.resolve("rotas.csv"));
    }

    @Bean
    public OcorrenciaRepository ocorrenciaRepository() {
        return new OcorrenciaRepositoryArquivo(BASE.resolve("ocorrencias.csv"));
    }

    @Bean
    public AutenticacaoService autenticacaoService(UsuarioRepository r) {
        return new AutenticacaoService(r);
    }

    @Bean
    public RotaService rotaService(RotaRepository r) {
        return new RotaService(r);
    }

    @Bean
    public OcorrenciaService ocorrenciaService(OcorrenciaRepository r) {
        return new OcorrenciaService(r);
    }

    @Bean
    public RelatorioService relatorioService(OcorrenciaRepository r) {
        return new RelatorioService(r);
    }

    @Bean
    public CalculadoraIndice calculadoraIndice() {
        return new CalculadoraIndice();
    }
}
