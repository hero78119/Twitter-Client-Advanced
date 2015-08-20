package com.codepath.apps.restclienttemplate.lib;

import org.json.JSONArray;

/**
* Created by jonaswu on 2015/8/11.
*/
public class SaveDataToDB {
    public boolean clean;
    public JSONArray data;
    public Long lessThen;

    public SaveDataToDB(boolean clean,
                        JSONArray data,
                        Long lessThen) {
        this.clean = clean;
        this.data = data;
        this.lessThen = lessThen;
    }
}
