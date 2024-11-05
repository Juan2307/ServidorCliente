package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket; // Java .net
import java.net.Socket; // Java .net
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Clase que representa el servidor de chat.
 * Se encarga de aceptar conexiones de clientes y manejar la comunicación entre ellos.
 */
public class Servidor extends JFrame implements Runnable {

    //region Private Fields

    private JTextArea txtmensajes;  // Área de texto para mostrar mensajes del chat
    private ArrayList<DataOutputStream> clientOutputs = new ArrayList<>(); // Lista de salidas de los clientes
    private ArrayList<String> clientNames = new ArrayList<>(); // Para guardar los nombres de los clientes

    //endregion

    //region Constructor

    /**
     * Constructor que configura la ventana del servidor y comienza a escuchar conexiones.
     */
    public Servidor() {
        setTitle("Servidor - Chat");
        txtmensajes = new JTextArea();
        txtmensajes.setBounds(10, 30, 400, 300);
        txtmensajes.setEditable(false);
        add(txtmensajes);

        setLayout(null);
        setSize(450, 400);
        setVisible(true);

        // Listener para manejar el cierre de la ventana
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = javax.swing.JOptionPane.showConfirmDialog(
                        null,
                        "¿Estás seguro de que quieres salir?",
                        "Confirmar salida",
                        javax.swing.JOptionPane.YES_NO_OPTION
                );
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    // Si el usuario confirma, cerrar la aplicación
                    System.exit(0);
                }
            }
        });

        Thread hilo = new Thread(this);
        hilo.start();
    }

    //endregion

    //region Public Methods

    /**
     * Método que se ejecuta en un hilo separado para manejar conexiones entrantes.
     */
    @Override
    public void run() {
        ServerSocket servidor = null;
        final int PUERTO = 5000;

        try {
            servidor = new ServerSocket(PUERTO);
            txtmensajes.append("Servidor iniciado. Esperando conexiones...\n");

            while (true) {
                Socket sc = servidor.accept();
                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                synchronized (clientOutputs) {
                    clientOutputs.add(out);
                }

                Thread clientHandler = new Thread(() -> handleClient(sc, in, out));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Private Methods

    /**
     * Maneja la comunicación con un cliente conectado.
     *
     * @param clientSocket Socket del cliente.
     * @param in          Flujo de entrada para recibir datos del cliente.
     * @param out         Flujo de salida para enviar datos al cliente.
     */
    private void handleClient(Socket clientSocket, DataInputStream in, DataOutputStream out) {
        String nombreCliente = "";
        try {
            nombreCliente = in.readUTF(); // Leer el nombre del cliente
            txtmensajes.append(nombreCliente + " se ha conectado.\n");
            synchronized (clientNames) {
                clientNames.add(nombreCliente); // Guardar el nombre en la lista
            }

            while (true) {
                String mensaje = in.readUTF();
                // Retransmitir mensaje a todos los clientes conectados
                synchronized (clientOutputs) {
                    for (DataOutputStream clientOut : clientOutputs) {
                        clientOut.writeUTF(mensaje);
                    }
                }
            }
        } catch (IOException e) {
            txtmensajes.append(nombreCliente + " se ha desconectado.\n");
        } finally {
            synchronized (clientOutputs) {
                clientOutputs.remove(out);
            }
            synchronized (clientNames) {
                clientNames.remove(nombreCliente); // Eliminar el nombre de la lista al desconectar
            }
        }
    }

    //endregion
}

