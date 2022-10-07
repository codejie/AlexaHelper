package com.SmartWatchVoice.bestapp.system.channel;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class MyLoggingInterceptor2 implements Interceptor {
    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    };

    private static final String TAG = MyLoggingInterceptor2.class.getName();

    private Level level = Level.NONE;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (this.level == Level.NONE) {
            return chain.proceed(request);
        }

        RequestBody requestBody = request.body();
        Connection connection = chain.connection();

        String requestStartMessage = "--> "
                + request.method() + " "
                + request.url()
                + (connection != null ? connection.protocol().toString() : "");
        if (this.level == Level.BODY || this.level == Level.HEADERS) {
            if (requestBody != null) {
                requestStartMessage += "(" + requestBody.contentLength() + "-byte body)";
            }
        }
        log(requestStartMessage);

        if (this.level == Level.BODY || this.level == Level.HEADERS) {
            log("[Request Headers]");
            Headers headers = request.headers();
            for (int i = 0; i < headers.size(); ++ i) {
                log(headers.name(i) + ": " + headers.value(i));
            }
        }

        if (this.level == Level.BODY && requestBody != null) {
//            Buffer buffer = new Buffer();
//            requestBody.writeTo(buffer);

            log("[Request Body]");
            final MediaType type =  requestBody.contentType();
//            log("Content-Type: " + type.toString());
            if (type != null && type.type().equals("multipart")) {
                MultipartBody multipartBody = (MultipartBody) requestBody;
                for (int index = 0; index < multipartBody.size(); ++ index) {
                    log("[Request Body Part - " + String.valueOf(index) + "]");
                    MultipartBody.Part part = multipartBody.part(index);
                    log("[Request Body Part Headers - " + String.valueOf(index) + "]");
                    Headers hds = part.headers();
                    for (int i = 0; i < hds.size(); ++ i) {
                        log(hds.name(i) + ": " + hds.value(i));
                    }
                    log("[Request Body Part Body - " + String.valueOf(index) + "]");
                    String contentType = hds.get("Content-Disposition");
                    if (contentType != null && contentType.indexOf("metadata") != -1) {
                        Buffer buffer = new Buffer();
                        part.body().writeTo(buffer);
                        log(buffer.readString(part.body().contentType() != null ? part.body().contentType().charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8));
                    } else {
                        log("<Audio Data>");
                    }
                }
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                log(buffer.readString(requestBody.contentType() != null ? requestBody.contentType().charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8));
            }
            log("--> END " + request.method() + " " + requestBody.contentLength() + "(-byte body)");
        } else {
            log("--> END " + request.method());
        }

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        log("<-- " + response.code() + " "
                + response.protocol().toString() + " "
                + (response.message().isEmpty() ? "" : response.message()) + " "
                + response.request().url() + " "
                + tookMs + "ms " + contentLength + " byte body");

        if (this.level == Level.BODY || this.level == Level.HEADERS) {
            log("[Response Headers]");
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); ++ i) {
                log(headers.name(i) + ": " + headers.value(i));
            }

            if (this.level == Level.BODY) {
                log("[Response Body]");
                if (contentLength == 0L) {
                    log("--> END HTTP (0) byte body");
                } else if (contentLength == -1L) {
                    final MediaType type = responseBody.contentType();
                    if (type != null) {
                        if (type.type().equals("multipart")) {
//                        ResponseBody body = RealResponseBody.create(responseBody.source(), type, responseBody.contentLength());
//                        log(body.string());
                            log("--> END HTTP (multipart) body");
                        } else if (type.subtype().equals("octet-stream")) {
                            log("--> END HTTP (octet-stream) body");
                       } else if (type.subtype().equals("json")) {
                            log(responseBody.string());
                            log("--> END HTTP (json) body");
                        } else {
                            log("--> END HTTP (unsupported) body");
                        }
                    } else {
                        log("<-- END HTTP (unknown) body)");
                    }
                } else {
                    Buffer buffer = response.peekBody(responseBody.contentLength()).source().getBuffer();
                    log(buffer.readString(requestBody.contentType() != null ? requestBody.contentType().charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8));

                    log("--> END HTTP (" + responseBody.contentLength() + ") byte body");
                }
            } else {
                log("<-- END HTTP");
            }
        }

        return response;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    private void log(String message) {
        Log.d(TAG, message);
    }
}
