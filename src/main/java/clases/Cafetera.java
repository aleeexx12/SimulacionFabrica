/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import front.Servidor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cafetera {

    private final Random random = new Random();
    private final Lock cerrojo = new ReentrantLock();
    private final Servidor servidor;
    private final List<String> listaEspera = new ArrayList<>(); // Lista de reposteros en espera

    public Cafetera(Servidor servidor) {
        this.servidor = servidor;
    }

    public void procesoCafe(String repostero, Runnable marcarDescansoTerminado) throws InterruptedException {
    listaEspera.add(repostero);
    actualizarListaEspera();
    servidor.actualizarEstadoRepostero(repostero, "Pausa café");

    cerrojo.lock();
    try {
        listaEspera.remove(repostero);
        actualizarListaEspera();

        if (servidor != null) {
            servidor.actualizarTextoCafetera(repostero);
        }

        Logger.log(repostero + " realiza una parada para el café.");
        int tiempoEspera = random.nextInt(4) + 3; // Descanso entre 3 y 6 segundos
        Thread.sleep(2000); 
        Thread.sleep(tiempoEspera * 1000); // Simula el descanso

        // Indicar que el descanso ha terminado
        marcarDescansoTerminado.run();

        Logger.log(repostero + " ha acabado su descanso.");

        if (servidor != null) {
            servidor.actualizarTextoCafetera(""); // Limpiar texto en la interfaz
        }
    } finally {
        cerrojo.unlock();
    }
}


    // Método para actualizar el JTextField2 con la lista de espera
    private void actualizarListaEspera() {
        if (servidor != null) {
            servidor.actualizarTextoListaEspera(String.join(", ", listaEspera));
        }
    }
}
