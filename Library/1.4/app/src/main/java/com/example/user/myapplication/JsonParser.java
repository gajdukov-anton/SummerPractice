package com.example.user.myapplication;

import android.content.Context;
import android.location.Address;
import android.util.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class JsonParser {
    public static Card readCompanyJSONFile(Context context) throws IOException,JSONException {

        // Read content of company.json
        String jsonText = readText(context, R.raw.data);


        JSONObject jsonRoot = new JSONObject(jsonText);


        String id= jsonRoot.getString("id");
        String name = jsonRoot.getString("name");
        String authors = jsonRoot.getString("authors");

//        JSONArray jsonArray = jsonRoot.getJSONArray("authors");
//        String[] authors = new String[jsonArray.length()];
//
//        for(int i=0;i < jsonArray.length();i++) {
//            authors[i] = jsonArray.getString(i);
//        }

        boolean available = jsonRoot.getBoolean("available");
        String year = jsonRoot.getString("year");
        String description = jsonRoot.getString("description");
        String link = jsonRoot.getString("link");

        Card card = new Card(name, description, R.drawable.emma);
        card.authors = authors;
        card.available = available;
        card.link = link;
        card.year = year;
        card._id = id;
        return card;
    }


    public static void writeJsonStream(Writer output, Card card ) throws IOException {

        JsonWriter jsonWriter = new JsonWriter(output);

        jsonWriter.beginObject();// begin root

        //jsonWriter.name("id").value(company.getId());
        jsonWriter.name("name").value(card.name);
        jsonWriter.name("authors").value(card.authors);
        jsonWriter.name("id").value(card._id);
        jsonWriter.name("available").value(card.available);
        jsonWriter.name("year").value(card.year);
        jsonWriter.name("link").value(card.link);
        jsonWriter.name("description").value(card.description);

        // end root
        jsonWriter.endObject();
    }



    private static String readText(Context context, int resId) throws IOException {
        InputStream is = context.getResources().openRawResource(resId);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        StringBuilder sb= new StringBuilder();
        String s= null;
        while((  s = br.readLine())!=null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}
