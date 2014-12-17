package com.project.ioiotimtopwm;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends IOIOActivity{
	////// Create object for widget //////

	// Declare togglebutton instance
	ToggleButton toggleOnOff;
	// Declare TextView instance
	TextView text_V,text_St;
	// Declare PWM output instance
	PwmOutput pwm;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		// Assign togglebutton with widget on layout_main.xml 
		toggleOnOff = (ToggleButton)findViewById(R.id.toggleOnOff);
        // Assign text view with widget on layout_main.xml 
		text_V = (TextView)findViewById(R.id.text_V);
		text_St = (TextView)findViewById(R.id.text_St);
	
				
		
	}
	// This class is thread for ioio board
	// control ioio board through this class 
	class Looper extends BaseIOIOLooper {
		// Declare analog input instance
    	AnalogInput AIn;
    	
		
		// This function will do when application is start Connect to ioio board
    	// Like onCreate function but use with ioio board
		protected void setup() throws ConnectionLostException, InterruptedException {
			// Assign analog input with port on IOIO board
        	AIn = ioio_.openAnalogInput(31);
        	// Assign PWM output with port on IOIO board (pins 34-40 and 45-48)
        	// at 100 Hz frequency(1-1M Hz)
			pwm = ioio_.openPwmOutput(34, 100);
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Connect" 
                	// when android device connect with IOIO board
                    Toast.makeText(getApplicationContext(),"Connected!", Toast.LENGTH_SHORT).show();
                    text_St.setText("Connected!");
                }        
            });
		}
		// This function will always running when device connect with ioio board
		// It use for control ioio board
		public void loop() throws ConnectionLostException, InterruptedException {
			if(toggleOnOff.isChecked()){
				// Read analog value from input and convert from 0-3.3v to dutyCycle 0-100%
				pwm.setDutyCycle(AIn.read());						
				runOnUiThread(new Runnable() {
	                public void run() {
	                	try {
	                		// Read analog value from input and convert to voltage
	                    	// then show voltage value on text view
							text_V.setText(String.format("%.3f", AIn.getVoltage()) + " V");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ConnectionLostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }        
	            });
			}else{
				
				pwm.setDutyCycle(0.00f);						
				runOnUiThread(new Runnable() {
	                public void run() {
	                	// Read analog value from input and convert to voltage
						// then show voltage value on text view
						text_V.setText("0.0 V");
	                }        
	            });
			}
			
			
			
			 //Delay time 100 milliseconds
			Thread.sleep(100);
		}
		// This function will always running when device disconnected with ioio board
		public void disconnected() {
			try {
				pwm.setDutyCycle(0.00f);
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
				
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Disconnected" 
                	// when android device disconnected with IOIO board
                    Toast.makeText(getApplicationContext(),"Disconnected!", Toast.LENGTH_SHORT).show();
                    text_St.setText("Disconnected!");
                    // Read analog value from input and convert to voltage
					// then show voltage value on text view
					text_V.setText("0.0 V");
                }        
            });
		}
		// This function will always running when device incompatible with ioio board
		public void incompatible() {
			try {
				pwm.setDutyCycle(0.00f);
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Incompatible" 
                	// when android device incompatible with IOIO board
                    Toast.makeText(getApplicationContext(),"Incompatible!", Toast.LENGTH_SHORT).show();
                    text_St.setText("Incompatible!");
                    // Read analog value from input and convert to voltage
					// then show voltage value on text view
					text_V.setText("0.0 V");
                }        
            });
		}
	}

	protected IOIOLooper createIOIOLooper() {
		return new Looper();
		
	}
	
}
