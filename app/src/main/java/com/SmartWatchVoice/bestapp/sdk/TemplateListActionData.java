package com.SmartWatchVoice.bestapp.sdk;

import java.util.ArrayList;

public class TemplateListActionData extends TemplateActionData {
    public static class Item {
        public Item(String left, String right) {
            this.left = left;
            this.right = right;
        }
        public String left;
        public String right;
    }

    public ArrayList<Item> items = new ArrayList<>();
}
