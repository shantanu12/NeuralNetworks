
public class IOArray {
	private Double[] input;
	private Double[] output;
	
	public void setInput(Double[] ip){
		this.input = new Double[ip.length];
		this.input = ip;
	}
	
	public void setOutput(Double[] op){
		this.output = new Double[op.length];
		this.output = op;
	}
	
	public Double[] getInput(){
		return this.input;
	}
	
	public Double[] getOutput(){
		return this.output;
	}
}
