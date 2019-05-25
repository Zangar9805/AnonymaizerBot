package kz.zangpro;

public enum LogTemplate {

    MESSAGE_EXCEPTION("User {} has caused an exception while sending message!"),
    MESSAGE_PROCESSING("Precessing user {}'s message."),
    MESSAGE_RECEIVED("User {} has received message from another user {}."),
    MESSAGE_LOST("User {} did not get message from another user {}."),
    MESSAGE_SENT("User {} sent message to other users: \"{}\"."),

    COMMAND_PROCESSING("User {} is executing '{}' command..."),
    COMMAND_SUCCESS("User {} has successfully executed '{}' command."),
    COMMAND_EXCEPTION("User {} command '{}' has caused an exception!");

    private final String mTemplate;

    LogTemplate(String template) {
        mTemplate = template;
    }

    public String getTemplate() {
        return mTemplate;
    }
}