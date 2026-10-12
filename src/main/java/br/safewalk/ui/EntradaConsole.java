package br.safewalk.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class EntradaConsole {
    private final Scanner sc;

    public EntradaConsole(Scanner sc) {
        this.sc = sc;
    }

    public String texto(String rotulo) {
        System.out.print(rotulo + ": ");
        return sc.nextLine().trim();
    }

    public int inteiro(String rotulo) {
        while (true) {
            String s = texto(rotulo);
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("numero invalido"); }
        }
    }

    public double decimal(String rotulo) {
        while (true) {
            String s = texto(rotulo).replace(',', '.');
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.println("numero invalido"); }
        }
    }

    public LocalDate data(String rotulo) {
        while (true) {
            String s = texto(rotulo + " (AAAA-MM-DD)");
            try { return LocalDate.parse(s); }
            catch (Exception e) { System.out.println("data invalida"); }
        }
    }

    public LocalTime hora(String rotulo) {
        while (true) {
            String s = texto(rotulo + " (HH:MM)");
            try { return LocalTime.parse(s); }
            catch (Exception e) { System.out.println("hora invalida"); }
        }
    }
}
