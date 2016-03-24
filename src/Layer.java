
public class Layer {
	private Double[] input;
	private Double[] output;
	private Matrix weightMatrix;
	private Double[] delta;

	public Layer(int rows, int cols) {
		this.weightMatrix = new Matrix(rows, cols);
		this.weightMatrix.randomize();
		this.input = new Double[rows];
		this.output = new Double[cols];
		this.delta = new Double[cols];
	}

	public Layer(Layer copy) {
		this.weightMatrix = new Matrix(copy.weightMatrix);
		this.input = new Double[copy.input.length];
		this.output = new Double[copy.output.length];
	}

	public void setInput(Double[] in) {
		this.input = in;
	}

	public Double[] getInput() {
		return this.input;
	}

	public void setOutput(Double[] out) {
		this.output = out;
	}

	public Double[] getOutput() {
		return this.output;
	}

	public void setDelta(Double[] del) {
		this.delta = del;
	}

	public Double[] getDelta() {
		return this.delta;
	}

	public int getMatrixColumnCount() {
		return this.weightMatrix.getColCount();
	}

	public Double[] getMatrixCol(int col) {
		return this.weightMatrix.getCol(col);
	}

	public void setMatrixCol(int col, Double[] data) {
		this.weightMatrix.setCol(col, data);
	}

	public Double[] getMatrixRow(int row) {
		return this.weightMatrix.getRow(row);
	}
}
