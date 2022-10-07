package com.SmartWatchVoice.bestapp.system.channel;

import java.util.ArrayList;
import java.util.List;

public class DownChannelDirectiveParser extends DirectiveParser {

    private String boundary = null;
    private String endBoundary = null;

    private StringBuilder contentBuilder = new StringBuilder();

    public List<Part> parseParts(String input) {
        int pos = -1;

        contentBuilder.append(input);

        if (boundary == null) {
            pos = input.indexOf("\r\n");
            if (pos != -1) {
                boundary = input.substring(0, pos + 2);
                endBoundary = input.substring(0, pos) + "--\r\n";

                contentBuilder.delete(0, pos + 2);
            } else {
                return null;
            }
        }

        List<Part> ret = new ArrayList<>();

        pos = contentBuilder.toString().indexOf(boundary);
        while (pos != -1) {
            ret.add(parsePart(contentBuilder.substring(0, pos)));

            contentBuilder.delete(0, pos + boundary.length());
            pos = contentBuilder.toString().indexOf(boundary);
        }

//        boundary = null;
//        endBoundary = null;
//        contentBuilder.setLength(0);

        return ret;
    }

}
