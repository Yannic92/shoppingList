package de.yannicklem.shoppinglist.core.user.restapi;

public class SLUserEndpoints {

    public static final String SLUSER_ENDPOINT = "/sLUsers";
    public static final String SLUSER_SPECIFIC_ENDPOINT = SLUSER_ENDPOINT + "/{name}";
    public static final String SLUSER_CURRENT_ENDPOINT = SLUSER_ENDPOINT + "/current";
    public static final String SLUSER_CONFIRMATION_ENDPOINT = SLUSER_SPECIFIC_ENDPOINT + "/confirmation";
}
