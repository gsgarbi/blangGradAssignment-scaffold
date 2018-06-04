package matching;

import java.util.List;

import matchings.BipartiteMatching;

public class ESS {
	
	List<Integer> connections;
	
	public ESS(List<Integer> samples) {
		this.connections = samples;
	}
	
	public int numConnected() {
		int numCon = 0;
		for (int i: this.connections){
			if (! (i == BipartiteMatching.FREE))
				numCon += 1;			
	}
		return numCon;
	}
	
	public int sampleSize() {
		return this.connections.size();
	}
	
	public void subdivide() {
		for (int i = 0; i < Math.sqrt(sampleSize()); i++) {
			;
		}
	}
}
		
