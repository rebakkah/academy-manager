package academy;

final class TestData {

    private TestData() {
    }

    // Short way to build a valid student in tests.
    static Student student(int id, String name, String city) {
        return new Student(id, name, new ContactDetails(id + "@example.com", city));
    }
}