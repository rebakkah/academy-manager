package academy;

public record Student(int id, String name, ContactDetails contact) {

    public Student {
        id = Validation.requirePositive(id, "student id");
        name = Validation.requireText(name, "name");
        contact = Validation.requireNonNull(contact, "contact");
    }

    // Convenience method: callers can write student.city()
    // instead of student.contact().city().
    public String city() {
        return contact.city();
    }
}