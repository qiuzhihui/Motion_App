package com.authorwjf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Environment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;



public class Main extends Activity implements SensorEventListener {
	
	private float mLastX, mLastY, mLastZ,mLastS;
	private boolean mInitialized,Writefile; //variable to seenif sensor is is initialized
	private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope,mGravity, mRotation_Vector;
    private final float NOISE = (float) 2.0;
    private final float NOISE1 = (float) 0.1;
    private final float NOISE2 = (float) 0.5;
    private final float NOISE3 = (float) 0.1;
    private File filepath;
	float deltaX2=0;
	float deltaY2=0;
	float deltaZ2=0;
	float array[][]=new float[3][80];
	double B[] = new double[80];
	int count=0;
	int flag=0;
	double a1=1,a2=1.2,a3=0.01;
	String status="idle";
	String laststatus[]={"idle","idle"};
	String standingcheck[]={"idle","idle"};
	protected PowerManager.WakeLock wl;
    private boolean change_flag=false;
    int change_status=0;
    int walking_value=0;
    int running_value=0;
    int falling_value=0;
    		
	
	
	
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final ArrayList<BasicNameValuePair> pairs_update = new ArrayList<BasicNameValuePair>();
    UrlEncodedFormEntity encodedEntity_status;
    HttpPost post_update = new HttpPost( "http://"+"192.168.1.3"+":8080/my_app/dbchange.jsp" );
    DefaultHttpClient client = new DefaultHttpClient();
	
	

