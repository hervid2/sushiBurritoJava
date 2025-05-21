package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MenuManagement extends JFrame {

    private JTable table;
    private JLabel netTotalLabel, saleTotalLabel, taxTotalLabel;
    private JTextField searchField;

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
        mainPanel.setBackground(new Color(255, 250, 205));
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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Agregar");
        JButton editButton = createStyledButton("Editar");
        JButton deleteButton = createStyledButton("Eliminar");
        JButton clearButton = createStyledButton("Limpiar");
        searchField = new JTextField();
        searchField.setToolTipText("Buscar por nombre...");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchField);

        leftPanel.add(buttonPanel, BorderLayout.NORTH);

        table = new JTable();
        String[] columns = {"Nombre", "Ingredientes", "Valor Neto", "IVA", "Valor Venta"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table.setModel(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(leftPanel, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        summaryPanel.setPreferredSize(new Dimension(250, 150));
        summaryPanel.setOpaque(false);

        netTotalLabel = new JLabel("Total Neto: $0");
        saleTotalLabel = new JLabel("Total Venta: $0");
        taxTotalLabel = new JLabel("Total IVA: $0");

        Font summaryFont = new Font("Segoe UI", Font.BOLD, 16);
        netTotalLabel.setFont(summaryFont);
        saleTotalLabel.setFont(summaryFont);
        taxTotalLabel.setFont(summaryFont);

        summaryPanel.add(netTotalLabel);
        summaryPanel.add(saleTotalLabel);
        summaryPanel.add(taxTotalLabel);

        centerPanel.add(summaryPanel, BorderLayout.EAST);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel backLabel = new JLabel("← Volver al menú anterior", SwingConstants.CENTER);
        backLabel.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

        addButton.addActionListener(e -> mostrarVentanaAgregarProducto());
        editButton.addActionListener(e -> mostrarVentanaEditarProducto());
        deleteButton.addActionListener(e -> mostrarConfirmacionEliminar());
        clearButton.addActionListener(e -> searchField.setText(""));

        agregarEfectoHover(addButton);
        agregarEfectoHover(editButton);
        agregarEfectoHover(deleteButton);
        agregarEfectoHover(clearButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 128, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private void agregarEfectoHover(JButton button) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 140, 0));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(255, 140, 0));
            }
        });
    }

    private void mostrarVentanaEditarProducto() {
        JDialog editDialog = crearVentanaProducto("Editar Producto", true);
        JButton save = (JButton) ((JPanel) editDialog.getContentPane().getComponent(0)).getComponent(10);
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
        JButton save = (JButton) ((JPanel) addDialog.getContentPane().getComponent(0)).getComponent(10);
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

    private JDialog crearVentanaProducto(String titulo, boolean esEdicion) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField name = new JTextField(esEdicion ? "Nombre Producto" : "");
        JTextField ingredients = new JTextField(esEdicion ? "Ingredientes" : "");
        JTextField net = new JTextField(esEdicion ? "10000" : "");
        JTextField tax = new JTextField(esEdicion ? "1900" : "");
        JTextField sale = new JTextField(esEdicion ? "11900" : "");

        panel.add(new JLabel("Nombre:"));
        panel.add(name);
        panel.add(new JLabel("Ingredientes:"));
        panel.add(ingredients);
        panel.add(new JLabel("Valor Neto:"));
        panel.add(net);
        panel.add(new JLabel("IVA ($):"));
        panel.add(tax);
        panel.add(new JLabel("Valor Venta:"));
        panel.add(sale);

        JButton save = new JButton(esEdicion ? "Guardar cambios" : "Agregar producto");
        JButton cancel = new JButton("Cancelar");
        panel.add(save);
        panel.add(cancel);

        save.setBackground(new Color(0, 128, 0));
        save.setForeground(Color.WHITE);
        cancel.setBackground(Color.GRAY);
        cancel.setForeground(Color.WHITE);

        cancel.addActionListener(e -> dialog.dispose());

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


