package com.lucho.repository;

import com.lucho.domain.Tweet;
import com.lucho.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Default {@link TweetRepository} implementation.
 * @author Luciano.Leggieri
 */
@Transactional(readOnly = true)
public class TweetRepositoryImpl implements TweetRepositoryCustom {

    /**
     * Maximum number of tweets to return in a search.
     */
    private static final int MAX_RESULTS = 20;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Tweet> getTweetsForUserIncludingFollows(final User user,
            final Long millis) {
        /*
        QTweet qtweet = QTweet.tweet1;
        QUser followedBy = new QUser("followedBy");
        BooleanExpression whereClause = followedBy.id.eq(user.getId());
        if (millis != null) {
            Date date = new Date(millis.longValue());
            whereClause = whereClause.and(qtweet.creationDate.after(date));
        }
        return this.from(qtweet).join(qtweet.owner.followedBy, followedBy)
                .where(whereClause).orderBy(qtweet.creationDate.desc())
                .limit(MAX_RESULTS).list(qtweet);
        */
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Tweet> searchTweets(final String textToSearch,
            final User user) {
        /*
        FullTextEntityManager fullTextEntityManager = Search
                .getFullTextEntityManager(this.getEntityManager());

        // create native Lucene query using the query DSL
        // alternatively you can write the
        // Lucene query using the Lucene query parser
        // or the Lucene programmatic API.
        // The Hibernate Search DSL is recommended though.
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Tweet.class).get();
        Query lQuery = qb.keyword().onField("tweet").matching(textToSearch)
                .createQuery();

        // wrap Lucene query in a org.hibernate.Query
        FullTextQuery query = fullTextEntityManager.createFullTextQuery(lQuery,
                Tweet.class);
        query.initializeObjectsWith(
                // first looks in 2nd level cache, we take advantage of it.
                ObjectLookupMethod.SECOND_LEVEL_CACHE,
                DatabaseRetrievalMethod.QUERY);
        query.setMaxResults(MAX_RESULTS);
        @SuppressWarnings("unchecked")
        List<Tweet> tweetList = query.getResultList();
        for (Tweet tweet : tweetList) {
            boolean followed = tweet.getOwner().getFollowedBy().contains(user);
            tweet.getOwner().setCanFollow(!followed);
        }
        return tweetList;
        */
        return Collections.emptyList();
    }

}
