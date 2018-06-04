package matchings;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVParser.*;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.distribution.FDistribution;
import org.ojalgo.matrix.decomposition.MatrixDecomposition.Values;



import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.*;

import javax.sound.sampled.DataLine;
import javax.xml.crypto.Data;
import java.util.Collections;


public class Sampler {


	private final Integer usedSS, compSize, dataLength, sqrtUsedSS;
	private ArrayList<Integer> vals    = 
			new ArrayList<Integer>(),
			permNum = new ArrayList<Integer>(),
			sampleNum = new ArrayList<Integer>(),
			fData = new ArrayList<Integer>();
	
	private ArrayList<Double> I = new ArrayList<Double>();
	private String dirSamples, name, dirTimes;
	private double EES;
	private String folder;
	private double ESSpms;
	
	private final static String DIR = "results/all/";
	
	private final static String SAMPLES = "/samples/matching.csv";
	
	public static void main(String[] args) throws IOException {
		

		String[][] files = {
				{"Non Locally Optimal", "2018-05-31-12-12-43-uLsqGaqe.exec/"}, //non lb
				{"Locally optimal", "2018-05-31-12-11-17-QhY0tbKt.exec/"} //lb 				
		};

		for (String[] info: files) {

			Sampler sampler = new Sampler(info);
			
			sampler.testF();
			
			sampler.getI();
			
			sampler.getEES();
			
			sampler.getESS_per_ms();
			
			String message = String.format("%s has ESS per ms = %f", sampler.name, sampler.ESSpms);
			
			System.out.println(message);

		}
	}
	
	
	public Sampler(String[] info) throws IOException {
		this.name = info[0];
		this.folder = info[1];
		this.dirSamples = DIR + folder + SAMPLES;
		CSVParser parser = CSVParser.parse(new File(dirSamples), 
				                           StandardCharsets.UTF_8, 
				                           CSVFormat.RFC4180.withFirstRecordAsHeader());
		for (CSVRecord r: parser.getRecords()) {
			 this.vals.add(Integer.parseInt(r.get("value")));
			 this.permNum.add(Integer.parseInt(r.get("permutation_index")));
			 this.sampleNum.add(Integer.parseInt(r.get("sample")));
		}
		
		this.dataLength = vals.size(); 
		int lastInd = dataLength - 1;
		compSize = permNum.get(lastInd) + 1; //10
		int totalSS = sampleNum.get(lastInd) + 1; //1000
		sqrtUsedSS = (int) Math.sqrt(totalSS); //31
		usedSS = sqrtUsedSS*sqrtUsedSS; // 31*31 = 961
	}

	// test function: number of edges
	public void testF() {
		for (int k = 0; k < usedSS; k++) {
			List<Integer> subStr = vals.subList(k*compSize, (k+1)*compSize);
			int sum = (int) subStr.stream().filter(i -> i != BipartiteMatching.FREE).count();
			fData.add(sum);			
		}
	}
	
	public double samplingTime() throws IOException{
		this.dirTimes = DIR + this.folder + "monitoring/runningTimeSummary.tsv";
		CSVParser parser = CSVParser.parse(new File(dirTimes), 
				                           StandardCharsets.UTF_8, 
				                           CSVFormat.RFC4180.withFirstRecordAsHeader());

		List<CSVRecord> allRows = parser.getRecords();

		String row2 = allRows.get(0).get(0);

		return Double.parseDouble(row2.substring(16));
	}
	
	public void getI() {
		for (int i = 0; i < sqrtUsedSS; i++) {
				I.add(meanInt(this.fData.subList(i*sqrtUsedSS, (i+1)*sqrtUsedSS)));
			}
		}
	
	public void getEES() {
		double VX = varInt(this.fData);
		double VI = var(this.I);
		
		this.EES = this.sqrtUsedSS*VX/VI;
	}
	
	public void getESS_per_ms() throws IOException {
		this.ESSpms = this.EES / this.samplingTime();
	}
	
	
	public static double mean(List<Double> s) {
		double sum = 0.0;
		int size = s.size();
		for (double i: s) {
			sum += i;
		}
		return sum/size;
	}

	public static double meanInt (List<Integer> z) {
		double sum = 0.0;
		int size = z.size();
		for (double i: z) {
			sum += i;
		}
		return sum/size;
	}
	
	public static double var(List<Double> s) {
		
		int n = s.size();
		
		ArrayList<Double> d2 = new ArrayList<Double>();
		for (int i = 0; i < n; i++) { 
			d2.add( (double) Math.pow(s.get(i) - mean(s), 2) );
		}

		return n*mean(d2)/(n-1);
	}
	
	public static double varInt(List<Integer> s) {

		
		int n = s.size();
		
		ArrayList<Double> d2 = new ArrayList<Double>();
		for (int i = 0; i < n; i++ ) { 
			d2.add( (double) Math.pow(s.get(i) - meanInt(s), 2) );
		}

		return n*mean(d2)/(n-1);
	}


}

