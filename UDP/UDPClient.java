package UDP;

import java.net.*;
import java.util.*;

class UDPClient {
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress serverAddress = InetAddress.getByName("localhost");

        while (true) {
            System.out.println(
                "Enter the string in lowercase (type 'exit' to quit):"
            );

            String sentence = sc.nextLine();

            byte[] sendData = sentence.getBytes();

            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length,
                            serverAddress, 5454);
            clientSocket.send(sendPacket);

            if (sentence.equalsIgnoreCase("exit")) {
                break;
            }

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String modifiedSentence = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength()
            );

            System.out.println("FROM SERVER: " + modifiedSentence);
        }

        clientSocket.close();
        sc.close();
    }
}

/*
Server is Ready for the client
RECEIVED: computer science
RECEIVED: exit
Server shutting down...

Enter the string in lowercase (type 'exit' to quit):
computer science
FROM SERVER: COMPUTER SCIENCE
Enter the string in lowercase (type 'exit' to quit):
exit
 */