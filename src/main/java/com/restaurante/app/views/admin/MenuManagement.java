package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.ProductoController;
import main.java.com.restaurante.app.models.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map; // Importar Map
import java.util.regex.Pattern;

public class MenuManagement extends JFrame {

    private JTable table;
    private JTextField searchField;
    private ProductoController productoController;
    private DefaultTableModel model;
    private String[] categoriasNombres; // Array de nombres de categorías para JComboBox
    private Map<Integer, String> categoriasIdNombreMap; // Mapa para obtener el nombre de la categoría por su ID
    private Map<String, Integer> categoriasNombreIdMap; // Mapa para obtener el ID de la categoría por su nombre

    /**
     * Constructor de MenuManagement.
     * Inicializa el controlador de productos y carga las categorías desde la base de datos.
     */
    public MenuManagement() {
        try {
            this.productoController = new ProductoController();
            // Cargar los nombres de las categorías para el JComboBox (orden alfabético por el DAO)
            this.categoriasNombres = productoController.obtenerCategorias();
            // Cargar los mapas para la conversión eficiente entre ID y nombre de categoría
            this.categoriasIdNombreMap = productoController.obtenerMapaCategoriasIdNombre();
            this.categoriasNombreIdMap = productoController.obtenerMapaCategoriasNombreId();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos o al cargar categorías: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Si hay un error de conexión, se puede optar por salir de la aplicación o deshabilitar funcionalidades
            return;
        }
        setupUI();
        cargarProductosEnTabla();
    }

