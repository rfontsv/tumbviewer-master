package com.nutrition.express.model.helper;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hm on 8/18/16.
 */
public class SearchHistoryHelper {
    private static final String DEFAULT_NAME = "search_history.json";
    private static final int size = 20;
    private LinkedList<String> linkedList;
    private List<Object> list;
    private String name;

    public SearchHistoryHelper() {
        this(DEFAULT_NAME);
    }

    public SearchHistoryHelper(String name) {
        this.name = name;
        list = LocalPersistenceHelper
                .getShortContent(name, new TypeToken<ArrayList<String>>(){}.getType());
        if (list == null) {
            list = new ArrayList<>(size);
        }
        linkedList = new LinkedList<>();
        for (Object object : list) {
            linkedList.addLast((String) object);
        }
    }

    private void save() {
        LocalPersistenceHelper.storeShortContent(name, list);
    }

    public List<Object> getHistories() {
        return list;
    }

    public void add(String keyword) {
        linkedList.remove(keyword);
        linkedList.addFirst(keyword);
        list.clear();
        list.addAll(linkedList);
        save();
    }

    public void remove(String keyword) {
        linkedList.remove(keyword);
        list.clear();
        list.addAll(linkedList);
        save();
    }

    public void clear() {
        linkedList.clear();
        list.clear();
        save();
    }

}
