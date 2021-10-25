package cc.mudev.bca_android.database;

public enum TB_CHAT_EVENT_TYPE {
    PARTICIPANT_IN("PARTICIPANT_IN", true),
    PARTICIPANT_OUT("PARTICIPANT_OUT", true),
    PARTICIPANT_KICKED("PARTICIPANT_KICKED", true),

    MESSAGE_POSTED("MESSAGE_POSTED", false),
    MESSAGE_POSTED_IMAGE("MESSAGE_POSTED_IMAGE", false),
    MESSAGE_DELETED("MESSAGE_DELETED", true);

    private final String eventName;
    public final boolean informationEvent;

    TB_CHAT_EVENT_TYPE(String eventName, boolean informationEvent) {
        this.eventName = eventName;
        this.informationEvent = informationEvent;
    }

    @Override
    public String toString() {
        return eventName;
    }
}
