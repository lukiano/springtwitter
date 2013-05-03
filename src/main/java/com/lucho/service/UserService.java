package com.lucho.service;

import org.atmosphere.cpr.AtmosphereResource;

import com.lucho.domain.User;

/**
 * Service that deals with knowing when a User's tweet line
 * refresh is needed because someone else that the User follows has made
 * new tweets.
 * @author Luciano.Leggieri
 */
public interface UserService {

    /**
     * Tells the system that the followers of this User's id need
     * to refresh their tweet lines.
     * @param ownerId a User id.
     */
    void refreshFollowersFor(Long ownerId);

    /**
     * Asks if this User should refresh his tweet line.
     * @param user a User.
     * @return true if this User should refresh his tweet line.
     */
    boolean shouldRefresh(User user);

    /**
     * Register a websocket connection that will return new tweets for
     * the user's followers.
     * @param user owner of the websocket connection.
     * @param resource a websocket connection.
     */
    void registerWebsocket(User user, AtmosphereResource resource);
}
