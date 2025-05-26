package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MenuManagement extends JFrame {

    private JTable table;
    private JTextField searchField;
    
    private String[] categorias = {"Infantil", "Bebidas", "Postres", "Entradas", "Platos Fuertes", "Acompañamientos"};

    public MenuManagement() {
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

        setTitle("Gestión de Carta - Sushi Burrito");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 10));
        mainPanel.setBackground(new Color(255, 250, 205)); // Un color crema suave
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

        // Los botones "Agregar", "Editar", "Eliminar"
        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // 3 columnas para estos botones
        actionButtonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Agregar");
        JButton editButton = createStyledButton("Editar");
        JButton deleteButton = createStyledButton("Eliminar");
        
        actionButtonPanel.add(addButton);
        actionButtonPanel.add(editButton);
        actionButtonPanel.add(deleteButton);
        
        leftPanel.add(actionButtonPanel, BorderLayout.NORTH); // Los botones de acción van al NORTE del leftPanel

        // Configuración de la tabla con la nueva columna "Categoría"
        table = new JTable();
        String[] columns = {"Nombre", "Ingredientes", "Valor Neto", "IVA", "Valor Venta", "Categoría"}; // Agregada "Categoría"
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table.setModel(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(leftPanel, BorderLayout.CENTER);

        // --- Panel para el campo de búsqueda y su label (anteriormente panel de resumen) ---
        JPanel searchAndClearPanel = new JPanel();
        searchAndClearPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Alineación a la derecha
        searchAndClearPanel.setPreferredSize(new Dimension(250, 80)); // Ajusta el tamaño para dos componentes
        searchAndClearPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filtrar por nombre:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchAndClearPanel.add(filterLabel);

        searchField = new JTextField(15); // Tamaño del campo de texto
        searchField.setToolTipText("Buscar por nombre...");
        searchAndClearPanel.add(searchField);
        
        JButton clearButton = createStyledButton("Limpiar"); // El botón limpiar se mueve aquí
        searchAndClearPanel.add(clearButton);

        centerPanel.add(searchAndClearPanel, BorderLayout.EAST); // Agregado a la derecha

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel backLabel = new JLabel("← Volver al menú anterior", SwingConstants.CENTER);
        backLabel.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Asumiendo que AdminPanelView está en el mismo paquete o accesible
                AdminPanelView adminPanel = new AdminPanelView();
                adminPanel.setVisible(true);
                dispose();
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
        clearButton.addActionListener(e -> searchField.setText("")); // Limpia el campo de búsqueda

        // Efectos hover (aplicados a los botones de acción y al nuevo botón "Limpiar")
        agregarEfectoHover(addButton);
        agregarEfectoHover(editButton);
        agregarEfectoHover(deleteButton);
        agregarEfectoHover(clearButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 128, 0)); // Verde oscuro
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private void agregarEfectoHover(JButton button) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 140, 0)); // Naranja brillante
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(200, 100, 0)); // Naranja más oscuro al presionar
            }
        });
    }

    private void mostrarVentanaEditarProducto() {
        JDialog editDialog = crearVentanaProducto("Editar Producto", true);
        // El botón de guardar es el último componente en el panel del diálogo,
        // ahora con 7 filas de datos y 2 botones, el índice es 14 (7*2 = 14, 0-indexed)
        JButton save = (JButton) ((JPanel) editDialog.getContentPane().getComponent(0)).getComponent(12); 
        save.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Deseas guardar los cambios del producto?",
                    "Confirmación de edición",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Producto editado exitosamente.");
                editDialog.dispose();
            }
        });
        editDialog.setVisible(true);
    }

    private void mostrarVentanaAgregarProducto() {
        JDialog addDialog = crearVentanaProducto("Agregar Producto", false);
        // El botón de guardar es el último componente en el panel del diálogo,
        // ahora con 7 filas de datos y 2 botones, el índice es 14 (7*2 = 14, 0-indexed)
        JButton save = (JButton) ((JPanel) addDialog.getContentPane().getComponent(0)).getComponent(12);
        save.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Deseas agregar este nuevo producto?",
                    "Confirmación de agregado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
                addDialog.dispose();
            }
        });
        addDialog.setVisible(true);
    }

    /**
     * Crea una ventana de diálogo para agregar o editar un producto.
     * @param titulo El título de la ventana de diálogo.
     * @param esEdicion Booleano que indica si la ventana es para edición (true) o para agregar (false).
     * @return El JDialog configurado.
     */
    private JDialog crearVentanaProducto(String titulo, boolean esEdicion) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(500, 400); // Aumentado el tamaño para la nueva fila
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10)); // Cambiado a 7 filas para la categoría
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(esEdicion ? "Nombre Producto" : "");
        JTextField ingredientsField = new JTextField(esEdicion ? "Ingredientes" : "");
        JTextField netValueField = new JTextField(esEdicion ? "10000" : "");
        JTextField taxValueField = new JTextField(esEdicion ? "1900" : "");
        JTextField saleValueField = new JTextField(esEdicion ? "11900" : "");
        JComboBox<String> categoryComboBox = new JComboBox<>(categorias);

        panel.add(new JLabel("Nombre:"));
        panel.add(nameField);
        panel.add(new JLabel("Ingredientes:"));
        panel.add(ingredientsField);
        panel.add(new JLabel("Valor Neto:"));
        panel.add(netValueField);
        panel.add(new JLabel("IVA ($):"));
        panel.add(taxValueField);
        panel.add(new JLabel("Valor Venta:"));
        panel.add(saleValueField);
        panel.add(new JLabel("Categoría:")); // Nuevo label para la categoría
        panel.add(categoryComboBox); // Nuevo ComboBox para la categoría

        JButton saveButton = new JButton(esEdicion ? "Guardar cambios" : "Agregar producto");
        JButton cancelButton = new JButton("Cancelar");
        panel.add(saveButton);
        panel.add(cancelButton);

        saveButton.setBackground(new Color(0, 128, 0));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        return dialog;
    }

    private void mostrarConfirmacionEliminar() {
        int result = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar este producto?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Producto eliminado.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}