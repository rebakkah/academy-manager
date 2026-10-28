package academy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Workshop {

    private final int id;
    private final String title;
    private final int capacity;

    // ArrayList keeps registration order.
    private final List<Student> participants = new ArrayList<>();

    protected Workshop(int id, String title, int capacity) {
        this.id = Validation.requirePositive(id, "workshop id");
        this.title = Validation.requireText(title, "title");
        this.capacity = Validation.requirePositive(capacity, "capacity");
    }

    // Polymorphism #1:
    // every concrete workshop knows how people join it.
    public abstract String joiningInstructions();

    public void register(Student student) {
        Validation.requireNonNull(student, "student");

        // Spec order: duplicate BEFORE capacity.
        if (isRegistered(student.id())) {
            throw new IllegalStateException(
                    "student " + student.id() + " is already registered");
        }

        if (availablePlaces() == 0) {
            throw new IllegalStateException(
                    "workshop " + id + " is full");
        }

        participants.add(student);
    }

    public boolean unregister(int studentId) {
        // Explicit iterator is required by the task.
        Iterator<Student> iterator = participants.iterator();

        while (iterator.hasNext()) {
            Student participant = iterator.next();

            if (participant.id() == studentId) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public List<Student> getParticipants() {
        // Return a copy: callers cannot mutate internal state.
        return List.copyOf(participants);
    }

    public int availablePlaces() {
        return capacity - participants.size();
    }

    private boolean isRegistered(int studentId) {
        return participants.stream()
                .anyMatch(participant -> participant.id() == studentId);
    }

    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public int capacity() {
        return capacity;
    }
}