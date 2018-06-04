package matchings;

import java.util.Arrays;
import java.util.List;

public class ESS {


	private final Integer[] allSamps;
	private final String dir;

	public ESS(String dir) {
		this.dir = dir;
		this.allSamps = SaveResults.read(dir);
	}

	public int sampleSize() {
		return allSamps.length;
	}

	public Integer[] truncate() {
		int N = this.sampleSize();
		int sqrtN = (int) Math.floor(Math.sqrt(N));
		return Arrays.copyOfRange(allSamps, 0, sqrtN*sqrtN);


	}

	public Integer[][] subDivide(Integer[] tSamps) {



		Integer newN = tSamps.length;
		this.newN = newN;

		int sqrtNewN = (int) Math.sqrt(newN);

		Integer[][] result = new Integer[sqrtNewN][sqrtNewN];


		for (int i = 0; i < sqrtNewN; i++) {
			result [i] = Arrays.copyOfRange(tSamps, i*sqrtNewN, (i+1)*sqrtNewN);
		}
		return result;
		
		
	}

	public static Double[] getI(Integer[][] s) {
		int size = s.length;
		Double[] I = new Double[size];
		for (int i = 0; i < size; i++) {
			I[i] = mean(s[i]);
		}
		return I;

	}




	public static double mean(Double[] s) {
		double sum = 0.0;
		int size = s.length;
		for (double i: s) {
			sum += i;
		}
		return sum/size;
	}

	public static double mean(Integer[] s) {
		double sum = 0.0;
		int size = s.length;
		for (double i: s) {
			sum += i;
		}
		return sum/size;
	}

	public static double var(Double[] s) {

		int size = s.length;
		Double[] d2 = new Double[size];
		for (int i = 0; i < size; i++ ) 
			d2[i] = (double) Math.pow(s[i] - mean(s), 2);

		return size*mean(d2)/(size-1);
	}

	public static double var(Integer[] s) {

		int size = s.length;
		Double[] d2 = new Double[size];
		for (int i = 0; i < size; i++ ) 
			d2[i] = (double) Math.pow(s[i] - mean(s), 2);

		return size*mean(d2)/(size-1);
	}




	public static void main(String[] args) {
		
		ESS ess;
		
		String[] files = {"nonLB5.txt", "LB5.txt"};
		
		for (String file: files) {

		ess = new ESS(file);
		
		Integer[] trunc = ess.truncate();
		
		Integer[][] subDivs = ess.subDivide(trunc);
		
		int len = subDivs.length;

		double VX = var(trunc);
		
		double VI = var(getI(subDivs));
		
		System.out.println(file);
		System.out.println("E= " + VX/VI*Math.sqrt(trunc.length));
		System.out.println("N= " + len*len);
		}
		
		
	}
	
	
	




}



