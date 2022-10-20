package com.SmartWatchVoice.bestapp.system.channel;

//
//public class HttpChannel {
//    public interface OnChannelResponse {
//        public void OnResponse(@NonNull Response response);
//    }
//
//    private static HttpChannel instance = null;
//
//    public static HttpChannel getInstance() {
//        if (instance == null) {
//            instance = new HttpChannel();
//        }
//        return instance;
//    }
//
//    private String AVS_BASE_URL = "https://alexa.na.gateway.devices.a2z.com";
//    private final String AVS_VERSION = "/v20160207";
//
//    public OkHttpClient client = null;
//
//    private String authClientId;
//    private String accessToken;
//    private String refreshToken;
//
//    private String VerifierCode;
//
//    public HttpChannel() {
//        MyLoggingInterceptor2 interceptor = new MyLoggingInterceptor2();
//        interceptor.setLevel(MyLoggingInterceptor2.Level.BODY);
//        client = new OkHttpClient().newBuilder()
//                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
//                .addInterceptor(interceptor)
//                .readTimeout(60, TimeUnit.MINUTES)
//                .writeTimeout(60, TimeUnit.MINUTES)
//                .connectTimeout(10000, TimeUnit.MILLISECONDS)
//                .pingInterval(280, TimeUnit.SECONDS)
//                .build();
//    }
//
//    public void setBaseUrl(String AVS_BASE_URL) {
//        this.AVS_BASE_URL = AVS_BASE_URL;
//    }
//
//    public String getAccessToken() {
//        return accessToken;
//    }
//
//    public String getRefreshToken() {
//        return refreshToken;
//    }
//
//    public void postRequests(final Map<String, RequestBody> bodies, Callback callback) {
//        final String boundary = Helper.makePartBoundary();
//
//        MultipartBody.Builder builder = new MultipartBody.Builder(boundary);
//        builder.setType(MediaType.parse("multipart/form-data"));
//
//        for (Map.Entry<String, RequestBody> entry : bodies.entrySet()) {
//            builder.addFormDataPart(entry.getKey(), null, entry.getValue());
//        }
//        RequestBody body = builder.build();
//
//        Request request = new Request.Builder()
//                .url(AVS_BASE_URL + AVS_VERSION + "/events")
//                .addHeader("authorization", "Bearer " + accessToken)
//                .addHeader("content_type", "multipart/form-data;boundary=" + boundary)
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(callback);
//    }
//
//    public void onAuthorizeSuccess(AuthorizeResult authorizeResult) {
//        final String authCode = authorizeResult.getAuthorizationCode();
//        final String redirectUri = authorizeResult.getRedirectURI();
//
//        this.authClientId = authorizeResult.getClientId();
//
//        fetchAuthToken(authCode, redirectUri);
//
//        RuntimeInfo.getInstance().updateAuthInfo(authCode, redirectUri);
//    }
//
//    private void fetchAuthToken(String authCode, String redirectUri) {
//        JsonObject body = new JsonObject();
//        body.addProperty("grant_type", "authorization_code");
//        body.addProperty("code", authCode);
//        body.addProperty("redirect_uri", redirectUri);
//        body.addProperty("client_id", DeviceInfo.ClientId); // this.authClientId);
//        body.addProperty("code_verifier", VerifierCode); // DeviceInfo.VerifierCode);
//
//        MediaType type = MediaType.parse("application/json;charset=utf-8");
//        Request request = new Request.Builder()
//                .url("https://api.amazon.com/auth/o2/token")
//                .post(RequestBody.create(body.toString(), type))
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                sendLogInfo("fetch token failed - " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.code() == 200 || response.code() == 204) {
//                    sendLogInfo("fetch token success..");
//
//                    JsonObject result = JsonParser.parseString(response.body().string()).getAsJsonObject();
//                    accessToken = result.get("access_token").getAsString();
//                    refreshToken = result.get("refresh_token").getAsString();
//
//                    RuntimeInfo.getInstance().updateAuthInfo(refreshToken);
//
//                    response.close();
//
//                    createDownChannel(new OnChannelResponse() {
//                        @Override
//                        public void OnResponse(@NonNull Response response) {
//                            //                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_CHANNEL_CREATED).sendToTarget();
//                            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_CHANNEL_CREATED);
//                            postSynchronizeState();
//                        }
//                    });
//                } else {
//                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_FAIL);
//                }
//            }
//        });
//    }
//
//    private void createDownChannel(@NonNull OnChannelResponse onChannelResponse) {
//        Request request = new Request.Builder()
//                .url(AVS_BASE_URL + AVS_VERSION + "/directives")
//                .addHeader("authorization", "Bearer " + accessToken)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                sendLogInfo("create down channel failed - " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                sendLogInfo("create down channel success..");
//                // down channel create
//                DownChannel.getInstance().create(response);
//                onChannelResponse.OnResponse(response);
//            }
//        });
//    }
//
//    private void postSynchronizeState() {
//        ThingInfo.getInstance().init();
//
//        new SynchronizeStateAction().create().post(new EventAction.OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
//                Logger.v("synchronize state - ");
//                sendLogInfo("synchronize state... ok");
//
////                Message.obtain(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS).sendToTarget();
////                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS).sendToTarget();
//
//                postVerifyGateway();
//            }
//        });
//    }
//
//    private void postVerifyGateway() {
//        new VerifyGatewayAction().create().post(new EventAction.OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
////                Logger.v("Verify gateway.");
//                sendLogInfo("verify gateway... ok");
////                Message.obtain(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS).sendToTarget();
////                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS).sendToTarget();
//
//                // postCapability
//                postAlexaDiscovery();
//            }
//        });
//    }
//
//    private void postAlexaDiscovery() {
//        new AddOrUpdateReportAction().create().post(new EventAction.OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
//                sendLogInfo("post alexa discovery... ok");
//                // go to speech fragment
////                Message.obtain(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS).sendToTarget();
////                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS).sendToTarget();
//
//                Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS);
//                Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS);
//
//                updateChanged();
//            }
//        });
//    }
//
//    public void authorize(RequestContext requestContext) {
//        RuntimeInfo.AuthorizationInfo authInfo = RuntimeInfo.getInstance().authInfo;
//        if (authInfo == null || authInfo.refreshToken == null) {
//            VerifierCode = UUID.randomUUID().toString();
//            final String CODE_CHALLENGE = Helper.makeCodeChallenge(VerifierCode); // DeviceInfo.VerifierCode);
//
//            final JsonObject attrs = new JsonObject();
//            attrs.addProperty("deviceSerialNumber", DeviceInfo.ProductSerialNumber);
//
//            final JsonObject scopeData = new JsonObject();
//            scopeData.add("productInstanceAttributes", attrs);
//            scopeData.addProperty("productID", DeviceInfo.ProductId);
//
//            try {
//                AuthorizationManager.authorize(new AuthorizeRequest.Builder(requestContext)
//                        .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"),
//                                ScopeFactory.scopeNamed("alexa:all", new JSONObject(scopeData.toString())))
//                        .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
//                        .withProofKeyParameters(CODE_CHALLENGE, "S256")
//                        .build());
//            } catch (JSONException e) {
//                sendLogInfo("fetch token fail - " + e.getMessage());
//                e.printStackTrace();
//            }
//        } else {
//            authorizeWithToken(authInfo);
//        }
//    }
//
//    public void authorizeWithToken(RuntimeInfo.AuthorizationInfo authInfo) {
//        refreshAccessToken(authInfo.refreshToken, new OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
//                createDownChannel(new OnChannelResponse() {
//                    @Override
//                    public void OnResponse(@NonNull Response response) {
//                        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_CHANNEL_CREATED);
//                        postSynchronizeState();
//                    }
//                });
//            }
//        });
//    }
//
//    public void refreshAccessToken() {
//        refreshAccessToken(refreshToken, new OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
////                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_REFRESH_TOKEN_SUCCESS).sendToTarget();
//                Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_REFRESH_TOKEN_SUCCESS);
//
//                Logger.v("refresh auth token success.");
//            }
//        });
//    }
//
//    private void refreshAccessToken(String token, OnChannelResponse onChannelResponse) {
//        JsonObject body = new JsonObject();
//        body.addProperty("grant_type", "refresh_token");
//        body.addProperty("refresh_token", token);
//        body.addProperty("client_id", DeviceInfo.ClientId); // authClientId);
//
//        MediaType type = MediaType.parse("application/json;charset=utf-8");
//        Request request = new Request.Builder()
//                .url("https://api.amazon.com/auth/o2/token")
//                .post(RequestBody.create(body.toString(), type))
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.code() == 200 || response.code() == 204) {
//                    JsonObject result = JsonParser.parseString(response.body().string()).getAsJsonObject();
//                    accessToken = result.get("access_token").getAsString();
//                    refreshToken = result.get("refresh_token").getAsString();
//
//                    response.close();
//
//                    RuntimeInfo.getInstance().updateAuthInfo(refreshToken);
//
//                    onChannelResponse.OnResponse(response);
//                } else {
//                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_FAIL);
//                }
//            }
//        });
//    }
//
//    public void ping() {
//        Request request = new Request.Builder()
//                .url(AVS_BASE_URL + "/ping")
//                .addHeader("authorization", "Bearer " + accessToken)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Logger.w("ping fail - " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
////                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_PING_CHANNEL_SUCCESS).sendToTarget();
//                Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_PING_CHANNEL_SUCCESS);
//                Logger.v("ping channel success - " + response.code());
//            }
//        });
//
//    }
//
//    public void recreateDownChannel() {
//        createDownChannel(new OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
//                Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_CHANNEL_CREATED);
//            }
//        });
//    }
//
//
//    private void updateChanged() {
//        new DoNotDisturbChangedAction(SettingInfo.getInstance().doNotDisturb).create().post();
//    }
//
//    private void sendLogInfo(String info) {
////        Message.obtain(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOG, info).sendToTarget();
//        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOG, info);
//    }
//}
