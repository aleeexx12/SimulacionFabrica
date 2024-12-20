package clases;

import front.Servidor;
import java.util.Random;


public class Reposteros extends Thread {

    private final Random random = new Random();
    private final int id;
    private final Hornos[] hornos;
    private final Cafetera cafetera;
    private final Servidor servidor;

    private boolean pausado = false;
    private final Object lock = new Object();
    private String estadoActual = "Trabajando"; // Estado inicial
    private boolean descansoTerminado = false; // Indicador único

    public Reposteros(int id, Hornos[] hornos, Cafetera cafetera, Servidor servidor) {
        this.id = id;
        this.hornos = hornos;
        this.cafetera = cafetera;
        this.servidor = servidor;
    }

    public void setEstadoActual(String estado) {
        this.estadoActual = estado;
        servidor.actualizarEstadoRepostero("Repostero " + id, estado); // Actualizar en la interfaz
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void pausar() {
        pausado = true;
    }

    public void reanudar() {
        synchronized (lock) {
            pausado = false;
            lock.notify();
        }

        synchronized (this) {
            if ("Pausa café".equals(estadoActual)) {
                if (descansoTerminado) {
                    setEstadoActual("Trabajando"); // Cambiar a "Trabajando" si termino el descanso
                } else {
                    servidor.actualizarEstadoRepostero("Repostero " + id, "Pausa café"); // Mantener en pausa café
                }
            } else {
                setEstadoActual("Trabajando"); // Si no estaba en pausa café, pasar a "Trabajando"
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (lock) {
                    while (pausado) {
                        lock.wait(); // Pausar el hilo
                    }
                }

                if ("Pausa café".equals(estadoActual)) {
                    descansoTerminado = false; // Reiniciar el indicador
                    cafetera.procesoCafe("Repostero " + id, () -> descansoTerminado = true); // Proceso con marcador
                    synchronized (lock) {
                        if (!pausado && descansoTerminado) {
                            setEstadoActual("Trabajando"); // Cambiar estado tras descanso
                        }
                    }
                } else {
                    // Flujo normal de producción
                    int tandas = random.nextInt(3) + 3;
                    for (int i = 0; i < tandas; i++) {
                        synchronized (lock) {
                            while (pausado) {
                                lock.wait(); // Pausar el hilo
                            }
                        }

                        int galletasProducidas = producirGalletas();
                        agregarGalletasAHorno(galletasProducidas);
                        Thread.sleep(1000); // Simular tiempo de producción
                    }
                    setEstadoActual("Pausa café");
                }

            } catch (InterruptedException e) {
                break; // Finalizar hilo
            }
        }
    }

    private int producirGalletas() throws InterruptedException {
        int tiempoProduccion = random.nextInt(3) + 2; // Tiempo de 2 a 4 segundos
        Thread.sleep(tiempoProduccion * 1000);
        int galletasProducidas = random.nextInt(9) + 37; // Entre 37 y 45 galletas
        Logger.log("Repostero " + id + " produjo " + galletasProducidas + " galletas.");

        servidor.actualizarGalletasRepostero("Repostero " + id, galletasProducidas); // Notificar al servidor
        return galletasProducidas;
    }

    private void agregarGalletasAHorno(int galletas) throws InterruptedException {
        int hornoInicial = random.nextInt(hornos.length);

        for (int i = 0; i < hornos.length; i++) {
            synchronized (lock) {
                while (pausado) {
                    lock.wait(); // Esperar reanudación
                }
            }

            int indice = (hornoInicial + i) % hornos.length; // Rotar sobre los hornos
            Hornos horno = hornos[indice];
            if (horno.agregarGalletas(galletas, "Repostero " + id)) {
                Logger.log("Repostero " + id + " depositó " + galletas + " galletas en el Horno " + horno.getId());
                return; // Galletas añadidas exitosamente
            }
        }
        Logger.log("Repostero " + id + " no encontró espacio en los hornos y desecha " + galletas + " galletas.");
        servidor.actualizarGalletasDesechadas("Repostero " + id, galletas); // Registrar galletas desechadas
    }
}