    /**
     * Configura la interfaz de usuario de la ventana de gestión de menú.
     */
    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception ex) {
            System.err.println("Error al configurar FlatLaf: " + ex.getMessage());
        }

        setTitle("Gestión de Carta - Sushi Burrito");
        setSize(1150, 700);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana

        JPanel mainPanel = new JPanel(new BorderLayout(20, 10));
        mainPanel.setBackground(new Color(255, 250, 205)); // Amarillo claro
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Gestión de Productos en Carta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        actionButtonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Agregar");
        JButton editButton = createStyledButton("Editar");
        JButton deleteButton = createStyledButton("Eliminar");

        Dimension buttonSize = new Dimension(100, 45);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);

        actionButtonPanel.add(addButton);
        actionButtonPanel.add(editButton);
        actionButtonPanel.add(deleteButton);

        leftPanel.add(actionButtonPanel, BorderLayout.NORTH);

        table = new JTable();
        // Se agregó la columna "ID" para almacenar el producto_id, la cual se ocultará.
        String[] columns = {"ID", "Nombre", "Ingredientes", "Valor Neto", "Impuesto Consumo", "Valor Venta", "Categoría"};
        model = new DefaultTableModel(columns, 0);
        table.setModel(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Ocultar la columna de ID (índice 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0); // También establecer el ancho preferido
        table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0); // Ocultar encabezado
        table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0); // Ocultar encabezado


        centerPanel.add(leftPanel, BorderLayout.CENTER);

        JPanel searchAndClearPanel = new JPanel();
        searchAndClearPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchAndClearPanel.setPreferredSize(new Dimension(250, 80));
        searchAndClearPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filtrar por nombre:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchAndClearPanel.add(filterLabel);

        searchField = new JTextField(15);
        searchField.setToolTipText("Buscar por nombre...");
        searchAndClearPanel.add(searchField);

        JButton clearButton = createStyledButton("Limpiar");
        searchAndClearPanel.add(clearButton);

        // Listener para el campo de búsqueda para filtrar la tabla dinámicamente
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
        });

        centerPanel.add(searchAndClearPanel, BorderLayout.EAST);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel backLabel = new JLabel("← Volver al menú anterior", SwingConstants.CENTER);
        backLabel.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navegar de vuelta al panel de administrador
                main.java.com.restaurante.app.views.admin.AdminPanelView adminPanel = new main.java.com.restaurante.app.views.admin.AdminPanelView();
                adminPanel.setVisible(true);
                dispose(); // Cierra la ventana actual
            }
        });

        JLabel copyright = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        footerPanel.add(backLabel, BorderLayout.NORTH);
        footerPanel.add(copyright, BorderLayout.SOUTH);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Action Listeners para los botones
        addButton.addActionListener(e -> mostrarVentanaAgregarProducto());
        editButton.addActionListener(e -> mostrarVentanaEditarProducto());
        deleteButton.addActionListener(e -> mostrarConfirmacionEliminar());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            filtrarTabla(); // Limpiar el filtro
        });

        // Añadir efectos hover a los botones
        agregarEfectoHover(addButton);
        agregarEfectoHover(editButton);
        agregarEfectoHover(deleteButton);
        agregarEfectoHover(clearButton);
    }

    /**
     * Muestra una ventana de diálogo para agregar un nuevo producto.
     */
    private void mostrarVentanaAgregarProducto() {
        JTextField nombre = new JTextField();
        JTextField ingredientes = new JTextField();
        JTextField valorNeto = new JTextField();
        JTextField impuestoConsumo = new JTextField();
        JTextField valorVenta = new JTextField();
        JComboBox<String> categoriaBox = new JComboBox<>(categoriasNombres); // Usar el array de nombres de categorías

        impuestoConsumo.setEditable(false); // Campo no editable, se calcula
        valorVenta.setEditable(false);     // Campo no editable, se calcula

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nombre:")); panel.add(nombre);
        panel.add(new JLabel("Ingredientes:")); panel.add(ingredientes);
        panel.add(new JLabel("Valor Neto:")); panel.add(valorNeto);
        panel.add(new JLabel("Impuesto Consumo (8%):")); panel.add(impuestoConsumo);
        panel.add(new JLabel("Valor Venta:")); panel.add(valorVenta);
        panel.add(new JLabel("Categoría:")); panel.add(categoriaBox);

        // Runnable para actualizar los campos calculados automáticamente
        Runnable updateCalculatedFields = () -> {
            try {
                String valorNetoStr = valorNeto.getText().trim();
                if (valorNetoStr.isEmpty()) {
                    impuestoConsumo.setText("");
                    valorVenta.setText("");
                    return;
                }
                double neto = Double.parseDouble(valorNetoStr.replace(',', '.')); // Soporte para coma o punto decimal
                 if (neto < 0) {
                     JOptionPane.showMessageDialog(this, "El valor neto no puede ser negativo.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
                     // Eliminar el último carácter ingresado para corregir el negativo
                     valorNeto.setText(valorNetoStr.substring(0, Math.max(0, valorNetoStr.length() -1 )));
                     impuestoConsumo.setText("");
                     valorVenta.setText("");
                     return;
                }

                double calculatedImpuesto = neto * 0.08;
                double calculatedVenta = neto + calculatedImpuesto;

                impuestoConsumo.setText(String.format(Locale.US, "%.2f", calculatedImpuesto));
                valorVenta.setText(String.format(Locale.US, "%.2f", calculatedVenta));

            } catch (NumberFormatException nfe) {
                // Si el valor neto no es un número válido, borrar los campos calculados
                impuestoConsumo.setText("");
                valorVenta.setText("");
            }
        };

        // Listener para actualizar los campos calculados cuando el valor neto cambia
        valorNeto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCalculatedFields.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCalculatedFields.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCalculatedFields.run(); }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Nuevo Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // Validar campos obligatorios
            if (nombre.getText().trim().isEmpty() || valorNeto.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor completa los campos obligatorios: Nombre y Valor Neto.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String valorNetoStr = valorNeto.getText().trim().replace(',', '.');
                double neto = Double.parseDouble(valorNetoStr);

                if (neto < 0) {
                    JOptionPane.showMessageDialog(this, "El valor neto no puede ser negativo.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Producto nuevo = new Producto();
                nuevo.setNombre(nombre.getText().trim());
                nuevo.setIngredientes(ingredientes.getText().trim());
                nuevo.setValorNeto(neto);

                double impuestoCalculado = neto * 0.08;
                double valorVentaCalculado = neto + impuestoCalculado;

                nuevo.setImpuesto(impuestoCalculado);
                nuevo.setValorVenta(valorVentaCalculado);

                // Obtener el ID de la categoría del mapa usando el nombre seleccionado
                String selectedCategoryName = (String) categoriaBox.getSelectedItem();
                Integer categoriaId = categoriasNombreIdMap.get(selectedCategoryName);
                if (categoriaId == null) {
                    throw new SQLException("Categoría seleccionada no encontrada en el mapa: " + selectedCategoryName);
                }
                nuevo.setCategoriaId(categoriaId);

                productoController.insertar(nuevo); // Insertar el nuevo producto
                cargarProductosEnTabla(); // Recargar la tabla para mostrar el nuevo producto
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor Neto inválido. Debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al insertar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprimir el stack trace para depuración
            }
        }
    }

    /**
     * Muestra una ventana de diálogo para editar un producto existente.
     * Carga los datos del producto seleccionado en los campos de edición.
     */
    private void mostrarVentanaEditarProducto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        // Recuperar el producto_id de la columna oculta (índice 0)
        int productoId = (int) model.getValueAt(modelRow, 0);

        // Recuperar otros valores, ajustando los índices debido a la nueva columna "ID"
        String nombreSeleccionado = model.getValueAt(modelRow, 1).toString();
        String ingredientesSeleccionado = model.getValueAt(modelRow, 2).toString();
        String valorNetoSeleccionado = model.getValueAt(modelRow, 3).toString(); // Ahora en el índice 3
        String categoriaActualTabla = model.getValueAt(modelRow, 6).toString(); // Ahora en el índice 6, este es el nombre de la tabla

        JTextField nombre = new JTextField(nombreSeleccionado);
        JTextField ingredientes = new JTextField(ingredientesSeleccionado);
        JTextField valorNeto = new JTextField(); // Se establecerá y activará el listener
        JTextField impuestoConsumo = new JTextField();
        JTextField valorVenta = new JTextField();
        JComboBox<String> categoriaBox = new JComboBox<>(categoriasNombres); // Usar el array de nombres de categorías
        categoriaBox.setSelectedItem(categoriaActualTabla); // Establecer la categoría actualmente mostrada

        impuestoConsumo.setEditable(false);
        valorVenta.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nombre:")); panel.add(nombre);
        panel.add(new JLabel("Ingredientes:")); panel.add(ingredientes);
        panel.add(new JLabel("Valor Neto:")); panel.add(valorNeto);
        panel.add(new JLabel("Impuesto Consumo (8%):")); panel.add(impuestoConsumo);
        panel.add(new JLabel("Valor Venta:")); panel.add(valorVenta);
        panel.add(new JLabel("Categoría:")); panel.add(categoriaBox);

        Runnable updateCalculatedFields = () -> {
            try {
                String valorNetoStr = valorNeto.getText().trim();
                if (valorNetoStr.isEmpty()) {
                    impuestoConsumo.setText("");
                    valorVenta.setText("");
                    return;
                }
                double neto = Double.parseDouble(valorNetoStr.replace(',', '.'));
                 if (neto < 0) {
                     JOptionPane.showMessageDialog(this, "El valor neto no puede ser negativo.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
                     valorNeto.setText(valorNetoStr.substring(0, Math.max(0, valorNetoStr.length() -1 )));
                     impuestoConsumo.setText("");
                     valorVenta.setText("");
                     return;
                }
                double calculatedImpuesto = neto * 0.08;
                double calculatedVenta = neto + calculatedImpuesto;

                impuestoConsumo.setText(String.format(Locale.US, "%.2f", calculatedImpuesto));
                valorVenta.setText(String.format(Locale.US, "%.2f", calculatedVenta));
            } catch (NumberFormatException nfe) {
                impuestoConsumo.setText("");
                valorVenta.setText("");
            }
        };

        valorNeto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCalculatedFields.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCalculatedFields.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCalculatedFields.run(); }
        });

        // Establecer el valor neto inicial, esto activará el listener para poblar otros campos
        valorNeto.setText(valorNetoSeleccionado);

        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (nombre.getText().trim().isEmpty() || valorNeto.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor completa los campos obligatorios: Nombre y Valor Neto.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String valorNetoStr = valorNeto.getText().trim().replace(',', '.');
                double neto = Double.parseDouble(valorNetoStr);

                if (neto < 0) {
                    JOptionPane.showMessageDialog(this, "El valor neto no puede ser negativo.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Producto actualizado = new Producto();
                actualizado.setId(productoId); // Establecer el ID real del producto para la operación de actualización
                actualizado.setNombre(nombre.getText().trim());
                actualizado.setIngredientes(ingredientes.getText().trim());
                actualizado.setValorNeto(neto);

                double impuestoCalculado = neto * 0.08;
                double valorVentaCalculado = neto + impuestoCalculado;

                actualizado.setImpuesto(impuestoCalculado);
                actualizado.setValorVenta(valorVentaCalculado);

                // Obtener el ID de la categoría del mapa usando el nombre seleccionado
                String selectedCategoryName = (String) categoriaBox.getSelectedItem();
                Integer categoriaId = categoriasNombreIdMap.get(selectedCategoryName);
                if (categoriaId == null) {
                    throw new SQLException("Categoría seleccionada no encontrada en el mapa: " + selectedCategoryName);
                }
                actualizado.setCategoriaId(categoriaId);

                productoController.actualizar(actualizado); // Actualizar el producto
                cargarProductosEnTabla(); // Recargar la tabla
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor Neto inválido. Debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Carga todos los productos de la base de datos en la tabla.
     */
    private void cargarProductosEnTabla() {
        try {
            List<Producto> productos = productoController.obtenerTodos();
            model.setRowCount(0); // Limpiar los datos existentes en la tabla
            for (Producto p : productos) {
                // Obtener el nombre de la categoría del mapa usando el ID de la categoría del producto
                String categoriaNombre = categoriasIdNombreMap.getOrDefault(p.getCategoriaId(), "Desconocida");

                model.addRow(new Object[]{
                    p.getId(), // Añadir el ID del producto como el primer elemento (columna oculta)
                    p.getNombre(),
                    p.getIngredientes(),
                    String.format(Locale.US, "%.2f", p.getValorNeto()), // Formatear para mostrar
                    String.format(Locale.US, "%.2f", p.getImpuesto()), // Formatear para mostrar
                    String.format(Locale.US, "%.2f", p.getValorVenta()), // Formatear para mostrar
                    categoriaNombre // Usar el nombre de categoría obtenido del mapa
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Filtra la tabla de productos basándose en el texto ingresado en el campo de búsqueda.
     */
    private void filtrarTabla() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        String texto = searchField.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null); // No hay filtro si el campo está vacío
        } else {
            // Filtro insensible a mayúsculas/minúsculas en la columna 'Nombre' (índice 1)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 1));
        }
    }

    /**
     * Crea un JButton con estilo predefinido.
     * @param text El texto del botón.
     * @return El botón con estilo.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 128, 0)); // Verde
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    /**
     * Agrega efectos de hover y presionado a un botón.
     * @param button El JButton al que se le aplicarán los efectos.
     */
    private void agregarEfectoHover(JButton button) {
        Color originalColor = button.getBackground();
        Color hoverColor = new Color(255, 140, 0); // Naranja para hover
        Color pressedColor = new Color(200, 100, 0); // Naranja más oscuro para presionado

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
            }
            public void mouseReleased(MouseEvent e) { // Restaurar el color de hover después de presionar
                if (button.getBounds().contains(e.getPoint())) {
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(originalColor);
                }
            }
        });
    }

    /**
     * Muestra un diálogo de confirmación para eliminar un producto seleccionado.
     */
    private void mostrarConfirmacionEliminar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);

        // Obtener el product_id de la columna oculta (índice 0)
        int productIdToDelete = (int) model.getValueAt(modelRow, 0);
        String nombreSeleccionado = model.getValueAt(modelRow, 1).toString(); // El nombre ahora está en el índice 1

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar el producto '" + nombreSeleccionado + "'?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productoController.eliminar(productIdToDelete); // Usar el ID de producto recuperado
                cargarProductosEnTabla(); // Refrescar la tabla
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}