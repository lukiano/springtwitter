package com.lucho.controller;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Container object that holds a message string.
 * Primarily used for JSON serialization.
 * @author Luciano.Leggieri
 *
 */
abstract class Message implements Serializable {

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
    public static Message success(final String msg) {
        return new NormalMessage(msg);
    }

    /**
     * Factory method to build Error Message objects.
     * @param msg the message string this Message will hold. Cannot be null.
     * @return a new Error Message object containing the desired string.
     */
    public static Message error(final int errorNumber, final String msg) {
        return new ErrorMessage(errorNumber, msg);
    }

    /**
     * Factory method to build Error Message objects.
     * @param msg the message string this Message will hold. Cannot be null.
     * @return a new Error Message object containing the desired string.
     */
    public static Message error(final String msg) {
        return error(0, msg);
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

    static final class NormalMessage extends Message {

        private NormalMessage(final String msg) {
              super(msg);
        }

    }

    static final class ErrorMessage extends Message {

        /**
         * Number that indicates the type of error. Its meaning may vary according to the message receiver.
         */
        private final int errorNumber;

        private ErrorMessage(final int number, final String msg) {
            super(msg);
            this.errorNumber = number;
        }

        /**
         * @return the error number.
         */
        @JsonProperty
        public int getErrorNumber() {
            return this.errorNumber;
        }

    }

}