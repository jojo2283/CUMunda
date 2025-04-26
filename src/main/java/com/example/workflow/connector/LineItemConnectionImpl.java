package com.example.workflow.connector;


import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Assignment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;

public class LineItemConnectionImpl implements LineItemConnection {

    private final OAuth10aService service;
    private final String endpointUrl;

    public LineItemConnectionImpl(String consumerKey, String consumerSecret, String endpointUrl) {
        this.endpointUrl = endpointUrl;
        this.service = new ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .build(new DefaultApi10a() {
                    @Override
                    public String getRequestTokenEndpoint() { return null; }
                    @Override
                    public String getAccessTokenEndpoint() { return null; }
                    @Override
                    protected String getAuthorizationBaseUrl() { return null; }
                });
    }

    @Override
    public Response createLineItem(Assignment assignment, Answer newAnswer) throws Exception {

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("label", assignment.getTitle());
            bodyMap.put("scoreMaximum", assignment.getPoints());
            bodyMap.put("resourceLinkId", assignment.getId().toString());
            bodyMap.put("resourceId", newAnswer.getId().toString());

            String bodyJson = new ObjectMapper().writeValueAsString(bodyMap);


            String bodyHash = calculateBodyHash(bodyJson);


            OAuthRequest request = new OAuthRequest(Verb.POST, endpointUrl);
            request.addHeader("Content-Type", "application/vnd.ims.lis.v2.lineitem+json");
            request.addHeader("Accept", "application/vnd.ims.lis.v2.lineitem+json");
            request.setPayload(bodyJson);

            request.addOAuthParameter("oauth_body_hash", bodyHash);

            service.signRequest(new OAuth1AccessToken("", ""), request);
            return service.execute(request);

    }

    @Override
    public void close() {

    }

    private String calculateBodyHash(String body) throws Exception {

        return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(body.getBytes(StandardCharsets.UTF_8))
        );
    }

}