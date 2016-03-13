package b2c.simpleinvite.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	private PrintWriter writer;
	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy, hh:mm:ss");
	
	private static Log LOG;
	
	public static void init(File file) throws IOException{
		Log.LOG = new Log();
		Log.LOG.writer = new PrintWriter(new FileWriter(file, true));
	}
	
	public static Log getCurrent(){
		return Log.LOG;
	}
	
	public void log(String info){
		something("INFO", info);
	}
	public void error(String info){
		something("ERROR", info);
	}
	private void something(String group, String info){
		writer.println("["+dateFormat.format(new Date())+"] ["+group+"]: "+info);
		writer.flush();
	}

}
