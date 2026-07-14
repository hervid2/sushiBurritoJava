package com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import com.restaurante.app.config.SpringContext;
import com.restaurante.app.models.Order;
import com.restaurante.app.models.OrderItem;
import com.restaurante.app.models.Product;
import com.restaurante.app.service.OrderService;
import com.restaurante.app.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class VerPedidosEnProgresoView extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private OrderService orderService;
    private ProductService productService;
    private int usuarioId;

    public VerPedidosEnProgresoView(int usuarioId) {
        this.usuarioId = usuarioId;
        try {
            orderService = SpringContext.getBean(OrderService.class);
            productService = SpringContext.getBean(ProductService.class);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        setupUI();
        loadPedidosEnProgreso();
    }

    private void loadPedidosEnProgreso() {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            List<Order> pedidosActivos = new ArrayList<>();
            pedidosActivos.addAll(orderService.findOrdersByStatus("pendiente"));
            pedidosActivos.addAll(orderService.findOrdersByStatus("preparando"));
            pedidosActivos.addAll(orderService.findOrdersByStatus("entregado"));

            for (Order pedido : pedidosActivos) {
                // El resumen de productos ya está cargado en el objeto Order
                String productosResumen = pedido.getProductSummary();
                double totalEstimado = calcularTotalEstimado(pedido.getId());

                String horaCreacion = (pedido.getCreatedAt() != null) ?
                                       pedido.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";

                tableModel.addRow(new Object[]{
                    pedido.getId(),
                    pedido.getTableNumber(),
                    "Mesero ID: " + pedido.getUserId(),
                    pedido.getStatus(),
                    horaCreacion,
                    "$" + df.format(totalEstimado)
                });
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos en progreso: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private double calcularTotalEstimado(int pedidoId) {
        double total = 0.0;
        List<OrderItem> detalles = orderService.findItemsByOrderId(pedidoId);
        for (OrderItem detalle : detalles) {
            Product producto = productService.findProductById(detalle.getProductId());
            if (producto != null) {
                total += producto.getSalePrice() * detalle.getQuantity();
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
