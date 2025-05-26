package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CerrarPedidosView extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel pedidosModel;

    public CerrarPedidosView() {
        setupUI();
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

        setTitle("Cerrar Pedidos - Sushi Burrito");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Cerrar Pedidos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de pedidos activos
        String[] columnas = {"ID Pedido", "Mesa", "Productos", "Estado"};
        pedidosModel = new DefaultTableModel(columnas, 0);
        pedidosTable = new JTable(pedidosModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        scrollPane.setPreferredSize(new Dimension(850, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        botonesPanel.setOpaque(false);

        JButton btnMarcarPagado = new JButton("Marcar como Pagado");
        btnMarcarPagado.setBackground(new Color(0, 128, 0));
        btnMarcarPagado.setForeground(Color.WHITE);
        btnMarcarPagado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMarcarPagado.setPreferredSize(new Dimension(200, 40));

        JButton btnMarcarEntregado = new JButton("Marcar como Entregado");
        btnMarcarEntregado.setBackground(new Color(0, 102, 204));
        btnMarcarEntregado.setForeground(Color.WHITE);
        btnMarcarEntregado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMarcarEntregado.setPreferredSize(new Dimension(220, 40));

        botonesPanel.add(btnMarcarPagado);
        botonesPanel.add(btnMarcarEntregado);
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        // Acción: Marcar como Pagado (y remover de la tabla)
        btnMarcarPagado.addActionListener(e -> {
            int selectedRow = pedidosTable.getSelectedRow();
            if (selectedRow != -1) {
                String estado = (String) pedidosModel.getValueAt(selectedRow, 3);
                if (!estado.equalsIgnoreCase("Pagado")) {
                    pedidosModel.setValueAt("Pagado", selectedRow, 3);
                    JOptionPane.showMessageDialog(this, "Pedido marcado como pagado y archivado.");
                    pedidosModel.removeRow(selectedRow); // Simula archivado
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido para marcarlo como pagado.");
            }
        });

        // Acción: Marcar como Entregado (pero no se elimina)
        btnMarcarEntregado.addActionListener(e -> {
            int selectedRow = pedidosTable.getSelectedRow();
            if (selectedRow != -1) {
                pedidosModel.setValueAt("Entregado", selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Pedido marcado como entregado.");
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido para marcarlo como entregado.");
            }
        });

        // Botón volver al panel del mesero
        JButton btnVolver = new JButton("← Volver al Panel del Mesero");
        btnVolver.setBackground(new Color(255, 140, 0));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(250, 40));

        btnVolver.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView().setVisible(true);
        });

        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.setOpaque(false);
        volverPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        volverPanel.add(btnVolver);
        mainPanel.add(volverPanel, BorderLayout.NORTH);

        // Datos de prueba
        pedidosModel.addRow(new Object[]{"001", "Mesa 5", "2 Sushi Roll, 1 Té Verde", "Pendiente"});
        pedidosModel.addRow(new Object[]{"002", "Mesa 3", "1 Ramen, 1 Gyozas", "Pendiente"});
    }
}

