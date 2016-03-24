
public class Matrix {
	Double[][] matrix;

	public Matrix(int rows, int cols) {
		this.matrix = new Double[rows][cols];
	}

	public Matrix(Matrix copy) {
		this.matrix = new Double[copy.getRowCount()][copy.getColCount()];
		for (int i = 0; i < copy.getRowCount(); i++) {
			for (int j = 0; j < copy.getColCount(); j++) {
				this.matrix[i][j] = copy.matrix[i][j];
			}
		}
	}

	public int getRowCount() {
		return this.matrix.length;
	}

	public int getColCount() {
		return this.matrix[0].length;
	}

	public Double[] getRow(int row) {
		return this.matrix[row];
	}

	public Double[] getCol(int col) {
		Double newMatrix[] = new Double[getRowCount()];
		for (int i = 0; i < getRowCount(); i++) {
			newMatrix[i] = this.matrix[i][col];
		}

		return newMatrix;
	}

	public void setCol(int col, Double[] data) {
		for (int i = 0; i < getRowCount(); i++) {
			this.matrix[i][col] = data[i];
		}
	}

	public void randomize() {
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColCount(); j++) {
				this.matrix[i][j] = Math.random();
			}
		}
	}
}
