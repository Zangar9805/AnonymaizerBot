package kz.zangpro;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Stream;

public final class AnonymizerBot extends TelegramLongPollingCommandBot {

    private static final Logger LOG = LogManager.getLogger(AnonymizerBot.class);

    private static final String BOT_NAME = "anonymaizer_bot";
    private static final String BOT_TOKEN = "823613637:AAFm0rp_Q9YXGBQpFterboxYqa0XKlImBmc";

    private final AnonymousService mAnonymouses;

    public AnonymizerBot(DefaultBotOptions botOptions) {

        super(botOptions, BOT_NAME);

        LOG.info("Initializing Anonymizer Bot...");

        LOG.info("Initializing anonymouses list...");
        mAnonymouses = new AnonymousService();

        LOG.info("Registering commands...");
        LOG.info("Registering '/start'...");
        register(new StartCommand(mAnonymouses));
        LOG.info("Registering '/set_name'...");
        register(new SetNameCommand(mAnonymouses));
        LOG.info("Registering '/stop'...");
        register(new StopCommand(mAnonymouses));
        LOG.info("Registering '/my_name'...");
        register(new MyNameCommand(mAnonymouses));
        HelpCommand helpCommand = new HelpCommand(this);
        LOG.info("Registering '/help'...");
        register(helpCommand);

        LOG.info("Registering default action'...");
        registerDefaultAction(((absSender, message) -> {

            LOG.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} is trynig to execute unknown command '{}'.", message.getFrom().getId(), message.getText());

            SendMessage text = new SendMessage();
            text.setChatId(message.getChatId());
            text.setText(message.getText() + " command not found!");

            try{
                absSender.execute(text);
            } catch (TelegramApiException e) {
                LOG.error("Error while replying unknown command to user {}.", message.getFrom(), e);
            }

            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});

        }));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        LOG.info("Proccesing non-command update...");

        if (!update.hasMessage()){
            LOG.error("Update doesn't  have a body!");
            throw new IllegalStateException("Update doesn't have a body!");
        }

        Message msg = update.getMessage();
        User user = msg.getFrom();

        LOG.info(LogTemplate.MESSAGE_PROCESSING.getTemplate(), user.getId());

        if (!canSendMessage(user, msg)){
            return;
        }

        String clearMessage = msg.getText();
        String messageForUsers = String.format("%s:\n%s", mAnonymouses.getDisplayedName(user), msg.getText());

        SendMessage answer = new SendMessage();

        answer.setText(clearMessage);
        answer.setChatId(msg.getChatId());
        replyToUser(answer, user, clearMessage);

        answer.setText(messageForUsers);
        Stream<Anonymous> anonymouses = mAnonymouses.anonymouses();
        anonymouses.filter(a -> !a.getUser().equals(user))
                .forEach(a -> {
                    answer.setChatId(a.getChat().getId());
                    sendMessageToUsers(answer, a.getUser(), user);
                });
    }

    private void sendMessageToUsers(SendMessage message, User reciver, User sender) {
        try {
            execute(message);
            LOG.log(Level.getLevel(LogLevel.SUCCESS.getValue()), LogTemplate.MESSAGE_RECEIVED.getTemplate(), reciver.getId(), sender.getId());
        } catch (TelegramApiException e) {
            LOG.error(LogTemplate.MESSAGE_LOST.getTemplate(), reciver.getId(), sender.getId(), e);
        }
    }

    private boolean canSendMessage(User user, Message msg) {
        SendMessage answer = new SendMessage();
        answer.setChatId(msg.getChatId());

        if (!msg.hasText() || msg.getText().trim().length() == 0){
            LOG.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} is trying to send empty message!", user.getId());
            answer.setText("You shouldn't send empty message!");
            replyToUser(answer, user, msg.getText());
            return false;
        }

        if (!mAnonymouses.hasAnonymous(user)){
            LOG.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} is trying to send message without starting the bot!", user.getId());
            answer.setText("Firstly you should start bot! Use /start command!");
            replyToUser(answer, user, msg.getText());
            return false;
        }

        if (mAnonymouses.getDisplayedName(user) == null){
            LOG.log(Level.getLevel(LogLevel.STRANGE.getValue()), "User {} is trying to send message without setting a name!", user.getId());
            answer.setText("You must set a name before sending messages. \nUse '/set_name <displayed_name>' commmand");
            replyToUser(answer, user, msg.getText());
            return false;
        }

        return true;
    }

    private void replyToUser(SendMessage message, User user, String messageText) {
        try {
            execute(message);
            LOG.log(Level.getLevel(LogLevel.SUCCESS.getValue()), LogTemplate.MESSAGE_RECEIVED.getTemplate(), user.getId(), messageText);
        } catch (TelegramApiException e) {
            LOG.error(LogTemplate.MESSAGE_EXCEPTION.getTemplate(), user.getId(), e);
        }
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
