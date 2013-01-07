package com.lucho.controller;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Container object that holds a message string.
 * Primarily used for JSON serialization.
 * @author Luciano.Leggieri
 *
 */
public final class Message implements Serializable {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The message itself.
     */
    private final String message;

    /**
     * Private constructor to avoid external instantiation.
     * @param msg the message string. Cannot be null.
     */
    private Message(final String msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Null message");
        }
        this.message = msg;
    }

    /**
     * Factory method to build Message objects.
     * @param msg the message string this Message will hold. Cannot be null.
     * @return a new Message object containing the desired string.
     */
    public static Message build(final String msg) {
        return new Message(msg);
    }

    @Override
    public boolean equals(final Object another) {
        if (another instanceof Message) {
            Message anotherMessage = (Message) another;
            return this.message.equals(anotherMessage.message);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }

    @Override
    public String toString() {
        return this.message;
    }

    /**
     * @return the message itself.
     */
    @JsonProperty
    public String getMessage() {
        return this.message;
    }

}