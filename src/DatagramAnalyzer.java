import java.util.Scanner;

public class DatagramAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar datagrama al usuario
        System.out.println("Ingrese el datagrama en hexadecimal (sin espacios):");
        String hexDatagram = scanner.nextLine();

        // Verificar longitud mínima del datagrama
        if (hexDatagram.length() < 12) { // Minímo hasta flags y desplazamiento
            System.out.println("El datagrama ingresado es demasiado corto.");
            return;
        }

        // Extraer los dos bytes de flags y desplazamiento
        String flagsAndOffsetHex = hexDatagram.substring(12, 16); // Posiciones correctas
        int flagsAndOffset = Integer.parseInt(flagsAndOffsetHex, 16);

        // Calcular los valores de los flags
        int reservedBit = (flagsAndOffset >> 15) & 1;
        int dontFragment = (flagsAndOffset >> 14) & 1;
        int moreFragments = (flagsAndOffset >> 13) & 1;

        // Calcular el desplazamiento en words de 64 bits
        int fragmentOffset = flagsAndOffset & 0x1FFF; // Máscara de 13 bits

        // Convertir el desplazamiento a bytes
        int fragmentOffsetBytes = fragmentOffset * 8;

        // Mostrar resultados
        System.out.println("Resultados:");
        System.out.println("Bit reservado: " + reservedBit);
        System.out.println("Don't Fragment (DF): " + dontFragment);
        System.out.println("More Fragments (MF): " + moreFragments);
        System.out.println("Desplazamiento en palabras de 64 bits: " + fragmentOffset);
        System.out.println("Desplazamiento en bytes: " + fragmentOffsetBytes);

        scanner.close();
    }
}
