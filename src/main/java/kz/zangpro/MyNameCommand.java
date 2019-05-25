package kz.zangpro;

import kz.zangpro.AnonymaizerCommand;
import kz.zangpro.AnonymousService;
import kz.zangpro.LogTemplate;
import org.apache.logging.log4j.Level;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public final class MyNameCommand extends AnonymaizerCommand {

    private final AnonymousService mAnonymouses;

    public MyNameCommand(AnonymousService mAnonymouses) {
        super("my_name", "show your current name will be displayed with your messages\n");
        this.mAnonymouses = mAnonymouses;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        log.info(LogTemplate.COMMAND_PROCESSING.getTemplate(), user.getId(), getCommandIdentifier());

        StringBuilder sb = new StringBuilder();

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());

        if (!mAnonymouses.hasAnonymous(user)){

            sb.append("You are not in bot users' list! Send /start command");
            log.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} is trying to execute '{}' without starting the bot", user.getId(), getCommandIdentifier());

        } else if (mAnonymouses.getDisplayedName(user) == null){

            sb.append("Currently you don't have a name. \nSet it using command:\n'/set_name <displayed_name>'");
            log.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} is trying to execute '{}' without having a name.", user.getId(), getCommandIdentifier());
        } else {

            log.info("User {} is executing '{}'. Name is '{}'.", user.getId(), getCommandIdentifier(), mAnonymouses.getDisplayedName(user));
            sb.append("You current name: ").append(mAnonymouses.getDisplayedName(user));
        }

        message.setText(sb.toString());
        execute(absSender, message, user);
    }
}
