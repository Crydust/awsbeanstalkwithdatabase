package be.crydust.spike.credentials;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordHelper {
    public static void main(String[] args) throws Exception {
        // [-a <algorithm>] [-e <encoding>] [-i <iterations>] [-s <salt-length>] [-k <key-length>] [-h <handler-class-name>] <credentials>
        // -a PBKDF2WithHmacSHA256 -e UTF-8 -i 185000 -s 8 -k 256 -h org.apache.catalina.realm.SecretKeyCredentialHandler 123456
        String algorithm = "PBKDF2WithHmacSHA256";
        int iterations = 185000;
        int saltLength = 8;
        int keyLength = 256;
        String credentials = "123456";

        Raw raw = new Raw(algorithm, iterations, saltLength, keyLength, credentials);

        Encoded encoded = raw.toEncoded();

        System.out.println("encodedTomcat = " + encoded.toTomcat());
        System.out.println("encodedSpring = " + encoded.toSpring());

        // encodedTomcat = 1b746ade68bdc4e8$185000$37563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f
        // encodedSpring = 1b746ade68bdc4e837563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f
        System.out.println(Encoded.parseTomcat("1b746ade68bdc4e8$185000$37563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f")
                .matches("123456", algorithm, keyLength));
        System.out.println(Encoded.parseTomcat("1b746ade68bdc4e8$185000$37563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f")
                .matches("wrong", algorithm, keyLength));
        System.out.println(Encoded.parseSpring("1b746ade68bdc4e837563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f", saltLength, iterations)
                .matches("123456", algorithm, keyLength));
        System.out.println(Encoded.parseSpring("1b746ade68bdc4e837563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f", saltLength, iterations)
                .matches("wrongÈ", algorithm, keyLength));
    }


    private static byte[] fromHex(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseUnsignedInt(s.substring(i, i + 2), 16);
        }
        return data;
    }

    private static String toHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] encode(char[] password, byte[] salt, String algorithm, int iterations, int keyLength) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return SecretKeyFactory.getInstance(algorithm)
                .generateSecret(new PBEKeySpec(password, salt, iterations, keyLength))
                .getEncoded();
    }

    private static class Raw {
        final String algorithm;
        final int iterations;
        final int saltLength;
        final int keyLength;
        final String credentials;

        Raw(String algorithm, int iterations, int saltLength, int keyLength, String credentials) {
            this.algorithm = algorithm;
            this.iterations = iterations;
            this.saltLength = saltLength;
            this.keyLength = keyLength;
            this.credentials = credentials;
        }

        Encoded toEncoded() throws NoSuchAlgorithmException, InvalidKeySpecException {
            char[] password = this.credentials.toCharArray();

            byte[] salt = new byte[this.saltLength];
            SecureRandom random = SecureRandom.getInstanceStrong();
            random.nextBytes(salt);

            byte[] key = encode(password, salt, algorithm, iterations, keyLength);

            return new Encoded(salt, this.iterations, key);
        }

    }

    private static class Encoded {
        final byte[] salt;
        final int iterations;
        final byte[] key;

        Encoded(byte[] salt, int iterations, byte[] key) {
            this.salt = salt;
            this.iterations = iterations;
            this.key = key;
        }

        static Encoded parseTomcat(String s) {
            Pattern p = Pattern.compile("(?i)(?<salt>[0-9a-z]+)\\$(?<iterations>[0-9]+)\\$(?<key>[0-9a-z]+)$");
            Matcher m = p.matcher(s);
            if (m.matches()) {
                return new Encoded(
                        fromHex(m.group("salt")),
                        Integer.parseInt(m.group("iterations")),
                        fromHex(m.group("key"))
                );
            }
            throw new IllegalArgumentException("could not parse " + s);
        }

        static Encoded parseSpring(String s, int saltLength, int iterations) {
            Pattern p = Pattern.compile("(?i)(?<salt>[0-9a-z]{" + (saltLength * 2) + "})(?<key>[0-9a-z]+)$");
            Matcher m = p.matcher(s);
            if (m.matches()) {
                return new Encoded(
                        fromHex(m.group("salt")),
                        iterations,
                        fromHex(m.group("key"))
                );
            }
            throw new IllegalArgumentException("could not parse " + s);
        }

        String toTomcat() {
            return toHex(salt) + "$" + iterations + "$" + toHex(key);
        }

        String toSpring() {
            return toHex(salt) + toHex(key);
        }

        boolean matches(String password, String algorithm, int keyLength) throws InvalidKeySpecException, NoSuchAlgorithmException {
            byte[] key = encode(password.toCharArray(), salt, algorithm, iterations, keyLength);
            return Arrays.equals(this.key, key);
        }
    }

}
