package com.project.ioio_8o_pwm;

import java.util.concurrent.TimeUnit;

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
	// Declare pulse width of PWM output instance is 2ms
	long[] pw = {2000,2000,2000,2000,2000,2000,2000,2000};
	// Assign PWM output with port on IOIO board (PWM pins in board is 34-40 and 45-48)
	PwmOutput[] pwm = new PwmOutput[8];
	int[] pin_pwm = {34,35,36,37,38,39,40,45};
	
	
	// Declare TextView instance
	TextView Status;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);
		// Assign text view with widget on layout_main.xml 
		Status = (TextView)findViewById(R.id.Status);
						
		
	}
	// This class is thread for ioio board
	// control ioio board through this class 
	class Looper extends BaseIOIOLooper {
		// Declare PWM output instance
		PwmOutput[] PWM = new PwmOutput[8];
    	
    	
		
		
		// This function will do when application is start Connect to ioio board
    	// Like onCreate function but use with ioio board
		protected void setup() throws ConnectionLostException, InterruptedException {
						
			//////////////////////CODE V.1 ///////////////////////////////////
			/*
			 // at 50 Hz frequency	
			 //Form is PwmOutput pwm = ioio.openPwmOutput(pinNum, freq);
			PWM[0] = ioio_.openPwmOutput(34, 50);
			PWM[1] = ioio_.openPwmOutput(35, 50);
			PWM[2] = ioio_.openPwmOutput(36, 50);
			PWM[3] = ioio_.openPwmOutput(37, 50);
			PWM[4] = ioio_.openPwmOutput(38, 50);
			PWM[5] = ioio_.openPwmOutput(39, 50);
			PWM[6] = ioio_.openPwmOutput(40, 50);
			PWM[7] = ioio_.openPwmOutput(45, 50);
			
			PWM[0].setPulseWidth(pw[0]);
			PWM[1].setPulseWidth(pw[1]);
			PWM[2].setPulseWidth(pw[2]);
			PWM[3].setPulseWidth(pw[3]);
			PWM[4].setPulseWidth(pw[4]);
			PWM[5].setPulseWidth(pw[5]);
			PWM[6].setPulseWidth(pw[6]);
			PWM[7].setPulseWidth(pw[7]);
			 */
			//////////////////////////////////////////////////////////////////
			
			////////////////////// CODE V.2 ///////////////////////////////////
			// at 50 Hz frequency	
			//Form is PwmOutput pwm = ioio.openPwmOutput(pinNum, freq);
			for(int i = 0;i<8;i++){
				PWM[i] = ioio_.openPwmOutput(pin_pwm[i], 50);
				PWM[i].setPulseWidth(pw[i]);
			}
			//////////////////////////////////////////////////////////////////
			
			
			
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Connect" 
                	// when android device connect with IOIO board
                    Toast.makeText(getApplicationContext(),"Connected!", Toast.LENGTH_SHORT).show();
                    // TextView message "Status : Connected!" 
                	// when android device connect with IOIO board
                    Status.setText("Status : Connected!");
                }        
            });
		}
		// This function will always running when device connect with ioio board
		// It use for control ioio board
		public void loop() throws ConnectionLostException, InterruptedException {
		
	
//////////////////////CODE V.1 ///////////////////////////////////
// Form is pwm.setPulseWidth(pw); -- where pw is the pulse width in microseconds.
/*				PWM[0].setPulseWidth(pw[0]);
				PWM[1].setPulseWidth(pw[1]);
				PWM[2].setPulseWidth(pw[2]);
				PWM[3].setPulseWidth(pw[3]);
				PWM[4].setPulseWidth(pw[4]);
				PWM[5].setPulseWidth(pw[5]);
				PWM[6].setPulseWidth(pw[6]);
				PWM[7].setPulseWidth(pw[7]);
				Thread.sleep(10);
			*/

//////////////////////////////////////////////////////////////////

//////////////////////CODE V.2 ///////////////////////////////////
// Form is pwm.setPulseWidth(pw); -- where pw is the pulse width in microseconds.
			for(int i = 0; i < 8; i++) {
				if(i>0){
					pw[i-1] = 0 ;
					pw[i] = 2000 ;
				
				}else if(i==0){
					pw[7] = 0 ;
					pw[i] = 2000 ;			
				}
				for(int j = 0; j < 8; j++) {
				//set pulse width in microseconds
				PWM[j].setPulseWidth(pw[j]);	
				}
				TimeUnit.MICROSECONDS.sleep(2000);
		      }

//////////////////////////////////////////////////////////////////

			

		}
		// This function will always running when device disconnected with ioio board
		public void disconnected() {
			
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Disconnected" 
                	// when android device disconnected with IOIO board
                    Toast.makeText(getApplicationContext(),"Disconnected!", Toast.LENGTH_SHORT).show();
                    // TextView message "Status : Disconnected!" 
                	// when android device Disconnected with IOIO board
                    Status.setText("Status : Disconnected!");
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
                    // TextView message "Status : Incompatible!"
                	// when android device Incompatible with IOIO board
                    Status.setText("Status : Incompatible!");
                }        
            });
		}
		
		
		
	}

	protected IOIOLooper createIOIOLooper() {
		return new Looper();
		
	}
	
}



