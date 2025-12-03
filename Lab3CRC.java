import java.util.*;

public class Lab3CRC {

    // Convert text to 8-bit binary
    public static String toBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    // Perform modulo-2 division
    public static String divide(String dividend, String divisor) {
        char[] div = dividend.toCharArray();
        char[] key = divisor.toCharArray();
        int n = div.length, m = key.length;

        for (int i = 0; i <= n - m; i++) {
            if (div[i] == '1') {
                for (int j = 0; j < m; j++) {
                    div[i + j] = (div[i + j] == key[j]) ? '0' : '1';
                }
            }
        }
        return new String(div).substring(n - m + 1); // remainder
    }

    // Encode data with CRC remainder
    public static String encode(String data, String key, boolean isBinary) {
        String binaryData = isBinary ? data : toBinary(data);

        // 16 zeros appended for CRC calculation (since CRC-CCITT is 16-bit)
        String appended = binaryData + new String(new char[16]).replace('\0', '0');

        String remainder = divide(appended, key);
        System.out.println("CRC Remainder (16 bits): " + remainder);
        return binaryData + remainder;
    }

    // Decode and check for error
    public static boolean hasError(String received, String key) {
        return divide(received, key).contains("1");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CRC-CCITT (16-bit) Error Detection ===");
        System.out.print("Enter 17-bit polynomial key (e.g., 10001000000100001): ");
        String key = sc.next();

        System.out.println("\nSelect Input Type:");
        System.out.println("1. Text / Number");
        System.out.println("2. Binary");
        int choice = sc.nextInt();
        boolean isBinary = (choice == 2);

        System.out.print("Enter data: ");
        String data = sc.next();

        // Encode
        String encoded = encode(data, key, isBinary);
        System.out.println("\nEncoded Data (with CRC): " + encoded);

        // Verify
        System.out.print("\nEnter received (binary) data: ");
        String received = sc.next();

        if (hasError(received, key))
            System.out.println("Error detected in received data!");
        else
            System.out.println("Data is error-free.");

        sc.close();
    }
}