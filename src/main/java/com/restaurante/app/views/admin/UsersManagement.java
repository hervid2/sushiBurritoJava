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

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        
        Dimension fieldSize = new Dimension(300, 44);

        JTextField emailField = new JTextField();
        emailField.setToolTipText("Ingrese un correo electrónico válido");
        emailField.putClientProperty("JTextField.placeholderText", "Correo electrónico");
        emailField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        emailField.setMaximumSize(fieldSize);
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(10));

        JTextField usernameField = new JTextField();
        usernameField.setToolTipText("Ingrese el nombre de usuario");
        usernameField.putClientProperty("JTextField.placeholderText", "Nombre de usuario");
        usernameField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        usernameField.setMaximumSize(fieldSize);
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setToolTipText("Ingrese una contraseña segura");
        passwordField.putClientProperty("JTextField.placeholderText", "Contraseña");
        passwordField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        passwordField.setMaximumSize(fieldSize);
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));

        JComboBox<String> roleComboBox = new JComboBox<>(new String[] {
            "Seleccionar rol", "Administrador", "Cocinero", "Mesero"
        });
        roleComboBox.setToolTipText("Seleccione un rol para el nuevo usuario");
        roleComboBox.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        roleComboBox.setMaximumSize(fieldSize);
        formPanel.add(roleComboBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JButton createUserButton = new JButton("Crear Usuario");
        createUserButton.setBackground(new Color(255, 140, 0));
        createUserButton.setForeground(Color.WHITE);
        createUserButton.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        createUserButton.setFocusPainted(false);
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createUserButton.setPreferredSize(new Dimension(520, 140));

        createUserButton.addMouseListener(new MouseAdapter() {
            Color originalColor = createUserButton.getBackground();
            Font originalFont = createUserButton.getFont();

            @Override
            public void mouseEntered(MouseEvent e) {
                createUserButton.setBackground(new Color(0, 128, 0));
                createUserButton.setFont(originalFont.deriveFont(18f));
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

        formPanel.add(createUserButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Bienvenido al panel de administración.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel copyrightLabel = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 12));

        bottomPanel.add(welcomeLabel, BorderLayout.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
}



