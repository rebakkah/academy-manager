package academy;

public final class OnlineWorkshop extends Workshop {

    private final String meetingUrl;

    public OnlineWorkshop(int id, String title, int capacity, String meetingUrl) {
        super(id, title, capacity);
        this.meetingUrl = Validation.requireText(meetingUrl, "meeting URL");
    }

    @Override
    public String joiningInstructions() {
        return "Join at " + meetingUrl + ".";
    }

    public String meetingUrl() {
        return meetingUrl;
    }
}