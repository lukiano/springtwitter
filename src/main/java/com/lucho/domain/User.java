package com.lucho.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lucho.repository.TweetRepository;
import com.lucho.repository.UserRepository;
import com.lucho.service.UserService;

/**
 * Represents a user of the system.
 */
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Entity
@Cacheable
@Table(name = "t_user",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }) }
)
@Configurable(preConstruction = true)
public class User implements UserDetails {

    /**
     * TweetRepository implementation.
     */
    @Inject
    @Transient
    @JsonIgnore
    private transient TweetRepository tweetRepository;

    /**
     * TweetRepository implementation.
     */
    @Inject
    @Transient
    @JsonIgnore
    private transient UserRepository userRepository;

    /**
     * User service.
     */
    @Inject
    @Transient
    @JsonIgnore
    private transient UserService userService;

    /**
     * Password encoder.
     */
    @Inject
    @Transient
    @JsonIgnore
    private transient PasswordEncoder passwordEncoder;


    /**
     * Unique identifier for serialization purposes.
     */
    private static final long serialVersionUID = 5788883283199993395L;

    /**
     * Maximum length of the username string.
     */
    private static final int MAX_USER_LENGTH = 32;

    /**
     * User id. It's unique.
     */
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue
    private Integer id;

    /**
     * User name. It's unique.
     */
    @NotEmpty
    @Size(max = MAX_USER_LENGTH)
    @Column(name = "username")
    //@org.springframework.data.mongodb.core.index.Indexed
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
    @ManyToMany
    @JsonIgnore
    private Set<User> followedBy;

    /**
     * User permissions.
     */
    @Transient
    @JsonIgnore
    private List<GrantedAuthority> authorities;

    /**
     * Temporal field, true if this user is not being
     * followed by the tweetline requester.
     */
    @Transient
    @JsonProperty
    private boolean canFollow;

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
     */
    public User(final String name, final String pass) {
        this.username = name;
        this.password = this.passwordEncoder.encode(pass);
        this.followedBy = new HashSet<User>();
        this.followedBy.add(this);
    }

    /**
     * @return the user id.
     */
    public final Integer getId() {
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
        this.userRepository.saveAndFlush(this);
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
