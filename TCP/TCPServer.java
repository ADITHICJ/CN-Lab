package TCP;

import java.net.*;
import java.io.*;

public class TCPServer {
    public static void main(String[] args) throws Exception {
        // Create server socket on port 3300
        ServerSocket sersock = new ServerSocket(3300);
        System.out.println("Server ready for connection...");

        // Accept client request
        Socket sock = sersock.accept();
        System.out.println("Client connected. Waiting for filename...");

        // Receive filename from client
        BufferedReader fileRead = new BufferedReader(
                new InputStreamReader(sock.getInputStream()));
        String fname = fileRead.readLine();

        // Read file from server system
        BufferedReader contentRead = new BufferedReader(
                new FileReader(fname));

        // Send file content to client
        PrintWriter pwrite = new PrintWriter(sock.getOutputStream(), true);
        String str;
        while ((str = contentRead.readLine()) != null) {
            pwrite.println(str);
        }

        // Closing resources
        System.out.println("File transfer complete. Closing connection.");
        contentRead.close();
        pwrite.close();
        fileRead.close();
        sock.close();
        sersock.close();
    }
}