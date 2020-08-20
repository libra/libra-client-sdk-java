// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Method;

import java.io.IOException;
import java.util.List;

public class JSONRPCClient {

    private final String uri;

    public JSONRPCClient(String uri) {
        this.uri = uri;

    }

    public String call(Method method, List<Object> params) throws LibraSDKException {
        int id = 0;
        JSONRPCRequest jsonrpcRequest = new JSONRPCRequest(id, method.name(), params.toArray());
        String requestJson = new Gson().toJson(jsonrpcRequest);
        String response;

        HttpPost post = new HttpPost(uri);
        post.addHeader("content-type", "application/json");
        try {
            post.setEntity(new StringEntity(requestJson));
            response = httpCall(post);
        } catch (IOException e) {
            throw new LibraSDKException(e);
        }


        return response;
    }

    private static String httpCall(HttpPost post) throws LibraSDKException {
        String result;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
//            result = EntityUtils.toString(response.getEntity(), "UTF-8");
            result = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new LibraSDKException(response.toString());
            }
//            if (resp.error != null) {
//                throw new JsonRpcErrorException(resp);
//            }
//            if (responseType == null) {
//                return null;
//            }

        } catch (IOException e) {
            throw new LibraSDKException(e);
        }


        return result;
    }
}