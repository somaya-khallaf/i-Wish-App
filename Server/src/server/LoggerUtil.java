package server;

import java.util.logging.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtil {

    private static class ColoredFormatter extends Formatter {

        private static final String ANSI_RESET = "\u001B[0m";
        private static final String ANSI_RED = "\u001B[31m";
        private static final String ANSI_GREEN = "\u001B[32m";
        private static final String ANSI_YELLOW = "\u001B[33m";
        private static final String ANSI_BLUE = "\u001B[34m";
        private static final String ANSI_CYAN = "\u001B[36m";

        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            Level level = record.getLevel();
            if (level == Level.SEVERE) {
                builder.append(ANSI_RED);
            } else if (level == Level.WARNING) {
                builder.append(ANSI_YELLOW);
            } else if (level == Level.INFO) {
                builder.append(ANSI_GREEN);
            } else if (level == Level.CONFIG) {
                builder.append(ANSI_BLUE);
            } else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
                builder.append(ANSI_CYAN);
            } else {
                builder.append(ANSI_RESET);
            }
            builder.append("[").append(dateFormat.format(new Date(record.getMillis()))).append("] ");
            builder.append("[").append(record.getLevel()).append("] ");
            builder.append(formatMessage(record));
            builder.append(ANSI_RESET);
            builder.append("\n");
            return builder.toString();
        }
    }

    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        configureLogger(logger);
    }

    private static void configureLogger(Logger logger) {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new ColoredFormatter());
        logger.addHandler(consoleHandler);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }
}