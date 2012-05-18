package cz.petrsobotka.chachaczech;

import android.view.View;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * Own binder which makes ListView backed by SimpleAdapter fetch appropriate data items.
 * This class exists because of custom laout items for ListView used in settings. 
 * @author Petr Sobotka
 *
 */
class DataBinder implements ViewBinder {

	@Override
	public boolean setViewValue(View view, Object data, String textRepresentation) {
		
		Setting s = (Setting) data;

		if(view instanceof android.widget.CheckedTextView)
		{
			((android.widget.CheckedTextView) view).setText(s.name);
			((android.widget.CheckedTextView) view).setChecked(s.active);
		}

		return true;
	}
	
}
