package main.java.com.restaurante.app.views.mesero;
 
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GenerarComandaView extends JFrame {

    private JTable resumenTable;
    private DefaultTableModel tableModel;

    public GenerarComandaView() {
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

        setTitle("Generar Comanda - Sushi Burrito");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Generar Comanda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Selector de mesa
        JPanel mesaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mesaPanel.setOpaque(false);
        JLabel lblMesa = new JLabel("Mesa:");
        lblMesa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JComboBox<String> comboMesa = new JComboBox<>();
        for (int i = 1; i <= 20; i++) {
            comboMesa.addItem(String.valueOf(i));
        }
        comboMesa.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboMesa.setPreferredSize(new Dimension(100, 30));
        mesaPanel.add(lblMesa);
        mesaPanel.add(comboMesa);
        centerPanel.add(mesaPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Tabla de resumen con nueva columna "Notas"
        tableModel = new DefaultTableModel(new Object[]{"Producto", "Categoría", "Cantidad", "Notas"}, 0);
        resumenTable = new JTable(tableModel);
        JScrollPane scrollResumen = new JScrollPane(resumenTable);
        scrollResumen.setPreferredSize(new Dimension(900, 200));
        scrollResumen.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(scrollResumen);
        centerPanel.add(Box.createVerticalStrut(20));

        // Panel de selección
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

        // Botones centrados
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        botonesPanel.setOpaque(false);

        JButton btnAgregar = new JButton("Agregar a la Comanda");
        btnAgregar.setBackground(new Color(255, 140, 0));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.setPreferredSize(new Dimension(200, 40));

        JButton btnConfirmar = new JButton("Confirmar Comanda");
        btnConfirmar.setBackground(new Color(0, 128, 0));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirmar.setPreferredSize(new Dimension(200, 40));

        JButton btnEliminar = new JButton("Eliminar Selección");
        btnEliminar.setBackground(new Color(255, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEliminar.setPreferredSize(new Dimension(200, 40));

        botonesPanel.add(btnAgregar);
        botonesPanel.add(btnConfirmar);
        botonesPanel.add(btnEliminar);
        centerPanel.add(botonesPanel);

        // Acción Agregar
        btnAgregar.addActionListener(e -> {
            String producto = (String) comboProducto.getSelectedItem();
            String categoria = (String) comboCategoria.getSelectedItem();
            int cantidad = (Integer) spinnerCantidad.getValue();
            String notas = txtNotas.getText();
            String mesa = (String) comboMesa.getSelectedItem();

            tableModel.addRow(new Object[]{mesa + " - " + producto, categoria, cantidad, notas});
            txtNotas.setText(""); 
        });

        // Acción Confirmar
        btnConfirmar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Estás seguro de que deseas generar esta comanda?",
                    "Confirmación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "¡La comanda fue generada con éxito!");
                tableModel.setRowCount(0);
            }
        });

        // Acción Eliminar 
        btnEliminar.addActionListener(e -> {
            int selectedRow = resumenTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar.");
            }
        });

        // Botón volver 
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



