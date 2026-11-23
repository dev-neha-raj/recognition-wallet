package dev.recognitionwallet.wallet.service.ingest;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.time.Instant;
import java.util.Optional;

/// Postgres-backed RawEventStore. Inserts via JdbcAggregateTemplate because the id is never null 
@Repository
public class JdbcRawEventStore implements RawEventStore {

private final JdbcAggregateTemplate template;
private final RawEventRepository repository;

public JdbcRawEventStore(JdbcAggregateTemplate template, RawEventRepository repository) {
    this.template = template;
    this.repository = repository;
}

@Override
public boolean insertIfAbsent(IngestedGrant grant) {
    try {   
        template.insert(RawEventRow.from(grant));
        return true;
    } catch (DataAccessException e) {
        if (DuplicateKeys.isDuplicateKey(e)){
            return false;
        }
       throw e;
    }
}

@Override
public Optional<IngestedGrant> findBySourceEventId(String sourceEventId) {
    return repository.findById(sourceEventId).map(RawEventRow::toGrant);
}

@Override
public void markPublished(String sourceEventId, Instant publishedAt) {
        repository.markPublished(sourceEventId, publishedAt);
    }
}
