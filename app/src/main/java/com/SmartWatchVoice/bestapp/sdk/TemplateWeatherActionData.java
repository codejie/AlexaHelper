package com.SmartWatchVoice.bestapp.sdk;

import java.util.ArrayList;

public class TemplateWeatherActionData extends TemplateActionData {
    public static class Item {
        public String day;
        public String date;
        public String iconUrl;
        public String code;
        public String high;
        public String low;
    }

    public String description;
    public String value;
    public String code;
    public String iconUrl;
    public String high;
    public String highUrl;
    public String low;
    public String lowUrl;

    public ArrayList<Item> items = new ArrayList<>();
}
