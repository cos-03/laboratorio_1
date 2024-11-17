import javax.swing.*;

public class DatagramaMenuJOprion {
    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            String menu = """
                === Menú de Operaciones con Datagramas ===
                1. Análisis de Flags y Desplazamiento en datagrama IPv4
                2. Fragmentar datagrama IP según MTU
                3. Salir
                Elige una opción:
                """;
            String input = JOptionPane.showInputDialog(menu);
            if (input == null) {
                JOptionPane.showMessageDialog(null, "Saliendo del programa...");
                break;
            }

            try {
                int option = Integer.parseInt(input);

                switch (option) {
                    case 1:
                        analyzeFlagsAndOffset();
                        break;
                    case 2:
                        fragmentAndDisplay();
                        break;
                    case 3:
                        exit = true;
                        JOptionPane.showMessageDialog(null, "Saliendo del programa...");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opción inválida. Por favor, elige una opción válida.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingresa un número.");
            }
        }
    }

    // Opción 1: Análisis de Flags y Desplazamiento
    private static void analyzeFlagsAndOffset() {
        String hexDatagram = JOptionPane.showInputDialog("Ingrese el datagrama en formato hexadecimal (sin espacios):").replaceAll("\\s", "");

        if (hexDatagram.length() < 40) {
            JOptionPane.showMessageDialog(null, "El datagrama ingresado es demasiado corto para un encabezado IPv4.");
            return;
        }

        String flagsAndOffsetHex = hexDatagram.substring(12, 16);
        int flagsAndOffsetDecimal = Integer.parseInt(flagsAndOffsetHex, 16);
        String flagsAndOffsetBinary = String.format("%16s", Integer.toBinaryString(flagsAndOffsetDecimal)).replace(' ', '0');

        String flagsBinary = flagsAndOffsetBinary.substring(0, 3);
        String offsetBinary = flagsAndOffsetBinary.substring(3);
        int offsetDecimal = Integer.parseInt(offsetBinary, 2);
        int offsetBytes = offsetDecimal * 8;

        StringBuilder analysis = new StringBuilder();
        analysis.append("Análisis del campo Flags y Desplazamiento:\n");
        analysis.append("Flags (binario): ").append(flagsBinary).append("\n");
        analysis.append("Desplazamiento (binario): ").append(offsetBinary).append("\n");
        analysis.append("Desplazamiento (decimal, Words de 64 bits): ").append(offsetDecimal).append("\n");
        analysis.append("Desplazamiento (bytes): ").append(offsetBytes).append("\n\n");

        analysis.append("Interpretación de los Flags:\n");
        analysis.append("Reservado (Bit 0): ").append(flagsBinary.charAt(0) == '0' ? "No utilizado (Cero)" : "Error").append("\n");
        analysis.append("DF (No Fragmentar, Bit 1): ").append(flagsBinary.charAt(1) == '1' ? "No se puede fragmentar" : "Se puede fragmentar").append("\n");
        analysis.append("MF (Más Fragmentos, Bit 2): ").append(flagsBinary.charAt(2) == '1' ? "Hay más fragmentos" : "No hay más fragmentos").append("\n");

        JOptionPane.showMessageDialog(null, analysis.toString());
    }

    // Opción 2: Fragmentar datagrama y mostrar en W16
    private static void fragmentAndDisplay() {
        try {
            int totalDatagramSize = Integer.parseInt(JOptionPane.showInputDialog("Introduce el tamaño total del datagrama (bytes, incluyendo encabezado):"));
            int headerSize = Integer.parseInt(JOptionPane.showInputDialog("Introduce el tamaño del encabezado (bytes):"));

            int dataSize = totalDatagramSize - headerSize;

            int mtu = Integer.parseInt(JOptionPane.showInputDialog("Introduce el MTU de la red (bytes):"));

            if (mtu <= headerSize) {
                JOptionPane.showMessageDialog(null, "El MTU es demasiado pequeño para contener siquiera el encabezado.");
                return;
            }

            int maxDataPerFragment = mtu - headerSize;
            int numFragments = (int) Math.ceil((double) dataSize / maxDataPerFragment);

            StringBuilder result = new StringBuilder();
            result.append("Fragmentación del Datagrama:\n");
            result.append("Tamaño total del datagrama: ").append(totalDatagramSize).append(" bytes\n");
            result.append("Tamaño del encabezado: ").append(headerSize).append(" bytes\n");
            result.append("Tamaño de los datos: ").append(dataSize).append(" bytes\n");
            result.append("MTU de la red: ").append(mtu).append(" bytes\n");
            result.append("Tamaño máximo de datos por fragmento: ").append(maxDataPerFragment).append(" bytes\n");
            result.append("Número de fragmentos necesarios: ").append(numFragments).append("\n\n");

            String dataHex = generateDataHex(dataSize); // Datos simulados en hexadecimal

            for (int i = 0; i < numFragments; i++) {
                int start = i * maxDataPerFragment;
                int end = Math.min(start + maxDataPerFragment, dataSize);
                int fragmentDataSize = end - start;
                int fragmentTotalSize = fragmentDataSize + headerSize;
                boolean moreFragments = (i < numFragments - 1);

                int offsetDecimal = start / 8;
                String flagsBinary = (moreFragments ? "001" : "000") + String.format("%013d", Integer.parseInt(Integer.toBinaryString(offsetDecimal)));
                String flagsAndOffsetHex = String.format("%04X", Integer.parseInt(flagsBinary, 2));

                String fragmentHex = generateHeaderHex(headerSize) + flagsAndOffsetHex + dataHex.substring(start * 2, end * 2);

                result.append("Fragmento ").append(i + 1).append(":\n");
                result.append(" - Tamaño total: ").append(fragmentTotalSize).append(" bytes\n");
                result.append(" - Datos [").append(start).append("-").append(end - 1).append("]\n");
                result.append(" - Más fragmentos: ").append(moreFragments ? "Sí" : "No").append("\n");
                result.append(" - Flags y Desplazamiento (hexadecimal): ").append(flagsAndOffsetHex).append("\n");
                result.append(" - Datagrama completo (hexadecimal): ").append(fragmentHex).append("\n\n");
            }

            JOptionPane.showMessageDialog(null, result.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingresa valores numéricos.");
        }
    }

    // Genera un encabezado simulado en hexadecimal
    private static String generateHeaderHex(int headerSize) {
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < headerSize * 2; i++) {
            header.append("0");
        }
        return header.toString();
    }

    // Genera datos simulados en hexadecimal
    private static String generateDataHex(int dataSize) {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < dataSize * 2; i++) {
            data.append(Integer.toHexString((i % 16)));
        }
        return data.toString();
    }
}
