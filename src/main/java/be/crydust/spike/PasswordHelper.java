package be.crydust.spike;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DO NOT USE IN PRODUCTION
 * <p>
 * This class is not protected against timing attacks and is also not reviewed.
 */
public class PasswordHelper {

    // example of how to encode password with tomcat
    // encodedTomcat = 1b746ade68bdc4e8$185000$37563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f
//    private static String encodeWithTomcat(String algorithm, int iterations, int saltLength, int keyLength, String credentials) throws NoSuchAlgorithmException {
//        SecretKeyCredentialHandler h = new SecretKeyCredentialHandler();
//        h.setAlgorithm(algorithm);
//        h.setIterations(iterations);
//        h.setSaltLength(saltLength);
//        h.setKeyLength(keyLength);
//        return h.mutate(credentials);
//    }

    // example of how to encode password with spring
    // encodedSpring = 1b746ade68bdc4e837563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f
//    private static String encodeWithSpring(String algorithm, int iterations, int saltLength, int keyLength, String credentials) throws NoSuchAlgorithmException {
//        if (saltLength != 8) {
//            throw new IllegalArgumentException("saltLength must be 8");
//        }
//        Pbkdf2PasswordEncoder e = new Pbkdf2PasswordEncoder("", iterations, keyLength);
//        e.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.valueOf(algorithm));
//        e.setEncodeHashAsBase64(false);
//        return  e.encode(credentials);
//    }

    public static void main(String[] args) throws Exception {
        // [-a <algorithm>] [-e <encoding>] [-i <iterations>] [-s <salt-length>] [-k <key-length>] [-h <handler-class-name>] <credentials>
        // ./digest.sh -a PBKDF2WithHmacSHA256 -e UTF-8 -i 185000 -s 8 -k 256 -h org.apache.catalina.realm.SecretKeyCredentialHandler 123456
        String algorithm = "PBKDF2WithHmacSHA256";
        int iterations = 185000;
        int saltLength = 8;
        int keyLength = 256;
        String credentials = args.length == 1 ? args[0] : "123456";

        Encoder encoder = new Encoder(algorithm, iterations, saltLength, keyLength);

        Encoded encoded = encoder.encode(credentials);

        System.out.println("credentials = " + credentials);
        System.out.println("encodedTomcat = " + encoded.toTomcat());
        System.out.println("encodedSpring = " + encoded.toSpring());

//        System.out.println(Encoded.parseTomcat("1b746ade68bdc4e8$185000$37563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f")
//                .matches("123456", algorithm, keyLength));
//        System.out.println(Encoded.parseTomcat("1b746ade68bdc4e8$185000$37563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f")
//                .matches("wrong", algorithm, keyLength));

//        System.out.println(Encoded.parseSpring("1b746ade68bdc4e837563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f", saltLength, iterations)
//                .matches("123456", algorithm, keyLength));
//        System.out.println(Encoded.parseSpring("1b746ade68bdc4e837563bd64c4bcf2d125c30e743faba31b3bdbf8940d644ccc60298b28cf14f1f", saltLength, iterations)
//                .matches("wrong", algorithm, keyLength));
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

    private static byte[] generateKey(char[] password, byte[] salt, String algorithm, int iterations, int keyLength) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return SecretKeyFactory.getInstance(algorithm)
                .generateSecret(new PBEKeySpec(password, salt, iterations, keyLength))
                .getEncoded();
    }

    private static class Encoder {
        final String algorithm;
        final int iterations;
        final int saltLength;
        final int keyLength;

        Encoder(String algorithm, int iterations, int saltLength, int keyLength) {
            this.algorithm = algorithm;
            this.iterations = iterations;
            this.saltLength = saltLength;
            this.keyLength = keyLength;
        }

        Encoded encode(String credentials) throws NoSuchAlgorithmException, InvalidKeySpecException {
            char[] password = credentials.toCharArray();

            byte[] salt = new byte[this.saltLength];
            SecureRandom random = SecureRandom.getInstanceStrong();
            random.nextBytes(salt);

            byte[] key = generateKey(password, salt, algorithm, iterations, keyLength);

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
            byte[] key = generateKey(password.toCharArray(), salt, algorithm, iterations, keyLength);
            return Arrays.equals(this.key, key);
        }
    }

}
