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

/**
 * ClientePoli1: Aplicación cliente para gestionar empleados mediante una interfaz gráfica
 * que interactúa con un servidor y una base de datos.
 */
public class ClientePoli1 extends JFrame implements ActionListener, Runnable {

    //region Private Fields

    // Componentes de la interfaz gráfica
    private JTextField txtmensaje;
    private JButton btnenviar, btnsalir;
    private JTextArea txtmensajes;

    // Conexión de red
    private Socket sc = null;
    private String ip;
    private int puerto;
    private String nombre;
    private DataOutputStream out;

    // Variables auxiliares y configuración de base de datos
    private Random random;
    private Connection connection;
    private String dbUrl = "jdbc:sqlserver://JUAN\\SQLEXPRESS:1433;databaseName=DATOS;user=sa;password=123456;integratedSecurity=false;encrypt=false;";

    //endregion

    //region Constructor

    /**
     * Constructor de la clase ClientePoli1.
     *
     * @param ip     Dirección IP del servidor.
     * @param puerto Puerto del servidor.
     * @param nombre Nombre del cliente.
     */
    public ClientePoli1(String ip, int puerto, String nombre) {

        // Inicialización de variables
        this.ip = ip;
        this.puerto = puerto;
        this.nombre = (nombre == null || nombre.isEmpty()) ? generarNombreAleatorio() : nombre;

        // Configuración de la ventana principal
        setTitle(this.nombre + " - Chat Cliente #1");
        setLayout(new BorderLayout());

        // Área de mensajes
        txtmensajes = new JTextArea();
        txtmensajes.setEditable(false);
        txtmensajes.setLineWrap(true);
        JScrollPane scrollMensajes = new JScrollPane(txtmensajes);
        add(scrollMensajes, BorderLayout.CENTER);

        // Panel para enviar mensajes
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

        // Configuración final de la ventana
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        // Manejo del cierre de la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar();
            }
        });

        // Mostrar menú inicial
        mostrarMenu();

        // Iniciar el hilo para gestionar mensajes entrantes
        Thread hilo = new Thread(this);
        hilo.start();

    }

    /**
     * Método ejecutado por el hilo para recibir mensajes del servidor.
     */
    @Override
    public void run() {

        try {

            sc = new Socket(ip, puerto);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            // Enviar el nombre del cliente al servidor
            out.writeUTF(nombre);

            // Escuchar mensajes entrantes
            while (true) {

                String mensaje = in.readUTF();
                txtmensajes.append(mensaje + "\n");

            }

        } catch (IOException e) {

            txtmensajes.append("Conexión cerrada.\n");

        }

    }

    /**
     * Manejo de acciones realizadas en los botones.
     *
     * @param e Evento de acción.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnsalir)
            desconectar();
        else if (e.getSource() == btnenviar)
            procesarComando();

    }

    /**
     * Procesa los comandos ingresados por el usuario.
     */
    private void procesarComando() {

        String mensaje = txtmensaje.getText().trim().toLowerCase();
        if (!mensaje.isEmpty()) {

            try {

                switch (mensaje) {

                    case "1":
                        txtmensajes.append("Opción seleccionada: Insertar un empleado.\n");
                        insertarEmpleado();
                        break;
                    case "2":
                        txtmensajes.append("Opción seleccionada: Modificar un empleado.\n");
                        modificarEmpleado();
                        break;
                    case "3":
                        txtmensajes.append("Opción seleccionada: Consultar un empleado.\n");
                        consultarEmpleado();
                        break;
                    case "4":
                        txtmensajes.append("Opción seleccionada: Eliminar un empleado.\n");
                        eliminarEmpleado();
                        break;
                    case "5":
                        txtmensajes.append("Saliendo del sistema...\n");
                        desconectar();
                        break;
                    default:
                        txtmensajes.append("Opción inválida. Por favor, intenta nuevamente.\n");

                }

                txtmensaje.setText("");

            } catch (Exception ex) {

                txtmensajes.append("Error al procesar el comando.\n");

            }

        }

    }

    //endregion

    //region Public Methods

    /**
     * Muestra el menú inicial en el área de mensajes.
     */
    private void mostrarMenu() {

        txtmensajes.append("Bienvenido al Sistema de Gestión de Empleados.\n");
        txtmensajes.append("Por favor, selecciona una opción:\n");
        txtmensajes.append("1. Insertar -> Insertar un empleado en la base de datos.\n");
        txtmensajes.append("2. Update -> Modificar un empleado en la base de datos.\n");
        txtmensajes.append("3. Select -> Consultar un empleado en la base de datos.\n");
        txtmensajes.append("4. Delete -> Borrar un empleado de la base de datos.\n");
        txtmensajes.append("5. Salir del sistema.\n");
        txtmensajes.append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ஜ۩۞۩ஜ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n\n\n");

    }

    private void insertarEmpleado() throws IOException {

        try {

            out.writeUTF("han realizado una operacion de inserción de registro");

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

            connection = DriverManager.getConnection(dbUrl);

            String query = "INSERT INTO EMPLEADOS " +
                    "(empl_nombre, empl_apellido, empl_email, empl_fecha_nac, empl_sueldo, empl_comision, empl_cargo_ID, empl_Gerente_ID, empl_dpto_ID, empl_estado) " +
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
                out.writeUTF("Operacion registro con exito");

            } else {

                out.writeUTF("Operacion registro sin exito");

            }

            stmt.close();
            connection.close();

        } catch (SQLException ex) {

            out.writeUTF("Operacion registro sin exito");
            txtmensajes.append("Error al insertar empleado: " + ex.getMessage() + "\n");
            txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");

        } catch (IOException ex) {

            txtmensajes.append("Error al enviar mensaje al servidor: " + ex.getMessage() + "\n");

        }

    }

    private void modificarEmpleado() throws IOException {

        Connection connection = null;
        PreparedStatement stmt = null;
        PreparedStatement updateStmt = null;

        try {

            // Obtener ID del empleado
            int empleadoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del empleado a modificar:"));

            out.writeUTF("Han realizado una operación de modificación para el ID: " + empleadoID);

            // Establecer conexión a la base de datos
            connection = DriverManager.getConnection(dbUrl);

            // Consultar si el empleado existe
            String query = "SELECT * FROM EMPLEADOS WHERE empl_ID = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, empleadoID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                // Solicitar datos del empleado
                String primerNombre = JOptionPane.showInputDialog("Ingrese el nombre del empleado:", rs.getString("empl_nombre"));
                String segundoNombre = JOptionPane.showInputDialog("Ingrese el apellido del empleado:", rs.getString("empl_apellido"));
                String email = JOptionPane.showInputDialog("Ingrese el correo electrónico del empleado:", rs.getString("empl_email"));
                String fechaNacimiento = JOptionPane.showInputDialog("Ingrese la fecha de nacimiento del empleado (yyyy-mm-dd):", rs.getString("empl_fecha_nac"));
                double sueldo = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el sueldo del empleado:", rs.getDouble("empl_sueldo")));
                double comision = Double.parseDouble(JOptionPane.showInputDialog("Ingrese la comisión del empleado:", rs.getDouble("empl_comision")));
                int cargoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del cargo del empleado:", rs.getInt("empl_cargo_id")));
                int gerenteID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del gerente del empleado:", rs.getInt("empl_gerente_id")));
                int departamentoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del departamento del empleado:", rs.getInt("empl_dpto_id")));
                String estado = JOptionPane.showInputDialog("Ingrese el estado del empleado (ACTIVO O INACTIVO):", rs.getString("empl_estado"));

                // Preparar la consulta de actualización
                String updateQuery = "UPDATE EMPLEADOS " +
                        "SET empl_nombre = ?, empl_apellido = ?, empl_email = ?, " +
                        "empl_fecha_nac = ?, empl_sueldo = ?, empl_comision = ?, empl_cargo_id = ?, " +
                        "empl_gerente_id = ?, empl_dpto_id = ?, empl_estado = ? WHERE empl_ID = ?";

                updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, primerNombre);
                updateStmt.setString(2, segundoNombre);
                updateStmt.setString(3, email);
                updateStmt.setString(4, fechaNacimiento);
                updateStmt.setDouble(5, sueldo);
                updateStmt.setDouble(6, comision);
                updateStmt.setInt(7, cargoID);
                updateStmt.setInt(8, gerenteID);
                updateStmt.setInt(9, departamentoID);
                updateStmt.setString(10, estado);
                updateStmt.setInt(11, empleadoID);

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {

                    txtmensajes.append("Empleado modificado correctamente.\n");
                    txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
                    out.writeUTF("Operacion modificar con exito");

                } else {

                    txtmensajes.append("No se pudo modificar el empleado. Verifique los datos.\n");
                    out.writeUTF("Operacion modificar sin exito");

                }

            } else {

                txtmensajes.append("Empleado no encontrado.\n");
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
                out.writeUTF("Operacion modificar sin exito");

            }

        } catch (SQLException ex) {

            txtmensajes.append("Error al modificar empleado: " + ex.getMessage() + "\n");
            txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");

        } catch (IOException ex) {

            out.writeUTF("Operacion registro sin exito");
            txtmensajes.append("Error al enviar mensaje al servidor: " + ex.getMessage() + "\n");

        }

    }

    private void consultarEmpleado() {

        try {

            // Obtener ID del empleado
            int empleadoID = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del empleado a consultar:"));

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
                out.writeUTF("Consulta Exitosa");

            } else {

                txtmensajes.append("Empleado no encontrado.\n");
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
                out.writeUTF("Consulta Fallida");

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
                    out.writeUTF("Operacion eliminar con exitosa");

                } else {

                    txtmensajes.append("Empleado no eliminado(Historico) correctamente.\n");
                    txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
                    out.writeUTF("Operacion eliminar sin exito");

                }

            } else {

                txtmensajes.append("Empleado no eliminado(cambio de estado) correctamente.\n");
                txtmensajes.append("-------------------------------------------------------------------------------------------------------------------\n\n\n");
                out.writeUTF("Operacion eliminar sin exito");

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

    /**
     * Desconecta al cliente del servidor y cierra la ventana.
     */
    private void desconectar() {

        try {

            if (out != null)
                out.writeUTF(nombre + " abandonó el chat."); // Notificar al servidor que el cliente se desconecta

            if (sc != null && !sc.isClosed())
                sc.close(); // Cerrar socket

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
