package com.spring.boot.rocks.repository;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import com.spring.boot.rocks.model.CatalogueItem;

import reactor.core.publisher.Mono;

public interface CatalogueRepository extends ReactiveSortingRepository<CatalogueItem, Long> {

    Mono<CatalogueItem> findBySku(String sku);
}
