import java.util.Scanner;

public class DatagramaMenuFinal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Menú de Operaciones con Datagramas ===");
            System.out.println("1. Análisis de Flags y Desplazamiento en datagrama IPv4");
            System.out.println("2. Fragmentar datagrama IP según MTU");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    analyzeFlagsAndOffset(scanner);
                    break;
                case 2:
                    fragmentAndDisplay(scanner);
                    break;
                case 3:
                    exit = true;
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, elige una opción válida.");
            }
        }

        scanner.close();
    }

    // Opción 1: Análisis de Flags y Desplazamiento
    private static void analyzeFlagsAndOffset(Scanner scanner) {
        scanner.nextLine(); // Limpiar el buffer
        System.out.println("Ingrese el datagrama en formato hexadecimal (sin espacios):");
        String hexDatagram = scanner.nextLine().replaceAll("\\s", "");

        if (hexDatagram.length() < 40) {
            System.out.println("El datagrama ingresado es demasiado corto para un encabezado IPv4.");
            return;
        }

        String flagsAndOffsetHex = hexDatagram.substring(12, 16);
        int flagsAndOffsetDecimal = Integer.parseInt(flagsAndOffsetHex, 16);
        String flagsAndOffsetBinary = String.format("%16s", Integer.toBinaryString(flagsAndOffsetDecimal)).replace(' ', '0');

        String flagsBinary = flagsAndOffsetBinary.substring(0, 3);
        String offsetBinary = flagsAndOffsetBinary.substring(3);
        int offsetDecimal = Integer.parseInt(offsetBinary, 2);
        int offsetBytes = offsetDecimal * 8;

        System.out.println("\nAnálisis del campo Flags y Desplazamiento:");
        System.out.println("Flags (binario): " + flagsBinary);
        System.out.println("Desplazamiento (binario): " + offsetBinary);
        System.out.println("Desplazamiento (decimal, Words de 64 bits): " + offsetDecimal);
        System.out.println("Desplazamiento (bytes): " + offsetBytes);

        System.out.println("\nInterpretación de los Flags:");
        System.out.println("Reservado (Bit 0): " + (flagsBinary.charAt(0) == '0' ? "No utilizado (Cero)" : "Error"));
        System.out.println("DF (No Fragmentar, Bit 1): " + (flagsBinary.charAt(1) == '1' ? "No se puede fragmentar" : "Se puede fragmentar"));
        System.out.println("MF (Más Fragmentos, Bit 2): " + (flagsBinary.charAt(2) == '1' ? "Hay más fragmentos" : "No hay más fragmentos"));
    }

    // Opción 2: Fragmentar datagrama y mostrar en W16
    private static void fragmentAndDisplay(Scanner scanner) {
        System.out.println("Introduce el tamaño total del datagrama (bytes, incluyendo encabezado):");
        int totalDatagramSize = scanner.nextInt();

        System.out.println("Introduce el tamaño del encabezado (bytes):");
        int headerSize = scanner.nextInt();

        int dataSize = totalDatagramSize - headerSize;

        System.out.println("Introduce el MTU de la red (bytes):");
        int mtu = scanner.nextInt();

        if (mtu <= headerSize) {
            System.out.println("El MTU es demasiado pequeño para contener siquiera el encabezado.");
            return;
        }

        int maxDataPerFragment = mtu - headerSize;
        int numFragments = (int) Math.ceil((double) dataSize / maxDataPerFragment);

        System.out.println("\nFragmentación del Datagrama:");
        System.out.println("Tamaño total del datagrama: " + totalDatagramSize + " bytes");
        System.out.println("Tamaño del encabezado: " + headerSize + " bytes");
        System.out.println("Tamaño de los datos: " + dataSize + " bytes");
        System.out.println("MTU de la red: " + mtu + " bytes");
        System.out.println("Tamaño máximo de datos por fragmento: " + maxDataPerFragment + " bytes");
        System.out.println("Número de fragmentos necesarios: " + numFragments);

        String dataHex = generateDataHex(dataSize); // Datos simulados en hexadecimal

        System.out.println("\nFragmentos generados:");
        for (int i = 0; i < numFragments; i++) {
            int start = i * maxDataPerFragment;
            int end = Math.min(start + maxDataPerFragment, dataSize);
            int fragmentDataSize = end - start;
            int fragmentTotalSize = fragmentDataSize + headerSize;
            boolean moreFragments = (i < numFragments - 1);

            int offsetDecimal = start / 8;
            String flagsBinary = (moreFragments ? "001" : "000") + String.format("%013d", Integer.parseInt(Integer.toBinaryString(offsetDecimal)));
            String flagsAndOffsetHex = String.format("%04X", Integer.parseInt(flagsBinary, 2));

            // Generar fragmento en formato hexadecimal
            String fragmentHex = generateHeaderHex(headerSize) + flagsAndOffsetHex + dataHex.substring(start * 2, end * 2);

            System.out.printf("Fragmento %d:\n", i + 1);
            System.out.printf(" - Tamaño total: %d bytes\n", fragmentTotalSize);
            System.out.printf(" - Datos [%d-%d]\n", start, end - 1);
            System.out.printf(" - Más fragmentos: %s\n", moreFragments ? "Sí" : "No");
            System.out.printf(" - Flags y Desplazamiento (hexadecimal): %s\n", flagsAndOffsetHex);
            System.out.printf(" - Datagrama completo (hexadecimal): %s\n", fragmentHex);

            // Mostrar en formato W16
            System.out.println(" - Datagrama en Words de 16 bits (W16):");
            for (int j = 0; j < fragmentHex.length(); j += 4) {
                System.out.print(fragmentHex.substring(j, Math.min(j + 4, fragmentHex.length())) + " ");
                if ((j / 4 + 1) % 8 == 0) System.out.println(); // Salto de línea cada 8 W16
            }
            System.out.println("\n");
        }
    }

    // Genera un encabezado simulado en hexadecimal
    private static String generateHeaderHex(int headerSize) {
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < headerSize * 2; i++) {
            header.append("0"); // Simular encabezado con ceros
        }
        return header.toString();
    }

    // Genera datos simulados en hexadecimal
    private static String generateDataHex(int dataSize) {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < dataSize * 2; i++) {
            data.append(Integer.toHexString((i % 16))); // Datos simulados
        }
        return data.toString();
    }
}
