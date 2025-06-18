package com.gachon.home_protector.global.config.database;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Profile("default")
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadTransaction = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return isReadTransaction ? "slave" : "master";
    }
}
