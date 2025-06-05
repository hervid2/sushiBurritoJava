package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import com.toedter.calendar.JDateChooser;
import main.java.com.restaurante.app.controllers.EstadisticasController;
import main.java.com.restaurante.app.models.EstadisticaProductoDTO;
import main.java.com.restaurante.app.models.Factura;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SalesStatisticsView extends JFrame {

    private JTable salesTable;
    private JLabel masVendidoLabel;
    private JLabel menosVendidoLabel;
    private JLabel totalIngresosLabel;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;

    public SalesStatisticsView() {
        setupUI();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
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

        JPanel datePanel = new JPanel();
        datePanel.setOpaque(false);
        datePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd/MM/yyyy");
        startDateChooser.setPreferredSize(new Dimension(150, 30));

        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd/MM/yyyy");
        endDateChooser.setPreferredSize(new Dimension(150, 30));

        datePanel.add(new JLabel("Desde:"));
        datePanel.add(startDateChooser);
        datePanel.add(new JLabel("Hasta:"));
        datePanel.add(endDateChooser);
        centerPanel.add(datePanel);

        String[] columnNames = {"Fecha", "Productos", "Valor Total"};
        salesTable = new JTable(new DefaultTableModel(columnNames, 0));
        salesTable.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        salesTable.setRowHeight(25);

        JScrollPane tableScrollPane = new JScrollPane(salesTable);
        tableScrollPane.setPreferredSize(new Dimension(850, 250));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(tableScrollPane);

        masVendidoLabel = new JLabel("Producto más vendido: N/A");
        menosVendidoLabel = new JLabel("Producto menos vendido: N/A");
        totalIngresosLabel = new JLabel("Total ingresos: $0.00");

        for (JLabel label : new JLabel[]{masVendidoLabel, menosVendidoLabel, totalIngresosLabel}) {
            label.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
            centerPanel.add(label);
        }

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

        generateButton.addActionListener(e -> {
            try {
                Date desde = startDateChooser.getDate();
                Date hasta = endDateChooser.getDate();
                if (desde == null || hasta == null) {
                    JOptionPane.showMessageDialog(this, "Selecciona un rango de fechas válido.");
                    return;
                }

                LocalDateTime desdeLdt = desde.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime hastaLdt = hasta.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                EstadisticasController controller = new EstadisticasController();
                Map<String, Object> datos = controller.generarEstadisticas(desdeLdt, hastaLdt);

                List<Factura> facturas = (List<Factura>) datos.get("facturas");
                DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
                model.setRowCount(0);

                for (Factura f : facturas) {
                    model.addRow(new Object[]{
                            f.getFechaFactura().toLocalDate().toString(),
                            "Pedido ID: " + f.getPedidoId(),
                            "$" + String.format("%.2f", f.getTotal())
                    });
                }

                masVendidoLabel.setText("Producto más vendido: " + ((EstadisticaProductoDTO) datos.get("masVendido")).getNombreProducto());
                menosVendidoLabel.setText("Producto menos vendido: " + ((EstadisticaProductoDTO) datos.get("menosVendido")).getNombreProducto());
                totalIngresosLabel.setText("Total ingresos: $" + String.format("%.2f", (double) datos.get("totalIngresos")));

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al obtener estadísticas: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        exportPDFButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar PDF de estadísticas");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Asegurarse de que el archivo tenga la extensión .pdf
                String filePath = selectedFile.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    selectedFile = new File(filePath + ".pdf");
                }
                generatePDF(selectedFile);
            }
        });

        centerPanel.add(buttonPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        JLabel copyright = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyright.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 12));
        footerPanel.add(copyright, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void generatePDF(File file) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            // CORRECCIÓN AQUÍ: Usar java.awt.Color para definir el color de fondo
            content.setNonStrokingColor(new Color(255, 250, 205)); // Fondo de la página
            content.addRect(0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
            content.fill();

            // Logo (asegúrate de que la ruta sea correcta y el archivo exista)
            try {
                // Modifica esta línea si la ruta del logo no es absoluta o está en un JAR
                PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/images/icons/logo.jpg", document);
                content.drawImage(logo, 50, PDRectangle.A4.getHeight() - 100, 80, 80);
            } catch (IOException ex) {
                System.err.println("No se pudo cargar el logo: " + ex.getMessage());
                // Considera usar un placeholder o lanzar una excepción para avisar al usuario
            }

            // Título del documento
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
            content.beginText();
            content.setNonStrokingColor(Color.BLACK);
            content.newLineAtOffset(150, PDRectangle.A4.getHeight() - 60);
            content.showText("Estadísticas de Ventas");
            content.endText();

            // Resumen de estadísticas
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            content.newLineAtOffset(50, PDRectangle.A4.getHeight() - 170);
            content.showText(masVendidoLabel.getText());
            content.newLineAtOffset(0, -15);
            content.showText(menosVendidoLabel.getText());
            content.newLineAtOffset(0, -15);
            content.showText(totalIngresosLabel.getText());
            content.endText();

            // --- Dibujar la tabla de ventas ---
            DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
            int rowCount = model.getRowCount();
            int colCount = model.getColumnCount();

            float margin = 50;
            float yStart = PDRectangle.A4.getHeight() - 280; // Posición inicial Y para la tabla
            float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;
            float rowHeight = 20f;
            float cellMargin = 5f;

            // Anchos de columna (ajusta según tus necesidades)
            float[] colWidths = {tableWidth * 0.3f, tableWidth * 0.4f, tableWidth * 0.3f};

            // Dibujar encabezados de la tabla
            float yPosition = yStart;
            float xPosition = margin;

            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            for (int i = 0; i < colCount; i++) {
                content.setNonStrokingColor(Color.DARK_GRAY); // Color de fondo para encabezados
                content.addRect(xPosition, yPosition - rowHeight, colWidths[i], rowHeight);
                content.fill();
                content.setNonStrokingColor(Color.WHITE); // Color de texto para encabezados
                content.beginText();
                content.newLineAtOffset(xPosition + cellMargin, yPosition - rowHeight + cellMargin);
                content.showText(model.getColumnName(i));
                content.endText();
                xPosition += colWidths[i];
            }
            yPosition -= rowHeight;

            // Dibujar filas de la tabla
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            content.setNonStrokingColor(Color.BLACK); // Color de texto para las celdas

            for (int i = 0; i < rowCount; i++) {
                xPosition = margin;
                for (int j = 0; j < colCount; j++) {
                    // Dibujar el borde de la celda
                    content.setStrokingColor(Color.LIGHT_GRAY);
                    content.setLineWidth(0.5f);
                    content.addRect(xPosition, yPosition - rowHeight, colWidths[j], rowHeight);
                    content.stroke();

                    // Dibujar el texto de la celda
                    content.beginText();
                    content.newLineAtOffset(xPosition + cellMargin, yPosition - rowHeight + cellMargin);
                    Object value = model.getValueAt(i, j);
                    content.showText(value != null ? value.toString() : "");
                    content.endText();
                    xPosition += colWidths[j];
                }
                yPosition -= rowHeight;
                // Si la tabla supera el límite de la página, crear una nueva página (lógica simple, se puede mejorar)
                if (yPosition < margin + rowHeight) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    // CORRECCIÓN AQUÍ: Restablecer el color de fondo para la nueva página
                    content.setNonStrokingColor(new Color(255, 250, 205));
                    content.addRect(0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
                    content.fill();
                    yPosition = PDRectangle.A4.getHeight() - margin; // Reiniciar la posición Y
                    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9); // Restablecer la fuente
                    content.setNonStrokingColor(Color.BLACK); // Restablecer el color de texto
                }
            }
            // --- Fin de dibujar la tabla ---

            content.close();
            document.save(file);
            JOptionPane.showMessageDialog(this, "PDF exportado exitosamente.");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar el PDF: " + ex.getMessage());
        }
    }
}