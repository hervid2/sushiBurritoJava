package main.java.com.restaurante.app.views.mesero;
 
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EditarPedidoView extends JFrame {

    private JTable tablaPedido;
    private DefaultTableModel tableModel;

    public EditarPedidoView() {
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

        setTitle("Editar Pedido - Sushi Burrito");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Editar Pedido", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Selector de pedido
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        selectorPanel.setOpaque(false);
        selectorPanel.setMaximumSize(new Dimension(900, 40));

        JLabel lblPedido = new JLabel("Selecciona Pedido:");
        lblPedido.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JComboBox<String> comboPedidos = new JComboBox<>(new String[]{"Pedido #101", "Pedido #102", "Pedido #103"});
        comboPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboPedidos.setPreferredSize(new Dimension(200, 30));

        selectorPanel.add(lblPedido);
        selectorPanel.add(comboPedidos);
        centerPanel.add(selectorPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Tabla de productos del pedido
        tableModel = new DefaultTableModel(new Object[]{"Producto", "Categoría", "Cantidad", "Notas", "Mesa"}, 0);
        tablaPedido = new JTable(tableModel);
        JScrollPane scrollTabla = new JScrollPane(tablaPedido);
        scrollTabla.setPreferredSize(new Dimension(900, 200));
        scrollTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(scrollTabla);
        centerPanel.add(Box.createVerticalStrut(20));

        // Panel de edición
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        inputPanel.setOpaque(false);
        inputPanel.setMaximumSize(new Dimension(700, 150));

        JLabel lblProducto = new JLabel("Producto:");
        JComboBox<String> comboProducto = new JComboBox<>(new String[]{
                "Sushi Roll", "Tempura", "Ramen", "Yakimeshi", "Té Verde",
                "Cerveza", "Agua", "Helado", "Brownie", "Guacamole", "Gyozas"
        });
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblCategoria = new JLabel("Categoría:");
        JComboBox<String> comboCategoria = new JComboBox<>(new String[]{
                "Infantil", "Bebidas", "Postres", "Entradas", "Platos Fuertes","Acompañamientos"
        });
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblCantidad = new JLabel("Cantidad:");
        JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spinnerCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblNotas = new JLabel("Notas:");
        JTextField txtNotas = new JTextField();
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        inputPanel.add(lblProducto);
        inputPanel.add(comboProducto);
        inputPanel.add(lblCategoria);
        inputPanel.add(comboCategoria);
        inputPanel.add(lblCantidad);
        inputPanel.add(spinnerCantidad);
        inputPanel.add(lblNotas);
        inputPanel.add(txtNotas);
        centerPanel.add(inputPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Botones de acción
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        botonesPanel.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBackground(new Color(255, 140, 0));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.setPreferredSize(new Dimension(200, 40));

        JButton btnModificar = new JButton("Modificar Producto");
        btnModificar.setBackground(new Color(0, 128, 0));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnModificar.setPreferredSize(new Dimension(200, 40));

        JButton btnEliminar = new JButton("Eliminar Producto");
        btnEliminar.setBackground(new Color(255, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEliminar.setPreferredSize(new Dimension(200, 40));

        botonesPanel.add(btnAgregar);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnEliminar);
        centerPanel.add(botonesPanel);

        // Acciones de botones
        btnAgregar.addActionListener(e -> {
            String producto = (String) comboProducto.getSelectedItem();
            String categoria = (String) comboCategoria.getSelectedItem();
            int cantidad = (Integer) spinnerCantidad.getValue();
            String notas = txtNotas.getText();
            // Sin selector de mesa, se puede setear vacío o valor por defecto
            String mesa = "";
            tableModel.addRow(new Object[]{producto, categoria, cantidad, notas, mesa});
            txtNotas.setText("");
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tablaPedido.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt(comboProducto.getSelectedItem(), selectedRow, 0);
                tableModel.setValueAt(comboCategoria.getSelectedItem(), selectedRow, 1);
                tableModel.setValueAt(spinnerCantidad.getValue(), selectedRow, 2);
                tableModel.setValueAt(txtNotas.getText(), selectedRow, 3);
                // Mantener el valor de mesa existente en la tabla
                txtNotas.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila para modificar.");
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tablaPedido.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar.");
            }
        });

        // Botón guardar cambios
        JButton btnConfirmar = new JButton("Guardar Cambios");
        btnConfirmar.setBackground(new Color(0, 102, 204));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirmar.setPreferredSize(new Dimension(220, 40));
        btnConfirmar.addActionListener(e -> JOptionPane.showMessageDialog(this, "¡Cambios guardados correctamente!"));

        JPanel panelConfirmar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelConfirmar.setOpaque(false);
        panelConfirmar.add(btnConfirmar);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(panelConfirmar);

        // Botón volver al Panel del Mesero
        JButton btnVolver = new JButton("← Volver al Panel del Mesero");
        btnVolver.setBackground(new Color(255, 140, 0));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(250, 40));
        btnVolver.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView().setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomPanel.add(btnVolver);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
}
