package com.home.task.tm.transactions;

public enum TransactionMessage {
    INIT,
    START_2PC,
    VOTE_REQUEST,
    VOTE_ABORT,
    GLOBAL_COMMIT,
    GLOBAL_ABORT,
    DECISION_REQUEST;
}
