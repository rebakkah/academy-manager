package academy;

public final class ClassroomWorkshop extends Workshop {

    private final String room;

    public ClassroomWorkshop(int id, String title, int capacity, String room) {
        super(id, title, capacity);
        this.room = Validation.requireText(room, "room");
    }

    @Override
    public String joiningInstructions() {
        return "Go to room " + room + ".";
    }

    public String room() {
        return room;
    }
}