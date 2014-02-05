package com.birbit.android.livecode.twitter.business;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.birbit.android.livecode.twitter.Config;
import com.birbit.android.livecode.twitter.business.exceptions.TwitterApiException;
import com.birbit.android.livecode.twitter.util.L;
import com.birbit.android.livecode.twitter.vo.DM;
import com.birbit.android.livecode.twitter.vo.Tweet;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedOutput;

/**
 * Created by yigit on 2/1/14.
 */
public class TwitterApiClient {

    private final TwitterService service;

    public TwitterApiClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.API_BASE)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new Gson()))
                .setClient(new WrapperClient())
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        return TwitterApiException.convert(retrofitError);
                    }
                })
                .build();
        service = restAdapter.create(TwitterService.class);
    }

    public TwitterService getService() {
        return service;
    }

    public interface TwitterService {
        @GET("/statuses/home_timeline.json")
        public List<Tweet> homeTimeline(@Query("since_id") String sinceId);
        @FormUrlEncoded
        @POST("/statuses/update.json")
        public Tweet postStatus(@Field("status") String status);
        @GET("/direct_messages.json")
        public List<DM> getReceivedDMs(@Field("count") int limit);
        @GET("/direct_messages.json")
        public List<DM> getReceivedDMs(@Field("since_id") String sinceId, @Field("count") int limit);
        @POST("/statuses/retweet/{id}.json")
        public Tweet retweet(@Path("id") String tweetId);
        @FormUrlEncoded
        @POST("/favorites/create.json")
        public Tweet favorite(@Field("id") String tweetId);
        @FormUrlEncoded
        @POST("/favorites/destroy.json")
        public Tweet removeFavorite(@Field("id") String tweetId);
    }

    private static class WrapperClient implements Client {
        OkClient okClient = new OkClient();
        WrapperClient() {

        }
        @Override
        public Response execute(Request request) throws IOException {
            if(Config.ARTIFICIAL_REQUEST_DELAY > 0) {
                try {
                    Thread.sleep(Config.ARTIFICIAL_REQUEST_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //prepare Authorization header
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("oauth_consumer_key", Config.CONNECTION_CONFIGURATION.consumerKey);
            //TODO create proper nonce

            headers.put("oauth_nonce", UUID.randomUUID().toString());
            headers.put("oauth_signature_method", "HMAC-SHA1");
            headers.put("oauth_timestamp", Long.toString(System.currentTimeMillis() / 1000));
            headers.put("oauth_token", Config.CONNECTION_CONFIGURATION.accessToken);
            headers.put("oauth_version", "1.0");
            //sign request
            TreeMap<String, String> signaturePairs = new TreeMap<String, String>();
            signaturePairs.putAll(headers);
            for(Map.Entry<String, String> entry : headers.entrySet()) {
                signaturePairs.put(percentEncode(entry.getKey()), percentEncode(entry.getValue()));
            }
            Uri uri = Uri.parse(request.getUrl());
            for(String queryParam : uri.getQueryParameterNames()) {
                signaturePairs.put(percentEncode(queryParam), percentEncode(uri.getQueryParameter(queryParam)));
            }
            if("POST".equals(request.getMethod().toUpperCase())) {
                TypedOutput output = request.getBody();
                if(output instanceof TypedByteArray) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    output.writeTo(os);
                    String val = os.toString("UTF-8");
                    if(val.length() > 0) {
                        Uri bodyUri = Uri.parse(Config.API_BASE + "/?" + val);
                        for(String param : bodyUri.getQueryParameterNames()) {
                            signaturePairs.put(percentEncode(param), percentEncode(bodyUri.getQueryParameter(param)));
                        }
                    }
                    //ugly ugly
                    L.d(val);
                }
            }
            StringBuilder signatureBuilder = new StringBuilder();
            boolean first = true;
            for(TreeMap.Entry<String, String> entry : signaturePairs.entrySet()) {
                if(first) {
                    first = false;
                } else {
                    signatureBuilder.append("&");
                }
                signatureBuilder.append(entry.getKey());
                signatureBuilder.append("=");
                signatureBuilder.append(entry.getValue());
            }
            String finalSignatureBase = request.getMethod().toUpperCase() + "&" +
                    percentEncode(uri.getScheme() + "://" + uri.getHost() + uri.getPath())
                    + "&" + percentEncode(signatureBuilder.toString());
            String signingKey = percentEncode(Config.CONNECTION_CONFIGURATION.consumerSecret)
                    + "&" + percentEncode(Config.CONNECTION_CONFIGURATION.accessTokenSecret);

            try {
                String signature = sha1(finalSignatureBase, signingKey);
                headers.put("oauth_signature", signature);
            } catch (NoSuchAlgorithmException e) {
                //ignore for demo
            } catch (InvalidKeyException e) {
                //ignore for demo
            }

            StringBuilder authorizationHeader = new StringBuilder();
            authorizationHeader.append("OAuth ");
            first = true;
            for(Map.Entry<String, String> header : headers.entrySet()) {
                if(first) {
                    first = false;
                } else {
                    authorizationHeader.append(", ");
                }
                authorizationHeader.append(percentEncode(header.getKey()));
                authorizationHeader.append("=");
                authorizationHeader.append('"');
                authorizationHeader.append(percentEncode(header.getValue()));
                authorizationHeader.append('"');
            }
            Request actualRequest = new Request(request.getMethod(), request.getUrl(),
                    Arrays.asList(new Header("Authorization", authorizationHeader.toString())),
                    request.getBody());

            return okClient.execute(actualRequest);
        }

        public static String sha1(String s, String keyString) throws
                UnsupportedEncodingException, NoSuchAlgorithmException,
                InvalidKeyException {

            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

            return Base64.encodeToString(bytes, Base64.URL_SAFE);
        }

        private static String percentEncode(String val) throws UnsupportedEncodingException {
            return Uri.encode(val, "UTF-8");
        }
    }
}
