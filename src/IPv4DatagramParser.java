import java.util.Scanner;

public class IPv4DatagramParser {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar el datagrama en formato hexadecimal
        System.out.println("Ingrese el datagrama en formato hexadecimal (sin espacios):");
        String hexDatagram = scanner.nextLine().replaceAll("\\s", "");

        // Analizar el datagrama proporcionado
        analyzeDatagram(hexDatagram);
    }

    private static void analyzeDatagram(String hexDatagram) {
        // Validar longitud mínima
        if (hexDatagram.length() < 40) {
            System.out.println("El datagrama tiene menos de 20 bytes. Se asumirá que algunos campos están incompletos.");
        }

        // División de campos (con manejo de longitud variable)
        String versionAndHeaderLength = safeSubstring(hexDatagram, 0, 2);
        String differentiatedServicesField = safeSubstring(hexDatagram, 2, 4);
        String totalLengthHex = safeSubstring(hexDatagram, 4, 8);
        String identificationHex = safeSubstring(hexDatagram, 8, 12);
        String flagsAndOffsetHex = safeSubstring(hexDatagram, 12, 16);
        String ttlHex = safeSubstring(hexDatagram, 16, 18);
        String protocolHex = safeSubstring(hexDatagram, 18, 20);
        String headerChecksum = safeSubstring(hexDatagram, 20, 24);
        String sourceIpHex = safeSubstring(hexDatagram, 24, 32);
        String destinationIpHex = safeSubstring(hexDatagram, 32, 40);

        // Decodificación y conversión
        int version = versionAndHeaderLength.isEmpty() ? -1 : Integer.parseInt(versionAndHeaderLength.substring(0, 1), 16);
        int headerLengthWords = versionAndHeaderLength.isEmpty() ? -1 : Integer.parseInt(versionAndHeaderLength.substring(1), 16);
        int headerLengthBytes = headerLengthWords * 4;
        int totalLength = totalLengthHex.isEmpty() ? -1 : Integer.parseInt(totalLengthHex, 16);
        int identification = identificationHex.isEmpty() ? -1 : Integer.parseInt(identificationHex, 16);

        int flagsAndOffsetDecimal = flagsAndOffsetHex.isEmpty() ? -1 : Integer.parseInt(flagsAndOffsetHex, 16);
        String flagsAndOffsetBinary = flagsAndOffsetDecimal == -1 ? "" : String.format("%16s", Integer.toBinaryString(flagsAndOffsetDecimal)).replace(' ', '0');
        String flagsBinary = flagsAndOffsetBinary.isEmpty() ? "" : flagsAndOffsetBinary.substring(0, 3);
        String offsetBinary = flagsAndOffsetBinary.isEmpty() ? "" : flagsAndOffsetBinary.substring(3);
        int offsetDecimal = offsetBinary.isEmpty() ? -1 : Integer.parseInt(offsetBinary, 2);
        int offsetBytes = offsetDecimal * 8;

        int timeToLive = ttlHex.isEmpty() ? -1 : Integer.parseInt(ttlHex, 16);
        int protocol = protocolHex.isEmpty() ? -1 : Integer.parseInt(protocolHex, 16);

        // Direcciones IP
        String sourceIp = sourceIpHex.isEmpty() ? "Desconocida" : hexToIp(sourceIpHex);
        String destinationIp = destinationIpHex.isEmpty() ? "Desconocida" : hexToIp(destinationIpHex);

        // Salida en formato solicitado
        System.out.println("\nDetalles del Datagrama");
        System.out.println("Entrada Hexadecimal: " + hexDatagram + "\n");

        System.out.println("División de los campos del datagrama:");
        System.out.println("Versión y longitud del encabezado: " + versionAndHeaderLength);
        System.out.println("Servicios diferenciados: " + differentiatedServicesField);
        System.out.println("Longitud total: " + totalLengthHex);
        System.out.println("Identificación: " + identificationHex);
        System.out.println("Flags y desplazamiento: " + flagsAndOffsetHex);
        System.out.println("Tiempo de vida (TTL): " + ttlHex);
        System.out.println("Protocolo: " + protocolHex);
        System.out.println("Suma de comprobación: " + headerChecksum);
        System.out.println("Dirección IP origen: " + sourceIpHex);
        System.out.println("Dirección IP destino: " + destinationIpHex + "\n");

        System.out.println("Decodificación y Conversión:");
        System.out.println("Versión: " + (version == -1 ? "Desconocida" : version));
        System.out.println("Longitud del encabezado: " + (headerLengthWords == -1 ? "Desconocida" : headerLengthWords + " words (" + headerLengthBytes + " bytes)."));
        System.out.println("Servicios Diferenciados:");
        System.out.println("  Valor hexadecimal: " + differentiatedServicesField);
        System.out.println("  DSCP: " + (differentiatedServicesField.isEmpty() ? "Desconocido" : "0x" + differentiatedServicesField.substring(0, 1)));
        System.out.println("  ECN: " + (differentiatedServicesField.isEmpty() ? "Desconocido" : "0x" + differentiatedServicesField.substring(1)));
        System.out.println("Longitud Total: " + (totalLength == -1 ? "Desconocida" : totalLength + " bytes."));
        System.out.println("Identificación: " + (identification == -1 ? "Desconocida" : identification + "."));
        System.out.println("Flags y Desplazamiento:");
        System.out.println("  Binario: " + (flagsAndOffsetBinary.isEmpty() ? "Desconocido" : flagsAndOffsetBinary));
        System.out.println("  Flags: " + (flagsBinary.isEmpty() ? "Desconocidos" : flagsBinary));
        System.out.println("  Desplazamiento: " + (offsetBytes == -1 ? "Desconocido" : offsetBytes + " bytes."));
        System.out.println("Tiempo de Vida: " + (timeToLive == -1 ? "Desconocido" : timeToLive + "."));
        System.out.println("Protocolo: " + (protocol == -1 ? "Desconocido" : getProtocolName(protocol) + "."));
        System.out.println("Dirección IP Origen: " + sourceIp);
        System.out.println("Dirección IP Destino: " + destinationIp);
    }

    private static String safeSubstring(String str, int start, int end) {
        return str.length() >= end ? str.substring(start, end) : "";
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
