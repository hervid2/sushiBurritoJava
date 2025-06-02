package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.database.FacturaDAO;
import main.java.com.restaurante.app.database.PedidoDAO;
import main.java.com.restaurante.app.database.ProductoDAO;
import main.java.com.restaurante.app.models.DetallePedido;
import main.java.com.restaurante.app.models.Factura; 
import main.java.com.restaurante.app.models.Pedido;
import main.java.com.restaurante.app.models.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime; 
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GenerarFacturaView extends JFrame {

    private JTable tablaFactura;
    private DefaultTableModel tablaFacturaModel;
    private JComboBox<Pedido> comboPedidos;
    private JLabel labelImpuesto;
    private JTextField campoPropina;
    private JLabel labelTotal;
    private JTextField campoMesa;

    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO;
    private FacturaDAO facturaDAO;
    private Map<Integer, Pedido> pedidosDisponibles;
    private DecimalFormat df;
    private int usuarioId;

    // Constantes para cálculos
    private static final double PORCENTAJE_IMPUESTO = 0.08; // 8%
    private static final double PORCENTAJE_PROPINA_SUGERIDA = 0.10; // 10%

    public GenerarFacturaView(int usuarioId) {
        this.usuarioId = usuarioId;
        df = new DecimalFormat("#,##0.00");

        try {
            pedidoDAO = new PedidoDAO();
            productoDAO = new ProductoDAO();
            facturaDAO = new FacturaDAO();
            pedidosDisponibles = new HashMap<>();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Considerar si es necesario cerrar la aplicación o deshabilitar funciones
        }
        setupUI();
        loadPedidosParaFacturar();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (pedidoDAO != null) pedidoDAO.close();
                if (productoDAO != null) productoDAO.close();
                if (facturaDAO != null) facturaDAO.close();
            }
        });
    }

    private void loadPedidosParaFacturar() {
        comboPedidos.removeAllItems();
        pedidosDisponibles.clear();
        try {
            // Obtener pedidos que están listos para ser facturados
            // Estados elegibles: "entregado", "preparando" (si se puede facturar antes de entrega)
            List<Pedido> pedidosEntregados = pedidoDAO.obtenerPedidosPorEstado("entregado");
            List<Pedido> pedidosPreparando = pedidoDAO.obtenerPedidosPorEstado("preparando");

            // Combine and sort if necessary, for now just add.
            for (Pedido p : pedidosEntregados) {
                comboPedidos.addItem(p);
                pedidosDisponibles.put(p.getPedidoId(), p);
            }
            for (Pedido p : pedidosPreparando) {
                comboPedidos.addItem(p);
                pedidosDisponibles.put(p.getPedidoId(), p);
            }

            if (comboPedidos.getItemCount() > 0) {
                comboPedidos.setSelectedIndex(0); // Seleccionar el primer pedido por defecto
            } else {
                JOptionPane.showMessageDialog(this, "No hay pedidos disponibles para facturar.", "Información", JOptionPane.INFORMATION_MESSAGE);
                tablaFacturaModel.setRowCount(0);
                campoMesa.setText("");
                labelImpuesto.setText("Impuesto (" + (PORCENTAJE_IMPUESTO * 100) + "%): $0");
                campoPropina.setText("0.00");
                labelTotal.setText("Total: $0");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayPedidoDetails(Pedido selectedPedido) {
        tablaFacturaModel.setRowCount(0);
        campoMesa.setText(String.valueOf(selectedPedido.getMesa()));

        double subtotal = 0.0;

        try {
            List<DetallePedido> detalles = pedidoDAO.obtenerDetallesPorPedidoId(selectedPedido.getPedidoId());
            for (DetallePedido dp : detalles) {
                Producto producto = productoDAO.obtenerProductoPorId(dp.getProductoId());
                if (producto != null) {
                    // Usar valor_venta del Producto
                    double precioUnitario = producto.getValorVenta();
                    double subtotalProducto = precioUnitario * dp.getCantidad();
                    subtotal += subtotalProducto;

                    tablaFacturaModel.addRow(new Object[]{
                        producto.getNombre(),
                        dp.getCantidad(),
                        "$" + df.format(precioUnitario),
                        "$" + df.format(subtotalProducto)
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles del pedido: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        updateTotals(subtotal);
    }

    private void updateTotals(double subtotalBase) {
        double impuesto = subtotalBase * PORCENTAJE_IMPUESTO;
        double propinaSugerida = subtotalBase * PORCENTAJE_PROPINA_SUGERIDA;
        
        labelImpuesto.setText("Impuesto (" + (PORCENTAJE_IMPUESTO * 100) + "%): $" + df.format(impuesto));
        campoPropina.setText(df.format(propinaSugerida));

        // Calcular y mostrar el total con la propina sugerida o la ingresada
        calculateAndDisplayTotal(subtotalBase, impuesto);
    }

    private void calculateAndDisplayTotal(double subtotalBase, double impuesto) {
        double propina = 0.0;
        try {
            propina = df.parse(campoPropina.getText()).doubleValue();
        } catch (java.text.ParseException | NumberFormatException ex) {
            campoPropina.setText("0.00"); // Asegura que el campo siempre tenga un número válido
            propina = 0.0;
        }

        double total = subtotalBase + impuesto + propina;
        labelTotal.setText("Total: $" + df.format(total));
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

        // Selector de pedidos y mesa
        JPanel pedidoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pedidoPanel.setOpaque(false);

        pedidoPanel.add(new JLabel("Seleccione pedido:"));
        comboPedidos = new JComboBox<>();
        comboPedidos.setPreferredSize(new Dimension(160, 30));
        pedidoPanel.add(comboPedidos);

        pedidoPanel.add(new JLabel("Mesa:"));
        campoMesa = new JTextField();
        campoMesa.setPreferredSize(new Dimension(60, 30));
        campoMesa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoMesa.setEditable(false);
        pedidoPanel.add(campoMesa);

        comboPedidos.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Pedido selectedPedido = (Pedido) comboPedidos.getSelectedItem();
                if (selectedPedido != null) {
                    displayPedidoDetails(selectedPedido);
                } else {
                    tablaFacturaModel.setRowCount(0);
                    campoMesa.setText("");
                    labelImpuesto.setText("Impuesto (" + (PORCENTAJE_IMPUESTO * 100) + "%): $0");
                    campoPropina.setText("0.00");
                    labelTotal.setText("Total: $0");
                }
            }
        });

        JButton generarFacturaBtn = new JButton("Generar Factura");
        generarFacturaBtn.setBackground(new Color(0, 128, 0));
        generarFacturaBtn.setForeground(Color.WHITE);
        generarFacturaBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generarFacturaBtn.setFocusPainted(false);
        generarFacturaBtn.setPreferredSize(new Dimension(160, 35));
        generarFacturaBtn.addActionListener(e -> generarFactura());

        pedidoPanel.add(generarFacturaBtn);
        mainPanel.add(pedidoPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Tabla
        String[] columnas = {"Descripción Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        tablaFacturaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFactura = new JTable(tablaFacturaModel);
        JScrollPane tablaScroll = new JScrollPane(tablaFactura);
        tablaScroll.setPreferredSize(new Dimension(750, 300));
        mainPanel.add(tablaScroll, BorderLayout.CENTER);

        // Panel de totales
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setOpaque(false);

        labelImpuesto = new JLabel("Impuesto (" + (PORCENTAJE_IMPUESTO * 100) + "%): $0", SwingConstants.RIGHT);
        labelImpuesto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelImpuesto.setAlignmentX(Component.RIGHT_ALIGNMENT);
        totalPanel.add(labelImpuesto);

        JPanel propinaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        propinaPanel.setOpaque(false);
        JLabel propinaLabel = new JLabel("Propina sugerida (" + (PORCENTAJE_PROPINA_SUGERIDA * 100) + "%): $");
        propinaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        campoPropina = new JTextField("0.00");
        campoPropina.setPreferredSize(new Dimension(80, 25));
        campoPropina.setHorizontalAlignment(JTextField.RIGHT);
        campoPropina.addActionListener(e -> {
            Pedido selectedPedido = (Pedido) comboPedidos.getSelectedItem();
            if (selectedPedido != null) {
                try {
                    double subtotalBase = 0.0;
                    List<DetallePedido> detalles = pedidoDAO.obtenerDetallesPorPedidoId(selectedPedido.getPedidoId());
                    for (DetallePedido dp : detalles) {
                        Producto producto = productoDAO.obtenerProductoPorId(dp.getProductoId());
                        if (producto != null) {
                            subtotalBase += producto.getValorVenta() * dp.getCantidad();
                        }
                    }
                    double impuesto = subtotalBase * PORCENTAJE_IMPUESTO;
                    calculateAndDisplayTotal(subtotalBase, impuesto);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al recalcular totales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        propinaPanel.add(propinaLabel);
        propinaPanel.add(campoPropina);
        totalPanel.add(propinaPanel);

        labelTotal = new JLabel("Total: $0", SwingConstants.RIGHT);
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        totalPanel.add(Box.createVerticalStrut(10));
        totalPanel.add(labelTotal);

        JButton volverBtn = new JButton("← Volver al Panel del Mesero");
        volverBtn.setBackground(new Color(255, 140, 0));
        volverBtn.setForeground(Color.WHITE);
        volverBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        volverBtn.setFocusPainted(false);
        volverBtn.setPreferredSize(new Dimension(250, 40));
        volverBtn.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView(usuarioId).setVisible(true);
        });

        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.setOpaque(false);
        volverPanel.add(volverBtn);
        totalPanel.add(Box.createVerticalStrut(15));
        totalPanel.add(volverPanel);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
    }

    private void generarFactura() {
        Pedido selectedPedido = (Pedido) comboPedidos.getSelectedItem();
        if (selectedPedido == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún pedido seleccionado para facturar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Calcular todos los valores finales
            double subtotalBase = 0.0;
            List<DetallePedido> detalles = pedidoDAO.obtenerDetallesPorPedidoId(selectedPedido.getPedidoId());
            for (DetallePedido dp : detalles) {
                Producto producto = productoDAO.obtenerProductoPorId(dp.getProductoId());
                if (producto != null) {
                    subtotalBase += producto.getValorVenta() * dp.getCantidad();
                }
            }
            double impuestoCalculado = subtotalBase * PORCENTAJE_IMPUESTO;
            double propinaIngresada = 0.0;
            try {
                propinaIngresada = df.parse(campoPropina.getText()).doubleValue();
            } catch (java.text.ParseException | NumberFormatException ex) {
                // Ya se maneja en calculateAndDisplayTotal, aquí solo nos aseguramos
            }
            double totalFinal = subtotalBase + impuestoCalculado + propinaIngresada;

            // Crear objeto Factura con LocalDateTime.now()
            Factura factura = new Factura();
            factura.setPedidoId(selectedPedido.getPedidoId());
            factura.setSubtotal(subtotalBase);
            factura.setImpuestoTotal(impuestoCalculado); // Usar el impuesto calculado
            factura.setTotal(totalFinal);
            factura.setFechaFactura(LocalDateTime.now()); // Establecer la fecha actual

            // Insertar factura en la DB
            facturaDAO.insertarFactura(factura); // Tu DAO no devuelve el ID, lo inserta.

            // Actualizar estado del pedido a "pagado"
            pedidoDAO.actualizarEstadoPedido(selectedPedido.getPedidoId(), "pagado");

            JOptionPane.showMessageDialog(this,
                    "Factura generada exitosamente para Pedido #" + selectedPedido.getPedidoId() +
                    "\nTotal a pagar: $" + df.format(totalFinal),
                    "Factura Generada", JOptionPane.INFORMATION_MESSAGE);

            // Recargar la lista de pedidos para que el pedido facturado ya no aparezca
            loadPedidosParaFacturar();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar la factura: " + ex.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}


