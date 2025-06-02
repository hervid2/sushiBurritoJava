package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.database.PedidoDAO;
import main.java.com.restaurante.app.models.Pedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class CancelarPedidoView extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel pedidosModel;
    private PedidoDAO pedidoDAO; // Instancia del DAO
    private int usuarioId; // Para volver al panel del mesero con el ID correcto

    public CancelarPedidoView(int usuarioId) { // Añadir usuarioId al constructor
        this.usuarioId = usuarioId;
        try {
            pedidoDAO = new PedidoDAO(); // Inicializar el DAO
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Considera si quieres deshabilitar funcionalidades o salir si no hay conexión
        }
        setupUI();
        loadPedidosParaCancelar(); // Cargar pedidos al inicio

        // Asegurarse de cerrar la conexión al cerrar la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (pedidoDAO != null) {
                    pedidoDAO.close();
                }
            }
        });
    }

    private void loadPedidosParaCancelar() {
        pedidosModel.setRowCount(0); // Limpiar tabla
        try {
            // Obtener pedidos que pueden ser cancelados (ej. Pendiente o Preparando)
            List<Pedido> pedidosPendientes = pedidoDAO.obtenerPedidosPorEstado("pendiente");
            List<Pedido> pedidosPreparando = pedidoDAO.obtenerPedidosPorEstado("preparando");

            // Combinar y mostrar en la tabla
            for (Pedido p : pedidosPendientes) {
                String productosResumen = p.getProductosResumen();
                pedidosModel.addRow(new Object[]{p.getPedidoId(), p.getMesa(), productosResumen, p.getEstado()});
            }
            for (Pedido p : pedidosPreparando) {
                String productosResumen = p.getProductosResumen();
                pedidosModel.addRow(new Object[]{p.getPedidoId(), p.getMesa(), productosResumen, p.getEstado()});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos para cancelar: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception ex) {
            System.err.println("Error al configurar FlatLaf: " + ex.getMessage());
        }

        setTitle("Cancelar Pedido - Sushi Burrito");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Usar DISPOSE_ON_CLOSE

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Cancelar Pedido", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de pedidos
        pedidosModel = new DefaultTableModel(new String[]{"ID Pedido", "Mesa", "Productos", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        pedidosTable = new JTable(pedidosModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        scrollPane.setPreferredSize(new Dimension(800, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón Cancelar Pedido centrado
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        botonesPanel.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar Pedido");
        btnCancelar.setBackground(new Color(220, 20, 60)); // Rojo carmesí
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancelar.setPreferredSize(new Dimension(200, 40));

        botonesPanel.add(btnCancelar);
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        // Acción de cancelar pedido
        btnCancelar.addActionListener(e -> {
            int selectedRow = pedidosTable.getSelectedRow();
            if (selectedRow != -1) {
                int pedidoId = (int) pedidosModel.getValueAt(selectedRow, 0); // Obtener ID del pedido
                String estadoActual = (String) pedidosModel.getValueAt(selectedRow, 3);

                if (estadoActual.equalsIgnoreCase("pendiente") || estadoActual.equalsIgnoreCase("preparando")) {
                    int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres cancelar el Pedido #" + pedidoId + "?", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            pedidoDAO.actualizarEstadoPedido(pedidoId, "cancelado");
                            JOptionPane.showMessageDialog(this, "Pedido #" + pedidoId + " cancelado exitosamente.");
                            loadPedidosParaCancelar(); // Recargar la tabla para reflejar el cambio
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(this, "Error al cancelar pedido en la DB: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se puede cancelar un pedido que ya está " + estadoActual + ".", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido para cancelarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Botón Volver al Panel del Mesero
        JButton btnVolver = new JButton("← Volver al Panel del Mesero");
        btnVolver.setBackground(new Color(255, 140, 0));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(250, 40));

        btnVolver.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView(usuarioId).setVisible(true); // Pasa el usuarioId
        });

        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.setOpaque(false);
        volverPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        volverPanel.add(btnVolver);
        mainPanel.add(volverPanel, BorderLayout.NORTH); // Añadir al norte para que esté arriba a la izquierda
    }
}