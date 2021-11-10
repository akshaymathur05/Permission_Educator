package com.example.permissioneducator;

import okhttp3.FormBody;

import okhttp3.RequestBody;

public class ServerConnection {
//    private String postBodyString;
    private RequestBody requestBody, requestBody1;

    protected RequestBody buildRequestBody(String msg){
        requestBody = new FormBody.Builder().add("perm_list", msg).build();
        return requestBody;

    }

    protected RequestBody requestBodyPermissionName(String permission){
        requestBody1 = new FormBody.Builder().add("perm", permission).build();
        return requestBody1;
    }
}
