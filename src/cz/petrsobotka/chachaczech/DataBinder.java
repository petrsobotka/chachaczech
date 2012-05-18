package cz.petrsobotka.chachaczech;

import java.util.HashMap;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

/**
 * Můj vlastní binder, který zajistí, aby SimpleAdapter pro ListView dokázal správně data 
 * naroubovat na jednotlivé položky. tato třída existuje proto, že moje položky 
 * v ListView jsem si sám vytvářel a proto je třeba je obsluhovat speciálně.
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
