package cz.petrsobotka.chachaczech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ChaChaCzechSettings extends ListActivity {
	
	public static final String PREF_FILE_NAME = "PrefFile";
	private SharedPreferences.Editor editor;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        
        // get the array with settings
        String[] labels = getResources().getStringArray(R.array.settings_items);
        ArrayList<Setting> settings = new ArrayList<Setting>();
        for(int i = 0; i < labels.length; i++ )
        {
        	
        	boolean preference = false;
            if(preferences.contains(Integer.toString(i)))
            	preference = preferences.getBoolean(Integer.toString(i), false);
            else {
            	editor.putBoolean(Integer.toString(i), preference); // value to store
            	editor.commit();
            }
        	Setting s = new Setting(i, labels[i], preference);
        	settings.add(s);
        }
        
        
        // map values to view elements
        String[] from = new String[] {"checkbox"};
        int[] to = new int[] { R.id.item_checkbox };
        
        // lets create data carrier
        List<HashMap<String, Setting>> data = new ArrayList<HashMap<String, Setting>>();
        
        // fill data
        for(Setting s: settings)
        {
        	HashMap<String, Setting> map = new HashMap<String, Setting>();
        	map.put("checkbox", s);
        	data.add(map);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.settings_item, from, to);

        adapter.setViewBinder(new DataBinder());
        ListView lv = getListView();
        lv.setAdapter(adapter);
       
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        	((CheckedTextView) view).toggle();
          	editor.putBoolean(Integer.toString(position), ((CheckedTextView) view).isChecked());
        	editor.commit();
          }
        });
        
    }
}