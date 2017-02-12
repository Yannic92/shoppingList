package de.yannicklem.shoppinglist.core.user.restapi.controller;

public class SLUserEndpoints {

    public static final String SLUSER_ENDPOINT = "/api/sLUsers";
    public static final String SLUSER_CURRENT_ENDPOINT = "/current";
    public static final String SLUSER_CURRENT_ENDPONT_FULL = SLUSER_ENDPOINT + SLUSER_CURRENT_ENDPOINT;
    public static final String SLUSER_CONFIRMATION_ENDPOINT = "/{id}/confirmation";
    public static final String SLUSER_CONFIRMATION_ENDPOINT_FULL = SLUSER_ENDPOINT + SLUSER_CONFIRMATION_ENDPOINT;
}
