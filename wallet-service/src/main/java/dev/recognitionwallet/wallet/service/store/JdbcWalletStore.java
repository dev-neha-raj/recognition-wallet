package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.common.model.Wallet;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcWalletStore implements WalletStore {

    private final JdbcAggregateTemplate template;
    private final WalletRepository repository;

    public JdbcWalletStore(
            JdbcAggregateTemplate template,
            WalletRepository repository) {
        this.template = template;
        this.repository = repository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        try {
            template.insert(WalletRowMapper.toRow(wallet));
        } catch (DbActionExecutionException e) {
            if (isDuplicateKey(e)) {
                throw new WalletAlreadyExistsException(wallet.employeeId());
            }
            throw e;
        } catch (DataAccessException e) {
            if (isDuplicateKey(e)) {
                throw new WalletAlreadyExistsException(wallet.employeeId());
            }
            throw e;
        }

        return wallet;
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return repository.findById(id)
                .map(WalletRowMapper::toDomain);
    }

    static boolean isDuplicateKey(Throwable t) {
        for (Throwable cause = t;
             cause != null;
             cause = cause.getCause()) {

            if (cause instanceof SQLException sqlException
                    && "23505".equals(sqlException.getSQLState())) {
                return true;
            }
        }

        return false;
    }
}