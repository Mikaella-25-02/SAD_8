package com.example.lab8kotlin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import ru.mirea.lab8.R;

public class PlaceAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<PlaceItem> mResults;

    public PlaceAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public PlaceItem getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.dropdown_item_line, parent, false);
        }
        PlaceItem placeItem = getItem(position);
        ((TextView) convertView.findViewById(R.id.text_address)).setText(placeItem.getAddress());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<PlaceItem> items = findPlaces(constraint.toString());
                    filterResults.values = items;
                    filterResults.count = items.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<PlaceItem>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    private List<PlaceItem> findPlaces(String place) {
        String response = "";
        String[] coordinates = new String[2];
        List<PlaceItem> list = new ArrayList<>();
        try{
            String address = place;
            HttpDataHandler http = new HttpDataHandler();
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                    address, HttpDataHandler.KEY_API);
            response = http.getHTTPData(url);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try{
            System.out.println(response);
            JSONObject jsonObject = new JSONObject(response);
            System.out.println(jsonObject.toString());
            coordinates[0] = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                    .get("lat").toString();
            coordinates[1] = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                    .get("lng").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            HttpDataHandler http = new HttpDataHandler();
            String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&language=%s&key=%s",
                    coordinates[0], coordinates[1] , "ru", HttpDataHandler.KEY_API);
            response = http.getHTTPData(url);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = ((JSONArray)jsonObject.get("results"));
            for (int i = 0; i < array.length(); i++) {
                list.add(new PlaceItem(array.getJSONObject(i).getString("formatted_address"),
                        array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location")
                                .get("lat").toString(),
                        array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location")
                                .get("lng").toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}