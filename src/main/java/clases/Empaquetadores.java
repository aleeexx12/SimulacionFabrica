package clases;

import front.Servidor;
import java.util.Random;

public class Empaquetadores extends Thread {

    private int id;
    private final Random random = new Random();

    private final Hornos horno; // Cada empaquetador está vinculado a un único horno
    private int galletasRecolectadas = 0;
    private final Almacen almacen;
    private final Servidor servidor;

    public Empaquetadores(int id, Hornos horno, Almacen almacen, Servidor servidor) {
        this.horno = horno;
        this.almacen = almacen;
        this.id = id;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                servidor.actualizarEstadoEmpaquetador("Empaquetador " + id, "Esperando");
                galletasRecolectadas += horno.getGalletasListas();

                servidor.actualizarEstadoEmpaquetador("Empaquetador " + id, "Recolectando galletas");
                Thread.sleep((random.nextInt(6) + 5) * 100); // Simula el tiempo de recolección

                Logger.log("Empaquetador" + id + " recogió 20 galletas del Horno" + horno.getId());

                if (galletasRecolectadas >= 100) {
                    servidor.actualizarEstadoEmpaquetador("Empaquetador " + id, "Empaquetando");
                    empaquetar();
                }

                servidor.actualizarEstadoEmpaquetador("Empaquetador " + id, "Esperando");

            } catch (InterruptedException e) {
                servidor.actualizarEstadoEmpaquetador("Empaquetador " + id, "Interrumpido");
                Logger.log("Empaquetador " + id + " fue interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void empaquetar() throws InterruptedException {
        Logger.log("Empaquetador del Horno" + horno.getId() + " empaqueta 100 galletas.");
        galletasRecolectadas -= 100;

        Logger.log("Empaquetador del Horno" + horno.getId() + " lleva las galletas al almacén.");
        Thread.sleep((random.nextInt(3) + 2) * 1000); // Simula el tiempo de transporte

        // Almacenar 100 galletas en el almacén, pasando el ID del empaquetador
        almacen.almacenarGalletas(100, id);
    }
}
