package eu.goodlike.cmd;

import com.google.common.collect.ImmutableList;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.misc.SpecialUtils;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.policy.ConcurrencyPolicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Executes arbitrary commands on the command line
 */
public final class CommandLineRunner implements AutoCloseable {

    /**
     * @return process launched using the input, Optional::empty if this launch failed
     * @throws NullPointerException if input is or contains null
     */
    public Optional<Process> execute(String... input) {
        Null.checkArray(input).ifAny("Input cannot be or contain null");
        return execute(ImmutableList.copyOf(input));
    }

    /**
     * @return process launched using the input, Optional::empty if this launch failed
     * @throws NullPointerException if input is or contains null
     */
    public Optional<Process> execute(List<String> input) {
        Null.checkList(input).ifAny("Input cannot be or contain null");

        String commandString = input.stream().collect(Collectors.joining(" "));
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command(input)
                .redirectErrorStream(true);

        try {
            parallelExecutionLimiter.acquire();
        } catch (InterruptedException e) {
            debugLogger.logMessage("Unexpected interruption while waiting for execution of: " + commandString);
            return Optional.empty();
        }

        debugLogger.logMessage("Running: " + commandString);
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            debugLogger.logMessage("Executing command on runtime failed: " + commandString);
            return Optional.empty();
        }
        SpecialUtils.runOnExit(() -> ensureMutualShutdown(process));

        executor.submit(() -> handleProcessExecution(process));
        return Optional.of(process);
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    // CONSTRUCTORS

    public CommandLineRunner(ConcurrencyPolicy concurrencyPolicy, CustomizedLogger debugLogger, CustomizedLogger processLogger) {
        int maxNumberOfThreads = concurrencyPolicy.getMaxConcurrentThreads();
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

    private void ensureMutualShutdown(Process process) {
        if (process.isAlive())
            process.destroyForcibly();
    }

    private void handleProcessExecution(Process process) {
        int processNumber = PROCESS_COUNT.incrementAndGet();
        String processPrefix = "P" + processNumber + "  --- ";

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        try {
            while ((line = input.readLine()) != null)
                processLogger.logMessage(processPrefix + line);
        } catch (IOException e) {
            debugLogger.logMessage("Unable to continue reading output from process P" + processNumber);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            debugLogger.logMessage("Unexpected interruption while waiting for process to finish: P" + processNumber);
        }

        debugLogger.logMessage("Process finished with exit code " + process.exitValue());
        parallelExecutionLimiter.release();
    }

    private static final AtomicInteger PROCESS_COUNT = new AtomicInteger(0);

}
