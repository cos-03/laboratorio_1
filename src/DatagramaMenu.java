import java.util.Scanner;

public class DatagramaMenu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Menú de Operaciones con Datagramas ===");
            System.out.println("1. Procesar datagrama IP en formato hexadecimal");
            System.out.println("2. Fragmentar datagrama IP según MTU");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    processHexDatagram(scanner);
                    break;
                case 2:
                    fragmentDatagram(scanner);
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

    // Opción 1: Procesar datagrama IP en formato hexadecimal
    private static void processHexDatagram(Scanner scanner) {
        scanner.nextLine(); // Limpiar el buffer
        System.out.println("Introduce el datagrama IP como una cadena de 32 hexadecimales (16 pares):");
        String hexDatagram = scanner.nextLine().trim();

        // Validar que la entrada tenga el tamaño correcto
        if (hexDatagram.length() != 32) {
            System.out.println("El datagrama debe tener exactamente 32 caracteres hexadecimales.");
            return;
        }

        // Convertir el datagrama en bytes
        byte[] datagramBytes = new byte[hexDatagram.length() / 2];
        for (int i = 0; i < datagramBytes.length; i++) {
            datagramBytes[i] = (byte) Integer.parseInt(hexDatagram.substring(i * 2, i * 2 + 2), 16);
        }

        // Mostrar los pares hexadecimales procesados
        System.out.println("Pares hexadecimales procesados:");
        for (int i = 0; i < datagramBytes.length; i++) {
            System.out.printf("Par %d: %02X\n", i + 1, datagramBytes[i]);
        }
    }

    // Opción 2: Fragmentar datagrama IP según MTU
    private static void fragmentDatagram(Scanner scanner) {
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

        System.out.println("\nFragmentos generados:");
        for (int i = 0; i < numFragments; i++) {
            int start = i * maxDataPerFragment;
            int end = Math.min(start + maxDataPerFragment, dataSize);
            int fragmentDataSize = end - start;
            int fragmentTotalSize = fragmentDataSize + headerSize;
            boolean moreFragments = (i < numFragments - 1);

            System.out.printf("Fragmento %d:\n", i + 1);
            System.out.printf(" - Tamaño total: %d bytes\n", fragmentTotalSize);
            System.out.printf(" - Datos [%d-%d]\n", start, end - 1);
            System.out.printf(" - Más fragmentos: %s\n", moreFragments ? "Sí" : "No");
        }
    }
}
/*
*OP1
* Introduce el datagrama IP como una cadena de 32 hexadecimales (16 pares):
4500003C1C4640004011B861C0A80001C0A800C7

* OP2
* Introduce el tamaño total del datagrama (bytes, incluyendo encabezado): 1420
Introduce el tamaño del encabezado (bytes): 20
Introduce el MTU de la red (bytes): 620

*
* */
