package com.knowYourfan.backEnd;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.InputMismatchException;
import java.util.Objects;

@Getter
@Embeddable
public class CPF implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String cpf;
    public String digitos;

    public CPF(){}
    public CPF(String cpf) {
        cpf = cpf.replaceAll("\\.","").replaceAll("-","");
        if(!isCPF(cpf)) throw new IllegalArgumentException("CPF invalido");
        this.digitos = cpf;
        this.cpf = format(cpf);
    }

    private boolean isCPF(String CPF) {

        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere "0" no inteiro 0
                // (48 eh a posicao de "0" na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            return (dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10));
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    private String format(String cpf) {
        StringBuilder sb = new StringBuilder(cpf.length()+3);

        sb.append(cpf, 0, 3).append(".").append(cpf, 3, 6).append(".").append(cpf, 6, 9)
                .append("-").append(cpf, 9, 11);

        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(this.getCpf(), cpf.getCpf());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCpf());
    }
}
