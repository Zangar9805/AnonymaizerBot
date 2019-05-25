package kz.zangpro;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

abstract class AnonymaizerCommand extends BotCommand {

    final Logger log = LogManager.getLogger(getClass());

    public AnonymaizerCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    void execute(AbsSender sender, SendMessage message, User user){
        try{
            sender.execute(message);
            log.log(Level.getLevel(LogLevel.SUCCESS.getValue()), LogTemplate.COMMAND_SUCCESS.getTemplate(), user.getId(), getCommandIdentifier());
        } catch (TelegramApiException e) {
            log.error(LogTemplate.COMMAND_EXCEPTION.getTemplate(), user.getId(), getCommandIdentifier(), e);
        }
    }
}
