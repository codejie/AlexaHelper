package com.SmartWatchVoice.bestapp.system.channel;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public abstract class DirectiveParser {
    public enum PartType {
        DIRECTIVE,
        OCTET_BUFFER,
        OCTET_FILE
    }
    public static abstract class Part {
        final private PartType type;
        final Map<String, String> headers;
        public Part(PartType type, Map<String, String> headers) {
            this.type = type;
            this.headers = headers;
        }

        public PartType getType() {
            return type;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        @Override
        public String toString() {
            String headerString = "";
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                headerString += "\n" + entry.getKey() + " : " + entry.getValue();
            };

            return "Part (" + type + ")" + headerString;
        }
    }

    public static class DirectivePart extends Part {
        private final Directive directive;
        public DirectivePart(Map<String, String> headers, Directive directive) {
            super(PartType.DIRECTIVE, headers);
            this.directive = directive;
        }

        public Directive getDirective() {
            return directive;
        }
    }

    public static class OctetBufferPart extends Part {
        private byte[] buffer;
        public OctetBufferPart(Map<String, String> headers, byte[] data) {
            super(PartType.OCTET_BUFFER, headers);

            this.buffer = data;
        }

        public byte[] getBuffer() {
            return buffer;
        }
    }

    public static class OctetFilePart extends Part {
        private String file;
        public OctetFilePart(Map<String, String> headers, String file) {
            super(PartType.OCTET_FILE, headers);
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }

    protected String boundary = null;
    protected String endBoundary = null;

//    protected StringBuilder contentBuilder = new StringBuilder();

    protected Part parsePart(String content) {
        int pos = content.indexOf("\r\n\r\n");
        if (pos == -1) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        Scanner scanner = new Scanner(content.substring(0, pos));
        while (scanner.hasNext()) {
            final String[] pair = scanner.nextLine().split(": ");
            headers.put(pair[0], pair[1]);
        }

        if (headers.get("Content-Type").contains("application/json")) {
            return buildDirectivePart(headers, content.substring(pos + 4));
        } else if (headers.get("Content-Type").contains("application/octet-stream")){
            return buildOctetBufferPart(headers, content.substring(pos + 4).getBytes());
        } else {
            return null;
        }
    }

    protected DirectivePart buildDirectivePart(Map<String, String> headers, String content) {
        Logger.v("directive content - \n" + content);

        Directive directive = new GsonBuilder().create().fromJson(content, Directive.DirectiveWrapper.class).directive;
        return new DirectivePart(headers, directive);
    }

    protected OctetBufferPart buildOctetBufferPart(Map<String, String> headers, byte[] buffer) {
        return new OctetBufferPart(headers, buffer);
    }

    protected OctetFilePart buildOctetFilePart(Map<String, String> headers, String file) {
        return new OctetFilePart(headers, file);
    }
}
