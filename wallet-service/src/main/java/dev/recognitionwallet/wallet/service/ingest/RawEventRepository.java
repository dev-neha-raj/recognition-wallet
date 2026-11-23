package dev.recognitionwallet.wallet.service.ingest;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface RawEventRepository extends ListCrudRepository<RawEventRow, String>{

///Repository query methods default to read-only transactions; an UPDATE needs a writable one.
    @Modifying
    @Transactional
    @Query("UPDATE raw_event SET published_at = :publishedAt WHERE source_event_id = :sourceEventId")
    int markPublished(@Param("sourceEventId") String sourceEventId, @Param("publishedAt") Instant publishedAt);
}


