import java.util.Scanner;

public class Lab8Lb {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter bucket capacity: ");
        int Bc = sc.nextInt();

        System.out.print("Enter output rate: ");
        int oR = sc.nextInt();

        System.out.print("Enter the number of packets: ");
        int numPackets = sc.nextInt();

        int[] packetsz = new int[numPackets];

        System.out.println("Enter packet sizes:");
        for (int i = 0; i < numPackets; i++) {
            packetsz[i] = sc.nextInt();
        }

        int cBs = 0; // current bucket size

        System.out.println("\nPacket\tBucket\tSent\tRemain\tStatus");

        for (int ps : packetsz) {

            boolean accepted = (cBs + ps <= Bc);

            // If accepted, add packet size to bucket
            int bucketBeforeSend = accepted ? (cBs + ps) : cBs;

            int sent = Math.min(oR, bucketBeforeSend);
            int remain = Math.max(0, bucketBeforeSend - oR);

            System.out.println(ps + "\t" + bucketBeforeSend + "\t" + sent + "\t" + remain + "\t" +
                    (accepted ? "Accepted" : "Dropped"));

            // Update bucket for next iteration
            cBs = remain;
        }

        sc.close();
    }
}