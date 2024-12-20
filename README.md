Descripción General

La fábrica simula el trabajo de reposteros, hornos y empaquetadores mediante el uso de hilos y sincronización. Además, incluye un módulo de programación distribuida que permite visualizar el estado de la fábrica de forma remota.
Características Principales
Programación Concurrente

    Reposteros:
        Producen entre 37 y 45 galletas por tanda (2-4 segundos).
        Usan hornos de manera ordenada y descansan para preparar café (con uso exclusivo de una cafetera).
    Hornos:
        Capacidad máxima de 200 galletas.
        Comienzan a hornear solo cuando están completamente llenos (8 segundos por horneado).
    Empaquetadores:
        Recogen lotes de 20 galletas de un horno específico y las empaquetan al alcanzar 100 unidades.
        Las galletas empaquetadas se transportan al almacén (2-4 segundos).

Programación Distribuida

    Servidor:
        Amplía el programa concurrente para incluir funcionalidad de visualización remota.
    Cliente:
        Visualiza el estado de los reposteros, hornos y almacén en tiempo real (actualización cada 1 segundo).
        Permite pausar o reanudar individualmente a los reposteros.

Interfaz Gráfica

    Muestra en tiempo real:
        Producción de galletas por reposteros.
        Galletas horneadas y empaquetadas.
        Estado del almacén.
        Galletas desperdiciadas (registradas en un log).

Requerimientos Técnicos

    Lenguaje: Java.
    Entorno: NetBeans.
    Bibliotecas utilizadas: Manejo de hilos y sincronización nativos de Java.

Archivos Incluidos

    src/: Código fuente del proyecto.
    evolucionGalletas.txt: Archivo log con eventos registrados.
    README.md: Documentación del proyecto.

Cómo Ejecutar el Proyecto

    Configura el entorno:
        Asegúrate de tener Java instalado (versión 8 o superior).
        Abre el proyecto en NetBeans.

    Ejecución:
        Ejecuta el servidor desde la clase principal del proyecto.
        Ejecuta el cliente desde el módulo de visualización.

Diagrama de Clases

El sistema utiliza un diseño basado en clases que representan las entidades principales (reposteros, hornos, empaquetadores). La sincronización asegura la comunicación eficiente entre las entidades.
Log de Eventos

El sistema registra todos los eventos importantes en un archivo evolucionGalletas.txt, incluyendo:

    Producción de galletas.
    Uso de la cafetera.
    Horneado y empaquetado.
    Galletas desperdiciadas.
