package eu.goodlike.twitch.download;

import eu.goodlike.io.log.CustomizedLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class CommandLineRunner implements AutoCloseable {

    public Process execute(String... input) {
        String commandString = Arrays.stream(input).collect(Collectors.joining(" "));

        try {
            parallelExecutionLimiter.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException("Unexpected interruption while executing: " + commandString, e);
        }

        ProcessBuilder processBuilder = new ProcessBuilder()
                .command(input)
                .redirectErrorStream(true);
        debugLogger.logMessage("Running: " + commandString);
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Executing command on runtime failed: " + commandString, e);
        }
        executor.submit(() -> outputExecutionToConsole(process));
        return process;
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    // CONSTRUCTORS

    public CommandLineRunner(int maxNumberOfThreads, CustomizedLogger debugLogger, CustomizedLogger processLogger) {
        this.parallelExecutionLimiter = new Semaphore(maxNumberOfThreads);
        this.executor = Executors.newFixedThreadPool(maxNumberOfThreads);
        this.debugLogger = debugLogger;
        this.processLogger = processLogger;
    }

    // PRIVATE

    private final Semaphore parallelExecutionLimiter;
    private final ExecutorService executor;
    private final CustomizedLogger debugLogger;
    private final CustomizedLogger processLogger;

    private void outputExecutionToConsole(Process process) {
        int processNumber = PROCESS_COUNT.incrementAndGet();
        String processPrefix = "P" + processNumber + " ---  ";

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        try {
            while ((line = input.readLine()) != null)
                processLogger.logMessage(processPrefix + line);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read output from process " + processNumber, e);
        }
        debugLogger.logMessage("Process finished with exit code " + process.exitValue());
        parallelExecutionLimiter.release();
    }

    private static final AtomicInteger PROCESS_COUNT = new AtomicInteger(0);

}
