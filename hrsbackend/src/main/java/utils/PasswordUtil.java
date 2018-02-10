package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by sasaradovanovic on 1/13/18.
 */
public class PasswordUtil {

    private static int workload = 12;

    public static String hashPassword(String plainText) {
        String salt = BCrypt.gensalt(workload);
        String hashedPassword = BCrypt.hashpw(plainText, salt);
        return(hashedPassword);
    }


    public static boolean checkPassword(String plainText, String storedHash) {
        boolean password_verified = false;

        if(null == storedHash || !storedHash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(plainText, storedHash);

        return(password_verified);
    }


    public static String hashActivationCode(String plainText) {
        plainText = plainText.replace("&", ">hrs>");
        String salt = BCrypt.gensalt(workload);
        String hashedActivationCode = BCrypt.hashpw(plainText, salt);
        return(hashedActivationCode);
    }

    public static boolean checkActivationCode(String receivedHash, String plainUsername) {
        boolean activation_code_verified = false;

        if(null == receivedHash || !receivedHash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        activation_code_verified = BCrypt.checkpw(plainUsername, receivedHash);

        return(activation_code_verified);
    }


}
