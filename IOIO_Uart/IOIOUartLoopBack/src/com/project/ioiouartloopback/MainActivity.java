package com.project.ioiouartloopback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends IOIOActivity{
	////// Create object for widget //////
	// Declare button instance
	Button Button_Send;
	// Declare edit text instance
	EditText Text_Tx, Text_Rx;
	// Declare TextView instance
	TextView Text_TimeLB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		// Assign button with widget on layout_main.xml 
		Button_Send = (Button)findViewById(R.id.Button_Send);
        // Assign edit text with widget on layout_main.xml  
		Text_Tx = (EditText)findViewById(R.id.Text_Tx);
		Text_Rx = (EditText)findViewById(R.id.Text_Rx);
		 // Assign text view with widget on layout_main.xml 
		Text_TimeLB = (TextView)findViewById(R.id.Text_Time);
		
	}
	// This class is thread for ioio board
	// control ioio board through this class 
	class Looper extends BaseIOIOLooper {
		// Declare UART instance
    	Uart uart;
    	// Declare output stream instance
    	OutputStream out;
    	// Declare input stream instance
    	InputStream in;
    	// Declare variable startTime,EndTime
    	long startTime;
    	long EndTime;
    	// Declare Delay to wait until data transfer is complete (should more or equal 200ms at 4800 bps,70 ms at 921600 bps)
    	int  Delay = 100;
    	// This function will do when application is start Connect to ioio board
    	// Like onCreate function but use with ioio board
		protected void setup() throws ConnectionLostException, InterruptedException {
			// Assign UART with port on IOIO board at 921600 bps
        	// No parity bit and one stop bit
        	uart = ioio_.openUart(3, 4, 921600, Uart.Parity.NONE, Uart.StopBits.ONE);
        	// Get input stream data from UART
        	in = uart.getInputStream();
        	// Get output stream data from UART
        	out = uart.getOutputStream();
        	
        	runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Connect" 
                	// when android device connect with IOIO board
                    Toast.makeText(getApplicationContext(), 
                            "Connected!", Toast.LENGTH_SHORT).show();
                }        
            });
		}
		// This function will always running when device connect with ioio board
		// It use for control ioio board
		public void loop() throws ConnectionLostException, InterruptedException {
			Button_Send.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			try {
        				// Get byte data from Text_Tx
        				byte[] wrBuff = Text_Tx.getText().toString().getBytes();
        				
        				// Write data
						out.write(wrBuff);
						startTime = System.nanoTime();
						
						// Delay to wait until data transfer is complete (should more than or equal 100 ms)
						Thread.sleep(Delay);
						
						byte[] rdBuff = new byte[in.available()];
						
						// Read data
						in.read(rdBuff);
						EndTime = System.nanoTime()-startTime;
						// Convert data to string and set on Text_Rx
	                	Text_Rx.setText(new String(rdBuff));
	                	
	                	Text_TimeLB.setText("With Delay"+Integer.toString(Delay)+"ms="+Long.toString(EndTime)+" ns \n"+
	                			            "Non Delay"+Long.toString(EndTime-(Delay*(10^9)))+" ns \n");
	                	
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	});
			
		}
		// This function will always running when device disconnected with ioio board
		public void disconnected() {
			runOnUiThread(new Runnable(){
				public void run() {
					// Toast message "Disconnected" 
                	// when android device disconnected with IOIO board
                    Toast.makeText(getApplicationContext(), 
                            "Disconnected!", Toast.LENGTH_SHORT).show();
				}
			});
		}
		// This function will always running when device incompatible with ioio board
		public void incompatible() {
			runOnUiThread(new Runnable(){
				public void run() {
					// Toast message "Incompatible" 
                	// when android device incompatible with IOIO board
                    Toast.makeText(getApplicationContext(), 
                            "Incompatible!", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	protected IOIOLooper createIOIOLooper() {
		return new Looper();
		
	}
	
}
