package clases;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import front.Servidor;

public class Almacen extends Thread {

    private final int capacidadMaxima;
    private int galletasAlmacenadas = 0;
    private final Lock cerrojo = new ReentrantLock();
    private Servidor servidor; // Referencia al Servidor, inicialmente null

    public Almacen(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    // Método para asignar el Servidor después de la inicialización
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    // Método para almacenar galletas
    public synchronized void almacenarGalletas(int cantidad, int idEmpaquetador) throws InterruptedException {
        while (galletasAlmacenadas + cantidad > capacidadMaxima) {
            Logger.log("El almacén está lleno. El empaquetador" + idEmpaquetador + " está esperando...");
            wait();
        }
        galletasAlmacenadas += cantidad;

        Logger.log("El empaquetador" + idEmpaquetador + " almacenó " + cantidad + " galletas. Total en almacén: " + galletasAlmacenadas);

        // Actualizar la cantidad en la interfaz gráfica
        if (servidor != null) {
            servidor.actualizarCantidadAlmacen(galletasAlmacenadas);
        }

        notifyAll();
    }

    public synchronized int obtenerGalletas() {
        return galletasAlmacenadas;
    }

    public synchronized boolean restarGalletas(int cantidad) {
    if (galletasAlmacenadas >= cantidad) {
        galletasAlmacenadas -= cantidad;

        Logger.log("Se consumieron " + cantidad + " galletas. Restantes: " + galletasAlmacenadas);

        // Actualizar la cantidad en la interfaz gráfica
        if (servidor != null) {
            servidor.actualizarCantidadAlmacen(galletasAlmacenadas);
        }

        notifyAll();
        return true;
    } else {
        Logger.log("Intento de consumir " + cantidad + " galletas, pero solo hay " + galletasAlmacenadas + " disponibles.");
        return false;
    }
    }

}