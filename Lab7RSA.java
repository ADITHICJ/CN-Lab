import java.math.BigInteger;
import java.util.*;

class RSA {
    BigInteger d, e, n;

    // Generate RSA keys
    void generateKeys(int bitLength) {
        Random rand = new Random();

        BigInteger p = BigInteger.probablePrime(bitLength, rand);
        BigInteger q = BigInteger.probablePrime(bitLength, rand);

        n = p.multiply(q); // modulus
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // choose e
        e = BigInteger.probablePrime(bitLength / 2, rand);
        while (!phi.gcd(e).equals(BigInteger.ONE) || e.compareTo(phi) >= 0) {
            e = BigInteger.probablePrime(bitLength / 2, rand);
        }

        d = e.modInverse(phi); // private key
    }

    BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }

    BigInteger decrypt(BigInteger cipher) {
        return cipher.modPow(d, n);
    }
}

public class Lab7RSA {
    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.generateKeys(512);

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter message: ");

        BigInteger message = new BigInteger(sc.next().getBytes());

        BigInteger encrypted = rsa.encrypt(message);
        BigInteger decrypted = rsa.decrypt(encrypted);

        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + new String(decrypted.toByteArray()));

        sc.close();
    }
}
