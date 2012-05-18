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
        
        // "0" is the key of preference which stores whether QWERTZ is ON
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
     * return false to pass the letter, return true to catch it and handle it ourselves
     */
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

    	/*
    	 * If ALT on and SPACE pressed, the system sends event with code 126 but in the log it is decoded as KEYCODE_RESET_META
    	 * which is not described in offical documentation. It is send by some other process and it causes reset of meta 
    	 * keys whatever state they are in. But this can't be handeled by our own MyMetaKeyListener.
    	 * 
    	 * If we throw it away, everything will be ok ;-)
    	 */
    	if(keyCode == 126)
    	{
    		//Log.v("EVENTTYPE", "THROW AWAY THIS STUFF");
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
	             *  'ů' takes place here because it is easier to remember: U and V are visually similar.
	             */
				case KeyEvent.KEYCODE_V:
					handleAccentKey(event, "ů", "Ů", ic);  
	                return true;
	            /*
	             * 'ě' takes place here because it is near to 'é' and because it is situated above 'W' on typical czech keyboard layout. 
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
     * This method handles an event, which has to cause insertion of an accented letter.
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
			 * Next two lines of code have to clear Shift for the purose of other processes which take part in input handling.
			 * When Shift is on, we properly handle it but if do not explicitly send a KeyEvent to the InputConnection, Shift stays turned on
			 * (There is probably some other process with its own MetaKeyListener). Then the next inserted letter (which we do not handle) handles the Shift
			 * and becomes capital letter. This behaviour is undesirable of course. That is why we use this workaround. We send two events indicating 
			 * Shift key press. The first causes CAPS LOCK (SHIFT LOCKED) and the second an entire Shift clearing.
			 * 
			 * If we respond to an event and CAPS LOCK (SHIFT LOCKED) is on (see if statement above), we do not change the Shift state, so 
			 * nothing has to be done. 
			 */
			keyDownUp(KeyEvent.KEYCODE_SHIFT_LEFT);
			keyDownUp(KeyEvent.KEYCODE_SHIFT_LEFT);
		} else if (shiftLocked) {
			ic.commitText(caps, 1);			
		}
		
		/*
		 * See the long comment for Shift clearing above, this is the same for ALT.
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
		Log.v("TEST", "Key up: " + Integer.toString(MyMetaKeyKeyListener.getMetaState(old)) + " on : " + Integer.toString(MyMetaKeyKeyListener.getMetaState(metaState)));
		//Log.v("EVENTTYPE", "out key code: " + keyCode);
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