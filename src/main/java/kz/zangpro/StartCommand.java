package kz.zangpro;

import kz.zangpro.AnonymaizerCommand;
import kz.zangpro.Anonymous;
import org.apache.logging.log4j.Level;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public final class StartCommand extends AnonymaizerCommand {

    private final AnonymousService mAnonymouses;

    public StartCommand(AnonymousService Anonymouses) {
        super("start", "start using bot\n");
        mAnonymouses = Anonymouses;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        log.info(LogTemplate.COMMAND_PROCESSING.getTemplate(), user.getId(), getCommandIdentifier());

        StringBuilder sb = new StringBuilder();

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());

        if (mAnonymouses.addAnonymous(new Anonymous(user, chat))){
            log.info("User {} is trying to execute '{}' the first time. Added to users' list.", user.getId(), getCommandIdentifier());
            sb.append("Hi, ").append(user.getUserName()).append("! You've been added to bot users' list\n")
                    .append("Please execute command: \n'/set_name <displayed_name>'\nwhere <displayed_name> is the name you want to use to hide your real name.");
        } else {
            log.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} has already executed '{}'. Is he tryning to do it one more time?", user.getId(), getCommandIdentifier());
            sb.append("You've already started bot! You can send message if you set your name(/set_name).");
        }

        message.setText(sb.toString());
        execute(absSender, message, user);
    }
}
