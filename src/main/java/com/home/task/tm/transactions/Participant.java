package com.home.task.tm.transactions;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Future;

public interface Participant {

    TransactionMessage notify(TransactionMessage message);
    TransactionMessage respond(Future<TransactionMessage> message, final List<Participant> other);
    void prepare(String query) throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    String getId();
}
