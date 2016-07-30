package com.myanmarunicorn.timbercalculator;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.myanmarunicorn.timbercalculator", "com.myanmarunicorn.timbercalculator.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.myanmarunicorn.timbercalculator", "com.myanmarunicorn.timbercalculator.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.myanmarunicorn.timbercalculator.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt3 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spn1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncalc = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblspecialthanks = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbldeveloper = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 39;BA.debugLine="lblTitle.Text = \"သစ္ခြဲသား တြက္ရန္\"";
mostCurrent._lbltitle.setText((Object)("သစ္ခြဲသား တြက္ရန္"));
 //BA.debugLineNum = 40;BA.debugLine="lbl1.Text = \"ထု\"";
mostCurrent._lbl1.setText((Object)("ထု"));
 //BA.debugLineNum = 41;BA.debugLine="lbl3.Text = \"ဗ်က္\"";
mostCurrent._lbl3.setText((Object)("ဗ်က္"));
 //BA.debugLineNum = 42;BA.debugLine="lbl5.Text = \"အေရအတြက္\"";
mostCurrent._lbl5.setText((Object)("အေရအတြက္"));
 //BA.debugLineNum = 43;BA.debugLine="spn1.AddAll(Array As String(\"ေပ\", \"တန္\", \"ေခ်ာင္း\"))";
mostCurrent._spn1.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"ေပ","တန္","ေခ်ာင္း"}));
 //BA.debugLineNum = 44;BA.debugLine="lbl6.Text = \"ႏႈန္းထား\"";
mostCurrent._lbl6.setText((Object)("ႏႈန္းထား"));
 //BA.debugLineNum = 45;BA.debugLine="lbl7.Text = \"က်ပ္\"";
mostCurrent._lbl7.setText((Object)("က်ပ္"));
 //BA.debugLineNum = 46;BA.debugLine="btnCalc.Text = \"တြက္ပါ\"";
mostCurrent._btncalc.setText((Object)("တြက္ပါ"));
 //BA.debugLineNum = 47;BA.debugLine="lblSpecialThanks.Text = \"ဤေဆာဖ့္ဝဲျဖစ္ေျမာက္ေရးအတြက္ အဖက္ဖက္မွကူညီေပးၾကေသာ အဖိုး ဦးတင္ဝင္း (သစ္ေတာဦးစီးအရာရွိ ၿငိမ္း) ႏွင့္ ဦးေလး ဦးေဇာ္လြင္ထြဋ္ (ျပည့္ၿဖိဳးေက်ာ္ သစ္ခြဲစက္) တို႔အား အထူးပင္ေက်းဇူးတင္ရွိပါသည္။\"";
mostCurrent._lblspecialthanks.setText((Object)("ဤေဆာဖ့္ဝဲျဖစ္ေျမာက္ေရးအတြက္ အဖက္ဖက္မွကူညီေပးၾကေသာ အဖိုး ဦးတင္ဝင္း (သစ္ေတာဦးစီးအရာရွိ ၿငိမ္း) ႏွင့္ ဦးေလး ဦးေဇာ္လြင္ထြဋ္ (ျပည့္ၿဖိဳးေက်ာ္ သစ္ခြဲစက္) တို႔အား အထူးပင္ေက်းဇူးတင္ရွိပါသည္။"));
 //BA.debugLineNum = 48;BA.debugLine="lblDeveloper.Text = \"ဖန္တီးသူ - ကိုေက်ာ္စြာသြင္ (ျမန္မာယူနီကြန္း)\"";
