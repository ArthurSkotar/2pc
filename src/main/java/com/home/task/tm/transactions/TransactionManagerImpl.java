package com.home.task.tm.transactions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.home.task.tm.transactions.TransactionMessage.*;

public class TransactionManagerImpl implements TransactionManager {

    private final long timeoutSeconds;
    private final List<Participant> participants;

    public TransactionManagerImpl(final int timeoutSeconds, final Participant... participants) {
        this.timeoutSeconds = timeoutSeconds;
        this.participants = Arrays.asList(participants);
    }

    @Override
    public void start() {
        System.out.println(START_2PC);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<List<TransactionMessage>> future = executor.submit(
                () -> participants.stream()
                        .map(p -> p.respond(CompletableFuture.completedFuture(VOTE_REQUEST), participants.stream()
                                .filter(participant -> !p.getId().equals(participant.getId())).collect(Collectors.toList())))
                        .collect(Collectors.toList())
        );
        executor.shutdown();
        try {
            List<TransactionMessage> result = future.get(timeoutSeconds, TimeUnit.SECONDS);
            Optional<TransactionMessage> abort = result.stream()
                    .filter(r -> r == VOTE_ABORT).findFirst();
            if (abort.isPresent()) {
                System.out.println(GLOBAL_ABORT);
                participants.forEach(p -> p.notify(GLOBAL_ABORT));
            } else {
                System.out.println(GLOBAL_COMMIT);
                participants.forEach(p -> p.notify(GLOBAL_COMMIT));
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            System.out.println(GLOBAL_ABORT);
            participants.forEach(p -> p.notify(GLOBAL_ABORT));
        }
    }
}
