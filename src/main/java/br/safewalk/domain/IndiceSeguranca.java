package br.safewalk.domain;

public class IndiceSeguranca {
    private final double score;
    private final String classificacao;

    public IndiceSeguranca(double score) {
        this.score = Math.max(0, Math.min(100, score));
        this.classificacao = classificar(this.score);
    }

    private static String classificar(double s) {
        if (s >= 85) return "A";
        if (s >= 70) return "B";
        if (s >= 50) return "C";
        if (s >= 30) return "D";
        return "E";
    }

    public double getScore() { return score; }
    public String getClassificacao() { return classificacao; }
}
