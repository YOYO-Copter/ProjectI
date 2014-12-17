package com.Project.ioio4timto4pwm;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends IOIOActivity{
	////// Create object for widget //////
	// Declare TextView instance
	    TextView[] text_V = new TextView[4];
	    
//////////////////////CODE V.1 ///////////////////////////////////
/*
	// Declare PWM output instance
	 	PwmOutput pwm1,pwm2,pwm3,pwm4;
	// Declare analog input instance
 		AnalogInput[] AIn = new AnalogInput[4];
*/
//////////////////////////////////////////////////////////////////

//////////////////////CODE V.2 ///////////////////////////////////
	 // Declare PWM output instance
	 // Assign PWM output with port on IOIO board (PWM pins in board is 34-40 and 45-48)
	    PwmOutput[] pwm = new PwmOutput[4];
		int[] pin_pwm = {37,38,39,40};
	// Declare analog input instance
    // The IOIO board has 16 pins (pins 31-46)
		AnalogInput[] AIn = new AnalogInput[4];
		int[] pin_AIn = {43,44,45,46};
//////////////////////////////////////////////////////////////////

	    
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
        // Assign text view with widget on activity_main.xml  		
		text_V[0] = (TextView)findViewById(R.id.V1);
		text_V[1] = (TextView)findViewById(R.id.V2);
		text_V[2] = (TextView)findViewById(R.id.V3);
		text_V[3] = (TextView)findViewById(R.id.V4);				
	}
	// This class is thread for ioio board
	// control ioio board through this class 
	class Looper extends BaseIOIOLooper {
		
		
		// This function will do when application is start Connect to ioio board
    	// Like onCreate function but use with ioio board
		protected void setup() throws ConnectionLostException, InterruptedException {
				
//////////////////////CODE V.1 ///////////////////////////////////
/*
			// Assign analog input with port on IOIO board
			// The IOIO board has 16 pins (pins 31-46)
        	AIn[0] = ioio_.openAnalogInput(43);
        	AIn[1] = ioio_.openAnalogInput(44);
        	AIn[2] = ioio_.openAnalogInput(45);
        	AIn[3] = ioio_.openAnalogInput(46);
        	// Assign PWM output with port on IOIO board (pins 34-40 and 45-48)
        	// at 100 Hz frequency(1-1M Hz)
			pwm1 = ioio_.openPwmOutput(37, 100);
			pwm2 = ioio_.openPwmOutput(38, 100); 
			pwm3 = ioio_.openPwmOutput(39, 100);
			pwm4 = ioio_.openPwmOutput(40, 100);
*/
//////////////////////////////////////////////////////////////////

////////////////////// CODE V.2 ///////////////////////////////////

			for(int i = 0;i<4;i++){
				// Assign analog input with port on IOIO board
				AIn[i] = ioio_.openAnalogInput(pin_AIn[i]);
	        	// at 100 Hz frequency(1-1M Hz)
				pwm[i] = ioio_.openPwmOutput(pin_pwm[i], 100);
				}

//////////////////////////////////////////////////////////////////

			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Connect" 
                	// when android device connect with IOIO board
                    Toast.makeText(getApplicationContext(),"Connected!", Toast.LENGTH_SHORT).show();
                }        
            });
		}
		// This function will always running when device connect with ioio board
		// It use for control ioio board
		public void loop() throws ConnectionLostException, InterruptedException {
			/////////////////////////// from1/////////////////////////////
			for(int a = 0;a<4;a++){
			pwm[a].setDutyCycle(AIn[a].read());	
			}
			runOnUiThread(new Runnable() {
                public void run() {
                	try {
                		// Read analog value from input and convert to voltage
                    	// then show voltage value on text view
                		for(int a = 0;a<4;a++){
						text_V[a].setText(String.format("%.3f", AIn[a].getVoltage()) + " .V");
                		}
                		
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ConnectionLostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }        
            });
					
			/////////////////////////////////////////////////////////////
         
			
		}
		// This function will always running when device disconnected with ioio board
		public void disconnected() {
			
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Disconnected" 
                	// when android device disconnected with IOIO board
                    Toast.makeText(getApplicationContext(),"Disconnected!", Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<4;i++){
                    text_V[i].setText(String.format(" - .V"));
                    }
                }        
            });
		}
		// This function will always running when device incompatible with ioio board
		public void incompatible() {
			
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Incompatible" 
                	// when android device incompatible with IOIO board
                    Toast.makeText(getApplicationContext(),"Incompatible!", Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<4;i++){
                        text_V[i].setText(String.format(" - .V"));
                        }
                }        
            });
		}
	}

	protected IOIOLooper createIOIOLooper() {
		return new Looper();
		
	}
	
}
