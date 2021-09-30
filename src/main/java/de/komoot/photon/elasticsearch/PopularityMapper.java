package de.komoot.photon.elasticsearch;

import de.komoot.photon.PhotonDoc;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PopularityMapper {
    private JSONObject popularityMap = null;

    public PopularityMapper() {
    }

    public int calculatePopularity(PhotonDoc doc) {
        int popularity = 0;
        Map<String, String> allTags = new HashMap<>();
        allTags.put(doc.getTagKey(), doc.getTagValue());
        allTags.putAll(doc.getExtratags());

        JSONObject jsonPopularityMap = getPopularityMap();
        for (Map.Entry<String, String> tagEntry: allTags.entrySet()) {
            String key = tagEntry.getKey();
            if (jsonPopularityMap.has(key)) {
                JSONObject osmTagScores = jsonPopularityMap.getJSONObject(key);
                if (osmTagScores.has("_score")) {
                    popularity += osmTagScores.getInt("_score");
                }


            }
        }


        if (jsonPopularityMap.has(doc.getTagKey())) {
            System.out.println("key : " + doc.getTagKey());
            JSONObject jsonObject = jsonPopularityMap.getJSONObject(doc.getTagKey());

            if (jsonObject.has(doc.getTagValue())) {
                popularity += jsonObject.getJSONObject(doc.getTagValue()).getInt("_score");
            }
        }

        Map<String, String> extraTags = doc.getExtratags();
        for (String extraTagKey: extraTags.keySet()) {
            if (jsonPopularityMap.has(extraTagKey)) {
                System.out.println("key : " + extraTagKey);
                popularity += jsonPopularityMap.getJSONObject(extraTagKey).getInt("_score");
            }
        }

        return popularity;
    }

    private JSONObject getPopularityMap() {
        if (popularityMap != null) {
            return popularityMap;
        }

        try {
            InputStream is = getClass().getResourceAsStream( "/popularity_map.json");
            String jsonTxt = IOUtils.toString(is, sun.nio.cs.UTF_8.INSTANCE);
            popularityMap = new JSONObject(jsonTxt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return popularityMap;
    }
}
