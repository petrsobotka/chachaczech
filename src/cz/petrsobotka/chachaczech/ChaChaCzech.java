package cz.petrsobotka.chachaczech;

import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;



public class ChaChaCzech extends InputMethodService  {

	/**
	 * Swap Y and Z and emulate Czech QWERTZ keyboard.
	 */
	private boolean QWERTZ = false;
	
    private long metaState = 0;
    
    private boolean shiftInactive = false;
    private boolean shiftActive = false;
    private boolean shiftLocked = false;
    
    private boolean altInactive = false;
    private boolean altActive = false;
    private boolean altLocked = false;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        
        SharedPreferences preferences = getSharedPreferences(ChaChaCzechSettings.PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        // "0" je klic preference, ktera udrzuje, zda je zapnuto QWERTZ
        if(preferences.contains("0"))
        	QWERTZ = preferences.getBoolean("0", false);
        else {
        	editor.putBoolean("0", QWERTZ); // value to store
        	editor.commit();
        }
        
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override 
    public void onFinishInput() {
        super.onFinishInput();
    }
	
	/**
	 * Když vrátím false, znak se normálně zapíše. Když true, tak se znak zachytí a musím něco udělat sám.
	 */
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

    	// když je zapnutý alt a zmáčkne se mezera, systém posílá událsot s číslem 126, 
    	// ale v logu se dekóoduje jako KEYCODE_RESET_META, což v manuálu není.
    	// každopádně to posílá nějaký jiný proces. Způsobuje to vypnutí met akláves ať už jsou v jakémkoli stavu.
    	// to ovšem není schopen reflektovat MyMetaKeyKeyListener, ten si myslí, že jsou meta klávesy stále aktivní. 
    	// když to zachytíme a zahodíme, bude zase vše v pořádku.
    	if(keyCode == 126)
    	{
    		//Log.v("TYPUDALOSTI", "ZAHAZUJU MALWARE");
    		return true;
    	}
    	
		InputConnection ic = getCurrentInputConnection();
		metaState = MyMetaKeyKeyListener.handleKeyDown(metaState, keyCode, event);

		shiftInactive = (MyMetaKeyKeyListener.getMetaState(metaState, MyMetaKeyKeyListener.META_SHIFT_ON) == 0);
		shiftActive = (MyMetaKeyKeyListener.getMetaState(metaState, MyMetaKeyKeyListener.META_SHIFT_ON) == 1);
		shiftLocked = (MyMetaKeyKeyListener.getMetaState(metaState, MyMetaKeyKeyListener.META_SHIFT_ON) == 2);

		altInactive = (MyMetaKeyKeyListener.getMetaState(metaState, MyMetaKeyKeyListener.META_ALT_ON) == 0);
		altActive = (MyMetaKeyKeyListener.getMetaState(metaState, MyMetaKeyKeyListener.META_ALT_ON) == 1);
		altLocked = (MyMetaKeyKeyListener.getMetaState(metaState, MyMetaKeyKeyListener.META_ALT_ON) == 2);
		
		
		//Log.v("TEST", "Dump SHIFT: " + Boolean.toString(shiftInactive) + " " + Boolean.toString(shiftActive) + " " + Boolean.toString(shiftLocked));
		//Log.v("TEST", "Dump ALT  : " + Boolean.toString(altInactive) + " " + Boolean.toString(altActive) + " " + Boolean.toString(altLocked));
		
        if (altActive && !event.isAltPressed()) {
    		switch (keyCode) {
				case KeyEvent.KEYCODE_A:
					handleAccentKey(event, "á", "Á", ic);
	                return true;
				case KeyEvent.KEYCODE_C:
					handleAccentKey(event, "č", "Č", ic);
	                return true;
				case KeyEvent.KEYCODE_D:
					handleAccentKey(event, "ď", "Ď", ic);
	                return true;
				case KeyEvent.KEYCODE_E:
					handleAccentKey(event, "é", "É", ic);
	                return true;
				case KeyEvent.KEYCODE_I:
					handleAccentKey(event, "í", "Í", ic);
	                return true;
				case KeyEvent.KEYCODE_N:
					handleAccentKey(event, "ň", "Ň", ic);
	                return true;
				case KeyEvent.KEYCODE_O:
					handleAccentKey(event, "ó", "Ó", ic);
	                return true;
				case KeyEvent.KEYCODE_R:
					handleAccentKey(event, "ř", "Ř", ic);
	                return true;
				case KeyEvent.KEYCODE_S:
					handleAccentKey(event, "š", "Š", ic);
	                return true;
				case KeyEvent.KEYCODE_T:
					handleAccentKey(event, "ť", "Ť", ic);
	                return true;
				case KeyEvent.KEYCODE_U:
					handleAccentKey(event, "ú", "Ú", ic);
	                return true;
	            /*
	             * 'ů' je sem umístěno kvůli zapamatování: U a V jsou vizuálně podobné
	             */
				case KeyEvent.KEYCODE_V:
					handleAccentKey(event, "ů", "Ů", ic);  
	                return true;
	            /*
	             * 'ě' je sem umístěno kvůli blízkosti k 'é' a také, protože nad W je 'ě' na běžné PC klávesnici
	             */
				case KeyEvent.KEYCODE_W:
					handleAccentKey(event, "ě", "Ě", ic);
	                return true;
				case KeyEvent.KEYCODE_Y:
					if(QWERTZ)
						handleAccentKey(event, "ž", "Ž", ic);
					else
						handleAccentKey(event, "ý", "Ý", ic);
	                return true;
				case KeyEvent.KEYCODE_Z:
					if(QWERTZ)
						handleAccentKey(event, "ý", "Ý", ic);
					else
						handleAccentKey(event, "ž", "Ž", ic);
	                return true;
				default:
			}
        } else if(QWERTZ && !altLocked && !event.isAltPressed()) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_Y:
	                keyDownUp(KeyEvent.KEYCODE_Z);
	                return true;
				case KeyEvent.KEYCODE_Z:
	                keyDownUp(KeyEvent.KEYCODE_Y);
	                return true;
				default:
			}
        }
        
        if (event.getKeyCode() != KeyEvent.KEYCODE_SHIFT_LEFT && event.getKeyCode() != KeyEvent.KEYCODE_ALT_LEFT)
        	metaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(metaState);
        
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN)
        	metaState = MyMetaKeyKeyListener.resetLockedMeta(metaState);
        
		return super.onKeyDown(keyCode, event);
	}
    
    /**
     * Metoda obslouží zachycenou událost, na kterou je potřeba reagovat posláním akcentovaného písmenka.
     * @param event
     * @param smallCaps
     * @param caps
     * @param ic
     */
    private void handleAccentKey(KeyEvent event, CharSequence smallCaps, CharSequence caps, InputConnection ic)
    {
		ic.clearMetaKeyStates(event.getMetaState());
		if(shiftInactive) {
			ic.commitText(smallCaps, 1);			
		} else if (shiftActive) {
			ic.commitText(caps, 1);
			
		    /*
		     * Následující dva řádky slouží pro vyčištění shiftu pro další procesy, které se podílejí na zpracování 
		     * zadávaného textu. Když je zapnutý shift, sice adekvátně reagujeme a shift zpracujeme, ale pokud
		     * se InputConnection vysloveně nepošle KeyEvent, tak shift jakoby zůstává zapnutý (jiný proces má svůj vlastní MetaKeKeyListener pravděpodobně).
		     * Další zadané písmeno (které nehandlujeme) pak shift zpracuje a stane se velkým písmenem. 
		     * To je nežádoucí. Proto je zde tento workaround. Pošleme dvě události, že byl zmáčkut SHIFT.
		     * První zmáčknutí způsobí zapnutí CAPS LOCK (SHIFT LOCKED) a to duhé úplné vypnutí shiftu. 
		     * Shift tak zcela zkonzumujeme. 
		     * 
		     * Když reagujeme na událost se zapnutým CAPS LOCK (SHIFT LOCKED) viz IF o jedno výše, stav SHIFTu neměníme, proto není třeba nic dělat.
		     */
			keyDownUp(KeyEvent.KEYCODE_SHIFT_LEFT);
			keyDownUp(KeyEvent.KEYCODE_SHIFT_LEFT);
		} else if (shiftLocked) {
			ic.commitText(caps, 1);			
		}
		
		/*
		 * POdobně jako výše clearujeme SHIFT, tak tady řešíme ALT.
		 */
		if(altActive)
		{
			keyDownUp(KeyEvent.KEYCODE_ALT_LEFT);
			keyDownUp(KeyEvent.KEYCODE_ALT_LEFT);
		}
		metaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(metaState);
    }
	
    @Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	long old = metaState;
        metaState = MyMetaKeyKeyListener.handleKeyUp(metaState, keyCode, event);
		Log.v("TEST", "Zdvih: " + Integer.toString(MyMetaKeyKeyListener.getMetaState(old)) + " na : " + Integer.toString(MyMetaKeyKeyListener.getMetaState(metaState)));
		//Log.v("TYPUDALOSTI", "odchozi kod: " + keyCode);
        return super.onKeyUp(keyCode, event);
    }
	
    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }	
}