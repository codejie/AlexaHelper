package com.SmartWatchVoice.bestapp.system.channel;

import android.os.Environment;

import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okio.BufferedSource;

public class ResponseStreamDirectiveParser extends DirectiveParser {

    public static final byte CR = 0x0D;
    public static final byte LF = 0x0A;

    public List<Part> parseParts(BufferedSource source) throws IOException {
        List<Part> parts = new ArrayList<Part>();

        boolean isHeader = false;
        boolean isBody = false;
        boolean isJson = false;
        Map<String, String> headers = new HashMap<>();
        while (!source.exhausted()) {
            if (!isBody) {
                String line = source.readUtf8Line();
                if (line != null) {
                    if (boundary == null) {
                        boundary = line;
                        endBoundary = boundary + "--";

                        isHeader = true;
                    } else if (line.equals(boundary)) {
                        isHeader = true;
                    } else if (line.equals(endBoundary)) {
                        boundary = null;
                        endBoundary = null;
                        isBody = false;
                        isHeader = false;
                    } else {
                        if (isHeader) {
                            if (!line.equals("")) {
                                String[] tmp = line.split(": ");
                                headers.put(tmp[0], tmp[1]);
                            } else {
                                isHeader = false;
                                isBody = true;

                                if (headers.get("Content-Type").contains("application/json")) {
                                    isJson = true;
                                } else if (headers.get("Content-Type").contains("application/octet-stream")) {
                                    isJson = false;
                                } else {
                                    Logger.w("unknown type -" + headers.get("Content-Type"));
                                }
                            }
                        }
                    }
                }
            } else {
                if (isJson) {
                    String l = source.readUtf8Line();
                    if (l != null) {
                        parts.add(buildDirectivePart(headers, l));

                        headers = new HashMap<>();
                        isJson = false;
                        isBody = false;
                    } else {
                        Logger.w("SOURCE read directive null");
                    }
                } else {
                    byte[] boundaryBytes = boundary.getBytes();

                    byte[] buffer = new byte[10240];
                    byte[] cache = new byte[32];
                    String filename = System.currentTimeMillis() + ".mp3";
                    File file = RuntimeInfo.getInstance().makeSpeechMP3File(filename); // new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "a.mp3");
                    FileOutputStream ofs = new FileOutputStream(file);

                    InputStream is = source.inputStream();
                    int read = 0;
                    int ch = -1;
                    while ((ch = is.read()) != -1) {
                        if (ch != CR) {
                            buffer[read] = (byte)ch;
                            ++ read;
                        } else {
                            cache[0] = (byte)ch;
                            int r = is.read(cache, 1, boundaryBytes.length + 1);
                            if (r == boundaryBytes.length + 1) {
                                if (cache[1] == LF && bytesEqual(cache, 2, boundaryBytes, boundaryBytes.length)) {
                                    break;
                                }
                            }
                            for (int i = 0; i < r + 1; ++ i) {
                                buffer[read] = cache[i];
                                ++ read;

                                if (read == 10240) {
                                    ofs.write(buffer, 0, read);
                                    read = 0;
                                }
                            }
                        }

                        if (read == 10240) {
                            ofs.write(buffer, 0, read);
                            read = 0;
                        }
                    }

                    ofs.write(buffer, 0, read);
                    ofs.close();

                    parts.add(buildOctetFilePart(headers, filename));

                    headers = new HashMap<>();
                    isBody = false;
                }
            }
        }
        Logger.d("source end.");

        return parts;
    }

    public static boolean bytesEqual(byte[] a, int offset, byte[] b, int count) {
        for (int i = offset; i < count; ++ i) {
            if (a[i] != b[i - offset]) {
                return false;
            }
        }
        return true;
    }
}
