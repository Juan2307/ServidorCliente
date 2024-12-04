package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase que representa la configuración del Cliente #1 de la aplicación de chat.
 * Permite al usuario ingresar la IP, puerto y nombre antes de conectarse.
 */
public class ConfiguracionClientePoli1 extends JFrame implements ActionListener {

    //region Private Fields

    private JTextField txtIP, txtPuerto, txtNombre; // Campos de entrada para IP, puerto y nombre
    private JButton btnConectar; // Botón para conectar al servidor
    private ClientePoli1 clientePoli1; // Instancia del ClientePoli1

    //endregion

    //region Constructor

    /**
     * Constructor que inicializa la ventana de configuración del Cliente #1.
     */
    public ConfiguracionClientePoli1() {

        // Crear campos y botón
        txtIP = new JTextField("127.0.0.1"); // IP predeterminada (localhost)
        txtPuerto = new JTextField("5000");  // Puerto predeterminado
        txtNombre = new JTextField();        // Nombre del usuario
        btnConectar = new JButton("Conectar");

        // Posicionar componentes
        txtIP.setBounds(10, 10, 200, 20);
        txtPuerto.setBounds(10, 40, 200, 20);
        txtNombre.setBounds(10, 70, 200, 20);
        btnConectar.setBounds(10, 100, 200, 30);

        // Etiquetas para los campos
        add(new JLabel("IP del Servidor:")).setBounds(220, 10, 100, 20);
        add(new JLabel("Puerto de Conexión:")).setBounds(220, 40, 100, 20);
        add(new JLabel("Nombre de Usuario:")).setBounds(220, 70, 100, 20);

        // Agregar ActionListener al botón de conectar
        btnConectar.addActionListener(this);

        // Agregar componentes a la ventana
        add(txtIP);
        add(txtPuerto);
        add(txtNombre);
        add(btnConectar);

        // Configuración de la ventana
        setLayout(null);
        setSize(350, 200);
        setTitle("Configuración del Cliente #1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    //endregion

    //region Public Methods

    /**
     * Maneja las acciones de los eventos de los componentes.
     *
     * @param e El evento de acción.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnConectar) {

            // Obtener datos de configuración
            String ip = txtIP.getText();
            int puerto = Integer.parseInt(txtPuerto.getText());
            String nombre = txtNombre.getText();

            // Crear instancia de ClientePoli1 con los datos de configuración
            clientePoli1 = new ClientePoli1(ip, puerto, nombre);

            // Cerrar la ventana de configuración
            this.dispose();

        }

    }

    //endregion

}
