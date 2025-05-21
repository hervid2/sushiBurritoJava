package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import com.toedter.calendar.JDateChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class SalesStatisticsView extends JFrame {

    private JTable salesTable;

    public SalesStatisticsView() {
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

        setTitle("Estadísticas de Ventas");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Estadísticas de Ventas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 26));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Botón de retorno
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
        centerPanel.add(backLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Selección de periodo
        JPanel periodPanel = new JPanel();
        periodPanel.setOpaque(false);
        periodPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        String[] periodOptions = {"Seleccionar período", "Diario", "Mensual"};
        JComboBox<String> periodComboBox = new JComboBox<>(periodOptions);
        periodComboBox.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        periodComboBox.setPreferredSize(new Dimension(200, 30));
        periodPanel.add(new JLabel("Periodo:"));
        periodPanel.add(periodComboBox);
        centerPanel.add(periodPanel);

        // Rangos de fechas con JCalendar
        JPanel datePanel = new JPanel();
        datePanel.setOpaque(false);
        datePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd/MM/yyyy");
        startDateChooser.setPreferredSize(new Dimension(150, 30));

        JDateChooser endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd/MM/yyyy");
        endDateChooser.setPreferredSize(new Dimension(150, 30));

        datePanel.add(new JLabel("Desde:"));
        datePanel.add(startDateChooser);
        datePanel.add(new JLabel("Hasta:"));
        datePanel.add(endDateChooser);
        centerPanel.add(datePanel);

        // Tabla de historial de ventas
        String[] columnNames = {"Fecha", "Productos", "Valor Total"};
        salesTable = new JTable(new DefaultTableModel(columnNames, 0));
        salesTable.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        salesTable.setRowHeight(25);

        JScrollPane tableScrollPane = new JScrollPane(salesTable);
        tableScrollPane.setPreferredSize(new Dimension(850, 250));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(tableScrollPane);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton generateButton = new JButton("Generar Estadísticas");
        JButton exportPDFButton = new JButton("Exportar a PDF");

        for (JButton button : new JButton[]{generateButton, exportPDFButton}) {
            button.setFont(new Font("Yu Gothic Medium", Font.BOLD, 14));
            button.setBackground(new Color(255, 140, 0));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(200, 40));
            buttonPanel.add(button);
        }

        // Acciones
        generateButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Lógica para generar estadísticas no implementada aún.");
        });

        exportPDFButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar PDF de estadísticas");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                generatePDF(selectedFile);
            }
        });

        centerPanel.add(buttonPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel copyright =
                new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyright.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 12));
        footerPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        footerPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        footerPanel.add(Box.createVerticalStrut(10), BorderLayout.EAST);
        footerPanel.add(Box.createVerticalStrut(10), BorderLayout.WEST);
        footerPanel.add(copyright, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void generatePDF(File file) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // Color de fondo
            content.setNonStrokingColor(255, 250, 205);
            content.addRect(0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
            content.fill();

            // Logo
            try {
                PDImageXObject logo = PDImageXObject.createFromFile("/main/resources/images/icons/logo.jpg", document);
                content.drawImage(logo, 50, PDRectangle.A4.getHeight() - 100, 80, 80);
            } catch (IOException ex) {
                System.err.println("No se pudo cargar el logo: " + ex.getMessage());
            }

            // Título
            
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);  
            content.beginText();
            content.setNonStrokingColor(Color.BLACK);
            content.newLineAtOffset(150, PDRectangle.A4.getHeight() - 60);
            content.showText("Estadísticas de Ventas");
            content.endText();

            // Placeholder de contenido
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            content.newLineAtOffset(50, PDRectangle.A4.getHeight() - 150);
            content.showText("Aquí irá el contenido de las estadísticas de ventas.");
            content.endText();

            content.close();

            document.save(file);
            JOptionPane.showMessageDialog(this, "PDF exportado exitosamente.");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar el PDF: " + ex.getMessage());
        }
    }
}

