package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;  // Java .net
import java.util.Random;

/**
 * Clase que representa un cliente de chat en una aplicación de mensajería.
 * Esta clase extiende JFrame e implementa ActionListener y Runnable para
 * gestionar la interfaz de usuario y la comunicación con el servidor de chat.
 */
public class ClientePoli1 extends JFrame implements ActionListener, Runnable {

    //region Private Fields
    private JTextField txtmensaje; // Campo para ingresar mensajes a enviar
    private JButton btnenviar, btnsalir; // Botones para enviar mensajes y salir del chat
    private JTextArea txtmensajes; // Área de texto para mostrar mensajes del chat

    private Socket sc = null; // Socket para la conexión con el servidor

    private String ip; // Dirección IP del servidor
    private int puerto; // Puerto de conexión al servidor
    private String nombre; // Nombre del usuario
    private DataOutputStream out; // Stream para enviar datos al servidor
    private Random random; // Para generar un número aleatorio para el nombre del usuario
    //endregion

    //region Constructor
    /**
     * Constructor de la clase ClientePoli1. Inicializa la interfaz gráfica y la conexión al servidor.
     *
     * @param ip Dirección IP del servidor al que se conecta.
     * @param puerto Puerto de conexión al servidor.
     * @param nombre Nombre del usuario. Si está vacío, se generará un nombre aleatorio.
     */
    public ClientePoli1(String ip, int puerto, String nombre) {
        this.ip = ip;
        this.puerto = puerto;

        // Asignar nombre aleatorio si está vacío
        this.nombre = (nombre == null || nombre.isEmpty()) ? generarNombreAleatorio() : nombre;

        setTitle(this.nombre + " - Chat Cliente #1"); // Título de la ventana
        setLayout(new BorderLayout()); // Configuración del layout

        // Configurar JTextArea para mostrar mensajes
        txtmensajes = new JTextArea();
        txtmensajes.setEditable(false); // El área de mensajes no es editable
        txtmensajes.setLineWrap(true); // Permitir el ajuste de línea
        JScrollPane scrollMensajes = new JScrollPane(txtmensajes); // Añadir scroll al área de texto
        add(scrollMensajes, BorderLayout.CENTER); // Añadir al centro de la ventana

        // Configurar JTextField y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        txtmensaje = new JTextField(); // Campo para ingresar mensajes
        panelInferior.add(txtmensaje, BorderLayout.CENTER); // Añadir al panel inferior

        btnenviar = new JButton("Enviar"); // Botón de enviar
        btnenviar.addActionListener(this); // Agregar ActionListener
        panelInferior.add(btnenviar, BorderLayout.EAST); // Añadir al panel

        btnsalir = new JButton("Salir"); // Botón de salir
        btnsalir.addActionListener(this); // Agregar ActionListener
        add(btnsalir, BorderLayout.SOUTH); // Añadir al sur de la ventana

        add(panelInferior, BorderLayout.NORTH); // Añadir panel inferior al norte de la ventana

        setSize(500, 400); // Configurar tamaño de la ventana
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // No cerrar la aplicación al cerrar la ventana
        setVisible(true); // Hacer visible la ventana

        // Listener para desconectar al cerrar la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar(); // Desconectar al cerrar
            }
        });

        Thread hilo = new Thread(this); // Crear un hilo para escuchar mensajes del servidor
        hilo.start(); // Iniciar el hilo
    }
    //endregion

    //region Public Methods
    /**
     * Método ejecutado por el hilo para escuchar mensajes del servidor.
     */
    @Override
    public void run() {
        try {
            sc = new Socket(ip, puerto); // Crear socket y conectar al servidor
            DataInputStream in = new DataInputStream(sc.getInputStream()); // Stream para recibir datos
            out = new DataOutputStream(sc.getOutputStream()); // Stream para enviar datos

            // Enviar el nombre del cliente al servidor
            out.writeUTF(nombre);

            // Escuchar mensajes del servidor
            while (true) {
                String mensaje = in.readUTF(); // Leer mensajes del servidor
                txtmensajes.append(mensaje + "\n"); // Mostrar mensaje en el área de texto
            }
        } catch (IOException e) {
            txtmensajes.append("Conexión cerrada.\n"); // Mostrar mensaje de cierre de conexión
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnsalir) {
            desconectar(); // Desconectar si se presiona el botón salir
        } else if (e.getSource() == btnenviar) {
            enviarMensaje(); // Enviar mensaje si se presiona el botón enviar
        }
    }
    //endregion

    //region Private Methods
    /**
     * Envía un mensaje al servidor. Si el mensaje contiene "chao", se desconecta del chat.
     */
    private void enviarMensaje() {
        String mensaje = txtmensaje.getText().trim(); // Obtener texto del campo de mensaje
        if (!mensaje.isEmpty()) { // Verificar que el mensaje no esté vacío
            try {
                // Detectar "chao" en cualquier parte del mensaje y desconectar
                if (mensaje.toLowerCase().contains("chao")) {
                    desconectar(); // Desconectar si el mensaje contiene "chao"
                } else {
                    out.writeUTF(nombre + ": " + mensaje); // Enviar mensaje al servidor
                    txtmensaje.setText(""); // Limpiar el campo de mensaje
                }
            } catch (IOException ex) {
                txtmensajes.append("Error al enviar el mensaje.\n"); // Mostrar error al enviar
            }
        }
    }

    /**
     * Desconecta al cliente del servidor y cierra la ventana.
     */
    private void desconectar() {
        try {
            if (out != null) {
                out.writeUTF(nombre + " abandonó el chat."); // Notificar al servidor que el cliente se desconecta
            }
            if (sc != null && !sc.isClosed()) {
                sc.close(); // Cerrar socket
            }
            dispose(); // Cerrar la ventana
        } catch (IOException e) {
            txtmensajes.append("Error al desconectar: " + e.getMessage() + "\n"); // Mostrar error al desconectar
        }
    }

    /**
     * Genera un nombre aleatorio para el usuario en caso de que no se proporcione uno.
     *
     * @return Nombre aleatorio generado.
     */
    private String generarNombreAleatorio() {
        random = new Random(); // Inicializar Random
        int numeroAleatorio = random.nextInt(1000); // Genera un número aleatorio entre 0 y 999
        return "UsuarioDesconocido" + numeroAleatorio; // Retorna nombre con número aleatorio
    }
    //endregion

}
