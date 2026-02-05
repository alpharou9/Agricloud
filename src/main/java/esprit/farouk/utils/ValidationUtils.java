package esprit.farouk.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{8,15}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-zÀ-ÿ\\s]{2,}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true; // phone is optional
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
}
