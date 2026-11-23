package dev.recognitionwallet.wallet.service;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// one injectable source of "now", so time-based rules can be tested a fixed clock.
@Configuration(proxyBeanMethods = false)
class ClockConfig {

@Bean
Clock clock(){
    return Clock.systemUTC();
}

}