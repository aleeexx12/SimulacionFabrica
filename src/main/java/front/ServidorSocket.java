/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package front;

/**
 *
 * @author Alex
 */
import clases.Almacen;
import clases.Logger;
import clases.Reposteros;
//import clases.Logger;
import java.io.*;
import java.net.*;

public class ServidorSocket extends Thread {

    private final Servidor servidor;
    private final Almacen almacen;
    private final Reposteros[] reposteros; // Arreglo de reposteros

    public ServidorSocket(Servidor servidor, Almacen almacen, Reposteros[] repostero) {
        this.servidor = servidor;
        this.almacen = almacen;
        this.reposteros = repostero; // Inicializar los reposteros
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5006)) {
            System.out.println("Servidor socket en espera de conexiones...");

            while (true) {
                try (Socket socket = serverSocket.accept(); DataOutputStream out = new DataOutputStream(socket.getOutputStream()); DataInputStream in = new DataInputStream(socket.getInputStream())) {

                    System.out.println("Cliente conectado desde: " + socket.getInetAddress());
                    String request = in.readUTF(); // Leer solicitud del cliente
                    System.out.println("Solicitud recibida: " + request);

                    switch (request) {
                        case "Repostero1":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasRepostero("Repostero 1")));
                            break;
                        case "Repostero2":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasRepostero("Repostero 2")));
                            break;
                        case "Repostero3":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasRepostero("Repostero 3")));
                            break;
                        case "Repostero4":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasRepostero("Repostero 4")));
                            break;
                        case "Repostero5":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasRepostero("Repostero 5")));
                            break;
                        // Galletas desechadas
                        case "DesechadasRepostero1":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasDesechadas("Repostero 1")));
                            break;
                        case "DesechadasRepostero2":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasDesechadas("Repostero 2")));
                            break;
                        case "DesechadasRepostero3":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasDesechadas("Repostero 3")));
                            break;
                        case "DesechadasRepostero4":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasDesechadas("Repostero 4")));
                            break;
                        case "DesechadasRepostero5":
                            out.writeUTF(String.valueOf(servidor.obtenerGalletasDesechadas("Repostero 5")));
                            break;
                        // Solicitudes de galletas
                        case "Horno1":
                            out.writeUTF(servidor.getHorno1Galletas());
                            break;
                        case "Horno2":
                            out.writeUTF(servidor.getHorno2Galletas());
                            break;
                        case "Horno3":
                            out.writeUTF(servidor.getHorno3Galletas());
                            break;

                        // Solicitudes de progreso
                        case "ProgresoHorno1":
                            out.writeUTF(String.valueOf(servidor.getProgresoHorno1()));
                            break;
                        case "ProgresoHorno2":
                            out.writeUTF(String.valueOf(servidor.getProgresoHorno2()));
                            break;
                        case "ProgresoHorno3":
                            out.writeUTF(String.valueOf(servidor.getProgresoHorno3()));
                            break;
                        case "Almacen":
                            out.writeUTF(String.valueOf(servidor.getGalletasAlmacen()));
                            break;
                        case "ConsumirGalletas":
                            System.out.println("Solicitud recibida: ConsumirGalletas");
                            int cantidad = in.readInt(); // Leer cantidad del cliente
                            System.out.println("Cantidad solicitada: " + cantidad);

                            synchronized (almacen) {
                                if (almacen.restarGalletas(cantidad)) {
                                    String respuesta = "Se consumieron " + cantidad + " galletas. Restantes: " + almacen.obtenerGalletas();
                                    System.out.println("Respuesta enviada: " + respuesta);
                                    out.writeUTF(respuesta); // Enviar respuesta al cliente
                                } else {
                                    String respuesta = "No hay suficientes galletas para consumir.";
                                    System.out.println("Respuesta enviada: " + respuesta);
                                    out.writeUTF(respuesta); // Enviar respuesta al cliente
                                }
                            }
                            break;
                        case "PausarRepostero":
                            int idPausar = in.readInt(); // Leer el ID del repostero
                            if (idPausar >= 1 && idPausar <= reposteros.length) {
                                reposteros[idPausar - 1].pausar();
                                servidor.actualizarEstadoRepostero("Repostero " + idPausar, "Pausado");
                                Logger.log("Se ha pausado el repostero " + idPausar);
                                out.writeUTF("Repostero " + idPausar + " pausado.");
                            } else {
                                out.writeUTF("ID de repostero inválido.");
                            }
                            break;

                        case "ReanudarRepostero":
                            int idReanudar = in.readInt();
                            if (idReanudar >= 1 && idReanudar <= reposteros.length) {
                                reposteros[idReanudar - 1].reanudar();
                                String estadoActual = reposteros[idReanudar - 1].getEstadoActual();
                                out.writeUTF("Repostero " + idReanudar + " reanudado y en estado: " + estadoActual);
                                Logger.log("Se ha reanudado el repostero " + idReanudar);

                            } else {
                                out.writeUTF("ID de repostero inválido.");
                            }
                            break;

                        default:
                            out.writeUTF("Solicitud no reconocida.");
                            break;

                    }
                } catch (IOException e) {
                    System.out.println("Error al procesar la solicitud: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor socket: " + e.getMessage());
        }
    }
}
