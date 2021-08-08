package com.jun.reactive.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.jun.reactive.domain.Quote;
import com.jun.reactive.repository.QuoteMongoReactiveRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class QuoteDataLoader implements ApplicationRunner {

    private final QuoteMongoReactiveRepository quoteMongoReactiveRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (quoteMongoReactiveRepository.count().block() != 0L) return;

        var idSupplier = getIdSequenceSupplier();
        var bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass()
            .getClassLoader()
            .getResourceAsStream("pg2000.txt"))));

        Flux.fromStream(bufferedReader.lines()
                .filter(l -> !l.trim().isEmpty()))
            .map(l -> quoteMongoReactiveRepository.save(new Quote(idSupplier.get(), "A Quote", l)))
            .subscribe(m -> log.info("New quote loaded : {}", m.block()));

        log.info("Repository now contains {} entries", quoteMongoReactiveRepository.count().block());
    }

    private Supplier<String> getIdSequenceSupplier() {
        return new Supplier<>() {
            Long l = 0L;

            @Override
            public String get() {
                return String.format("%05d", l++);
            }
        };
    }
}
