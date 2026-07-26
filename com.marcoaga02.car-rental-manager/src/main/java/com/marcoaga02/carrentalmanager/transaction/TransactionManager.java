package com.marcoaga02.carrentalmanager.transaction;

public interface TransactionManager {

	<T> T doInTransaction(TransactionCode<T> code);

}