mostCurrent._lbldeveloper.setText((Object)("ဖန္တီးသူ - ကိုေက်ာ္စြာသြင္ (ျမန္မာယူနီကြန္း)"));
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _btncalc_click() throws Exception{
float _i1 = 0f;
float _i2 = 0f;
float _i3 = 0f;
int _i4 = 0;
float _a1 = 0f;
float _a2 = 0f;
float _a3 = 0f;
int _a4 = 0;
 //BA.debugLineNum = 59;BA.debugLine="Sub btnCalc_Click";
 //BA.debugLineNum = 60;BA.debugLine="If edt1.Text.Length = 0 Then";
if (mostCurrent._edt1.getText().length()==0) { 
 //BA.debugLineNum = 61;BA.debugLine="Msgbox(\"ထု ႐ိုက္ထည့္ပါ။\", \"Timber Calculator\")";
anywheresoftware.b4a.keywords.Common.Msgbox("ထု ႐ိုက္ထည့္ပါ။","Timber Calculator",mostCurrent.activityBA);
 //BA.debugLineNum = 62;BA.debugLine="Return";
if (true) return "";
 }else if(mostCurrent._edt2.getText().length()==0) { 
 //BA.debugLineNum = 64;BA.debugLine="Msgbox(\"ဗ်က္ ႐ိုက္ထည့္ပါ။\", \"Timber Calculator\")";
anywheresoftware.b4a.keywords.Common.Msgbox("ဗ်က္ ႐ိုက္ထည့္ပါ။","Timber Calculator",mostCurrent.activityBA);
 //BA.debugLineNum = 65;BA.debugLine="Return";
if (true) return "";
 }else if(mostCurrent._edt3.getText().length()==0) { 
 //BA.debugLineNum = 67;BA.debugLine="Msgbox(\"အေရအတြက္ ႐ိုက္ထည့္ပါ။\", \"Timber Calculator\")";
anywheresoftware.b4a.keywords.Common.Msgbox("အေရအတြက္ ႐ိုက္ထည့္ပါ။","Timber Calculator",mostCurrent.activityBA);
 //BA.debugLineNum = 68;BA.debugLine="Return";
if (true) return "";
 }else if(mostCurrent._edt4.getText().length()==0) { 
 //BA.debugLineNum = 70;BA.debugLine="Msgbox(\"ႏႈန္းထား ႐ိုက္ထည့္ပါ။\", \"Timber Calculator\")";
anywheresoftware.b4a.keywords.Common.Msgbox("ႏႈန္းထား ႐ိုက္ထည့္ပါ။","Timber Calculator",mostCurrent.activityBA);
 //BA.debugLineNum = 71;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 73;BA.debugLine="Dim i1 As Float = edt1.Text";
_i1 = (float)(Double.parseDouble(mostCurrent._edt1.getText()));
 //BA.debugLineNum = 74;BA.debugLine="Dim i2 As Float = edt2.Text";
_i2 = (float)(Double.parseDouble(mostCurrent._edt2.getText()));
 //BA.debugLineNum = 75;BA.debugLine="Dim i3 As Float = edt3.Text";
_i3 = (float)(Double.parseDouble(mostCurrent._edt3.getText()));
 //BA.debugLineNum = 76;BA.debugLine="Dim i4 As Int = edt4.Text";
_i4 = (int)(Double.parseDouble(mostCurrent._edt4.getText()));
 //BA.debugLineNum = 77;BA.debugLine="Dim a1 As Float";
_a1 = 0f;
 //BA.debugLineNum = 78;BA.debugLine="Dim a2 As Float";
_a2 = 0f;
 //BA.debugLineNum = 79;BA.debugLine="Dim a3 As Float";
_a3 = 0f;
 //BA.debugLineNum = 80;BA.debugLine="Select spn1.SelectedItem";
switch (BA.switchObjectToInt(mostCurrent._spn1.getSelectedItem(),"ေပ","တန္","ေခ်ာင္း")) {
case 0:
 //BA.debugLineNum = 82;BA.debugLine="a1 = i3";
_a1 = _i3;
 //BA.debugLineNum = 83;BA.debugLine="a2 = (i3 / 7200) * (i1 * i2)";
_a2 = (float) ((_i3/(double)7200)*(_i1*_i2));
 //BA.debugLineNum = 84;BA.debugLine="a3 = a1 / 18";
_a3 = (float) (_a1/(double)18);
 break;
case 1:
 //BA.debugLineNum = 86;BA.debugLine="a1 = (i3 * 7200) / (i1 * i2)";
_a1 = (float) ((_i3*7200)/(double)(_i1*_i2));
 //BA.debugLineNum = 87;BA.debugLine="a2 = i3";
_a2 = _i3;
 //BA.debugLineNum = 88;BA.debugLine="a3 = a1 / 18";
_a3 = (float) (_a1/(double)18);
 break;
case 2:
 //BA.debugLineNum = 90;BA.debugLine="a1 = i3 * 18";
_a1 = (float) (_i3*18);
 //BA.debugLineNum = 91;BA.debugLine="a2 = (a1 / 7200) * (i1 * i2)";
_a2 = (float) ((_a1/(double)7200)*(_i1*_i2));
 //BA.debugLineNum = 92;BA.debugLine="a3 = i3";
_a3 = _i3;
 break;
}
;
 //BA.debugLineNum = 94;BA.debugLine="Dim a4 As Int = a3 * (i1 * i2) * i4";
_a4 = (int) (_a3*(_i1*_i2)*_i4);
 //BA.debugLineNum = 95;BA.debugLine="Msgbox(\"စုစုေပါင္းေပ = \" & a1 & \"'\" & CRLF & \"စုစုေပါင္းတန္ = \" & a2 & \" တန္\" & CRLF & \"18' အေခ်ာင္းေပါင္း = \" & a3 & \" ေခ်ာင္း\" & CRLF & \"သင့္ေငြ = \" & a4 & \" က်ပ္\", \"Timber Calculator\")";
anywheresoftware.b4a.keywords.Common.Msgbox("စုစုေပါင္းေပ = "+BA.NumberToString(_a1)+"'"+anywheresoftware.b4a.keywords.Common.CRLF+"စုစုေပါင္းတန္ = "+BA.NumberToString(_a2)+" တန္"+anywheresoftware.b4a.keywords.Common.CRLF+"18' အေခ်ာင္းေပါင္း = "+BA.NumberToString(_a3)+" ေခ်ာင္း"+anywheresoftware.b4a.keywords.Common.CRLF+"သင့္ေငြ = "+BA.NumberToString(_a4)+" က်ပ္","Timber Calculator",mostCurrent.activityBA);
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 19;BA.debugLine="Private lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private lbl1 As Label";
mostCurrent._lbl1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private edt1 As EditText";
mostCurrent._edt1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private lbl2 As Label";
mostCurrent._lbl2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private lbl3 As Label";
mostCurrent._lbl3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private edt2 As EditText";
mostCurrent._edt2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private lbl4 As Label";
mostCurrent._lbl4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private lbl5 As Label";
mostCurrent._lbl5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private edt3 As EditText";
mostCurrent._edt3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private spn1 As Spinner";
mostCurrent._spn1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private lbl6 As Label";
mostCurrent._lbl6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private edt4 As EditText";
mostCurrent._edt4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private lbl7 As Label";
mostCurrent._lbl7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private btnCalc As Button";
mostCurrent._btncalc = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private lblSpecialThanks As Label";
mostCurrent._lblspecialthanks = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lblDeveloper As Label";
mostCurrent._lbldeveloper = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
}
