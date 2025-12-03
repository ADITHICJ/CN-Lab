import java.util.Scanner;

public class Lab8Tb {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Taking user inputs
        System.out.print("Enter bucket capacity: ");
        int Bc = sc.nextInt();

        System.out.print("Enter token generation rate: ");
        int tR = sc.nextInt();

        System.out.print("Enter number of packets: ");
        int n = sc.nextInt();

        int[] packets = new int[n];
        System.out.println("Enter packet sizes:");

        for (int i = 0; i < n; i++) {
            packets[i] = sc.nextInt();
        }

        // Initial tokens
        int tokens = 0;

        // Table header
        System.out.println("\nPacket\tBefore\tAfterAdd\tSent\tLeft\tStatus");

        // Processing each packet
        for (int ps : packets) {

            int before = tokens;  // tokens before adding
            tokens = Math.min(Bc, tokens + tR); // add tokens
            int afterAdd = tokens;

            int sent = 0;
            String status;

            if (ps <= tokens) {
                sent = ps;
                tokens -= ps;
                status = "Accepted";
            } else {
                status = "Dropped";
            }

            // Print result in one formatted line
            System.out.printf("%d\t\t%d\t\t%d\t\t%d\t\t%d\t%s\n",
                    ps, before, afterAdd, sent, tokens, status);
        }

        sc.close();
    }
}