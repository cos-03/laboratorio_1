import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*Datso de entrada

Encabezado
45000224cc3400b9400124dac0a802e3c0a802de
Payload
4a6f6e20446f65207061696e732074686520686f75736520616e642067617264656e2e20546865206865616465722073686f756c642062652065786163746c79203230
mtu
50
bytes

*/
public class DatagramaIPV4GUI {
/*Explicación del Código:
Interfaz gráfica:
Recibe el encabezado en hexadecimal, el payload en hexadecimal y el MTU.
Fragmentación (Opción 2):
Divide el datagrama si el tamaño total supera el MTU.
Genera fragmentos con encabezados actualizados (campos de Flags y Offset).
Analiza cada fragmento y presenta detalles en un cuadro de diálogo desplazable.
Análisis del datagrama:
Decodifica y descompone los campos del datagrama para su presentación detallada.
Prueba del Código:
Puedes compilar y ejecutar este código en cualquier entorno compatible con Java, como IntelliJ IDEA, Eclipse o NetBeans. Ajusta los datos de entrada para observar cómo se fragmenta y analiza el datagrama.*/
    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("Analizador y Fragmentador de Datagramas IPv4");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Etiqueta de instrucción
        JLabel label = new JLabel("Ingrese el encabezado, el payload y el MTU:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Campos de texto para ingresar datos
        JTextField headerField = new JTextField();
        JTextField payloadField = new JTextField();
        JTextField mtuField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Encabezado (Hex):"));
        inputPanel.add(headerField);
        inputPanel.add(new JLabel("Payload (Hex):"));
        inputPanel.add(payloadField);
        inputPanel.add(new JLabel("MTU (en bytes):"));
        inputPanel.add(mtuField);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton fragmentButton = new JButton("Fragmentar");
        JButton cancelButton = new JButton("Cancelar");

        buttonPanel.add(fragmentButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Agregar panel a la ventana
        frame.add(panel);

        // Mostrar la ventana
        frame.setVisible(true);

        // Acción del botón "Fragmentar"
        fragmentButton.addActionListener(e -> {
            String headerHex = headerField.getText().replaceAll("\\s", "");
            String payloadHex = payloadField.getText().replaceAll("\\s", "");
            String mtuText = mtuField.getText().replaceAll("\\s", "");

            if (headerHex.isEmpty() || payloadHex.isEmpty() || mtuText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int mtu = Integer.parseInt(mtuText);
                if (mtu < 28) {
                    JOptionPane.showMessageDialog(frame, "El MTU debe ser al menos 28 bytes.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                analyzeAndFragmentDatagram(headerHex, payloadHex, mtu, frame);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "El MTU debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción del botón "Cancelar"
        cancelButton.addActionListener(e -> System.exit(0));
    }

    private static void analyzeAndFragmentDatagram(String headerHex, String payloadHex, int mtu, JFrame frame) {
        int headerLength = 20; // Longitud fija del encabezado IPv4 en bytes
        int payloadLength = payloadHex.length() / 2; // Convertir de longitud en caracteres hexadecimales a bytes
        int totalLength = headerLength + payloadLength;

        if (totalLength <= mtu) {
            JOptionPane.showMessageDialog(frame, "El datagrama no necesita fragmentación, ya que su tamaño es menor o igual al MTU.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int fragmentPayloadSize = mtu - headerLength; // Tamaño máximo de datos por fragmento
        int fragmentCount = (int) Math.ceil((double) payloadLength / fragmentPayloadSize);
        StringBuilder fragmentsDetails = new StringBuilder();

        for (int i = 0; i < fragmentCount; i++) {
            int offset = i * fragmentPayloadSize; // Desplazamiento del fragmento actual en bytes
            int currentPayloadSize = Math.min(fragmentPayloadSize, payloadLength - offset); // Tamaño de datos del fragmento actual

            // Obtener el payload del fragmento
            String fragmentPayload = payloadHex.substring(offset * 2, (offset + currentPayloadSize) * 2);

            // Crear un nuevo encabezado para el fragmento
            String flagsAndOffset;
            if (i < fragmentCount - 1) {
                // MF (More Fragments) = 1 y calcular offset
                flagsAndOffset = String.format("%04X", (0x2000 | (offset / 8)));
            } else {
                // MF (More Fragments) = 0 y calcular offset
                flagsAndOffset = String.format("%04X", (offset / 8));
            }

            String fragmentHeader = headerHex.substring(0, 8) + flagsAndOffset + headerHex.substring(12);

            // Generar el datagrama del fragmento completo
            String fragmentDatagram = fragmentHeader + fragmentPayload;

            // Analizar el fragmento generado
            String fragmentAnalysis = analyzeDatagram(fragmentDatagram);

            fragmentsDetails.append("Fragmento ").append(i + 1).append(":\n").append(fragmentAnalysis).append("\n\n");
        }

        // Mostrar todos los fragmentos analizados en un cuadro de diálogo
        JTextArea textArea = new JTextArea(fragmentsDetails.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(frame, scrollPane, "Fragmentos Generados y Análisis", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String analyzeDatagram(String hexDatagram) {
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

        int version = Integer.parseInt(versionAndHeaderLength.substring(0, 1), 16);
        int headerLengthWords = Integer.parseInt(versionAndHeaderLength.substring(1), 16);
        int totalLength = Integer.parseInt(totalLengthHex, 16);
        int identification = Integer.parseInt(identificationHex, 16);

        int flagsAndOffsetDecimal = Integer.parseInt(flagsAndOffsetHex, 16);
        String flagsAndOffsetBinary = String.format("%16s", Integer.toBinaryString(flagsAndOffsetDecimal)).replace(' ', '0');
        String flagsBinary = flagsAndOffsetBinary.substring(0, 3);
        int offsetDecimal = Integer.parseInt(flagsAndOffsetBinary.substring(3), 2);

        int timeToLive = Integer.parseInt(ttlHex, 16);
        int protocol = Integer.parseInt(protocolHex, 16);

        String sourceIp = hexToIp(sourceIpHex);
        String destinationIp = hexToIp(destinationIpHex);

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
        analysis.append("Longitud del encabezado: ").append(headerLengthWords).append(" words (").append(headerLengthWords * 4).append(" bytes).\n");
        analysis.append("Servicios Diferenciados:\n");
        analysis.append("  DSCP: 0x").append(differentiatedServicesField.substring(0, 1)).append("\n");
        analysis.append("  ECN: 0x").append(differentiatedServicesField.substring(1)).append("\n");
        analysis.append("Longitud Total: ").append(totalLength).append(" bytes.\n");
        analysis.append("Identificación: ").append(identification).append("\n");
        analysis.append("Flags y Desplazamiento:\n");
        analysis.append("  Binario: ").append(flagsAndOffsetBinary).append("\n");
        analysis.append("  Reservado: ").append(flagsBinary.charAt(0) == '1' ? "Usado" : "No utilizado").append("\n");
        analysis.append("  DF: ").append(flagsBinary.charAt(1) == '1' ? "No fragmentar" : "Fragmentar").append("\n");
        analysis.append("  MF: ").append(flagsBinary.charAt(2) == '1' ? "Más fragmentos" : "No más fragmentos").append("\n");
        analysis.append("  Desplazamiento: ").append(offsetDecimal * 8).append(" bytes.\n");
        analysis.append("Tiempo de Vida: ").append(timeToLive).append("\n");
        analysis.append("Protocolo: ").append(getProtocolName(protocol)).append("\n");
        analysis.append("Dirección IP Origen: ").append(sourceIp).append("\n");
        analysis.append("Dirección IP Destino: ").append(destinationIp).append("\n");

        return analysis.toString();
    }

    private static String hexToIp(String hex) {
        StringBuilder ip = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            ip.append(Integer.parseInt(hex.substring(i, i + 2), 16));
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
            default: return "Otro";
        }
    }
}
