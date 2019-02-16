package com.home.task.tm.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.home.task.tm.transactions.TransactionMessage.*;

public class ParticipantImpl implements Participant {

    private final Connection connection;
    private final long timeSeconds;
    private final String query;
    private final String uuid;
    private TransactionMessage status;

    public ParticipantImpl(final Connection connection, final long timeSeconds, String query) {
        this.connection = connection;
        this.timeSeconds = timeSeconds;
        this.query = query;
        this.uuid = UUID.randomUUID().toString();
        System.out.println(INIT);
    }

    @Override
    public TransactionMessage notify(TransactionMessage message) {
        return status;
    }


    @Override
    public TransactionMessage respond(Future<TransactionMessage> message, final List<Participant> other) {
        try {
            message.get(timeSeconds, TimeUnit.SECONDS);
            prepare(query);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            if (other.stream().map(p -> p.notify(DECISION_REQUEST)).anyMatch(tm -> tm == GLOBAL_ABORT)) {
                System.out.println(GLOBAL_ABORT);
                try {
                    rollback();
                } catch (SQLException e1) {
                    return GLOBAL_ABORT;
                }
                return GLOBAL_ABORT;
            } else {
                System.out.println(GLOBAL_COMMIT);
                status = GLOBAL_COMMIT;
                try {
                    commit();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    try {
                        rollback();
                    } catch (SQLException e2) {
                        return GLOBAL_ABORT;
                    }
                    status=GLOBAL_ABORT;
                    return GLOBAL_ABORT;
                }
                status=GLOBAL_COMMIT;
                return GLOBAL_COMMIT;
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                rollback();
            } catch (SQLException e1) {
                return GLOBAL_ABORT;
            }
            status = GLOBAL_ABORT;
            return GLOBAL_ABORT;
        }
        status = GLOBAL_COMMIT;
        return GLOBAL_COMMIT;
    }

    @Override
    public void prepare(String query) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("BEGIN;" + query + "PREPARE TRANSACTION '" + uuid + "';")) {
            preparedStatement.execute();
        }
    }

    @Override
    public void commit() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("COMMIT PREPARED '" + uuid + "';")) {
            preparedStatement.execute();
        }
    }

    @Override
    public void rollback() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("ROLLBACK PREPARED '" + uuid + "';")) {
            preparedStatement.execute();
        }
    }

    @Override
    public String getId() {
        return uuid;
    }
}
