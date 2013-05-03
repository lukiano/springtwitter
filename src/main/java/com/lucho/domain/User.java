package com.lucho.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucho.repository.TweetRepository;
import com.lucho.repository.UserRepository;
import com.lucho.service.UserService;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.graphdb.Direction;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a user of the system.
 */
@NodeEntity
@Configurable(preConstruction = true)
public class User implements UserDetails {

    /**
     * TweetRepository implementation.
     */
    @Inject
    @JsonIgnore
    private transient TweetRepository tweetRepository;

    /**
     * TweetRepository implementation.
     */
    @Inject
    @JsonIgnore
    private transient UserRepository userRepository;

    /**
     * User service.
     */
    @Inject
    @JsonIgnore
    private transient UserService userService;

    /**
     * Password encoder.
     */
    @Inject
    @JsonIgnore
    private transient PasswordEncoder passwordEncoder;


    /**
     * Unique identifier for serialization purposes.
     */
    private static final long serialVersionUID = 5788883283199993395L;

    /**
     * Minimum length for the username.
     */
    public static final int MIN_USER_LENGTH = 4;

    /**
     * Maximum length for the username.
     */
    public static final int MAX_USER_LENGTH = 32;

    /**
     * Minimum length for the password.
     */
    public static final int MIN_PASS_LENGTH = 4;

    /**
     * Maximum length for the password.
     */
    public static final int MAX_PASS_LENGTH = 64;

    /**
     * User id. It's unique.
     */
    @GraphId
    @org.springframework.data.annotation.Id
    private Long id;

    /**
     * Email. It's unique.
     */
    @NotEmpty
    @Email
    @Indexed(fieldName = "email", indexName = "emailIndex", indexType = IndexType.UNIQUE)
    private String email;

    /**
     * User name. It's unique.
     */
    @NotEmpty
    @Size(min = MIN_USER_LENGTH, max = MAX_USER_LENGTH)
    @Indexed(fieldName = "username", indexName = "userIndex")
    private String username;

    /**
     * User password.
     */
    @NotEmpty
    @JsonIgnore
    private String password;

    /**
     * Set of {@link User} that follow this one.
     */
    @JsonIgnore
    @RelatedTo(type = "FOLLOWED_BY", direction = Direction.INCOMING)
    private Set<User> followedBy;

    /**
     * User permissions.
     */
    @JsonIgnore
    private transient List<GrantedAuthority> authorities;

    /**
     * Temporal field, true if this user is not being
     * followed by the tweetline requester.
     */
    @JsonProperty
    private transient boolean canFollow;

    /**
     * Default Class Constructor.
     */
    protected User() {
    }

    /**
     * Creates a new User filling the obligatory fields.
     *
     * @param name User name.
     * @param pass User password.
     * @param mail User email.
     */
    public User(final String name, final String pass, final String mail) {
        this.username = name;
        this.email = mail;
        this.password = this.passwordEncoder.encode(pass);
        this.followedBy = new HashSet<User>();
        this.followedBy.add(this);
    }

    /**
     * @return the user id.
     */
    public final Long getId() {
        return id;
    }

    @Override
    public final String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public final Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @JsonIgnore
    @Override
    public final String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public final boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public final boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public final boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public final boolean isEnabled() {
        return true;
    }

    /**
     * @return the users that follow this one.
     */
    @JsonIgnore
    public final Set<User> getFollowedBy() {
        return followedBy;
    }

    /**
     * Set the user permissions.
     * @param theAuthorities a list of permissions
     */
    public final void setAuthorities(
            final List<GrantedAuthority> theAuthorities) {
        this.authorities = theAuthorities;
    }

    /**
     * Temporal field, true if this user is not being
     * followed by the tweetline requester.
     * @param canIFollow true if this user is not being
     * followed by the tweetline requester.
     */
    public final void setCanFollow(final boolean canIFollow) {
        this.canFollow = canIFollow;
    }

    /**
     * @return true if this user is not being
     * followed by the tweetline requester.
     */
    public final boolean isCanFollow() {
        return this.canFollow;
    }

    @Override
    public final boolean equals(final Object another) {
        return (another == this)
                || another instanceof User
                && this.username.equals(((User) another).username);
    }

    @Override
    public final int hashCode() {
        return this.username.hashCode();
    }

    /**
     * Persist this user.
     */
    @JsonIgnore
    public final void save() {
        this.userRepository.save(this);
    }

    /**
     * Make this user to follow another one.
     * @param userToFollow the user to follow.
     * @return true if the user to follow was not already
     * being followed. False otherwise.
     */
    @JsonIgnore
    public final Boolean followUser(final User userToFollow) {
        return this.userRepository.followUser(this, userToFollow);
    }

    /**
     * @param millis List will only contain tweets newer than this date.
     * @return a list of tweets: tweetline for the user.
     */
    @JsonIgnore
    public final List<Tweet> getTweetsIncludingFollows(final Long millis) {
        return this.tweetRepository.getTweetsForUserIncludingFollows(
                this, millis);
    }

    /**
     * @return true if this user has new contents in its tweetline.
     */
    @JsonIgnore
    public final Boolean shouldRefresh() {
        return this.userService.shouldRefresh(this);
    }

    @Override
    public final String toString() {
        return this.username;
    }
}
