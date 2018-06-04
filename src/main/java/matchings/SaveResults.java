package matchings;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

/**
 *
 * @author samy
 */
public class SaveResults {

	//    public static void main(String[] args) throws IOException {
	//         generateCsvFile();
	//         run();
	//         
	//    }
	public static Integer[] read(String fileName) {

		String	csvFile = fileName;

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";




		try {



			br = new BufferedReader(new FileReader(csvFile));


			//boolean firstTime=false;

			line = br.readLine();
			//firstTime=false;





		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String trimmedLine = line.substring(0, line.length()-2);






		String [] data = trimmedLine.split(cvsSplitBy);


		Integer[] intData = new Integer[data.length];
		int k = 0;
		for (String s: data) {
			intData[k] = Integer.decode(s);
			k++;
		}

		return intData;     
	}


	public static void generateCsvFile()
	{
		try
		{


			FileWriter writer = new FileWriter("xyu.txt",false);

			writer.append("NumConnected");
			writer.append('\n');


			//generate whatever data you want





			writer.flush();
			writer.close();

		}

		catch(IOException e)
		{
			e.printStackTrace();
		} 
	}


	public static void generateCsvFile(String fileName, String value)
	{
		try
		{

			FileWriter writer = new FileWriter(fileName,true);

			writer.append(value);


			//generate whatever data you want

			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 

	}
}
