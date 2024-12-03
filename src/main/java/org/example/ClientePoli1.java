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
import java.sql.*;
import java.time.LocalDate;
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

    private Connection connection;
    private String dbUrl = "jdbc:sqlserver://JUAN\\SQLEXPRESS:1433;databaseName=DATOS;user=sa;password=123456;integratedSecurity=false;encrypt=false;";

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
                    insertarEmpleado();
                } else if (mensaje.equals("2")) {
                    txtmensajes.append("Opción seleccionada: Consultar un empleado.\n");
                    consultarEmpleado();
                } else if (mensaje.equals("3")) {
                    txtmensajes.append("Opción seleccionada: Eliminar un empleado.\n");
                    eliminarEmpleado();
                } else {
                    txtmensajes.append("Opción inválida. Por favor, intenta nuevamente.\n");
                }
                txtmensaje.setText("");
            } catch (Exception ex) {
                txtmensajes.append("Error al procesar el comando.\n");
            }
        }
    }

    private void mostrarMenu() {
        txtmensajes.append("Bienvenido al Sistema de Gestión de Empleados.\n");
        txtmensajes.append("Por favor, selecciona una opción:\n");
        txtmensajes.append("1. Insertar -> Insertar un empleado en la base de datos.\n");
        txtmensajes.append("2. Select -> Consultar un empleado en la base de datos.\n");
        txtmensajes.append("3. Delete -> Borrar un empleado de la base de datos, previa inserción de la tabla Históricos.\n");
        txtmensajes.append("5. Salir del sistema.\n");
        txtmensajes.append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ஜ۩۞۩ஜ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n\n\n");
    }

    private void insertarEmpleado() {
        try {
            // Obtener datos del empleado
            String primerNombre = JOptionPane.showInputDialog("Ingrese el nombre del empleado:");
            String segundoNombre = JOptionPane.showInputDialog("Ingrese el apellido del empleado:");
            String email = JOptionPane.showInputDialog("Ingrese el correo electrónico del empleado:");
            String fechaNacimiento = JOptionPane.showInputDialog("Ingrese la fecha de nacimiento del empleado (yyyy-mm-dd):");
            double sueldo = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el sueldo del empleado:"));
            double comision = Double.parseDouble(JOptionPane.showInputDialog("Ingrese la comisión del empleado:"));
            int cargoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del cargo del empleado:"));
            int gerenteID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del gerente del empleado:"));
            int departamentoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del departamento del empleado:"));

            // Establecer conexión a la base de datos
            connection = DriverManager.getConnection(dbUrl);

            String query = "INSERT INTO EMPLEADOS (empl_nombre, empl_apellido, empl_email, empl_fecha_nac, empl_sueldo, empl_comision, empl_cargo_ID, empl_Gerente_ID, empl_dpto_ID, empl_estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, primerNombre);
            stmt.setString(2, segundoNombre);
            stmt.setString(3, email);
            stmt.setString(4, fechaNacimiento);
            stmt.setDouble(5, sueldo);
            stmt.setDouble(6, comision);
            stmt.setInt(7, cargoID);
            stmt.setInt(8, gerenteID);
            stmt.setInt(9, departamentoID);
            stmt.setString(10, "ACTIVO");

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                txtmensajes.append("Empleado insertado correctamente.\n");
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");

                // Enviar al servidor el mensaje de inserción
                out.writeUTF("han realizado una operacion de inserción de registro con ID: " + cargoID);
            }

            stmt.close();
            connection.close();

        } catch (SQLException ex) {
            txtmensajes.append("Error al insertar empleado: " + ex.getMessage() + "\n");
            txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
        } catch (IOException ex) {
            txtmensajes.append("Error al enviar mensaje al servidor: " + ex.getMessage() + "\n");
        }
    }

    private void consultarEmpleado() {
        try {
            // Obtener ID del empleado
            int empleadoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del empleado a consultar:"));

            // Enviar al servidor el mensaje de consulta
            out.writeUTF("han realizado una operacion de consulta para el ID: " + empleadoID);

            // Establecer conexión a la base de datos
            connection = DriverManager.getConnection(dbUrl);

            String query = "SELECT * FROM EMPLEADOS WHERE empl_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, empleadoID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String empleadoInfo = "Empleado encontrado: \n";
                empleadoInfo += "ID: " + rs.getInt("empl_ID") + "\n";
                empleadoInfo += "Nombre: " + rs.getString("empl_nombre") + " " + rs.getString("empl_apellido") + "\n";
                empleadoInfo += "Email: " + rs.getString("empl_email") + "\n";
                empleadoInfo += "Fecha de Nacimiento: " + rs.getString("empl_fecha_nac") + "\n";
                empleadoInfo += "Sueldo: " + rs.getDouble("empl_sueldo") + "\n";
                empleadoInfo += "Comisión: " + rs.getDouble("empl_comision") + "\n";
                empleadoInfo += "Estado: " + rs.getString("empl_estado") + "\n";
                txtmensajes.append(empleadoInfo);
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
            } else {
                txtmensajes.append("Empleado no encontrado.\n");
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
            }

            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException ex) {
            txtmensajes.append("Error al consultar empleado: " + ex.getMessage() + "\n");
            txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
        } catch (IOException ex) {
            txtmensajes.append("Error al enviar mensaje al servidor: " + ex.getMessage() + "\n");
        }
    }

    private void eliminarEmpleado() {

        try {
            // Obtener ID del empleado
            int empleadoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del empleado a eliminar:"));

            // Enviar al servidor el mensaje de consulta
            out.writeUTF("han realizado una operacion de eliminacion para el ID: " + empleadoID);

            // Establecer conexión a la base de datos
            connection = DriverManager.getConnection(dbUrl);

            String query = "UPDATE EMPLEADOS SET empl_estado = 'INACTIVO' WHERE empl_ID = ?";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, empleadoID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {

                String query2 = "SELECT * FROM EMPLEADOS WHERE empl_ID = ?";
                PreparedStatement stmt2 = connection.prepareStatement(query2);
                stmt2.setInt(1, empleadoID);
                ResultSet rs = stmt2.executeQuery();

                LocalDate today = LocalDate.now();
                java.sql.Date sqlDate = java.sql.Date.valueOf(today);

                String query3 = "INSERT INTO HISTORICO (emphist_fecha_retiro, emphist_cargo_ID, emphist_dpto_ID, emphist_empleado_ID) " + "VALUES (?, ?, ?, ?)";
                PreparedStatement stmt3 = connection.prepareStatement(query3);
                stmt3.setDate(1, sqlDate);
                stmt3.setInt(2, 1);
                stmt3.setInt(3, 1);
                stmt3.setInt(4, 3);
//
//                stmt3.setInt(2, rs.getInt("empl_cargo_ID"));
//                stmt3.setInt(3, rs.getInt("empl_dpto_ID"));
//                stmt3.setInt(4, rs.getInt("empl_ID"));

                int rowsAffected2 = stmt3.executeUpdate();
                if (rowsAffected2 > 0) {

                    txtmensajes.append("Empleado eliminado correctamente.\n");
                    txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");

                    // Enviar al servidor el mensaje de inserción
                    out.writeUTF("han realizado una operacion de eliminacion de registro con ID: " + empleadoID);

                }

            } else {
                txtmensajes.append("Empleado no eliminado correctamente.\n");
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
            }

            stmt.close();
            connection.close();

        } catch (SQLException ex) {
            txtmensajes.append("Error al eliminar empleado: " + ex.getMessage() + "\n");
            txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
        } catch (IOException ex) {
            txtmensajes.append("Error al enviar mensaje al servidor: " + ex.getMessage() + "\n");
        }

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
