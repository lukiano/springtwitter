package com.lucho.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucho.repository.TweetRepository;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Domain object that represents a tweet an user wrote.
 */
@NodeEntity
@Configurable
public class Tweet implements Serializable {

    /**
     * Unique identifier for serialization purposes.
     */
    private static final long serialVersionUID = 1944203463910744459L;

    /**
     * TweetRepository implementation.
     */
    @Inject
    @JsonIgnore
    private transient TweetRepository tweetRepository;

    /**
     * Maximum length for a tweet text.
     */
    private static final int MAX_TWEET_LENGTH = 140;

    /**
     * Tweet id.
     */
    @GraphId
    @JsonIgnore
    @org.springframework.data.annotation.Id
    private Long id;

    /**
     * Tweet text.
     */
    @NotEmpty
    @Size(max = MAX_TWEET_LENGTH)
    @JsonProperty
    @Indexed(fieldName = "tweet", indexName = "tweetIndex", indexType = IndexType.FULLTEXT)
    private String tweet;

    /**
     * Tweet language.
     */
    private String language;

    /**
     * Tweet owner.
     */
    @NotNull
    @JsonProperty
    @RelatedTo(type = "BELONGS_TO")
    private User owner;

    /**
     * Tweet creation date.
     */
    @NotNull
    @Past
    @JsonProperty
    private Date creationDate;

    /**
     * Default Class Constructor.
     */
    protected Tweet() {
    }

    /**
     * Creates a new Tweet filling the obligatory fields.
     * @param user Tweet owner.
     * @param text Tweet contents.
     * @param lang Tweet language.
     */
    public Tweet(final User user, final String text, final String lang) {
        this.owner = user;
        this.tweet = text;
        this.language = lang;
        //this.creationDate = new DateTime();
        this.creationDate = new Date();
    }

    /**
     * @return the user who owns this tweet.
     */
    public final User getOwner() {
        return owner;
    }

    /**
     * Persists this tweet and refresh the owner's followers.
     */
    @JsonIgnore
    public final void save() {
        this.tweetRepository.save(this);
    }

}
