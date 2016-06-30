package eu.goodlike.twitch.download.configurations.policy;

import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.io.log.CustomizedLoggers;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Defines configurations for logging
 */
public final class LogPolicy {

    /**
     * The logger returned by this method does not necessarily log anything (in the case where logging is off)
     * @return logger which will be used for debug info
     */
    public CustomizedLogger getDebugLogger() {
        return debugLogger;
    }

    /**
     * The logger returned by this method does not necessarily log anything (in the case where logging is off)
     * @return logger which will be used for process output
     */
    public CustomizedLogger getProcessLogger() {
        return processLogger;
    }

    // CONSTRUCTORS

    public static LogPolicy from(OptionsProvider optionsProvider) {
        Null.check(optionsProvider).ifAny("Options provider cannot be null");
        List<CustomizedLogger> debugLoggers = new ArrayList<>();
        List<CustomizedLogger> processLoggers = new ArrayList<>();

        if (optionsProvider.isDebugOutputEnabled())
            debugLoggers.add(CustomizedLoggers.forConsole());

        if (optionsProvider.isProcessOutputEnabled())
            processLoggers.add(CustomizedLoggers.forConsole());

        Optional<CustomizedLogger> fileLogger = optionsProvider.getLogFileLocation()
                .map(CustomizedLoggers::forFile);

        fileLogger.ifPresent(debugLoggers::add);
        fileLogger.ifPresent(processLoggers::add);
        return new LogPolicy(CustomizedLoggers.combine(debugLoggers), CustomizedLoggers.combine(processLoggers));
    }

    public LogPolicy(CustomizedLogger debugLogger, CustomizedLogger processLogger) {
        Null.check(debugLogger, processLogger).ifAny("Loggers cannot be null");

        this.debugLogger = debugLogger;
        this.processLogger = processLogger;
    }

    // PRIVATE

    private final CustomizedLogger debugLogger;
    private final CustomizedLogger processLogger;

}
