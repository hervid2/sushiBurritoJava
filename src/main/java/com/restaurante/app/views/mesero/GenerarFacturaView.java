package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GenerarFacturaView extends JFrame {

    private JTable tablaFactura;
    private JComboBox<String> comboPedidos;
    private JLabel labelImpuesto;
    private JTextField campoPropina;
    private JLabel labelTotal;

    public GenerarFacturaView() {
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

        setTitle("Generar Factura - Sushi Burrito");
        setSize(800, 670);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 250, 205));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        setContentPane(mainPanel);

        // Panel superior
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setOpaque(false);

        JLabel labelNombreRestaurante = new JLabel("Sushi Burrito", SwingConstants.CENTER);
        labelNombreRestaurante.setFont(new Font("Yu Gothic UI", Font.BOLD, 26));
        headerPanel.add(labelNombreRestaurante);

        JLabel labelNIT = new JLabel("NIT: 901234567-8", SwingConstants.CENTER);
        labelNIT.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        headerPanel.add(labelNIT);

        JLabel labelFactura = new JLabel("Factura de Venta", SwingConstants.CENTER);
        labelFactura.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(labelFactura);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Selector de pedidos
        JPanel pedidoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pedidoPanel.setOpaque(false);
        pedidoPanel.add(new JLabel("Seleccione pedido:"));

        comboPedidos = new JComboBox<>(new String[]{"Pedido #1", "Pedido #2", "Pedido #3"});
        comboPedidos.setPreferredSize(new Dimension(200, 30));
        pedidoPanel.add(comboPedidos);

        JButton generarFacturaBtn = new JButton("Generar Factura");
        generarFacturaBtn.setBackground(new Color(0, 128, 0));
        generarFacturaBtn.setForeground(Color.WHITE);
        generarFacturaBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generarFacturaBtn.setFocusPainted(false);
        generarFacturaBtn.setPreferredSize(new Dimension(160, 35));
        generarFacturaBtn.addActionListener(e -> {
            // TODO: Lógica para calcular la factura y mostrar en la tabla
            JOptionPane.showMessageDialog(this, "Factura generada para " + comboPedidos.getSelectedItem());
        });

        pedidoPanel.add(generarFacturaBtn);
        mainPanel.add(pedidoPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Tabla
        String[] columnas = {"Descripción Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        tablaFactura = new JTable(new DefaultTableModel(columnas, 0));
        JScrollPane tablaScroll = new JScrollPane(tablaFactura);
        tablaScroll.setPreferredSize(new Dimension(750, 300));
        mainPanel.add(tablaScroll, BorderLayout.CENTER);

        // Panel de totales
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setOpaque(false);

        labelImpuesto = new JLabel("Impuesto (8%): $0", SwingConstants.RIGHT);
        labelImpuesto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelImpuesto.setAlignmentX(Component.RIGHT_ALIGNMENT);
        totalPanel.add(labelImpuesto);

        JPanel propinaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        propinaPanel.setOpaque(false);
        JLabel propinaLabel = new JLabel("Propina sugerida (10%): $");
        propinaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        campoPropina = new JTextField("0");
        campoPropina.setPreferredSize(new Dimension(80, 25));
        propinaPanel.add(propinaLabel);
        propinaPanel.add(campoPropina);
        totalPanel.add(propinaPanel);

        labelTotal = new JLabel("Total: $0", SwingConstants.RIGHT);
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        totalPanel.add(Box.createVerticalStrut(10));
        totalPanel.add(labelTotal);

        // Botón volver al panel del mesero
        JButton volverBtn = new JButton("← Volver al Panel del Mesero");
        volverBtn.setBackground(new Color(255, 140, 0));
        volverBtn.setForeground(Color.WHITE);
        volverBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        volverBtn.setFocusPainted(false);
        volverBtn.setPreferredSize(new Dimension(250, 40));
        
        volverBtn.addActionListener(e -> {
        	this.dispose();
        	new WaiterPanelView().setVisible(true);
        	}); 

        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.setOpaque(false);
        volverPanel.add(volverBtn);
        totalPanel.add(Box.createVerticalStrut(15));
        totalPanel.add(volverPanel);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
    }
}


