package academy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class Academy {

    // HashMap: fast lookup by ID.
    private final Map<Integer, Student> students = new HashMap<>();

    // LinkedHashMap: lookup by ID + keeps workshop creation order.
    private final Map<Integer, Workshop> workshops = new LinkedHashMap<>();

    public void addStudent(Student student) {
        Validation.requireNonNull(student, "student");

        if (students.putIfAbsent(student.id(), student) != null) {
            throw new IllegalArgumentException(
                    "duplicate student id: " + student.id());
        }
    }

    public void addWorkshop(Workshop workshop) {
        Validation.requireNonNull(workshop, "workshop");

        if (workshops.putIfAbsent(workshop.id(), workshop) != null) {
            throw new IllegalArgumentException(
                    "duplicate workshop id: " + workshop.id());
        }
    }

    public Optional<Student> findStudent(int id) {
        return Optional.ofNullable(students.get(id));
    }

    public Optional<Workshop> findWorkshop(int id) {
        return Optional.ofNullable(workshops.get(id));
    }

    public void register(int studentId, int workshopId) {
        Student student = requireStudent(studentId);
        Workshop workshop = requireWorkshop(workshopId);

        // Academy does not know capacity/duplicate rules.
        // Workshop owns them.
        workshop.register(student);
    }

    public boolean unregister(int studentId, int workshopId) {
        requireStudent(studentId);

        Workshop workshop = requireWorkshop(workshopId);

        // Only the reference inside this workshop is removed.
        // The student remains in Academy.students.
        return workshop.unregister(studentId);
    }

    private Student requireStudent(int id) {
        return findStudent(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "unknown student id: " + id));
    }

    private Workshop requireWorkshop(int id) {
        return findWorkshop(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "unknown workshop id: " + id));
    }
}
