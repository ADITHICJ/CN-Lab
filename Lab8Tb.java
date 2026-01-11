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
/*
Enter bucket capacity: 10
Enter token generation rate: 3
Enter number of packets: 4
Enter packet sizes:
5 6 3 2

Packet  Before  AfterAdd        Sent    Left    Status
5               0               3               0               3       Dropped
6               3               6               6               0       Accepted
3               0               3               3               0       Accepted
2               0               3               2               1       Accepted
*/