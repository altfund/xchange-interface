package org.altfund.xchangeinterface.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelper {

    private JsonNodeFactory nodeFactory;
    private ObjectMapper mapper;

    public JsonHelper(JsonNodeFactory nodeFactory, ObjectMapper mapper) {
        this.nodeFactory = nodeFactory;
        this.mapper = mapper;
    }

    public ObjectNode getObjectNode() {
        return nodeFactory.objectNode();
    }

    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
