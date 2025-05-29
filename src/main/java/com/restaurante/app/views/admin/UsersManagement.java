package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.UsuarioController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class UsersManagement extends JFrame {
    public UsersManagement() {
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

        setTitle("Gestión de Usuarios");
        setSize(913, 663);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Formulario de Registro de Usuarios", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel backLabel = new JLabel("← Volver al menú anterior", SwingConstants.CENTER);
        backLabel.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AdminPanelView adminPanel = new AdminPanelView();
                adminPanel.setVisible(true);
                dispose();
            }
        });

        Dimension fieldSize = new Dimension(300, 40);

        // Email
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] {
            "Seleccionar rol", "Administrador", "Cocinero", "Mesero"
        });

        formContainer.add(createFieldPanel("Correo electrónico:", emailField, fieldSize));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldPanel("Nombre de usuario:", usernameField, fieldSize));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldPanel("Contraseña:", passwordField, fieldSize));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldPanel("Rol del usuario:", roleComboBox, fieldSize));

        formContainer.add(backLabel);
        formContainer.add(Box.createVerticalStrut(20));

        JButton createUserButton = new JButton("Crear Usuario");
        createUserButton.setBackground(new Color(255, 140, 0));
        createUserButton.setForeground(Color.WHITE);
        createUserButton.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        createUserButton.setFocusPainted(false);
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createUserButton.setMaximumSize(fieldSize);

        // Acción del botón para registrar usuario
        createUserButton.addActionListener(e -> {
            String correo = emailField.getText().trim();
            String nombre = usernameField.getText().trim();
            String contrasena = new String(passwordField.getPassword()).trim();
            String rolSeleccionado = (String) roleComboBox.getSelectedItem();

            if (rolSeleccionado.equals("Seleccionar rol")) {
                JOptionPane.showMessageDialog(this, "Por favor selecciona un rol válido.");
                return;
            }

            try {
                UsuarioController controller = new UsuarioController();
                boolean exito = controller.registrarUsuario(nombre, correo, rolSeleccionado.toLowerCase(), contrasena);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
                    emailField.setText("");
                    usernameField.setText("");
                    passwordField.setText("");
                    roleComboBox.setSelectedIndex(0);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar usuario:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        formContainer.add(createUserButton);
        mainPanel.add(formContainer, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        JLabel copyrightLabel = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 12));
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldPanel(String labelText, JComponent inputField, Dimension size) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(size.width, Short.MAX_VALUE));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(label);

        inputField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        inputField.setMaximumSize(size);
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(inputField);

        return panel;
    }
}
