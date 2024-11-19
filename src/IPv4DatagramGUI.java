import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IPv4DatagramGUI {

    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("Analizador de Datagramas IPv4");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Etiqueta de instrucción
        JLabel label = new JLabel("Ingrese el datagrama en formato hexadecimal:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Campo de texto para ingresar el datagrama
        JTextField textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton analyzeButton = new JButton("Ingresar");
        JButton cancelButton = new JButton("Cancelar");

        buttonPanel.add(analyzeButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Agregar panel a la ventana
        frame.add(panel);

        // Mostrar la ventana
        frame.setVisible(true);

        // Acción del botón "Ingresar"
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hexDatagram = textField.getText().replaceAll("\\s", "");

                // Validar longitud mínima
                if (hexDatagram.length() < 40) {
                    JOptionPane.showMessageDialog(frame, "El datagrama ingresado es demasiado corto para un encabezado IPv4.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Realizar análisis del datagrama
                String analysis = analyzeDatagram(hexDatagram);

                // Mostrar resultados en un cuadro de diálogo
                JTextArea textArea = new JTextArea(analysis);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(480, 300));

                JOptionPane.showMessageDialog(frame, scrollPane, "Resultado del Análisis", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Acción del botón "Cancelar"
        cancelButton.addActionListener(e -> System.exit(0));
    }

    private static String analyzeDatagram(String hexDatagram) {
        // División de campos
        String versionAndHeaderLength = hexDatagram.substring(0, 2);
        String differentiatedServicesField = hexDatagram.substring(2, 4);
        String totalLengthHex = hexDatagram.substring(4, 8);
        String identificationHex = hexDatagram.substring(8, 12);
        String flagsAndOffsetHex = hexDatagram.substring(12, 16);
        String ttlHex = hexDatagram.substring(16, 18);
        String protocolHex = hexDatagram.substring(18, 20);
        String headerChecksum = hexDatagram.substring(20, 24);
        String sourceIpHex = hexDatagram.substring(24, 32);
        String destinationIpHex = hexDatagram.substring(32, 40);

        // Decodificación y conversión
        int version = Integer.parseInt(versionAndHeaderLength.substring(0, 1), 16);
        int headerLengthWords = Integer.parseInt(versionAndHeaderLength.substring(1), 16);
        int headerLengthBytes = headerLengthWords * 4;
        int totalLength = Integer.parseInt(totalLengthHex, 16);
        int identification = Integer.parseInt(identificationHex, 16);

        int flagsAndOffsetDecimal = Integer.parseInt(flagsAndOffsetHex, 16);
        String flagsAndOffsetBinary = String.format("%16s", Integer.toBinaryString(flagsAndOffsetDecimal)).replace(' ', '0');
        String flagsBinary = flagsAndOffsetBinary.substring(0, 3);
        String offsetBinary = flagsAndOffsetBinary.substring(3);
        int offsetDecimal = Integer.parseInt(offsetBinary, 2);
        int offsetBytes = offsetDecimal * 8;

        int timeToLive = Integer.parseInt(ttlHex, 16);
        int protocol = Integer.parseInt(protocolHex, 16);

        // Direcciones IP
        String sourceIp = hexToIp(sourceIpHex);
        String destinationIp = hexToIp(destinationIpHex);

        // Construcción del análisis
        StringBuilder analysis = new StringBuilder();
        analysis.append("Detalles del Datagrama\n");
        analysis.append("Entrada Hexadecimal: ").append(hexDatagram).append("\n\n");

        analysis.append("División de los campos del datagrama:\n");
        analysis.append("Versión y longitud del encabezado: ").append(versionAndHeaderLength).append("\n");
        analysis.append("Servicios diferenciados: ").append(differentiatedServicesField).append("\n");
        analysis.append("Longitud total: ").append(totalLengthHex).append("\n");
        analysis.append("Identificación: ").append(identificationHex).append("\n");
        analysis.append("Flags y desplazamiento: ").append(flagsAndOffsetHex).append("\n");
        analysis.append("Tiempo de vida (TTL): ").append(ttlHex).append("\n");
        analysis.append("Protocolo: ").append(protocolHex).append("\n");
        analysis.append("Suma de comprobación: ").append(headerChecksum).append("\n");
        analysis.append("Dirección IP origen: ").append(sourceIpHex).append("\n");
        analysis.append("Dirección IP destino: ").append(destinationIpHex).append("\n\n");

        analysis.append("Decodificación y Conversión:\n");
        analysis.append("Versión: ").append(version).append("\n");
        analysis.append("Longitud del encabezado: ").append(headerLengthWords).append(" words (").append(headerLengthBytes).append(" bytes).\n");
        analysis.append("Servicios Diferenciados:\n");
        analysis.append("  DSCP: 0x").append(differentiatedServicesField.substring(0, 1)).append("\n");
        analysis.append("  ECN: 0x").append(differentiatedServicesField.substring(1)).append("\n");
        analysis.append("Longitud Total: ").append(totalLength).append(" bytes.\n");
        analysis.append("Identificación: ").append(identification).append("\n");
        analysis.append("Flags y Desplazamiento:\n");
        analysis.append("  Binario: ").append(flagsAndOffsetBinary).append("\n");
        analysis.append("  Reservado: ").append(flagsBinary.charAt(0) == '0' ? "No utilizado\n" : "Utilizado\n");
        analysis.append("  DF: ").append(flagsBinary.charAt(1) == '1' ? "No fragmentar\n" : "Fragmentar\n");
        analysis.append("  MF: ").append(flagsBinary.charAt(2) == '1' ? "Más fragmentos\n" : "No más fragmentos\n");
        analysis.append("  Desplazamiento: ").append(offsetBytes).append(" bytes.\n");
        analysis.append("Tiempo de Vida: ").append(timeToLive).append("\n");
        analysis.append("Protocolo: ").append(getProtocolName(protocol)).append("\n");
        analysis.append("Dirección IP Origen: ").append(sourceIp).append("\n");
        analysis.append("Dirección IP Destino: ").append(destinationIp).append("\n");

        return analysis.toString();
    }

    private static String hexToIp(String hex) {
        StringBuilder ip = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            int octet = Integer.parseInt(hex.substring(i, i + 2), 16);
            ip.append(octet);
            if (i < hex.length() - 2) {
                ip.append(".");
            }
        }
        return ip.toString();
    }

    private static String getProtocolName(int protocol) {
        switch (protocol) {
            case 1: return "ICMP";
            case 6: return "TCP";
            case 17: return "UDP";
            default: return "Unknown";
        }
    }
}
