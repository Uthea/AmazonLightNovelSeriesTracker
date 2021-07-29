package com.vindra.seriestracker.webhook.discord;

import com.vindra.seriestracker.entity.Kindle;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponseFactory {

    public static Map<String, String> generateKindleUpdateMap(Object[] currentState, Object[] previousState, String[] propertyNames) {

        Map<String, String> updateField = new HashMap<>();


        for (int i = 0; i < propertyNames.length; i++) {

            switch (propertyNames[i]) {

                case "kindleUnlimited":
                    if ( (boolean) previousState[i] == true && (boolean) currentState[i] == false) {
                        updateField.put("Kindle Unlimited Eligible", "No");
                    } else if ((boolean) previousState[i] == false && (boolean) currentState[i] == true){
                        updateField.put("Kindle Unlimited Eligible", "Yes");
                    }

                    break;
                case "preOrder":
                    if ( (boolean) previousState[i] == true && (boolean) currentState[i] == false) {
                        updateField.put("Released", "Yes");
                    }
                    break;
                case "price":
                    if ( (int) previousState[i] != (int) currentState[i]) {
                        updateField.put("New Price", "¥"+currentState[i].toString());
                    }
                    break;
            }

        }

        return updateField;


    }


    public static Map<String, String> generateKindleSeriesMap(Object[] state, String[] propertyNames) {

        Map<String, String> seriesMap = new HashMap<>();

        String[] fields = {"seriesTitle", "numberOfBook"};


        for (int i = 0; i < propertyNames.length; i++) {

            if (Arrays.asList(fields).contains(propertyNames[i])) {
                seriesMap.put(propertyNames[i], state[i].toString());
            }

        }

        return seriesMap;
    }
}
