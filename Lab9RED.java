// Import all utility classes (Scanner, Random, etc.)
import java.util.*;

// Main class for Lab 9 – Random Early Detection (RED)
public class Lab9RED {

    // Inner class that implements RED queue behavior
    public static class RandomEarlyDetection {

        // Minimum threshold: below this no packets are dropped
        private double minThreshold;

        // Maximum threshold: above this all packets are dropped
        private double maxThreshold;

        // Maximum probability of packet drop between thresholds
        private double maxDropProb;

        // Maximum size of the queue
        private int queueSize;

        // Current number of packets in the queue
        private int currentQueue;

        // Random object to generate random numbers for probabilistic drop
        private Random random = new Random();

        // Constructor to initialize RED parameters
        public RandomEarlyDetection(double min, double max, double prob, int size) {
            minThreshold = min;      // Set minimum threshold
            maxThreshold = max;      // Set maximum threshold
            maxDropProb = prob;      // Set maximum drop probability
            queueSize = size;        // Set queue capacity
            currentQueue = 0;        // Initially queue is empty
        }

        // Method to simulate arrival of a packet
        public boolean enqueue() {

            // Check if queue is already full
            if (currentQueue >= queueSize) {
                // Drop packet if queue is full (tail drop)
                System.out.println("Packet dropped (Queue Full)");
                return false;
            }

            // Calculate drop probability based on current queue size
            double dropProb = calcDropProb();

            // Decide randomly whether to drop packet based on RED probability
            if (dropProb > 0 && shouldDrop(dropProb)) {
                // Packet dropped early to avoid congestion
                System.out.println("Packet dropped (RED)");
                return false;
            }

            // If packet is not dropped, add it to the queue
            currentQueue++;

            // Display current queue size after enqueue
            System.out.println("Packet enqueued. Current queue size: " + currentQueue);

            // Return true to indicate successful enqueue
            return true;
        }

        // Method to calculate RED drop probability
        private double calcDropProb() {

            // If queue size is below minimum threshold, no packet drop
            if (currentQueue < minThreshold)
                return 0.0;

            // If queue size reaches or exceeds maximum threshold, drop all packets
            else if (currentQueue >= maxThreshold)
                return 1.0;

            // If queue size is between thresholds, increase probability linearly
            else
                return maxDropProb *
                       ((currentQueue - minThreshold) /
                       (maxThreshold - minThreshold));
        }

        // Method to decide whether to drop packet using probability
        private boolean shouldDrop(double prob) {

            // Generate random number between 0 and 1
            // Drop packet if random number is less than probability
            return random.nextDouble() < prob;
        }
    }

    // Main method – execution starts here
    public static void main(String[] args) {

        // Scanner object to read user input
        Scanner sc = new Scanner(System.in);

        // Read minimum threshold from user
        System.out.print("Enter the minimum threshold: ");
        double min = sc.nextDouble();

        // Read maximum threshold from user
        System.out.print("Enter the maximum threshold: ");
        double max = sc.nextDouble();

        // Read maximum drop probability
        System.out.print("Enter the maximum drop probability (0-1): ");
        double prob = sc.nextDouble();

        // Read queue size
        System.out.print("Enter the queue size: ");
        int size = sc.nextInt();

        // Read number of packets to be simulated
        System.out.print("Enter the number of packets: ");
        int n = sc.nextInt();

        // Create RED queue object with given parameters
        RandomEarlyDetection red = new RandomEarlyDetection(min, max, prob, size);

        // Simulate arrival of packets one by one
        for (int i = 0; i < n; i++) {
            red.enqueue();   // Attempt to enqueue each packet
        }

        // Close scanner to release resources
        sc.close();
    }
}
/*
Enter the minimum threshold: 3
Enter the maximum threshold: 5
Enter the maximum drop probability (0-1): 0.3
Enter the queue size: 7
Enter the number of packets: 6
Packet enqueued. Current queue size: 1
Packet enqueued. Current queue size: 2
Packet enqueued. Current queue size: 3
Packet enqueued. Current queue size: 4
Packet enqueued. Current queue size: 5
Packet dropped (RED)
*/