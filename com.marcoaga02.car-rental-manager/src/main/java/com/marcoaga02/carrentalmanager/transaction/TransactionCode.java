package com.marcoaga02.carrentalmanager.transaction;

import java.util.function.Function;

@FunctionalInterface
public interface TransactionCode<T> extends Function<TransactionContext, T> {
}
