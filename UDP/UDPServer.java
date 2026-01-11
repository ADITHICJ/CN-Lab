package UDP;

import java.net.*;

class UDPServer {
    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(5454);
        System.out.println("Server is Ready for the client");

        byte[] receiveData = new byte[1024];

        while (true) {
            // Receive packet
            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            // Read only actual data
            String sentence = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength()
            ).trim();

            System.out.println("RECEIVED: " + sentence);

            // Termination condition
            if (sentence.equalsIgnoreCase("exit")) {
                System.out.println("Server shutting down...");
                break;
            }

            // Convert to uppercase
            String capitalized = sentence.toUpperCase();
            byte[] sendData = capitalized.getBytes();

            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            // Send response
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length,
                            clientAddress, clientPort);
            serverSocket.send(sendPacket);
        }

        serverSocket.close();
    }
}
