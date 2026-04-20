package pio.daw;

import java.io.*;
import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        LocalDateTime fechaEntrega = LocalDateTime.of(2026, 4, 20, 0, 0); 
        StringBuilder factura = new StringBuilder();


        try (BufferedReader br = new BufferedReader(new FileReader("residuos.csv"))) {
            String line = br.readLine(); // Saltar cabecera
            while ((line = br.readLine()) != null) {

                String[] campos = line.split(";");
                String id = campos[0];
                String isotopo = campos[1];
                Double actEsp = Double.parseDouble(campos[2]);
                Double masa = Double.parseDouble(campos[3]);

                Radionuclido residuo = new Radionuclido(id, isotopo, actEsp, masa, fechaEntrega);
                factura.append(residuo.toFactura());

            }


        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSV: " + e.getMessage());
            
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("factura.txt"))) {
            bw.write(factura.toString());
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de factura: " + e.getMessage());
        }
    }
}
