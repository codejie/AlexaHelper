package com.SmartWatchVoice.bestapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.SmartWatchVoice.bestapp.databinding.ActivityMainBinding;
import com.SmartWatchVoice.bestapp.handler.DirectiveCallback;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.channel.HttpChannel;
import com.SmartWatchVoice.bestapp.utils.Logger;

import jie.android.alexahelper.AppDeviceCallback;
import jie.android.alexahelper.Device;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

//    interface AppDeviceCallback {
//        void onMessage(int what, Object result);
//    }

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Handler handler = null;
    private HandlerThread directiveThread = null;
    private Handler directiveHandler = null;

    private Device device = null;

//    private RequestContext requestContext;

//    private Function2<? super Integer, Object, Unit> callback = (what, result) -> {
//        switch (what) {
//            case 1:
//                Logger.v("get 1");
//                break;
//        }
//        return Unit.INSTANCE;
//    };
    private AppDeviceCallback callback = new AppDeviceCallback() {
        @Override
        public void onMessage(int what, Object result) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        DeviceInfo.init(this);
//        ThingInfo.getInstance().init();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

//        RuntimeInfo.getInstance().start(this);
        initHandlers();

//        initRequestContext();
        DeviceInfo.ProductSerialNumber = "1234";
        device = Device.Companion.create(); //new ProductInfo(DeviceInfo.ClientId, DeviceInfo.ProductId, DeviceInfo.ProductSerialNumber));
        device.attach((Context) this, callback);
//        Device.Companion.getInstance().attach(this, new ProductInfo(DeviceInfo.ClientId, DeviceInfo.ProductId, DeviceInfo.ProductSerialNumber));

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
        device.onResume(this);
    }

    @Override
    protected void onDestroy() {
        Logger.e("onDestroy()");

        directiveThread.quit();

        SettingInfo.getInstance().flush();
        RuntimeInfo.getInstance().stop();

        device.detach(this);

        super.onDestroy();
    }

    private void initHandlers() {
        handler = Handler.createAsync(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Logger.v("MainHandler - " + message);
                switch (message.what) {
                    case HandlerConst.MSG_FETCH_TOKEN:
                        fetchProductAccessToken();
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

//    private void initRequestContext() {
//        requestContext = RequestContext.create(this.getApplicationContext());
//        requestContext.registerListener(new AuthorizeListener() {
//            @Override
//            public void onSuccess(AuthorizeResult authorizeResult) {
//                HttpChannel.getInstance().onAuthorizeSuccess(authorizeResult);
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

    public void fetchProductAccessToken() {
//        HttpChannel.getInstance().authorize(requestContext);
//        Device.Companion.getInstance().login();
        device.login();
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
        HttpChannel.getInstance().refreshAccessToken();
    }

    private void pingDownChannel() {
        HttpChannel.getInstance().ping();
    }

    private void onDownChannelClosed() {
        RuntimeInfo.getInstance().mainHandler.removeMessages(HandlerConst.MSG_PING_CHANNEL);
        HttpChannel.getInstance().recreateDownChannel();
    }

}