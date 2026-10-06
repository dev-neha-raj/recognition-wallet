package dev.recognitionwallet.wallet.service.messaging;

import java.util.List;

/**
 * kafka topic names for the wallet event pipeline.
 * 
 constants (not config) because @kafkaListener annotations need compile-time values.
Every topic has a dead-letter twin named <topic>.dlt
*
 */

public final class Topics {
    public static final String AI_USAGE_INGESTED = "wallet.ai-usage-ingested.v1";
    public static final String JUDGE_REQUESTED = "wallet.judge-requested.v1";
    public static final String QUALITY_SCORED = "wallet.quality-scored.v1";
    public static final String REWARD_DRAWN = "wallet.reward-drawn.v1";

    public static final String DLT_SUFFIX = ".dlt";

    public static final List<String> MAIN_TOPICS = List.of(
            AI_USAGE_INGESTED,
            JUDGE_REQUESTED,
            QUALITY_SCORED,
            REWARD_DRAWN
    );

    private Topics() {
        // Prevent instantiation
    }

    public static String deadLetterOf(String topic) {
        return topic + DLT_SUFFIX;
    }
}