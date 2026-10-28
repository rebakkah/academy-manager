package academy;

// record = immutable data holder.
// Accessors are automatically email() and city().
public record ContactDetails(String email, String city) {

    // Compact constructor runs before implicit field assignment.
    public ContactDetails {
        email = Validation.requireText(email, "email");
        city = Validation.requireText(city, "city");
    }
}