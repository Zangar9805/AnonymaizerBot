package kz.zangpro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public final class BotInitializer {

    private static final Logger LOG = LogManager.getLogger(BotInitializer.class);

    public static void main(String[] args) {

        try {
            LOG.info("Initializing API context...");
            ApiContextInitializer.init();

            TelegramBotsApi botsApi = new TelegramBotsApi();

            LOG.info("Configuring bot options...");
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

            LOG.info("Registering Anonymaizer...");
            botsApi.registerBot(new AnonymizerBot(botOptions));

            LOG.info("Anonymaizer bot is ready for work!");

        } catch (TelegramApiRequestException e) {
            LOG.error("Error while initializing bot!", e);
        }
    }
}
