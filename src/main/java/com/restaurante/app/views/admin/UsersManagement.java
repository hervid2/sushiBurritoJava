package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        // Panel contenedor principal para el formulario
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

        // Email Field with Label
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setOpaque(false);
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailPanel.setMaximumSize(new Dimension(fieldSize.width, Short.MAX_VALUE));
        
        JLabel emailLabel = new JLabel("Correo electrónico:");
        emailLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        emailPanel.add(emailLabel);
        
        JTextField emailField = new JTextField();
        emailField.setToolTipText("Ingrese un correo electrónico válido");
        emailField.putClientProperty("JTextField.placeholderText", "Ingrese el correo electrónico");
        emailField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        emailField.setMaximumSize(fieldSize);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(emailField);
        formContainer.add(emailPanel);
        formContainer.add(Box.createVerticalStrut(10));

        // Username Field with Label
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setOpaque(false);
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernamePanel.setMaximumSize(new Dimension(fieldSize.width, Short.MAX_VALUE));
        
        JLabel usernameLabel = new JLabel("Nombre de usuario:");
        usernameLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        usernamePanel.add(usernameLabel);
        
        JTextField usernameField = new JTextField();
        usernameField.setToolTipText("Ingrese el nombre de usuario");
        usernameField.putClientProperty("JTextField.placeholderText", "Ingrese el nombre de usuario");
        usernameField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        usernameField.setMaximumSize(fieldSize);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameField);
        formContainer.add(usernamePanel);
        formContainer.add(Box.createVerticalStrut(10));

        // Password Field with Label
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setOpaque(false);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(fieldSize.width, Short.MAX_VALUE));
        
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        passwordPanel.add(passwordLabel);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setToolTipText("Ingrese una contraseña segura");
        passwordField.putClientProperty("JTextField.placeholderText", "Ingrese la contraseña");
        passwordField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        passwordField.setMaximumSize(fieldSize);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordPanel.add(passwordField);
        formContainer.add(passwordPanel);
        formContainer.add(Box.createVerticalStrut(10));

        // Role ComboBox with Label
        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
        rolePanel.setOpaque(false);
        rolePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rolePanel.setMaximumSize(new Dimension(fieldSize.width, Short.MAX_VALUE));
        
        JLabel roleLabel = new JLabel("Rol del usuario:");
        roleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        rolePanel.add(roleLabel);
        
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] {
            "Seleccionar rol", "Administrador", "Cocinero", "Mesero"
        });
        roleComboBox.setToolTipText("Seleccione un rol para el nuevo usuario");
        roleComboBox.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        roleComboBox.setMaximumSize(fieldSize);
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolePanel.add(roleComboBox);
        formContainer.add(rolePanel);
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(backLabel);
        formContainer.add(Box.createVerticalStrut(20));

        // Create User Button
        JButton createUserButton = new JButton("Crear Usuario");
        createUserButton.setBackground(new Color(255, 140, 0));
        createUserButton.setForeground(Color.WHITE);
        createUserButton.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        createUserButton.setFocusPainted(false);
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createUserButton.setMaximumSize(fieldSize);

        createUserButton.addMouseListener(new MouseAdapter() {
            Color originalColor = createUserButton.getBackground();
            Font originalFont = createUserButton.getFont();

            @Override
            public void mouseEntered(MouseEvent e) {
                createUserButton.setBackground(new Color(0, 128, 0));
                createUserButton.setFont(originalFont.deriveFont(16f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createUserButton.setBackground(originalColor);
                createUserButton.setFont(originalFont);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    createUserButton,
                    "¿Estás seguro de que deseas crear este usuario?",
                    "Confirmar creación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                createUserButton.setBackground(new Color(0, 128, 0));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                createUserButton.setBackground(originalColor);
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
}
