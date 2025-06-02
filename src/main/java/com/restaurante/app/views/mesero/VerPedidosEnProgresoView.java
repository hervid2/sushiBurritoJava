package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.database.PedidoDAO;
import main.java.com.restaurante.app.database.ProductoDAO;
import main.java.com.restaurante.app.models.Pedido;
import main.java.com.restaurante.app.models.DetallePedido;
import main.java.com.restaurante.app.models.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;

public class VerPedidosEnProgresoView extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO;
    private int usuarioId;

    public VerPedidosEnProgresoView(int usuarioId) {
        this.usuarioId = usuarioId;
        try {
            pedidoDAO = new PedidoDAO();
            productoDAO = new ProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        setupUI();
        loadPedidosEnProgreso();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (pedidoDAO != null) {
                    pedidoDAO.close();
                }
                if (productoDAO != null) {
                    productoDAO.close();
                }
            }
        });
    }

    private void loadPedidosEnProgreso() {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            List<Pedido> pedidosActivos = new ArrayList<>();
            pedidosActivos.addAll(pedidoDAO.obtenerPedidosPorEstado("pendiente"));
            pedidosActivos.addAll(pedidoDAO.obtenerPedidosPorEstado("preparando"));
            pedidosActivos.addAll(pedidoDAO.obtenerPedidosPorEstado("entregado"));

            for (Pedido pedido : pedidosActivos) {
                // *** CAMBIO CLAVE AQUÍ: Usar pedido.getProductosResumen() directamente ***
                String productosResumen = pedido.getProductosResumen(); // El resumen ya está cargado en el objeto Pedido
                double totalEstimado = calcularTotalEstimado(pedido.getPedidoId());

                String horaCreacion = (pedido.getFechaCreacion() != null) ?
                                       new java.text.SimpleDateFormat("HH:mm").format(pedido.getFechaCreacion()) : "N/A";

                tableModel.addRow(new Object[]{
                    pedido.getPedidoId(),
                    pedido.getMesa(),
                    "Mesero ID: " + pedido.getUsuarioId(),
                    pedido.getEstado(),
                    horaCreacion,
                    "$" + df.format(totalEstimado)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos en progreso: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private double calcularTotalEstimado(int pedidoId) throws SQLException {
        double total = 0.0;
        List<DetallePedido> detalles = pedidoDAO.obtenerDetallesPorPedidoId(pedidoId);
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDAO.obtenerProductoPorId(detalle.getProductoId());
            if (producto != null) {
                total += producto.getValorVenta() * detalle.getCantidad();
            }
        }
        return total;
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception e) {
            System.err.println("Error al configurar FlatLaf: " + e.getMessage());
        }

        setTitle("Pedidos en Progreso - Sushi Burrito");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Pedidos en Progreso", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 128, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"# Pedido", "Mesa", "Mesero", "Estado", "Hora", "Total Estimado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pedidosTable = new JTable(tableModel);
        pedidosTable.setRowHeight(28);
        pedidosTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pedidosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        pedidosTable.setSelectionBackground(new Color(204, 255, 204));

        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton backButton = new JButton("Volver");
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setBackground(new Color(255, 140, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView(usuarioId).setVisible(true);
        });

        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
}
