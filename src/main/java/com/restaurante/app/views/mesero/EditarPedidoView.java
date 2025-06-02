package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.PedidoController; // Usaremos PedidoController para la lógica de negocio
import main.java.com.restaurante.app.database.PedidoDAO; // Necesario para cerrar la conexión al final, aunque la lógica la maneja Controller
import main.java.com.restaurante.app.database.ProductoDAO; // Necesario para cerrar la conexión al final
import main.java.com.restaurante.app.models.Categoria;
import main.java.com.restaurante.app.models.DetallePedido;
import main.java.com.restaurante.app.models.DetallePedidoDTO; // Importar DetallePedidoDTO
import main.java.com.restaurante.app.models.Pedido;
import main.java.com.restaurante.app.models.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet; // Para categorías únicas
import java.util.List;
import java.util.Map;
import java.util.Set; // Para categorías únicas

public class EditarPedidoView extends JFrame {

    private JTable tablaPedido;
    private DefaultTableModel tableModel;
    private int usuarioId;
    private JComboBox<Pedido> comboPedidos;
    private JComboBox<Producto> comboProducto;
    private JComboBox<Categoria> comboCategoria;
    private JSpinner spinnerCantidad;
    private JTextField txtNotas;
    private JComboBox<String> comboMesaPedido;
    private JComboBox<String> comboEstadoPedido;

    private PedidoController pedidoController; // Usaremos el controlador para toda la lógica de DB
    private ProductoDAO productoDAO; // Mantener para cargar productos/categorías iniciales y para obtener objetos Producto/Categoria

    // Mapas para productos y categorías
    private Map<Integer, List<Producto>> productosPorCategoria;
    private Map<Integer, Categoria> categoriasMap;
    private Map<Integer, Producto> productosMap;

    // Para mantener el detalle_id al editar filas (necesario para diferenciar detalles nuevos de existentes)
    private static final int COL_PRODUCTO_ID = 4; // Columna oculta para almacenar el ID del producto
    private static final int COL_DETALLE_ID = 5; // Columna oculta para almacenar el ID del detalle (0 si es nuevo)

