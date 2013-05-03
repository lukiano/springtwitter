package com.lucho.repository;

import com.lucho.domain.Tweet;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Repository that handles Tweet objects.
 * @author Luciano.Leggieri
 */
public interface TweetRepository extends GraphRepository<Tweet>,
        TweetRepositoryCustom {}
