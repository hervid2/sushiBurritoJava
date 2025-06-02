package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.PedidoController;
import main.java.com.restaurante.app.models.DetallePedidoDTO; // Tu DTO
import main.java.com.restaurante.app.database.ProductoDAO; // Importar el DAO de Producto
import main.java.com.restaurante.app.models.Producto;     // Importar el modelo Producto
import main.java.com.restaurante.app.models.Categoria;    // Importar el modelo Categoria
import main.java.com.restaurante.app.models.Pedido;       // Importar el modelo Pedido

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set; // Para conjuntos, si es necesario para categorías
import java.util.HashSet; // Para HashSet

public class GenerarComandaView extends JFrame {

    private JTable resumenTable;
    private DefaultTableModel tableModel;
    private int usuarioId;
    private JComboBox<Producto> comboProducto;
    private JComboBox<Categoria> comboCategoria;
    private ProductoDAO productoDAO; // Instancia del DAO
    private PedidoController pedidoController; // Instancia del controlador de pedidos

    // Mapas para almacenar productos por categoría y categorías por ID
    private Map<Integer, List<Producto>> productosPorCategoria;
    private Map<Integer, Categoria> categoriasMap; // Para mapear categoria_id a objetos Categoria
    private Map<Integer, Producto> productosMap; // Añadido para buscar productos por ID fácilmente

