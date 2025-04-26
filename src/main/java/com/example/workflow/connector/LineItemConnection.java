package com.example.workflow.connector;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Assignment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.scribejava.core.model.Response;

public interface LineItemConnection extends java.io.Closeable {
    Response createLineItem(Assignment assignment, Answer newAnswer) throws Exception;
}