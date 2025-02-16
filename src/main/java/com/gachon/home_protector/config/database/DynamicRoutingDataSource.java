package com.gachon.home_protector.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadTransaction = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return isReadTransaction ? "slave" : "master";
    }
}
