/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clases;

import front.Servidor;

public class FabricaGalletas {

    public static void main(String[] args) {

        Almacen almacen = new Almacen(1000);


        Reposteros[] reposteros = new Reposteros[5];

        Servidor servidor = new Servidor(almacen, reposteros);
        almacen.setServidor(servidor);

        Hornos[] hornos = new Hornos[3];
        for (int i = 0; i < hornos.length; i++) {
            hornos[i] = new Hornos(i + 1, servidor); // Pasar el servidor a cada horno
        }
        servidor.setHornos(hornos);

        Cafetera cafetera = new Cafetera(servidor);

        // Inicializar y arrancar los reposteros
        for (int i = 0; i < reposteros.length; i++) {
            reposteros[i] = new Reposteros(i + 1, hornos, cafetera, servidor);
            reposteros[i].start();
        }

        for (int i = 0; i < hornos.length; i++) {
            Empaquetadores empaquetador = new Empaquetadores(i + 1, hornos[i], almacen, servidor);
            empaquetador.start();
        }

        // Hacer visible la interfaz gráfica del servidor
        servidor.setVisible(true);
    }
}

