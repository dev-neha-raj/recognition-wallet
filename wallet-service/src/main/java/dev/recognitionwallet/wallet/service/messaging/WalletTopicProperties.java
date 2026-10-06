package dev.recognitionwallet.wallet.service.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

//Topic Layout settings. partitions and replicas differ per environment(1 broker locally, 3 in a real cluster).
@ConfigurationProperties("wallet.kafka.topics")
public record WalletTopicProperties(
   @DefaultValue("3") int partitions, 
    @DefaultValue("1") int replicas
    ){}