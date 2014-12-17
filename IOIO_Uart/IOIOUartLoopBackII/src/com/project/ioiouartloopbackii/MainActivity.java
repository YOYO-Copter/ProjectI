package com.project.ioiouartloopbackii;

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
		Button Button_Start;
		// Declare edit text instance
		EditText Text_Byte, Text_Delay, Text_Times;
		// Declare TextView instance
		TextView Text_True,Text_False,Text_Av,TextView_Send,TextView_Recive,Text_Fin,Text_Per;
		char test = 'z'; // character for test
		String Text_Sent =""; // Text for send
		String Text_Recive = ""; // Text for recive
		String Text_Test = ""; // Check text between Sent and Recive
		int Count_True = 0 ; // Count true
		int Count_False = 0 ; // Count false
		int uart_pinNumIn = 3 ;	// pin for Uart Input
		int uart_pinNumOut = 4 ; // pin for Uart Output
		int baud_rate = 115200 ; // Uart budrate for Uart
		
		int Num_Byte,Num_Times ;
		long Num_Delay;
		
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		// Assign button with widget on activity_main.xml 
		Button_Start = (Button)findViewById(R.id.Button);
        // Assign edit text with widget on activity_main.xml  
		Text_Byte = (EditText)findViewById(R.id.Text_Byte);
		Text_Times = (EditText)findViewById(R.id.Text_Times);
		Text_Delay = (EditText)findViewById(R.id.Text_Delay);
		 // Assign text view with widget on activity_main.xml 
		Text_Fin = (TextView)findViewById(R.id.Text_Fin);
		Text_Per = (TextView)findViewById(R.id.Text_Per);
		Text_True = (TextView)findViewById(R.id.Text_True);
		Text_False = (TextView)findViewById(R.id.Text_False);
		Text_Av = (TextView)findViewById(R.id.Text_Av);
		TextView_Send = (TextView)findViewById(R.id.TextView_Send);
		TextView_Recive = (TextView)findViewById(R.id.TextView_Recive);
		
				
		
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

		
		
		
		// This function will do when application is start Connect to ioio board
    	// Like onCreate function but use with ioio board
		protected void setup() throws ConnectionLostException, InterruptedException {
			// Assign UART with port on IOIO board at 115200 bps
        	// No parity bit and one stop bit
        	uart = ioio_.openUart(uart_pinNumIn, uart_pinNumOut, baud_rate, Uart.Parity.NONE, Uart.StopBits.ONE);
        	// Get input stream data from UART
        	in = uart.getInputStream();
        	// Get output stream data from UART
        	out = uart.getOutputStream();
        	
      
			
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
			Button_Start.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			try {
        				ConverseText();
        				Create_String();
        				startTime = System.nanoTime();
        				for(int i = 0;i< Num_Times;i++){
        				// Get byte data from Text_Tx
        				byte[] wrBuff = Text_Sent.getBytes();
        				
        				// Write data
						out.write(wrBuff);
						
						
						// Delay to wait until data transfer is complete (should more than or equal 100 ms)
						Thread.sleep(Num_Delay);
						 
						byte[] rdBuff = new byte[in.available()];
						// Read data
						in.read(rdBuff);
						Text_Recive = new String(rdBuff);
						
						// Check text between Sent and Recive if true type 'T',false type 'F'
						if(Text_Sent.equals(Text_Recive)){
							Count_True++;
							Text_Test = Text_Test + 'T';
						}else{
							Count_False++;
							Text_Test = Text_Test + 'F';
						}
						
						Text_Fin.setText(String.valueOf(i+1));
						Text_Per.setText(String.valueOf(((i+1)*100)/Num_Times)+" %");
						
        				}
        				EndTime = (System.nanoTime()-startTime)/(Long.parseLong(Text_Times.getText().toString(),10)*1000);
         				Text_True.setText(String.valueOf(Count_True)+"  "+String.valueOf((Count_True*100)/Num_Times)+" %");
        				Text_False.setText(String.valueOf(Count_False)+"  "+String.valueOf((Count_False*100)/Num_Times)+" %");
        				TextView_Send.setText(Text_Sent);
        			//	TextView_Recive.setText(Text_Recive);
        				TextView_Recive.setText(Text_Test);
        				Text_Av.setText(String.valueOf(EndTime)+" us,All = "+ String.valueOf(((System.nanoTime()-startTime)/(1000000000)))+" s");
        			
        				
        				reset();
        				
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
			reset();
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Disconnected" 
                	// when android device disconnected with IOIO board
                    Toast.makeText(getApplicationContext(),"Disconnected!", Toast.LENGTH_SHORT).show();
                }        
            });
		}
		// This function will always running when device incompatible with ioio board
		public void incompatible() {
			reset();
			
			runOnUiThread(new Runnable() {
                public void run() {
                	// Toast message "Incompatible" 
                	// when android device incompatible with IOIO board
                    Toast.makeText(getApplicationContext(),"Incompatible!", Toast.LENGTH_SHORT).show();
                }        
            });
		}
		
		public void ConverseText() {
			// for convert text type
			Num_Byte = Integer.parseInt(Text_Byte.getText().toString());
			Num_Times = Integer.parseInt(Text_Times.getText().toString());
			Num_Delay = Long.parseLong(Text_Delay.getText().toString(),10);
			
			}
		
		public void Create_String() { // create string for send
		for(int i = 0;i< Num_Byte ;i++){
			Text_Sent = Text_Sent + test;
		}
		}
		public void reset() { // reset  
			Count_True = 0 ;
			Count_False = 0 ;
			Text_Sent = "";
			Text_Recive = "";
			Text_Test = "";
		}
	}

	protected IOIOLooper createIOIOLooper() {
		return new Looper();
		
	}
	
}