    private DebugServer server;
    private Button button1,button2,change,walking,running,falling,plus,minus;
    String filename="random.txt";
    String filename1="gravity.txt";
    MediaPlayer fall,stand,walk,run,standup,sitdown,oncreate;


	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mInitialized = false;
        Writefile = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_GAME);
        
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_GAME);
        
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, mGravity , SensorManager.SENSOR_DELAY_GAME);
        
        mRotation_Vector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotation_Vector , SensorManager.SENSOR_DELAY_GAME);    
        
        addListenerOnButton();
        fall = MediaPlayer.create(getBaseContext(), R.raw.fall);
        stand = MediaPlayer.create(getBaseContext(), R.raw.standing);
        walk = MediaPlayer.create(getBaseContext(), R.raw.walking);
        run = MediaPlayer.create(getBaseContext(), R.raw.running);
        standup = MediaPlayer.create(getBaseContext(), R.raw.standingup);
        sitdown = MediaPlayer.create(getBaseContext(), R.raw.sittingdown);
        oncreate = MediaPlayer.create(getBaseContext(), R.raw.oncreate);
        
        oncreate.start();

        
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        





        
        
        
        
        /*
        final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
    		TextView path= (TextView)findViewById(R.id.filepath);

            	
				if(Writefile==true){
					String data=Float.toString(deltaX2) +" " + Float.toString(deltaY2) + " " + Float.toString(deltaZ2) + "\r\n";
					writeToFile(data);
					path.setText(filepath.getAbsolutePath());
					//path.setText(readFromFile());

				}
				                super.handleMessage(msg);
            }
        };
        
        class MyThread implements Runnable {
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
        		while (true) {
        			try {
        				Thread.sleep(100);// stop 0.01 seconds
        				Message message = new Message();
        				message.what = 1;
        				handler.sendMessage(message);// send message
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        	}
        }
        
		new Thread(new MyThread()).start();
*/
        
 
        //TODO: New stuff
        
      //as http client
      //  HttpClient client = new DefaultHttpClient();
      //  HttpPost post = new HttpPost("www.google.com"); //just for now
        
      //as http server using nanohttpd
        server = new DebugServer();
        try {
            server.start();
        } catch(IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
        Log.w("Httpd", "Web server initialized.");
     
    }
    
    public void addListenerOnButton() {
        button1 = (Button) findViewById(R.id.click1);
        button2 = (Button) findViewById(R.id.click2);
        walking = (Button) findViewById(R.id.walking);
        running = (Button) findViewById(R.id.running);
        falling = (Button) findViewById(R.id.falling);
        change = (Button) findViewById(R.id.change);
        plus = (Button) findViewById(R.id.plus);
        minus = (Button) findViewById(R.id.minus);


        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Writefile = true;
 			    wl.acquire();

            }
 
        });

        
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Writefile = false;

            }
 
        });
        
        
        
        walking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	change_status=1;
            }
 
        });
        
        
        running.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	change_status=2;
            }
 
        });
        
        falling.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	change_status=3;
            }
 
        });
        
        
        change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {            	
            	change_flag=(!change_flag);
            }
 
        });
        
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	if(change_flag==true){
	            	switch(change_status){
	            		case 1: {walking_value=walking_value+1;break;}
	            		case 2: {running_value=running_value+1;break;}
	            		case 3: {falling_value=falling_value+1;break;}
	            		
	            	}
            	}

            }
 
        });
        
        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	if(change_flag==true){

	            	switch(change_status){
	        		case 1: {walking_value=walking_value-1;break;}
	        		case 2: {running_value=running_value-1;break;}
	        		case 3: {falling_value=falling_value-1;break;}
	        		
	            	}
            	}
            }
 
        });
        
        
        
        
        
    }
   
    
    
    
    private void writeToFile(String filename ,String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            filepath = getFileStreamPath(filename);

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } 
    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    
    

    
    
    
    
    
    
 // DON'T FORGET to stop the server
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    protected void onResume() {
        super.onResume();
      //  mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
       // mSensorManager.unregisterListener(this);
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		TextView tvX2= (TextView)findViewById(R.id.x_axis2);
		TextView tvY2= (TextView)findViewById(R.id.y_axis2);
		TextView tvZ2= (TextView)findViewById(R.id.z_axis2);


		TextView path= (TextView)findViewById(R.id.filepath);
		TextView parameter= (TextView)findViewById(R.id.parameter);
		String content= "walking:"+walking_value +"   " + "running:"+ running_value + "   "+"falling:" + falling_value + "\r\n";

		parameter.setText(content);


		double std_value = 0,mean_value=0,max_value=0;


		ImageView iv = (ImageView)findViewById(R.id.image);
		AnalogClock clk = (AnalogClock)findViewById(R.id.analogClock1);
		
		int Type=event.sensor.getType();
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			tvX2.setText("0.0");
			tvY2.setText("0.0");
			tvZ2.setText("0.0");
			mInitialized = true;
		//	clk.setVisibility(View.VISIBLE);
		//	clk.bringToFront();
		} else {
			
			
			
			
			if(Sensor.TYPE_ACCELEROMETER==Type){
				
				float deltaX1 = Math.abs(mLastX - x);
				float deltaY1 = Math.abs(mLastY - y);
				float deltaZ1 = Math.abs(mLastZ - z);
				deltaX2 = mLastX - x;
				deltaY2 = mLastY - y;
				deltaZ2 = mLastZ - z;				
				
				
				
//				if (deltaX < NOISE) deltaX = (float)0.0;
//				if (deltaY < NOISE) deltaY = (float)0.0;
//				if (deltaZ < NOISE) deltaZ = (float)0.0;
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				tvX.setText(Float.toString(deltaX1));
				tvY.setText(Float.toString(deltaY1));
				tvZ.setText(Float.toString(deltaZ1));
				clk.setVisibility(View.VISIBLE);
				clk.bringToFront();
				
				if(Writefile==true){
					
					
					array[0][count] = deltaX2;
					array[1][count] = deltaY2;
					array[2][count]=  deltaZ2;
	
					count=(count+1)%80;
					
					if(count==79){
						for(int i=0; i<B.length ; i++){
							B[i]=Math.sqrt((float) Math.pow(array[0][i], 2) + (float) Math.pow(array[1][i], 2) + (float) Math.pow(array[2][i], 2));
						}
						
					
						//mean
						for(int i = 0; i < B.length; i++){
						   mean_value += B[i]; // this is the calculation for summing up all the values
						}
	
						 mean_value = mean_value / B.length;
						//std
						 for (int i=0; i<B.length;i++)
						 {
							 std_value = std_value + Math.pow(B[i] - mean_value, 2);
						 }
						 std_value = std_value/80;
						 std_value = Math.sqrt(std_value);
						//max
						 max_value=B[0];
						 for(int i = 1; i < B.length; i++){
							   if(B[i]>max_value){
								   max_value=B[i];
							   }
							}
						 double A1,A2,A3,A4;
						 A1=Math.sqrt( a1*Math.pow((std_value-0.1312), 2) + a2*Math.pow((mean_value-0.221), 2) + a3*Math.pow((max_value-0.7), 2) );
						 A2=Math.sqrt( a1*Math.pow((std_value-1.13-walking_value), 2) + a2*Math.pow((mean_value-1.45-walking_value), 2) + a3*Math.pow((max_value-5.74), 2) );
						 A3=Math.sqrt( a1*Math.pow((std_value-2.7-running_value), 2) + a2*Math.pow((mean_value-4.8-running_value), 2) + a3*Math.pow((max_value-18), 2) );
						 A4=Math.sqrt( a1*Math.pow((std_value-6-falling_value), 2) + a2*Math.pow((mean_value-2.5-falling_value), 2) + a3*Math.pow((max_value-48), 2) );
						 
						 double compare;
						 compare = Math.min(Math.min(A1,A2),Math.min(A3,A4));
						 
						pairs_update.clear();
						pairs_update.add(new BasicNameValuePair( "id", "88" ));
		
						 if(compare==A1){
							 status="standing";
							 pairs_update.add(new BasicNameValuePair( "status", "standing" ));

							 
						 }else if(compare==A2){
							 status="walking";
							 pairs_update.add(new BasicNameValuePair( "status", "walking" ));
		 
						 }else if(compare==A3){
							 status="running";
							 pairs_update.add(new BasicNameValuePair( "status", "running" ));

							 
						 }else if(compare==A4){
							 status="falling";
							 pairs_update.add(new BasicNameValuePair( "status", "falling" ));
	
						 }
						 
						pairs_update.add(new BasicNameValuePair( "timestamp", df.format(new Date())));


						 
						 flag=0;
						if(status!=laststatus[1]){
							 flag=1;
							 String data=status;
							 path.setText(data);


							if(status=="falling"){
								fall.start();
								path.setBackgroundColor(Color.RED); 


							}
							
							if(status=="standing"){
								stand.start();
								path.setBackgroundColor(Color.BLACK); 

							}
							
							if(status=="walking"){
								walk.start();
								path.setBackgroundColor(Color.GREEN); 

							}
							if(status=="running"){
								run.start();
								path.setBackgroundColor(Color.YELLOW); 

							}
							
						
							
							if(flag==1){
								try {
									encodedEntity_status = new UrlEncodedFormEntity( pairs_update );
									post_update.setEntity( encodedEntity_status );
									client.execute( post_update );
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							
							
							laststatus[1]=status;
							laststatus[0]=laststatus[1];
						}
						
					}
					

				}
				
				
				
				
				
				if(Writefile==true){
					if(standingcheck[0]!=standingcheck[1]){
						if(standingcheck[0]=="Vertical"&&standingcheck[1]=="Horizontal"){
							//standing up 
							if(status=="standing"){
								standup.start();
							}

						}
						if(standingcheck[0]=="Horizontal"&&standingcheck[1]=="Vertical"){
							//sitting down
							if(status=="standing"){
								sitdown.start();
							}
						}	
					}
					
				}
				
				
				
				
			/*/
				
				if(deltaZ1 > NOISE){				
					DebugServer.changeValues(deltaX1, deltaY1, deltaZ1, "Lateral",Type);
				}
				
			
				if (deltaX1 > deltaY1) {
					clk.setScaleY(1);
					clk.setScaleX(deltaX1/2);
					
					if(deltaZ1>deltaX1)
					{
						DebugServer.changeValues(deltaX1, deltaY1, deltaZ1, "Lateral",Type);
					}else
					{
						DebugServer.changeValues(deltaX1, deltaY1, deltaZ1, "Horizontal",Type);
					}
					
				} else if (deltaY1 > deltaX1) {
					clk.setScaleY(deltaY1/2);
					clk.setScaleX(1);
					
					if(deltaZ1>deltaY1)
					{
						DebugServer.changeValues(deltaX1, deltaY1, deltaZ1, "Lateral",Type);
					}else
					{
						DebugServer.changeValues(deltaX1, deltaY1, deltaZ1, "Vertical",Type);
					}
				} else {
					clk.bringToFront();			
					clk.setScaleY(1);
					clk.setScaleX(1);
				}
			
			
				
				*/
			
			}else if(Sensor.TYPE_GRAVITY==Type){
				
				
				
				if(Writefile==true){
					
					
					//String data=Float.toString(x) +" " + Float.toString(y) + " " + Float.toString(z) + "\r\n";
					//writeToFile(filename1, data);
					//path.setText(Float.toString(mAccelerometer.getMinDelay()));
					//path.setText(filepath.getAbsolutePath());
					//path.setText(readFromFile());
					
				}
				
				
				if (Math.abs(x) < NOISE2) x = (float)0.0;
				if (Math.abs(y) < NOISE2) y = (float)0.0;
				if (Math.abs(z) < NOISE2) z = (float)0.0;				
				tvX2.setText(Float.toString(x));
				tvY2.setText(Float.toString(y));
				tvZ2.setText(Float.toString(z));
				
				float compare =Math.max(Math.max(Math.abs(x),Math.abs(y)), Math.abs(z));
				if(compare == Math.abs(x)){
					DebugServer.changeValues(x, y, z, "Lateral",Type);
					standingcheck[1]=standingcheck[0];
					standingcheck[0]="Lateral";
				}
				else if(compare == Math.abs(y)){
					DebugServer.changeValues(x, y, z, "Vertical",Type);
					standingcheck[1]=standingcheck[0];
					standingcheck[0]="Vertical";
				}
				else if(compare == Math.abs(z)){
					DebugServer.changeValues(x, y, z, "Horizontal",Type);
					standingcheck[1]=standingcheck[0];
					standingcheck[0]="Horizontal";
				}

				
			}
			
			
			
		}
	}	
}