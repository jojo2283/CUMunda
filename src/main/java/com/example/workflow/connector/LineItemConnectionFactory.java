package com.example.workflow.connector;



public class LineItemConnectionFactory {

    private final String consumerKey;
    private final String consumerSecret;
    private final String endpointUrl;

    public LineItemConnectionFactory(String consumerKey, String consumerSecret, String endpointUrl) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.endpointUrl = endpointUrl;
    }

    public LineItemConnectionImpl getConnection() {
        return new LineItemConnectionImpl(consumerKey, consumerSecret, endpointUrl);
    }
}