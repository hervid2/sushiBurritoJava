package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.UsuarioController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
        setSize(913, 1050); // Mantenemos el tamaño que ha funcionado para la visibilidad
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Gestión de Usuarios", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Eliminamos el JScrollPane y agregamos centralPanel directamente a mainPanel
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        centralPanel.setOpaque(false);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Añadir un poco de espacio en la parte inferior del centralPanel
        mainPanel.add(centralPanel, BorderLayout.CENTER);

        // --- Panel de Creación de Usuarios ---
        JPanel createPanel = new JPanel();
        createPanel.setLayout(new BoxLayout(createPanel, BoxLayout.Y_AXIS));
        createPanel.setOpaque(false);
        TitledBorder createBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 2), "Crear Nuevo Usuario", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Yu Gothic Medium", Font.BOLD, 16), Color.DARK_GRAY);
        createPanel.setBorder(createBorder);
        createPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        createPanel.setMaximumSize(new Dimension(500, Short.MAX_VALUE)); // Permite que el panel se estire verticalmente si es necesario

        Dimension fieldSize = new Dimension(300, 40);

        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] {
            "Seleccionar rol", "Administrador", "Cocinero", "Mesero"
        });

        createPanel.add(createFieldPanel("Correo electrónico:", emailField, fieldSize));
        createPanel.add(Box.createVerticalStrut(10));
        createPanel.add(createFieldPanel("Nombre de usuario:", usernameField, fieldSize));
        createPanel.add(Box.createVerticalStrut(10));
        createPanel.add(createFieldPanel("Contraseña:", passwordField, fieldSize));
        createPanel.add(Box.createVerticalStrut(10));
        createPanel.add(createFieldPanel("Rol del usuario:", roleComboBox, fieldSize));
        createPanel.add(Box.createVerticalStrut(20));

        JButton createUserButton = new JButton("Crear Usuario");
        Color createButtonOriginalColor = new Color(255, 140, 0);
        Color hoverGreenColor = new Color(34, 139, 34); // Color verde institucional
        createUserButton.setBackground(createButtonOriginalColor);
        createUserButton.setForeground(Color.WHITE);
        createUserButton.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        createUserButton.setFocusPainted(false);
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createUserButton.setMaximumSize(fieldSize);

        createUserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                createUserButton.setBackground(hoverGreenColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createUserButton.setBackground(createButtonOriginalColor);
            }
        });

        createUserButton.addActionListener(e -> {
            String correo = emailField.getText().trim();
            String nombre = usernameField.getText().trim();
            String contrasena = new String(passwordField.getPassword()).trim();
            String rolSeleccionado = (String) roleComboBox.getSelectedItem();

            if (correo.isEmpty() || nombre.isEmpty() || contrasena.isEmpty() || rolSeleccionado.equals("Seleccionar rol")) {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos para crear un usuario.");
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
        createPanel.add(createUserButton);

        centralPanel.add(createPanel);
        centralPanel.add(Box.createVerticalStrut(30));

        // --- Panel de Eliminación de Usuarios ---
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.Y_AXIS));
        deletePanel.setOpaque(false);
        TitledBorder deleteBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(255, 69, 0), 2), "Eliminar Usuario Existente", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Yu Gothic Medium", Font.BOLD, 16), Color.DARK_GRAY);
        deletePanel.setBorder(deleteBorder);
        deletePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        deletePanel.setMaximumSize(new Dimension(500, Short.MAX_VALUE));

        JTextField deleteUserEmailField = new JTextField();
        deletePanel.add(createFieldPanel("Correo electrónico del usuario a eliminar:", deleteUserEmailField, fieldSize));
        deletePanel.add(Box.createVerticalStrut(10));

        JButton deleteUserButton = new JButton("Eliminar Usuario");
        Color deleteButtonOriginalColor = new Color(255, 69, 0);
        deleteUserButton.setBackground(deleteButtonOriginalColor);
        deleteUserButton.setForeground(Color.WHITE);
        deleteUserButton.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        deleteUserButton.setFocusPainted(false);
        deleteUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteUserButton.setMaximumSize(fieldSize);

        deleteUserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deleteUserButton.setBackground(hoverGreenColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deleteUserButton.setBackground(deleteButtonOriginalColor);
            }
        });

        deleteUserButton.addActionListener(e -> {
            String correoAEliminar = deleteUserEmailField.getText().trim();

            if (correoAEliminar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingresa el correo electrónico del usuario a eliminar.");
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas eliminar el usuario con correo: " + correoAEliminar + "?\nEsta acción es irreversible.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    UsuarioController controller = new UsuarioController();
                    boolean exito = controller.eliminarUsuario(correoAEliminar);
                    if (exito) {
                        JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente.");
                        deleteUserEmailField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró un usuario con ese correo o no se pudo eliminar.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar usuario:\n" + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        deletePanel.add(deleteUserButton);

        centralPanel.add(deletePanel);
        centralPanel.add(Box.createVerticalStrut(30)); // Espacio entre secciones

        // --- NUEVO PANEL: Actualización de Correo de Usuarios ---
        JPanel updateEmailPanel = new JPanel();
        updateEmailPanel.setLayout(new BoxLayout(updateEmailPanel, BoxLayout.Y_AXIS));
        updateEmailPanel.setOpaque(false);
        TitledBorder updateBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2), "Actualizar Correo de Usuario", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Yu Gothic Medium", Font.BOLD, 16), Color.DARK_GRAY);
        updateEmailPanel.setBorder(updateBorder);
        updateEmailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateEmailPanel.setMaximumSize(new Dimension(500, Short.MAX_VALUE));

        JTextField oldEmailField = new JTextField();
        JTextField newEmailField = new JTextField();

        updateEmailPanel.add(createFieldPanel("Correo actual del usuario:", oldEmailField, fieldSize));
        updateEmailPanel.add(Box.createVerticalStrut(10));
        updateEmailPanel.add(createFieldPanel("Nuevo correo electrónico:", newEmailField, fieldSize));
        updateEmailPanel.add(Box.createVerticalStrut(10));

        JButton updateEmailButton = new JButton("Actualizar Correo");
        Color updateButtonOriginalColor = new Color(34, 139, 34); // Color verde
        updateEmailButton.setBackground(updateButtonOriginalColor);
        updateEmailButton.setForeground(Color.WHITE);
        updateEmailButton.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        updateEmailButton.setFocusPainted(false);
        updateEmailButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateEmailButton.setMaximumSize(fieldSize);

        updateEmailButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                updateEmailButton.setBackground(new Color(46, 184, 46)); // Un verde un poco más claro al hacer hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateEmailButton.setBackground(updateButtonOriginalColor);
            }
        });

        updateEmailButton.addActionListener(e -> {
            String oldEmail = oldEmailField.getText().trim();
            String newEmail = newEmailField.getText().trim();

            if (oldEmail.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingresa el correo actual y el nuevo correo.");
                return;
            }
            if (oldEmail.equals(newEmail)) {
                 JOptionPane.showMessageDialog(this, "El nuevo correo no puede ser igual al actual.");
                return;
            }

            try {
                UsuarioController controller = new UsuarioController();
                boolean exito = controller.actualizarCorreoUsuario(oldEmail, newEmail);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Correo de usuario actualizado exitosamente.");
                    oldEmailField.setText("");
                    newEmailField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró un usuario con el correo actual o el nuevo correo ya está en uso.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar el correo del usuario:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });
        updateEmailPanel.add(updateEmailButton);

        centralPanel.add(updateEmailPanel); // Añadir el nuevo panel al centralPanel
        centralPanel.add(Box.createVerticalStrut(30));

        // Mover el backLabel aquí, debajo de los formularios y antes del footer
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
            @Override
            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(hoverGreenColor); // Cambiar color al hacer hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(Color.BLACK); // Volver al color original
            }
        });
        centralPanel.add(backLabel);


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
        panel.setAlignmentX(Component.CENTER_ALIGNMENT); // Alinear el contenido del panel de campo al centro

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar la etiqueta dentro de su panel
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(label);

        inputField.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        inputField.setMaximumSize(size);
        inputField.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el campo de entrada dentro de su panel
        panel.add(inputField);

        panel.setMaximumSize(new Dimension(size.width, panel.getPreferredSize().height)); // Ajustar el tamaño máximo del panel al contenido
        return panel;
    }
}