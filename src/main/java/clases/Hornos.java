package clases;

import front.Servidor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hornos {

    private final Servidor servidor;
    private final int capacidadMaxima = 200;
    private int galletasActuales = 0;
    private boolean horneando = false;
    private int galletasListas;
    private final int id; // Identificador único del horno
    private int progresoHorneado = 0; // Progreso actual del horneado

    public Hornos(int id, Servidor servidor) {
        this.id = id;
        this.servidor = servidor;
    }

    // Método sincronizado para agregar galletas al horno
    public synchronized boolean agregarGalletas(int cantidad, String reposteroId) throws InterruptedException {
        while (horneando || galletasActuales == capacidadMaxima) {
            wait(); // Espera si el horno está lleno o en proceso de horneado
        }

        int espacioRestante = capacidadMaxima - galletasActuales;

        if (cantidad <= espacioRestante) {
            galletasActuales += cantidad;
            Logger.log(reposteroId + " deja " + cantidad + " galletas en el Horno" + id + ". Total en horno: " + galletasActuales);
        } else {
            galletasActuales = capacidadMaxima; // Llena el horno
            int desperdicio = cantidad - espacioRestante;
            servidor.actualizarGalletasDesechadas(reposteroId, desperdicio);
            Logger.log(reposteroId + " deja " + espacioRestante + " galletas en el Horno" + id + " y desecha " + desperdicio + " galletas.");
        }

        if (galletasActuales == capacidadMaxima) {
            hornear(reposteroId); // Si el horno está lleno, comienza el horneado
        }

        return true;
    }

    // Método para hornear galletas
    // Método para obtener el progreso del horneado
    public synchronized int getProgresoHorneado() {
        return progresoHorneado;
    }

    private void hornear(String reposteroId) {
        horneando = true;
        Logger.log("Se inicia horneado en el Horno" + id + " con " + galletasActuales + " galletas.");
        progresoHorneado = 0; // Resetea el progreso al iniciar horneado

        new Thread(() -> {
            try {
                for (int i = 1; i <= 8; i++) { // Proceso en 8 pasos
                    Thread.sleep(1000); // Espera 1 segundo
                    synchronized (this) {
                        progresoHorneado = (i * 100) / 8; // Calcula el porcentaje
                    }
                }
                synchronized (this) {
                    galletasListas += 200; // Galletas listas
                    galletasActuales = 0; // Vacía el horno
                    progresoHorneado = 0; // Resetea el progreso
                    horneando = false;
                    notifyAll(); // Notifica a otros hilos para que se liberen
                }
                Logger.log("El Horno" + id + " ha terminado el horneado. Horno vacío y listo para nuevas galletas.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.log("Error durante el horneado en Horno" + id + ": " + e.getMessage());
            }
        }).start();
    }

    public synchronized int getGalletasListas() throws InterruptedException {
        while (galletasListas < 20) {
            wait(); // Espera hasta que haya suficientes galletas
        }
        galletasListas -= 20;
        notify(); // Notifica a otros empaquetadores
        return 20;
    }

    public synchronized int getId() {
        return id;
    }

    // Esto sirve para mostrarlo en el tesxtfield del servidor
    public synchronized int getGalletasActuales() {
        return galletasActuales;
    }

}
