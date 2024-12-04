package org.example;

/**
 * Clase principal que inicia el servidor y las configuraciones de los clientes.
 */
public class Main {

    //region Public Methods

    /**
     * Método principal que se ejecuta al iniciar la aplicación.
     * Inicia el servidor y las configuraciones para los clientes.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {

        // Iniciar el servidor
        new Servidor();  // Esto abrirá la ventana de la interfaz y comenzará a escuchar conexiones

        // Configuración de los clientes
        new ConfiguracionClientePoli1();     // Configuración para ClientePoli1

    }

    //endregion

}
