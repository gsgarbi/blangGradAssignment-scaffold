package matchings;

public class Pair {

    private final int element0;
    private final int element1;


    public Pair(int element0, int element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public int getFirst() {
        return element0;
    }

    public int getSecond() {
        return element1;
    }
    
    public Boolean equals(Pair q) {
        return (this.getFirst() == q.getFirst()) && (this.getSecond() == q.getSecond());
    }
    
    public Pair invert() {
    	Pair inv = new Pair(this.getSecond(), this.getFirst());
    	return inv;
    }
    
    public String toString() {
        return "(" + getFirst() + ", " + getSecond() + ")";
    }

}
