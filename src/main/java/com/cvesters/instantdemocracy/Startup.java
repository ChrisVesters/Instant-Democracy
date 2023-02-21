package com.cvesters.instantdemocracy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Startup implements CommandLineRunner {

    private static final int THREAD_COUNT = 8;
    private static final long VOTERS_PER_THREAD = 10_000_000;

    private final Random rand = new Random();
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private final List<Callable<Void>> tasks = new ArrayList<>(THREAD_COUNT);

    private final VotingService votingService;

    public Startup(final VotingService votingService) {
        this.votingService = votingService;
    }

    @Override
    public void run(String... args) throws InterruptedException {
        prepareVoting();
        startVoting();
        votingService.printResults();
    }

    private void prepareVoting() {
        for (int thread = 0; thread < THREAD_COUNT; ++thread) {
            final Callable<Void> t = () -> {
                final int contestants = votingService.getContestants();
                for (int voter = 0; voter < VOTERS_PER_THREAD; ++voter) {
                    votingService.vote(rand.nextInt(contestants));
                }
                return null;
            };
            tasks.add(t);
        }
    }

    private void startVoting() throws InterruptedException {
        final Instant start = Instant.now();
        executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        final Instant end = Instant.now();
        final long duration = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("Voting duration: " + duration + "ms");
    }
}