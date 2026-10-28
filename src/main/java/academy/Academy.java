package academy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class Academy {

    // HashMap: fast lookup by ID.
    private final Map<Integer, Student> students = new HashMap<>();

    // LinkedHashMap: lookup by ID + keeps workshop creation order.
    private final Map<Integer, Workshop> workshops = new LinkedHashMap<>();

    // One comparator reused everywhere:
    // sort by name, then by ID as tie-breaker.
    private static final Comparator<Student> BY_NAME_THEN_ID =
            Comparator.comparing(Student::name, String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(Student::id);

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

    public List<Student> searchStudents(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }

        String query = text.trim().toLowerCase(Locale.ROOT);

        return students.values().stream()
                .filter(student -> query.isEmpty()
                        || student.name().toLowerCase(Locale.ROOT).contains(query))
                .sorted(BY_NAME_THEN_ID)
                .toList();
    }

    public List<String> participantNames(int workshopId) {
        return sortedNames(requireWorkshop(workshopId).getParticipants());
    }

    public TreeMap<String, Long> participantCountsByCity(int workshopId) {
        return requireWorkshop(workshopId).getParticipants().stream()
                .collect(Collectors.groupingBy(
                        Student::city,
                        TreeMap::new,
                        Collectors.counting()));
    }

    public TreeSet<String> allCities() {
        return students.values().stream()
                .map(Student::city)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public List<String> buildReport() {
        List<String> lines = new ArrayList<>();

        for (Workshop workshop : workshops.values()) {
            if (!lines.isEmpty()) {
                lines.add("");
            }

            List<Student> participants = workshop.getParticipants();

            lines.add(workshop.title() + " | "
                    + participants.size() + "/" + workshop.capacity()
                    + " participants");

            lines.add(workshop.joiningInstructions());

            for (String name : sortedNames(participants)) {
                lines.add("- " + name);
            }
        }

        return List.copyOf(lines);
    }

    private List<String> sortedNames(List<Student> participants) {
        return participants.stream()
                .sorted(BY_NAME_THEN_ID)
                .map(Student::name)
                .toList();
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