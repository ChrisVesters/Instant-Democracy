package com.cvesters.instantdemocracy;

import org.springframework.stereotype.Service;

@Service
public class VotingService {

    private static final int CONTESTANTS = 8;

    private final long[] votes = new long[CONTESTANTS];

    public int getContestants() {
        return CONTESTANTS;
    }

    // Very naive and wrong implementation.
    public void vote(final int contestant) {
        votes[contestant]++;
    }

    public void printResults() {
        long total = 0;
        for (int contestant = 0; contestant < CONTESTANTS; ++contestant) {
            long contestantVotes = votes[contestant];
            total += contestantVotes;
            System.out.printf("%2d - %d%n", contestant, contestantVotes);
        }
        System.out.println("Total - " + total);
    }
}