    public EditarPedidoView(int usuarioId) {
        this.usuarioId = usuarioId;
        try {
            this.pedidoController = new PedidoController(); // Inicializar el controlador
            this.productoDAO = new ProductoDAO(); // Se mantiene para la carga inicial de datos y obtener productos/categorias por ID
            loadProductosAndCategorias();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar o cargar datos iniciales: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        setupUI();
        loadPedidos();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (pedidoController != null) {
                    pedidoController.closeDAOs(); // Cerrar los DAOs a través del controlador
                }
                // Si productoDAO se inicializó directamente aquí y no a través del controlador, también cerrarlo
                if (productoDAO != null) {
                    productoDAO.close();
                }
            }
        });
    }

    private void loadProductosAndCategorias() throws SQLException {
        // Usa productoDAO directamente aquí para poblar los JComboBox y mapas de datos
        // ya que el controlador no tiene métodos para obtener listas completas de productos/categorias directamente.
        List<Producto> allProducts = productoDAO.obtenerTodosLosProductos();
        List<Categoria> allCategories = productoDAO.obtenerTodasLasCategorias();

        productosPorCategoria = new HashMap<>();
        categoriasMap = new HashMap<>();
        productosMap = new HashMap<>();

        for (Categoria cat : allCategories) {
            categoriasMap.put(cat.getCategoriaId(), cat);
            productosPorCategoria.put(cat.getCategoriaId(), new ArrayList<>());
        }

        for (Producto prod : allProducts) {
            productosMap.put(prod.getId(), prod);
            if (productosPorCategoria.containsKey(prod.getCategoriaId())) {
                productosPorCategoria.get(prod.getCategoriaId()).add(prod);
            }
        }
    }

    private void loadPedidos() {
        try {
            // Usa el controlador para obtener la lista de pedidos
            List<Pedido> pedidos = pedidoController.obtenerTodosLosPedidos(); // Asumiendo que existe este método en PedidoController
            comboPedidos.removeAllItems();
            for (Pedido p : pedidos) {
                comboPedidos.addItem(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadDetallesPedido(Pedido pedido) {
        tableModel.setRowCount(0);
        if (pedido == null) {
            comboMesaPedido.setSelectedIndex(-1); // Limpiar selección de mesa
            comboEstadoPedido.setSelectedIndex(-1); // Limpiar selección de estado
            return;
        }

        try {
            // Usa el controlador para obtener los detalles del pedido
            List<DetallePedido> detalles = pedidoController.obtenerDetallesPorPedidoId(pedido.getPedidoId());

            // Setear la mesa y el estado del pedido principal
            comboMesaPedido.setSelectedItem(String.valueOf(pedido.getMesa()));
            comboEstadoPedido.setSelectedItem(pedido.getEstado());

            for (DetallePedido dp : detalles) {
                Producto producto = productosMap.get(dp.getProductoId());
                Categoria categoria = null;
                String categoriaNombre = "N/A";

                if (producto != null) {
                    categoria = categoriasMap.get(producto.getCategoriaId());
                    if (categoria != null) {
                        categoriaNombre = categoria.getNombre();
                    }
                }

                // Asegúrate de añadir el Detalle ID (dp.getDetalleId())
                tableModel.addRow(new Object[]{
                    producto != null ? producto.getNombre() : "Producto Desconocido",
                    categoriaNombre,
                    dp.getCantidad(),
                    dp.getNotas(),
                    dp.getProductoId(), // COL_PRODUCTO_ID
                    dp.getDetalleId()   // COL_DETALLE_ID (Muy importante para la edición)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles del pedido: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
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

        setTitle("Editar Pedido - Sushi Burrito");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        comboPedidos = new JComboBox<>();
        comboPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboPedidos.setPreferredSize(new Dimension(200, 30));
        // Listener para cargar detalles cuando se selecciona un pedido
        comboPedidos.addActionListener(e -> {
            Pedido selectedPedido = (Pedido) comboPedidos.getSelectedItem();
            loadDetallesPedido(selectedPedido);
            // La lógica para setear mesa y estado ahora está dentro de loadDetallesPedido
            // para asegurar que se actualizan cuando se carga un nuevo pedido.
        });

        selectorPanel.add(lblPedido);
        selectorPanel.add(comboPedidos);
        centerPanel.add(selectorPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Selector de Mesa y Estado del Pedido
        JPanel pedidoInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pedidoInfoPanel.setOpaque(false);
        pedidoInfoPanel.setMaximumSize(new Dimension(900, 40));

        JLabel lblMesaPedido = new JLabel("Mesa del Pedido:");
        lblMesaPedido.setFont(new Font("Segoe UI", Font.BOLD, 16));
        comboMesaPedido = new JComboBox<>();
        for (int i = 1; i <= 20; i++) {
            comboMesaPedido.addItem(String.valueOf(i));
        }
        comboMesaPedido.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboMesaPedido.setPreferredSize(new Dimension(100, 30));

        JLabel lblEstadoPedido = new JLabel("Estado del Pedido:");
        lblEstadoPedido.setFont(new Font("Segoe UI", Font.BOLD, 16));
        comboEstadoPedido = new JComboBox<>(new String[]{"pendiente", "preparando", "entregado", "cancelado"});
        comboEstadoPedido.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboEstadoPedido.setPreferredSize(new Dimension(150, 30));

        pedidoInfoPanel.add(lblMesaPedido);
        pedidoInfoPanel.add(comboMesaPedido);
        pedidoInfoPanel.add(lblEstadoPedido);
        pedidoInfoPanel.add(comboEstadoPedido);
        centerPanel.add(pedidoInfoPanel);
        centerPanel.add(Box.createVerticalStrut(20));


        // Tabla de productos del pedido con columnas ocultas
        tableModel = new DefaultTableModel(new Object[]{"Producto", "Categoría", "Cantidad", "Notas", "Producto ID", "Detalle ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas no son editables
            }
            // Sobreescribir getColumnClass para que las columnas ocultas no intenten mostrarse como Strings vacíos
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == COL_PRODUCTO_ID || columnIndex == COL_DETALLE_ID) {
                    return Integer.class; // O Object.class si no hay un tipo específico de renderizado
                }
                return super.getColumnClass(columnIndex);
            }
        };
        tablaPedido = new JTable(tableModel);
        // Ocultar columnas de IDs
        tablaPedido.getColumnModel().getColumn(COL_PRODUCTO_ID).setMinWidth(0);
        tablaPedido.getColumnModel().getColumn(COL_PRODUCTO_ID).setMaxWidth(0);
        tablaPedido.getColumnModel().getColumn(COL_PRODUCTO_ID).setWidth(0);
        tablaPedido.getColumnModel().getColumn(COL_PRODUCTO_ID).setPreferredWidth(0);

        tablaPedido.getColumnModel().getColumn(COL_DETALLE_ID).setMinWidth(0);
        tablaPedido.getColumnModel().getColumn(COL_DETALLE_ID).setMaxWidth(0);
        tablaPedido.getColumnModel().getColumn(COL_DETALLE_ID).setWidth(0);
        tablaPedido.getColumnModel().getColumn(COL_DETALLE_ID).setPreferredWidth(0);

        // Listener para cargar datos de la fila seleccionada en los campos de edición
        tablaPedido.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPedido.getSelectedRow() != -1) {
                int selectedRow = tablaPedido.getSelectedRow();
                // String nombreProducto = (String) tableModel.getValueAt(selectedRow, 0); // No necesario, usamos ID
                // String nombreCategoria = (String) tableModel.getValueAt(selectedRow, 1); // No necesario, usamos ID
                int cantidad = (int) tableModel.getValueAt(selectedRow, 2);
                String notas = (String) tableModel.getValueAt(selectedRow, 3);
                int productoId = (int) tableModel.getValueAt(selectedRow, COL_PRODUCTO_ID); // Obtener ID de producto

                // Setear cantidad y notas
                spinnerCantidad.setValue(cantidad);
                txtNotas.setText(notas);

                // Setear producto y categoría en los JComboBox
                Producto prodParaSeleccionar = productosMap.get(productoId);
                if (prodParaSeleccionar != null) {
                    Categoria catParaSeleccionar = categoriasMap.get(prodParaSeleccionar.getCategoriaId());
                    if (catParaSeleccionar != null) {
                        comboCategoria.setSelectedItem(catParaSeleccionar);
                        // Asegurarse de que el listener de comboCategoria se dispare y luego seleccionar el producto
                        // Esto se hace con SwingUtilities.invokeLater para asegurar que el comboProducto se haya llenado
                        SwingUtilities.invokeLater(() -> comboProducto.setSelectedItem(prodParaSeleccionar));
                    }
                }
            }
        });


        JScrollPane scrollTabla = new JScrollPane(tablaPedido);
        scrollTabla.setPreferredSize(new Dimension(900, 200));
        scrollTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(scrollTabla);
        centerPanel.add(Box.createVerticalStrut(20));

        // Panel de edición de producto
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        inputPanel.setOpaque(false);
        inputPanel.setMaximumSize(new Dimension(700, 150));

        JLabel lblProducto = new JLabel("Producto:");
        comboProducto = new JComboBox<>(); // Ahora de tipo Producto
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblCategoria = new JLabel("Categoría:");
        comboCategoria = new JComboBox<>(); // Ahora de tipo Categoria
        // Llenar comboCategoria con las categorías cargadas
        for (Categoria cat : categoriasMap.values()) {
            comboCategoria.addItem(cat);
        }
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // Listener para actualizar comboProducto cuando cambia la categoría
        comboCategoria.addActionListener(e -> {
            Categoria selectedCategory = (Categoria) comboCategoria.getSelectedItem();
            comboProducto.removeAllItems();
            if (selectedCategory != null) {
                List<Producto> products = productosPorCategoria.get(selectedCategory.getCategoriaId());
                if (products != null) {
                    for (Producto p : products) {
                        comboProducto.addItem(p);
                    }
                }
            }
        });
        // Asegurarse de que el comboProducto se cargue inicialmente
        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }

        JLabel lblCantidad = new JLabel("Cantidad:");
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spinnerCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblNotas = new JLabel("Notas:");
        txtNotas = new JTextField();
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
            Producto selectedProducto = (Producto) comboProducto.getSelectedItem();
            Categoria selectedCategoria = (Categoria) comboCategoria.getSelectedItem();
            int cantidad = (Integer) spinnerCantidad.getValue();
            String notas = txtNotas.getText().trim(); // Usar trim para limpiar espacios

            if (selectedProducto == null || selectedCategoria == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto y categoría válidos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Añadir Producto ID y Detalle ID (0 para nuevos detalles, ya que no tienen un ID de BD aún)
            tableModel.addRow(new Object[]{
                selectedProducto.getNombre(),
                selectedCategoria.getNombre(),
                cantidad,
                notas,
                selectedProducto.getId(), // COL_PRODUCTO_ID
                0 // COL_DETALLE_ID (0 para un nuevo detalle)
            });
            txtNotas.setText("");
            spinnerCantidad.setValue(1);
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tablaPedido.getSelectedRow();
            if (selectedRow != -1) {
                Producto selectedProducto = (Producto) comboProducto.getSelectedItem();
                Categoria selectedCategoria = (Categoria) comboCategoria.getSelectedItem();
                int cantidad = (Integer) spinnerCantidad.getValue();
                String notas = txtNotas.getText().trim();

                if (selectedProducto == null || selectedCategoria == null) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto y categoría válidos para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Obtener el Detalle ID de la fila seleccionada (para mantenerlo al modificar un detalle existente)
                int detalleId = (int) tableModel.getValueAt(selectedRow, COL_DETALLE_ID);

                // Actualizar la fila seleccionada en la tabla
                tableModel.setValueAt(selectedProducto.getNombre(), selectedRow, 0);
                tableModel.setValueAt(selectedCategoria.getNombre(), selectedRow, 1);
                tableModel.setValueAt(cantidad, selectedRow, 2);
                tableModel.setValueAt(notas, selectedRow, 3);
                tableModel.setValueAt(selectedProducto.getId(), selectedRow, COL_PRODUCTO_ID);
                tableModel.setValueAt(detalleId, selectedRow, COL_DETALLE_ID); // Se mantiene el ID del detalle
                txtNotas.setText("");
                spinnerCantidad.setValue(1);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila para modificar.");
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tablaPedido.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                txtNotas.setText(""); // Limpiar campos al eliminar
                spinnerCantidad.setValue(1);
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
        btnConfirmar.addActionListener(e -> {
            Pedido selectedPedido = (Pedido) comboPedidos.getSelectedItem();
            if (selectedPedido == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un pedido para guardar los cambios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas guardar los cambios en este pedido?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Actualizar datos del pedido principal desde los JComboBox
                    selectedPedido.setMesa(Integer.parseInt((String)comboMesaPedido.getSelectedItem()));
                    selectedPedido.setEstado((String)comboEstadoPedido.getSelectedItem());
                    // selectedPedido.setUsuarioId(usuarioId); // Mantener el usuario original o actualizar si aplica

                    List<DetallePedidoDTO> detallesParaActualizarController = new ArrayList<>();
                    // Generar DetallePedidoDTOs para el controlador
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        int productoId = (int) tableModel.getValueAt(i, COL_PRODUCTO_ID);
                        int cantidad = (int) tableModel.getValueAt(i, 2);
                        String notas = (String) tableModel.getValueAt(i, 3);

                        DetallePedidoDTO dto = new DetallePedidoDTO(productoId, cantidad, notas);
                        detallesParaActualizarController.add(dto);
                    }

                    // Llama al controlador para actualizar el pedido y sus detalles.
                    // El controlador se encargará de generar los resúmenes y llamar al DAO.
                    pedidoController.actualizarPedido(selectedPedido, detallesParaActualizarController);

                    JOptionPane.showMessageDialog(this, "¡Cambios guardados correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadPedidos(); // Recargar la lista de pedidos (por si cambió el estado o mesa)
                    // Después de actualizar, selecciona nuevamente el pedido en el combobox para recargar sus detalles
                    // Esto asegura que la tabla refleje el estado de la DB tras el UPDATE
                    for(int i = 0; i < comboPedidos.getItemCount(); i++) {
                        Pedido p = (Pedido)comboPedidos.getItemAt(i);
                        if (p.getPedidoId() == selectedPedido.getPedidoId()) {
                            comboPedidos.setSelectedItem(p);
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error de base de datos al guardar cambios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Error al convertir mesa a número: " + ex.getMessage(), "Error de Datos", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al guardar cambios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

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
            new WaiterPanelView(usuarioId).setVisible(true); // Pasar usuarioId al volver
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomPanel.add(btnVolver);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // Método para obtener todos los pedidos (necesario para el JComboBox de pedidos)
    // Este método lo he añadido temporalmente aquí para que el comboPedidos funcione.
    // Idealmente, se debería tener un método en PedidoController para obtener todos los pedidos.
    // Si ya lo tienes, puedes borrar este y usar el del controlador.
    // (Por ejemplo, si PedidoController tiene un 'obtenerTodosLosPedidos()', úsalo en loadPedidos()).
    // Ya lo tienes, así que lo haré que use el controlador.
}