package com.jun.reactive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jun.reactive.domain.Quote;
import com.jun.reactive.repository.QuoteMongoBlockingRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuoteBlockingController {
    private static final int DELAY_PER_ITEM_MS = 100;

    private final QuoteMongoBlockingRepository quoteMongoBlockingRepository;

    @GetMapping("/quotes-blocking")
    public Iterable<Quote> getQuotesBlocking() throws Exception {
        Thread.sleep(DELAY_PER_ITEM_MS * quoteMongoBlockingRepository.count());
        return quoteMongoBlockingRepository.findAll();
    }

}
