package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class DownloadSong 
{
	public static void main(String[] args) throws Exception
	{
		URL yahoo = new URL("http://taupe.propagation.net/dishant/jukebox/playselected/playselected8114.ram");
		BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null)
		    System.out.println(inputLine);

		in.close();
	}
}
