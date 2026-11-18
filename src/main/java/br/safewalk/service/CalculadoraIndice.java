package br.safewalk.service;

import br.safewalk.domain.IndiceSeguranca;
import br.safewalk.domain.Ocorrencia;
import br.safewalk.domain.Rota;
import br.safewalk.domain.Trecho;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CalculadoraIndice {

    public IndiceSeguranca calcular(Rota rota, List<Ocorrencia> todas) {
        double distancia = rota.distanciaTotal();
        if (distancia <= 0) return new IndiceSeguranca(50);

        double pesoAcumulado = 0;
        LocalDate hoje = LocalDate.now();

        for (Trecho t : rota.getTrechos()) {
            for (Ocorrencia o : todas) {
                if (!o.getTrechoId().equals(t.getId())) continue;
                double peso = o.getTipo().getPesoBase();
                long dias = ChronoUnit.DAYS.between(o.getData(), hoje);
                double decay = Math.max(0.15, 1.0 - (dias / 730.0));
                pesoAcumulado += peso * decay;
            }
        }

        double densidade = pesoAcumulado / distancia;
        if (rota.ehNoturna()) densidade *= 1.3;

        double score = 100 - Math.min(100, densidade * 4.0);
        return new IndiceSeguranca(score);
    }
}
