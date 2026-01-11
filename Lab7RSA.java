import java.math.BigInteger;
import java.util.*;

class RSA {

    // prk → private key exponent (d)
    // puk → public key exponent (e)
    // n   → modulus (n = p × q)
    BigInteger prk, puk, n;

    // Method to generate public and private keys
    void getkeys(int bitlen) {

        // Random number generator (used to generate primes)
        Random r = new Random();

        // Formula: p = random large prime
        BigInteger p = BigInteger.probablePrime(bitlen, r);

        // Formula: q = random large prime
        BigInteger q = BigInteger.probablePrime(bitlen, r);

        // Formula: n = p × q
        n = p.multiply(q);

        // Formula: φ(n) = (p − 1)(q − 1)
        BigInteger phi = p.subtract(BigInteger.ONE)
                          .multiply(q.subtract(BigInteger.ONE));

        // Choose public key exponent e such that:
        // 1 < e < φ(n)
        // gcd(e, φ(n)) = 1
        puk = BigInteger.probablePrime(bitlen / 2, r);

        // Repeat until e is valid
        while (!phi.gcd(puk).equals(BigInteger.ONE) || puk.compareTo(phi) >= 0) {
            puk = BigInteger.probablePrime(bitlen / 2, r);
        }

        // Formula: d = e^-1 mod phi(n)
        // Compute private key exponent
        prk = puk.modInverse(phi);

        // Public Key  = (e, n)
        // Private Key = (d, n)
    }

    // Encryption function
    BigInteger encrypt(BigInteger m) {

        // Formula: c = m^e mod n
        return m.modPow(puk, n);
    }

    // Decryption function
    BigInteger decrypt(BigInteger c) {

        // Formula: m = c^d mod n
        return c.modPow(prk, n);
    }
}

class Lab7RSA {

    public static void main(String[] args) {

        // Create RSA object
        RSA rsa = new RSA();

        // Generate RSA keys using 512-bit primes
        rsa.getkeys(512);

        // Read input from user
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the message to be encrypted: ");

        // Convert input string into BigInteger
        // (RSA works on numbers, not strings)
        BigInteger m = new BigInteger(sc.next().getBytes());

        // Encrypt the message using public key
        BigInteger c = rsa.encrypt(m);

        // Display encrypted message
        // NOTE: longValue() may truncate large values (used here only for lab output)
        System.out.println("Encrypted message: " + c.longValue());

        // Decrypt the message using private key
        BigInteger d = rsa.decrypt(c);

        // Convert decrypted number back to original message
        System.out.println("Decrypted message: " + new String(d.toByteArray()));

        sc.close();
    }
}
/*
Enter the message to be encrypted: Hello
Encrypted message: -3948195796417449325
Decrypted message: Hello
*/
