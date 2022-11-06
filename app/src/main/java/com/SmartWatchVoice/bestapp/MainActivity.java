package com.SmartWatchVoice.bestapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.databinding.ActivityMainBinding;
import com.SmartWatchVoice.bestapp.handler.DirectiveCallback;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.sdk.SDKAction;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.channel.Helper;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.SmartWatchVoice.bestapp.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK;
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnActionListener;
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

//    private RequestContext requestContext = null;

    private Handler handler = null;
    private HandlerThread directiveThread = null;
    private Handler directiveHandler = null;

    private SmartWatchSDK smartWatchSDK = new SmartWatchSDK();

    private OnActionListener onActionListener = new OnActionListener() {
        @Override
        public void onAction(@NonNull String data, @Nullable Object extra, @NonNull OnResultCallback callback) {
            Logger.d("onAction - " + data);

            try {
                JSONObject action = new JSONObject(data);

                String name = action.getString("name");

                switch (name) {
                    case "alexa.speechSpeak": {
                        onSpeechSpeak(action, extra, callback);
                    }
                    break;
                    case "alexa.doNotDisturbUpdated": {
                        onDoNotDisturbUpdatedAction(action, extra, callback);
                    }
                    break;
                    case "alexa.timeZoneUpdated": {
                        onTimeZoneUpdatedAction(action, extra, callback);
                    }
                    break;
                    case "alexa.localesUpdated": {
                        onLocalesUpdatedAction(action, extra, callback);
                    }
                    break;
                    case "alexa.alertAdded": {
                        onAlertAdded(action, extra, callback);
                    }
                    break;
                    case "alexa.alertDeleted": {
                        onAlertDeleted(action, extra, callback);
                    }
                    break;
                    default: {
                        Logger.w("Unsupported action - " + name);

                        JSONObject result = new JSONObject();
                        result.put("type", "result");
                        result.put("name", action.getString("name"));
                        result.put("version", 1);

                        callback.onResult(result.toString(), null);
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void onSpeechSpeak(JSONObject action, Object extra, OnResultCallback callback) throws IOException, JSONException {
        // file
        if (extra != null) {
            String filename = System.currentTimeMillis() + ".mp3";
            File file = RuntimeInfo.getInstance().makeSpeechMP3File(filename); // new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "a.mp3");
            FileOutputStream ofs = new FileOutputStream(file);
            byte[] b = (byte[]) extra;
            ofs.write(b);
            ofs.close();

            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_AUDIO_FILE, filename);
        }

        JSONObject payload = action.getJSONObject("payload");
        if (payload != null) {
            JSONObject caption = payload.getJSONObject("caption");
            if (caption != null) {
                String content = caption.getString("content");
                if (content != null) {
                    Payload.Caption webvtt = new Payload.Caption();
                    webvtt.content = content;
                    webvtt.type = caption.getString("type");
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_WEB_VTT, webvtt);
                }
            }
        }

    }

    private void onAlertDeleted(JSONObject action, Object extra, OnResultCallback callback) {
        try {
            JSONObject payload = action.getJSONObject("payload");
            JSONArray tokens = payload.getJSONArray("tokens");
            for (int i = 0; i < tokens.length(); ++ i) {
                String token = tokens.getString(i);
                SettingInfo.getInstance().alertInfo.deleteAlert(token);
            }
//            String token = payload.getString("token");

            JSONObject p = new JSONObject();
            p.put("tokens", tokens);

            JSONObject result = new JSONObject();
            result.put("type", "result");
            result.put("name", action.getString("name"));
            result.put("version", 1);
            result.put("payload", p);

            callback.onResult(result.toString(), null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onAlertAdded(JSONObject action, Object extra, OnResultCallback callback) {
        try {
            JSONObject payload = action.getJSONObject("payload");
            String token = payload.getString("token");
            String type = payload.getString("type");
            String scheduledTime = payload.getString("scheduledTime");
            Long loopCount = payload.getLong("loopCount");
            Long loopPause = payload.getLong("loopPause");
            String label = payload.getString("label");
//            String startTime = payload.getString("startTime");

            SettingInfo.getInstance().alertInfo.setAlert(token, type, scheduledTime, loopCount, loopPause, label);

            JSONObject p = new JSONObject();
            p.put("token", token);

            JSONObject result = new JSONObject();
            result.put("type", "result");
            result.put("name", action.getString("name"));
            result.put("version", 1);
            result.put("payload", p);

            callback.onResult(result.toString(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onLocalesUpdatedAction(JSONObject action, Object extra, OnResultCallback callback) {
        try {
            JSONArray locales = action.getJSONObject("payload").getJSONArray("locales");
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < locales.length(); ++ i) {
                list.add(locales.getString(i));
            }
            SettingInfo.getInstance().locales = list;

            JSONObject payload = new JSONObject();
//            JsonArray lo = new JsonArray();
//            SettingInfo.getInstance().locales.forEach( it -> lo.add(it));
            payload.put("locales", locales);

            JSONObject result = new JSONObject();
            result.put("type", "result");
            result.put("name", action.getString("name"));
            result.put("version", 1);
            result.put("payload", payload);

            callback.onResult(result.toString(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onTimeZoneUpdatedAction(JSONObject action, Object extra, OnResultCallback callback) {
        try {
            String timezone = action.getJSONObject("payload").getString("timeZone");
            SettingInfo.getInstance().timeZone = timezone;

            JSONObject payload = new JSONObject();
            payload.put("timeZone", SettingInfo.getInstance().timeZone);

            JSONObject result = new JSONObject();
            result.put("type", "result");
            result.put("name", action.getString("name"));
            result.put("version", 1);
            result.put("payload", payload);

            callback.onResult(result.toString(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onDoNotDisturbUpdatedAction(JSONObject action, Object extra, OnResultCallback callback) {

        try {
            Boolean enabled = action.getJSONObject("payload").getBoolean("enabled");
            SettingInfo.getInstance().doNotDisturb = enabled;

            JSONObject payload = new JSONObject();
            payload.put("enabled", SettingInfo.getInstance().doNotDisturb);

            JSONObject result = new JSONObject();
            result.put("type", "result");
            result.put("name", action.getString("name"));
            result.put("version", 1);
            result.put("payload", payload);

            callback.onResult(result.toString(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        smartWatchSDK.attach(this, onActionListener);

        SDKAction.sdk = smartWatchSDK;

//        JSONObject json = new JSONObject();
//        try {
//            json.put("type", "action");
//            json.put("name", "sdk.test");
//            json.put("version", 1);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        smartWatchSDK.action(json.toString(), null, new OnResultCallback() {
//            @Override
//            public void onResult(@NonNull String data, @Nullable Object extra) {
//                Logger.d("onResult - " + data);
//            }
//        });

        DeviceInfo.init(this);
        RuntimeInfo.getInstance().start(this);

        initHandlers();
//        initRequestContext();

        setDeviceInfo(new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                Logger.d("setInfo result - " + data);
            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.aboutFragment) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        } else
        if (id == R.id.action_test) {
            actionTest();
        }

        return super.onOptionsItemSelected(item);
    }

    private void actionTest() {
//        pingDownChannel();
        RuntimeInfo.getInstance().loginFragmentHandler.sendEmptyMessage(HandlerConst.MSG_LOGIN_SUCCESS);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        requestContext.onResume();
//        Device.Companion.getInstance().onResume();
        smartWatchSDK.resume(this);
    }

    @Override
    protected void onDestroy() {
        Logger.e("onDestroy()");
        directiveThread.quit();

        smartWatchSDK.detach(this);


//        SettingInfo.getInstance().flush();
        RuntimeInfo.getInstance().stop();
//

        super.onDestroy();
    }

    private void initHandlers() {
        handler = Handler.createAsync(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Logger.v("MainHandler - " + message);
                switch (message.what) {
                    case HandlerConst.MSG_FETCH_TOKEN:
                        setDeviceInfo(new OnResultCallback() {
                            @Override
                            public void onResult(@NonNull String data, @Nullable Object extra) {
                                Logger.d("setInfo result - " + data);
                                if (RuntimeInfo.getInstance().authInfo == null ||  RuntimeInfo.getInstance().authInfo.refreshToken == null) {
//                                    authorize();
                                    fetchProductAccessToken();
                                } else {
                                    loginWithToken();
                                }
                            }
                        });
                        break;
                    case HandlerConst.MSG_LOGIN_SUCCESS:
                    case HandlerConst.MSG_REFRESH_TOKEN_SUCCESS:
                        scheduleTokenRefresh();
                        break;
                    case HandlerConst.MSG_CHANNEL_CREATED:
                    case HandlerConst.MSG_PING_CHANNEL_SUCCESS:
                        scheduleChannelPing();
                        break;
                    case HandlerConst.MSG_REFRESH_TOKEN:
                        refreshAuthorizationToken();
                        break;
                    case HandlerConst.MSG_PING_CHANNEL:
                        pingDownChannel();
                        break;
                    case HandlerConst.MSG_CHANNEL_CLOSED:
                        onDownChannelClosed();
                        break;
                    default:;
                }
                return true;
            }
        });
        RuntimeInfo.getInstance().mainHandler = handler;

        directiveThread = new HandlerThread("DirectiveHandlerThread");
        directiveThread.start();
        directiveHandler = Handler.createAsync(directiveThread.getLooper(), new DirectiveCallback());
        RuntimeInfo.getInstance().directiveHandler = directiveHandler;
    }
//
//    private void initRequestContext() {
//        requestContext = RequestContext.create(this.getApplicationContext());
//        requestContext.registerListener(new AuthorizeListener() {
//            @Override
//            public void onSuccess(AuthorizeResult authorizeResult) {
////                HttpChannel.getInstance().onAuthorizeSuccess(authorizeResult);
//                onAuthorizeSuccess(authorizeResult);
//            }
//
//            @Override
//            public void onError(AuthError authError) {
//                Logger.e("Authorization error - " + authError.getMessage());
//            }
//
//            @Override
//            public void onCancel(AuthCancellation authCancellation) {
//                Logger.w("Authorization canceled.");
//            }
//        });
//    }
//
//    private void onAuthorizeSuccess(AuthorizeResult authorizeResult) {
//        final String authCode = authorizeResult.getAuthorizationCode();
//        final String redirectUri = authorizeResult.getRedirectURI();
//
//        String authClientId = authorizeResult.getClientId();
//
////        fetchAuthToken(authCode, redirectUri);
//
//        RuntimeInfo.getInstance().updateAuthInfo(authCode, redirectUri);
//        RuntimeInfo.getInstance().authInfo.authClientId = authClientId;
//
//        fetchAuthorizationToken();
//    }
//
//    public void authorize() {
////        RuntimeInfo.AuthorizationInfo authInfo = RuntimeInfo.getInstance().authInfo;
////        if (authInfo == null || authInfo.refreshToken == null) {
////        String VerifierCode = UUID.randomUUID().toString();
//        RuntimeInfo.getInstance().updateAuthInfoVerifierCode(UUID.randomUUID().toString());
//        final String CODE_CHALLENGE = Helper.makeCodeChallenge(RuntimeInfo.getInstance().authInfo.verifierCode); //VerifierCode); // DeviceInfo.VerifierCode);
//
//        final JsonObject attrs = new JsonObject();
//        attrs.addProperty("deviceSerialNumber", DeviceInfo.ProductSerialNumber);
//
//        final JsonObject scopeData = new JsonObject();
//        scopeData.add("productInstanceAttributes", attrs);
//        scopeData.addProperty("productID", DeviceInfo.ProductId);
//
//        try {
//            AuthorizationManager.authorize(new AuthorizeRequest.Builder(requestContext)
//                    .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"),
//                            ScopeFactory.scopeNamed("alexa:all", new JSONObject(scopeData.toString())))
//                    .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
//                    .withProofKeyParameters(CODE_CHALLENGE, "S256")
//                    .build());
//        } catch (JSONException e) {
//            Logger.w("fetch token fail - " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public void fetchProductAccessToken() {

        JSONObject json = new JSONObject();
        try {
            json.put("type", "action");
            json.put("name", "alexa.login");
            json.put("version", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        smartWatchSDK.action(json.toString(), null, new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                Logger.d("login - " + data);
                try {
                    JSONObject result = new JSONObject(data);
                    JSONObject payload = result.getJSONObject("payload");
                    String token = payload.getString("refreshToken");
                    RuntimeInfo.getInstance().updateAuthInfo(token);

                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS);
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS);

                } catch (JSONException e) {
                    Logger.w("login result parse failed - " + e.getMessage());
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_FAIL);
                }
            }
        });
    }

    private void fetchAuthorizationToken() {
        JSONObject payload = new JSONObject();
        JSONObject json = new JSONObject();

        try {
            payload.put("verifierCode", RuntimeInfo.getInstance().authInfo.verifierCode);
            payload.put("authorizationCode", RuntimeInfo.getInstance().authInfo.code);
            payload.put("redirectUri", RuntimeInfo.getInstance().authInfo.redirectUri);
            payload.put("authorizationClientId", RuntimeInfo.getInstance().authInfo.authClientId);

            json.put("type", "action");
            json.put("name", "alexa.login");
            json.put("version", 1);

            json.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        smartWatchSDK.action(json.toString(), null, new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                Logger.d("login - " + data);
                try {
                    JSONObject result = new JSONObject(data);
                    JSONObject payload = result.getJSONObject("payload");
                    String token = payload.getString("refreshToken");
                    RuntimeInfo.getInstance().updateAuthInfo(token);

                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS);
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS);

                } catch (JSONException e) {
                    Logger.w("login result parse failed - " + e.getMessage());
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_FAIL);
                }
            }
        });
    }

    private void loginWithToken() {
        JSONObject payload = new JSONObject();

        JSONObject json = new JSONObject();
        try {
            payload.put("refreshToken", RuntimeInfo.getInstance().authInfo.refreshToken);

            json.put("type", "action");
            json.put("name", "alexa.loginWithToken");
            json.put("version", 1);

            json.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        smartWatchSDK.action(json.toString(), null, new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                Logger.d("login with Token - " + data);
                try {
                    JSONObject result = new JSONObject(data);
                    RuntimeInfo.getInstance().updateAuthInfo(result.getJSONObject("payload").getString("refreshToken"));

                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_SUCCESS);
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LOGIN_SUCCESS);
                } catch (JSONException e) {
                    Logger.d("login with Token result parse failed - " + e.getMessage());
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().loginFragmentHandler, HandlerConst.MSG_LOGIN_FAIL);
                }
            }
        });
    }

    private void scheduleTokenRefresh() {
        Message msg = Message.obtain();
        msg.what = HandlerConst.MSG_REFRESH_TOKEN;
        RuntimeInfo.getInstance().mainHandler.sendMessageDelayed(msg, 55 * 60 * 1000);
    }

    private void scheduleChannelPing() {
        Message msg = Message.obtain();
        msg.what = HandlerConst.MSG_PING_CHANNEL;

        RuntimeInfo.getInstance().mainHandler.sendMessageDelayed(msg, 280 * 1000);
        Logger.v("schedule channel ping");
    }

    private void refreshAuthorizationToken() {
//        HttpChannel.getInstance().refreshAccessToken();
    }

    private void pingDownChannel() {
//        HttpChannel.getInstance().ping();
    }

    private void onDownChannelClosed() {
        RuntimeInfo.getInstance().mainHandler.removeMessages(HandlerConst.MSG_PING_CHANNEL);
//        HttpChannel.getInstance().recreateDownChannel();
    }

    private void setDeviceInfo(OnResultCallback callback) {
        JSONObject product = new JSONObject();
        try {
            product.put("id", DeviceInfo.ProductId);
            product.put("clientId", DeviceInfo.ClientId);
            product.put("serialNumber", DeviceInfo.ProductSerialNumber);
            product.put("name", "TouchAlexa");
            product.put("friendlyName", "TouchAce");
            product.put("description", "Touch Self");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject manufacturer = new JSONObject();
        try {
            manufacturer.put("name", "TouchManufacturer");
            manufacturer.put("model", "Touch-1");
            manufacturer.put("firmware", "1.0");
            manufacturer.put("software", "20221020");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject payload = new JSONObject();
        try {
            payload.put("product", product);
            payload.put("manufacturer", manufacturer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject info = new JSONObject();
        try {
            info.put("type", "action");
            info.put("name", "device.setInfo");
            info.put("version", 1);
            info.put("payload", payload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        smartWatchSDK.action(info.toString(), null, callback);
    }
}