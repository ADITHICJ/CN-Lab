package TCP;

import java.net.*;
import java.io.*;

public class TCPClient {
    public static void main(String[] args) throws Exception {
        // Connect to server (localhost:3300)
        Socket sock = new Socket("127.0.0.1", 3300);
        System.out.println("Connected to server.");

        // Ask user to enter filename
        System.out.print("Enter the filename: ");
        BufferedReader keyRead = new BufferedReader(
                new InputStreamReader(System.in));
        String fname = keyRead.readLine();

        // Send filename to server
        PrintWriter pwrite = new PrintWriter(sock.getOutputStream(), true);
        pwrite.println(fname);

        // Receive file content from server
        BufferedReader socketRead = new BufferedReader(
                new InputStreamReader(sock.getInputStream()));
        String str;
        System.out.println("\n--- File Content ---");
        while ((str = socketRead.readLine()) != null) {
            System.out.println(str);
        }

        // Closing resources
        pwrite.close();
        socketRead.close();
        keyRead.close();
        sock.close();
    }
}