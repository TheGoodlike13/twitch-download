package eu.goodlike.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class CommandLineRunner implements AutoCloseable {

    public Process execute(String... input) {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command(input)
                .redirectErrorStream(true);
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Executing command on runtime failed: " + Arrays.toString(input), e);
        }
        executor.submit(() -> outputExecutionToConsole(process, input));
        return process;
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    // CONSTRUCTORS

    public CommandLineRunner() {
        this(DEFAULT_THREAD_COUNT);
    }

    public CommandLineRunner(int numberOfThreads) {
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    // PRIVATE

    private final ExecutorService executor;

    private void outputExecutionToConsole(Process process, String... command) {
        String processPrefix = "Process " + PROCESS_COUNT.incrementAndGet() + " --- ";
        String commandString = Arrays.stream(command).collect(Collectors.joining(" "));
        System.out.println(processPrefix + "Running: " + commandString);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        try {
            while ((line = input.readLine()) != null)
                System.out.println(processPrefix + line);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read output from executed command: " + commandString, e);
        }
        System.out.println(processPrefix + "Process finished with exit code " + process.exitValue());
    }

    private static final int DEFAULT_THREAD_COUNT;
    static {
        int availableCores = Runtime.getRuntime().availableProcessors();
        DEFAULT_THREAD_COUNT = availableCores > 4 ? availableCores : 4;
    }

    private static final AtomicInteger PROCESS_COUNT = new AtomicInteger(0);

}
