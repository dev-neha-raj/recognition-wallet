package dev.recognitionwallet.wallet.service.store;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring data jdbc repository for WalletRow
 * 
 * Used for read only. Inserts go through jdbcAggregateTemplate.insert() because our ids are
 * assigned by the domain, and save() would treat a non-null id as an UPDATE.
 * 
 */

public interface WalletRepository extends ListCrudRepository<WalletRow, UUID> {

//Derived query: spring Data builds 'WHERE employee_id = ?' from the method name    

    Optional<WalletRow> findByEmployeeId(String employeeId);
}