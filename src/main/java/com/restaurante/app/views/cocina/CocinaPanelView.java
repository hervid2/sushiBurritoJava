package main.java.com.restaurante.app.views.cocina;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.PedidoController; // Importar el controlador
import main.java.com.restaurante.app.views.authentication.LoginView; // Para el botón de cerrar sesión

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDateTime; // Para el tipo de dato de hora
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Vector; // Necesario para getDataVector()

public class CocinaPanelView extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable tablaEntrantes;
    private JTable tablaPreparacion;
    private DefaultTableModel modelEntrantes;
    private DefaultTableModel modelPreparacion;
    private PedidoController pedidoController; // Instancia del controlador

    public CocinaPanelView() {
        try {
            this.pedidoController = new PedidoController(); // Inicializar el controlador
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al inicializar el controlador de pedidos:\n" + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Considera si quieres salir o deshabilitar funcionalidades
        }
        setupUI();
        cargarPedidos();

        // Asegurarse de cerrar las conexiones del controlador al cerrar la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (pedidoController != null) {
                    pedidoController.closeDAOs();
                }
            }
        });
    }

    private void cargarPedidos() {
        modelEntrantes.setRowCount(0); // Limpiar tabla de entrantes
        modelPreparacion.setRowCount(0); // Limpiar tabla de preparación

        try {
            // Obtener todos los pedidos relevantes para la cocina
            List<Map<String, Object>> pedidos = pedidoController.obtenerPedidosParaCocina();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

            for (Map<String, Object> p : pedidos) {
                String estado = (String) p.get("estado"); // Asumiendo que el mapa ahora también tiene el estado

                // Comprobamos el estado para decidir en qué tabla va
                if ("pendiente".equalsIgnoreCase(estado)) {
                    modelEntrantes.addRow(new Object[]{
                        p.get("id"),
                        "Mesa " + p.get("mesa"),
                        p.get("productos"),
                        p.get("categorias"), // Ahora es una cadena de categorías separadas por coma
                        formatter.format((LocalDateTime) p.get("hora"))
                    });
                } else if ("preparando".equalsIgnoreCase(estado)) {
                    modelPreparacion.addRow(new Object[]{
                        p.get("id"),
                        "Mesa " + p.get("mesa"),
                        p.get("productos"),
                        p.get("categorias"),
                        formatter.format((LocalDateTime) p.get("hora"))
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos para la cocina:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception ex) {
            System.err.println("Error configurando FlatLaf: " + ex.getMessage());
        }

        setTitle("Panel de Cocina - Sushi Burrito");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ¡CAMBIADO A DISPOSE_ON_CLOSE!

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Panel de Cocina", SwingConstants.CENTER);
        title.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        topPanel.add(title, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(255, 140, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.addActionListener(e -> {
            this.dispose(); // Cierra la ventana actual de cocina
            new LoginView().setVisible(true); // Abre la ventana de login
        });
        topPanel.add(logoutButton, BorderLayout.EAST);
        // topPanel.add(Box.createRigidArea(logoutButton.getPreferredSize()), BorderLayout.WEST); // Esto puede causar problemas de layout, mejor quitarlo si no es necesario
        // Si quieres un espacio a la izquierda, usa un EmptyBorder o un componente invisible
        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        leftSpacer.setPreferredSize(logoutButton.getPreferredSize()); // Mismo tamaño que el botón de logout
        topPanel.add(leftSpacer, BorderLayout.WEST);


        mainPanel.add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 15;
                g2.setColor(isSelected ? new Color(200, 200, 200) : getBackground());
                g2.fillRoundRect(x, y, w, h + arc / 2, arc, arc);
                g2.setColor(isSelected ? new Color(200, 200, 200) : getBackground());
                g2.fillRect(x, y + arc / 2, w, h - arc / 2);
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, y, w, h + arc / 2, arc, arc);
                g2.drawLine(x, y + h, x + w, y + h);
                g2.dispose();
            }
        });

        // TAB 1: Pedidos Entrantes
        modelEntrantes = new DefaultTableModel(new Object[]{"ID Pedido", "Mesa", "Productos", "Categorías", "Hora Entrada"}, 0) { // Cambiado a "Categorías"
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        tablaEntrantes = new JTable(modelEntrantes);
        configurarTabla(tablaEntrantes);

        JScrollPane scrollEntrantes = new JScrollPane(tablaEntrantes);
        JPanel panelEntrantes = new JPanel(new BorderLayout());
        panelEntrantes.setOpaque(false);
        panelEntrantes.add(scrollEntrantes, BorderLayout.CENTER);

        JButton btnPasarPrep = new JButton("Pasar a Preparación");
        btnPasarPrep.setBackground(new Color(0, 128, 0));
        btnPasarPrep.setForeground(Color.WHITE);
        btnPasarPrep.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPasarPrep.setPreferredSize(new Dimension(220, 40));
        btnPasarPrep.addActionListener((ActionEvent e) -> {
            int fila = tablaEntrantes.getSelectedRow();
            if (fila != -1) {
                int pedidoId = (int) modelEntrantes.getValueAt(fila, 0); // Obtener ID directamente
                try {
                    pedidoController.actualizarEstado(pedidoId, "preparando"); // Usar la instancia del controlador
                    JOptionPane.showMessageDialog(this, "Pedido #" + pedidoId + " pasado a preparación.");
                    cargarPedidos(); // Recargar ambas tablas para reflejar el cambio
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar estado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido entrante.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel botonEntrantes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonEntrantes.setOpaque(false);
        botonEntrantes.add(btnPasarPrep);
        panelEntrantes.add(botonEntrantes, BorderLayout.SOUTH);
        tabbedPane.addTab("Pedidos Entrantes", panelEntrantes);

        // TAB 2: En Preparación
        modelPreparacion = new DefaultTableModel(new Object[]{"ID Pedido", "Mesa", "Productos", "Categorías", "Hora Entrada"}, 0) { // Cambiado a "Categorías"
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        tablaPreparacion = new JTable(modelPreparacion);
        configurarTabla(tablaPreparacion);

        JScrollPane scrollPreparacion = new JScrollPane(tablaPreparacion);
        JPanel panelPrep = new JPanel(new BorderLayout());
        panelPrep.setOpaque(false);
        panelPrep.add(scrollPreparacion, BorderLayout.CENTER);

        JButton btnMarcarListo = new JButton("Marcar como Preparado");
        btnMarcarListo.setBackground(new Color(0, 128, 0));
        btnMarcarListo.setForeground(Color.WHITE);
        btnMarcarListo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMarcarListo.setPreferredSize(new Dimension(220, 40));
        
        btnMarcarListo.addActionListener((ActionEvent e) -> {
            int fila = tablaPreparacion.getSelectedRow();
            if (fila != -1) {
                int pedidoId = (int) modelPreparacion.getValueAt(fila, 0);
                try {
                    pedidoController.actualizarEstado(pedidoId, "entregado"); // Cambiar a "entregado" para que el mesero lo cierre
                    JOptionPane.showMessageDialog(this, "Pedido #" + pedidoId + " marcado como preparado. Notifica al mesero para entrega.");
                    cargarPedidos(); // Recargar ambas tablas para reflejar el cambio
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar estado del pedido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido en preparación.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel botonPrep = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonPrep.setOpaque(false);
        botonPrep.add(btnMarcarListo);
        panelPrep.add(botonPrep, BorderLayout.SOUTH);
        tabbedPane.addTab("En Preparación", panelPrep);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void configurarTabla(JTable table) {
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(28); // Altura de fila por defecto, ajustada por MultiLineTableCellRenderer

        TableColumn column;
        int totalWidth = 900; // Ancho total de la tabla

        // Definir anchos de columna
        column = table.getColumnModel().getColumn(0); column.setPreferredWidth((int)(totalWidth * 0.10)); // ID Pedido
        column = table.getColumnModel().getColumn(1); column.setPreferredWidth((int)(totalWidth * 0.10)); // Mesa
        column = table.getColumnModel().getColumn(2); column.setPreferredWidth((int)(totalWidth * 0.45)); // Productos
        column.setCellRenderer(new MultiLineTableCellRenderer()); // Para que los productos puedan ocupar varias líneas
        column = table.getColumnModel().getColumn(3); column.setPreferredWidth((int)(totalWidth * 0.20)); // Categorías
        column.setCellRenderer(new MultiLineTableCellRenderer()); // Para que las categorías puedan ocupar varias líneas
        column = table.getColumnModel().getColumn(4); column.setPreferredWidth((int)(totalWidth * 0.15)); // Hora Entrada

        // Aplicar el renderer multilínea a todas las columnas que lo necesiten, o a la tabla por defecto
        // table.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer()); // Esto aplica a todas, pero puede ser excesivo.
        // Es mejor aplicarlo solo a las columnas de "Productos" y "Categorías" como arriba.
    }

    // MultiLineTableCellRenderer se mantiene igual
    private class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineTableCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());

            // Ajuste dinámico de la altura de la fila
            int currentColumnWidth = table.getColumnModel().getColumn(column).getWidth();
            setSize(currentColumnWidth, 0); // Establece el ancho para calcular la altura preferida
            int preferredHeight = getPreferredSize().height;
            int currentRowHeight = table.getRowHeight(row);
            if (preferredHeight > currentRowHeight) {
                table.setRowHeight(row, preferredHeight);
            }

            return this;
        }
    }
}