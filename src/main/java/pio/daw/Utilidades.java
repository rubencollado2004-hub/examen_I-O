package pio.daw;

import java.util.HashMap;

public class Utilidades {

    
    public static final HashMap<String, Double> energias = new HashMap<>();

    
    public static final HashMap<String, Double> semividas = new HashMap<>();

    static {
        // Sr90 (Estroncio-90): beta ~0.546 MeV — producto de fisión común
        energias.put("Sr90", 0.546e6 * 1.602e-19);
        // Co60 (Cobalto-60): beta + 2 gammas ~2.824 MeV totales
        energias.put("Co60", 2.824e6 * 1.602e-19);
        // Cs137 (Cesio-137): beta ~0.512 MeV
        energias.put("Cs137", 0.512e6 * 1.602e-19);

        // Sr90: semivida 28.8 años → segundos
        semividas.put("Sr90", 28.8 * 365.25 * 24 * 3600);
        // Co60: semivida 5.27 años → segundos
        semividas.put("Co60", 5.27 * 365.25 * 24 * 3600);
        // Cs137: semivida 30.17 años → segundos
        semividas.put("Cs137", 30.17 * 365.25 * 24 * 3600);
    }

    /**
     * Integración numérica por la regla de Simpson compuesta.
     * Calcula el área bajo la curva f entre limInf y limSup.
     */
    public static double integrar(FuncionUnivariable f, double limInf, double limSup) {
        int n = 1000; // número de subintervalos (debe ser par)
        if (n % 2 != 0) n++;
        double h = (limSup - limInf) / n;

        double suma = f.evaluar(limInf) + f.evaluar(limSup);
        for (int i = 1; i < n; i++) {
            double x = limInf + i * h;
            suma += (i % 2 == 0 ? 2 : 4) * f.evaluar(x);
        }
        return suma * h / 3.0;
    }

    /**
     * Método de bisección: encuentra x en [limInf, limSup] tal que f(x) = y.
     * Funciona tanto para funciones crecientes como decrecientes.
     */
    public static double biseccion(FuncionUnivariable f, double y, double limInf, double limSup) {
        double tolerancia = 1e-6;
        double a = limInf;
        double b = limSup;

        // g(x) = f(x) - y; buscamos la raíz de g
        double ga = f.evaluar(a) - y;

        for (int i = 0; i < 200; i++) {
            double mid = (a + b) / 2.0;
            double gMid = f.evaluar(mid) - y;

            if (Math.abs(gMid) < tolerancia || (b - a) / 2.0 < tolerancia) {
                return mid;
            }

            if (ga * gMid < 0) {
                b = mid;
            } else {
                a = mid;
                ga = gMid;
            }
        }
        return (a + b) / 2.0;
    }
}
