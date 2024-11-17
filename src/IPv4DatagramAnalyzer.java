
import java.util.Scanner;

public class IPv4DatagramAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Paso 1: Ingresar el datagrama en formato hexadecimal
        System.out.println("Ingrese el datagrama en formato hexadecimal (sin espacios):");
        String hexDatagram = scanner.nextLine().replaceAll("\\s", "");

        // Validar longitud mínima (al menos 20 bytes en un datagrama IPv4)
        if (hexDatagram.length() < 40) {
            System.out.println("El datagrama ingresado es demasiado corto para un encabezado IPv4.");
            return;
        }

        // Paso 2: Extraer los 4 dígitos hexadecimales correspondientes al campo Flags y Desplazamiento
        String flagsAndOffsetHex = hexDatagram.substring(12, 16);
        int flagsAndOffsetDecimal = Integer.parseInt(flagsAndOffsetHex, 16);

        // Convertir a binario con 16 bits
        String flagsAndOffsetBinary = String.format("%16s", Integer.toBinaryString(flagsAndOffsetDecimal)).replace(' ', '0');

        // Paso 3: Separar flags y desplazamiento
        String flagsBinary = flagsAndOffsetBinary.substring(0, 3); // Primeros 3 bits
        String offsetBinary = flagsAndOffsetBinary.substring(3); // Últimos 13 bits

        int offsetDecimal = Integer.parseInt(offsetBinary, 2); // Desplazamiento en Words de 64 bits
        int offsetBytes = offsetDecimal * 8; // Convertir a bytes

        // Paso 4: Mostrar resultados
        System.out.println("\nAnálisis del campo Flags y Desplazamiento:");
        System.out.println("Flags (binario): " + flagsBinary);
        System.out.println("Desplazamiento (binario): " + offsetBinary);
        System.out.println("Desplazamiento (decimal, Words de 64 bits): " + offsetDecimal);
        System.out.println("Desplazamiento (bytes): " + offsetBytes);

        // Interpretación de los flags
        System.out.println("\nInterpretación de los Flags:");
        System.out.println("Reservado (Bit 0): " + (flagsBinary.charAt(0) == '0' ? "No utilizado (Cero)" : "Error"));
        System.out.println("DF (No Fragmentar, Bit 1): " + (flagsBinary.charAt(1) == '1' ? "No se puede fragmentar" : "Se puede fragmentar"));
        System.out.println("MF (Más Fragmentos, Bit 2): " + (flagsBinary.charAt(2) == '1' ? "Hay más fragmentos" : "No hay más fragmentos"));
    }
}
