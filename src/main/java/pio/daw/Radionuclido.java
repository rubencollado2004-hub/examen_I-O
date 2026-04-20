package pio.daw;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Radionuclido {

    // Tarea 1: declara aquí los atributos privados ───────
    private String id;
    private String isotopo;
    private Double masa;
    private Double actividadEspecificaInicial;
    private LocalDateTime fechaEntrega;

    // Tarea 1: constructor ─────────

    public Radionuclido(String id, String isotopo, Double actividadEspecificaInicial,
                        Double masa, LocalDateTime fechaEntrega) {
        this.id = id;
        this.isotopo = isotopo;
        this.masa = masa;
        this.actividadEspecificaInicial = actividadEspecificaInicial;
        this.fechaEntrega = fechaEntrega;
    }

    //Tarea 1: getters ───────────────────

    public String getId() {
        return id;
    }

    public String getIsotopo() {
        return isotopo;
    }

    public Double getMasa() {
        return masa;
    }

    public Double getActividadEspecificaInicial() {
        return actividadEspecificaInicial;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public double getActividadInicial() {
        return actividadEspecificaInicial * masa;
    }

    // Tarea 2: actividad en una fecha concreta ────────────

    public double actividad(LocalDateTime fecha) {
        long t = ChronoUnit.SECONDS.between(fechaEntrega, fecha);
        double semivida = Utilidades.semividas.get(isotopo);
        double lambda = Math.log(2) / semivida;
        double A0 = getActividadInicial();
        return A0 * Math.exp(-lambda * t);
    }

    // Tarea 3: fracción de actividad restante (0..1) ────────────

    public double porcentajeActividad(LocalDateTime fecha) {
        return actividad(fecha) / getActividadInicial();
    }

    // Tarea 4:  fecha en que la actividad baja al 10% ───────────

    public LocalDateTime getFechaSegura() {
        double semivida = Utilidades.semividas.get(isotopo);
        long tMax = (long) semivida;
        while (porcentajeActividad(fechaEntrega.plusSeconds(tMax)) >= 0.1) {
            tMax *= 2;
        }
        double tSeg = Utilidades.biseccion(
            t -> porcentajeActividad(fechaEntrega.plusSeconds((long) t)),
            0.1,
            0,
            tMax
        );
        return fechaEntrega.plusSeconds((long) tSeg);
    }

    // Tarea 5: coste de refrigeración a euros ────────────────

    public double getCosteRefrigeracion() {
        LocalDateTime fechaSegura = getFechaSegura();
        long tSeg = ChronoUnit.SECONDS.between(fechaEntrega, fechaSegura);
        double E = Utilidades.energias.get(isotopo);
        FuncionUnivariable P_ele = t -> actividad(fechaEntrega.plusSeconds((long) t)) * E / 4.0;
        double E_gastada = Utilidades.integrar(P_ele, 0, tSeg);
        return E_gastada / (double)3_600_000.0;
    }

    // Tarea 6:  bloque de factura en texto ──────────────────

    public String toFactura() {
        LocalDateTime fechaSegura = getFechaSegura();
        double coste = getCosteRefrigeracion();
        return String.format(
            "========================================\n" +
            "FACTURA DE REFRIGERACIÓN — RESIDUO #%s\n" +
            "========================================\n" +
            "Isótopo           : %s\n" +
            "Masa              : %.1f kg\n" +
            "Fecha de entrega  : %s\n" +
            "Fecha segura      : %s\n" +
            "Coste total       : %.2f €\n" +
            "----------------------------------------\n",
            id, isotopo, masa, fechaEntrega.toString().replace('T', ' '),
            fechaSegura.toString().replace('T', ' '), coste
        );
    }
}