    public GenerarComandaView(int usuarioId) {
        this.usuarioId = usuarioId;
        try {
            this.productoDAO = new ProductoDAO(); // Inicializar el DAO de productos
            this.pedidoController = new PedidoController(); // Inicializar el controlador de pedidos
            loadProductosAndCategorias(); // Cargar los datos de la DB al iniciar la vista
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar o cargar datos del menú desde la base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Considera si quieres salir de la aplicación o deshabilitar funcionalidades si no hay conexión
        }
        setupUI();
        // Es una buena práctica cerrar los DAOs/Controladores cuando la vista se cierra
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (productoDAO != null) {
                    productoDAO.close();
                }
                if (pedidoController != null) {
                    pedidoController.closeDAOs(); // Asegúrate de tener este método en tu PedidoController
                }
            }
        });
    }

    /**
     * Carga todos los productos y categorías desde la base de datos
     * y los organiza en mapas para facilitar su uso en la interfaz.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    private void loadProductosAndCategorias() throws SQLException {
        List<Producto> allProducts = productoDAO.obtenerTodosLosProductos();
        List<Categoria> allCategories = productoDAO.obtenerTodasLasCategorias();

        productosPorCategoria = new HashMap<>();
        categoriasMap = new HashMap<>();
        productosMap = new HashMap<>(); // Inicializar el nuevo mapa

        // Poblar el mapa de categorías y preparar listas vacías para productos
        for (Categoria cat : allCategories) {
            categoriasMap.put(cat.getCategoriaId(), cat);
            productosPorCategoria.put(cat.getCategoriaId(), new ArrayList<>());
        }

        // Asignar cada producto a su lista de categoría correspondiente y al mapa de productos
        for (Producto prod : allProducts) {
            productosMap.put(prod.getId(), prod); // Añadir al nuevo mapa
            if (productosPorCategoria.containsKey(prod.getCategoriaId())) {
                productosPorCategoria.get(prod.getCategoriaId()).add(prod);
            } else {
                System.err.println("Producto '" + prod.getNombre() + "' tiene una categoriaId (" + prod.getCategoriaId() + ") que no existe.");
            }
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

        setTitle("Generar Comanda - Sushi Burrito");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        tableModel = new DefaultTableModel(new Object[]{"Producto", "Categoría", "Cantidad", "Notas", "Producto ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resumenTable = new JTable(tableModel);
        // Ocultar la columna de Producto ID
        resumenTable.getColumnModel().getColumn(4).setMinWidth(0);
        resumenTable.getColumnModel().getColumn(4).setMaxWidth(0);
        resumenTable.getColumnModel().getColumn(4).setWidth(0);
        resumenTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        JScrollPane scrollResumen = new JScrollPane(resumenTable);
        scrollResumen.setPreferredSize(new Dimension(900, 200));
        scrollResumen.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(scrollResumen);
        centerPanel.add(Box.createVerticalStrut(20));

        // Panel de selección
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        inputPanel.setOpaque(false);
        inputPanel.setMaximumSize(new Dimension(700, 150));

        JLabel lblCategoria = new JLabel("Categoría:");
        comboCategoria = new JComboBox<>();
        for (Categoria cat : categoriasMap.values()) {
            comboCategoria.addItem(cat);
        }
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblProducto = new JLabel("Producto:");
        comboProducto = new JComboBox<>();
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        comboCategoria.addActionListener(e -> {
            Categoria selectedCategory = (Categoria) comboCategoria.getSelectedItem();
            comboProducto.removeAllItems();
            if (selectedCategory != null) {
                List<Producto> productos = productosPorCategoria.get(selectedCategory.getCategoriaId());
                if (productos != null) {
                    for (Producto p : productos) {
                        comboProducto.addItem(p);
                    }
                }
            }
        });

        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }


        JLabel lblCantidad = new JLabel("Cantidad:");
        JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spinnerCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblNotas = new JLabel("Notas:");
        JTextField txtNotas = new JTextField();
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        inputPanel.add(lblCategoria);
        inputPanel.add(comboCategoria);
        inputPanel.add(lblProducto);
        inputPanel.add(comboProducto);
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
            Producto selectedProducto = (Producto) comboProducto.getSelectedItem();
            if (selectedProducto == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Categoria selectedCategoria = (Categoria) comboCategoria.getSelectedItem();
            if (selectedCategoria == null) {
                 JOptionPane.showMessageDialog(this, "No hay categoría seleccionada. Revisa la carga de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            int cantidad = (Integer) spinnerCantidad.getValue();
            String notas = txtNotas.getText().trim();

            tableModel.addRow(new Object[]{selectedProducto.getNombre(), selectedCategoria.getNombre(), cantidad, notas, selectedProducto.getId()});
            txtNotas.setText("");
            spinnerCantidad.setValue(1);
        });

        // Acción Confirmar - ¡Modificada para pasar los resúmenes!
        btnConfirmar.addActionListener(e -> {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "La comanda no contiene productos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas generar esta comanda?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    List<DetallePedidoDTO> detalles = new ArrayList<>();
                    StringBuilder productosResumen = new StringBuilder();
                    Set<String> categoriasUnicas = new HashSet<>(); // Usamos un Set para asegurar categorías únicas

                    String mesaSeleccionada = (String) comboMesa.getSelectedItem();
                    int numeroMesa = Integer.parseInt(mesaSeleccionada);

                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        int productoId = (int) tableModel.getValueAt(i, 4);
                        int cantidad = (int) tableModel.getValueAt(i, 2);
                        String notas = (String) tableModel.getValueAt(i, 3);
                        String nombreProducto = (String) tableModel.getValueAt(i, 0); // Nombre del producto desde la tabla
                        String nombreCategoria = (String) tableModel.getValueAt(i, 1); // Nombre de la categoría desde la tabla

                        DetallePedidoDTO detalle = new DetallePedidoDTO(productoId, cantidad, notas);
                        detalles.add(detalle);

                        // Construir el resumen de productos
                        if (productosResumen.length() > 0) {
                            productosResumen.append(", ");
                        }
                        productosResumen.append(cantidad).append(" ").append(nombreProducto);

                        // Añadir categoría al set para mantenerlas únicas
                        categoriasUnicas.add(nombreCategoria);
                    }

                    // Convertir el Set de categorías a una cadena separada por comas
                    String categoriasResumenStr = String.join(", ", categoriasUnicas);

                    // Crear el objeto Pedido
                    Pedido nuevoPedido = new Pedido();
                    nuevoPedido.setUsuarioId(usuarioId);
                    nuevoPedido.setMesa(numeroMesa);
                    nuevoPedido.setEstado("pendiente"); // Estado inicial

                    // Llama al controlador con todos los argumentos necesarios
                    // Asumiendo que crearPedido es el método del controlador que envuelve el DAO
                    pedidoController.crearPedido(nuevoPedido, detalles, productosResumen.toString(), categoriasResumenStr);

                    JOptionPane.showMessageDialog(this, "¡La comanda fue generada con éxito!", "Comanda Creada", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla de resumen
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error de conexión a base de datos al crear la comanda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado al crear la comanda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
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
            new WaiterPanelView(usuarioId).setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomPanel.add(btnVolver);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
}