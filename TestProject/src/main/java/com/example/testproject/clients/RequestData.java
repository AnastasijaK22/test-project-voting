package com.example.testproject.clients;

public class RequestData {
    private String uri;
    private String method;
    private String requestJson;
    private Expect expectedResult;

    public String getUri() {
        return this.uri;
    }

    public String getMethod() {
        return this.method;
    }

    public String getRequestJson() {
        return this.requestJson;
    }
    public Expect getExpectedResult() {
        return this.expectedResult;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }
    public void setExpectedResult(Expect expect) {
        this.expectedResult = expect;
    }
}
