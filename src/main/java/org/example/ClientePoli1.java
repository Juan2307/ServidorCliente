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
import java.net.Socket;
import java.util.Random;

public class ClientePoli1 extends JFrame implements ActionListener, Runnable {

    private JTextField txtmensaje;
    private JButton btnenviar, btnsalir;
    private JTextArea txtmensajes;

    private Socket sc = null;

    private String ip;
    private int puerto;
    private String nombre;
    private DataOutputStream out;
    private Random random;

    public ClientePoli1(String ip, int puerto, String nombre) {
        this.ip = ip;
        this.puerto = puerto;

        this.nombre = (nombre == null || nombre.isEmpty()) ? generarNombreAleatorio() : nombre;

        setTitle(this.nombre + " - Chat Cliente #1");
        setLayout(new BorderLayout());

        txtmensajes = new JTextArea();
        txtmensajes.setEditable(false);
        txtmensajes.setLineWrap(true);
        JScrollPane scrollMensajes = new JScrollPane(txtmensajes);
        add(scrollMensajes, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        txtmensaje = new JTextField();
        panelInferior.add(txtmensaje, BorderLayout.CENTER);

        btnenviar = new JButton("Procesar");
        btnenviar.addActionListener(this);
        panelInferior.add(btnenviar, BorderLayout.EAST);

        btnsalir = new JButton("Salir");
        btnsalir.addActionListener(this);
        add(btnsalir, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.NORTH);

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar();
            }
        });

        // Mensaje por defecto y menú inicial
        mostrarMenu();

        Thread hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        try {
            sc = new Socket(ip, puerto);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            out.writeUTF(nombre);

            while (true) {
                String mensaje = in.readUTF();
                txtmensajes.append(mensaje + "\n");
            }
        } catch (IOException e) {
            txtmensajes.append("Conexión cerrada.\n");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnsalir) {
            desconectar();
        } else if (e.getSource() == btnenviar) {
            procesarComando();
        }
    }

    private void procesarComando() {
        String mensaje = txtmensaje.getText().trim().toLowerCase();
        if (!mensaje.isEmpty()) {
            try {
                if (mensaje.equals("5")) {
                    txtmensajes.append("Saliendo del sistema...\n");
                    desconectar();
                } else if (mensaje.equals("1")) {
                    txtmensajes.append("Opción seleccionada: Insertar un empleado.\n");
                    // Aquí iría la lógica para insertar un empleado
                    out.writeUTF("INSERTAR_EMPLEADO");
                } else if (mensaje.equals("2")) {
                    txtmensajes.append("Opción seleccionada: Actualizar los datos de un empleado.\n");
                    // Aquí iría la lógica para actualizar un empleado
                    out.writeUTF("ACTUALIZAR_EMPLEADO");
                } else if (mensaje.equals("3")) {
                    txtmensajes.append("Opción seleccionada: Consultar un empleado.\n");
                    // Aquí iría la lógica para consultar un empleado
                    out.writeUTF("CONSULTAR_EMPLEADO");
                } else if (mensaje.equals("4")) {
                    txtmensajes.append("Opción seleccionada: Borrar un empleado.\n");
                    // Aquí iría la lógica para borrar un empleado
                    out.writeUTF("BORRAR_EMPLEADO");
                } else {
                    txtmensajes.append("Opción inválida. Por favor, intenta nuevamente.\n");
                }
                txtmensaje.setText("");
            } catch (IOException ex) {
                txtmensajes.append("Error al procesar el comando.\n");
            }
        }
    }

    private void mostrarMenu() {
        txtmensajes.append("Bienvenido al Sistema de Gestión de Empleados.\n");
        txtmensajes.append("Por favor, selecciona una opción:\n");
        txtmensajes.append("1. Insertar -> Insertar un empleado en la base de datos.\n");
        txtmensajes.append("2. Update -> Actualizar los datos de un empleado en la base de datos.\n");
        txtmensajes.append("3. Select -> Consultar un empleado en la base de datos.\n");
        txtmensajes.append("4. Delete -> Borrar un empleado de la base de datos (y agregarlo a históricos).\n");
        txtmensajes.append("5. Salir del sistema.\n");
        txtmensajes.append("-----------------------------------------------------------------------------------------------------------------\n\n");
    }

    private void desconectar() {
        try {
            if (out != null) {
                out.writeUTF(nombre + " abandonó el sistema.");
            }
            if (sc != null && !sc.isClosed()) {
                sc.close();
            }
            dispose();
        } catch (IOException e) {
            txtmensajes.append("Error al desconectar: " + e.getMessage() + "\n");
        }
    }

    private String generarNombreAleatorio() {
        random = new Random();
        int numeroAleatorio = random.nextInt(1000);
        return "UsuarioDesconocido" + numeroAleatorio;
    }

}

