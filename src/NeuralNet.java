import java.util.ArrayList;

public class NeuralNet {
	public void feedForward(ArrayList<Layer> network, Double[] input) {
		Layer first = network.get(0);
		first.setInput(input);
		for (int i = 0; i < network.size(); i++) {
			Layer currentLayer = network.get(i);
			int outputCount = currentLayer.getMatrixColumnCount();
			Double output[] = new Double[outputCount];
			for (int j = 0; j < outputCount; j++) {
				Double[] weights = currentLayer.getMatrixCol(j);
				Double res = Utils.matrixMultiply(currentLayer.getInput(), weights);
				output[j] = Utils.sigmoid(res);
			}
			currentLayer.setOutput(output);
			if ((i + 1) != network.size()) {
				Layer next = network.get(i + 1);
				Double[] inputArray = new Double[outputCount + 1];
				for (int temp = 0; temp < outputCount; temp++) {
					inputArray[temp] = output[temp];
				}
				inputArray[outputCount] = 1.0;
				next.setInput(inputArray);
			}
		}
	}

	public void backPropagate(ArrayList<Layer> network, Double[] output) {
		// calculate error terms
		for (int i = network.size() - 1; i >= 0; i--) {
			Double[] achievedOutput = network.get(i).getOutput();
			Double[] delta = new Double[achievedOutput.length];
			if (i == (network.size() - 1)) {
				// this is the output layer
				for (int x = 0; x < achievedOutput.length; x++) {
					delta[x] = achievedOutput[x] * (1 - achievedOutput[x]) * (output[x] - achievedOutput[x]);
				}
			} else {
				// this is a hidden layer
				Double[] deltaNext = network.get(i + 1).getDelta();
				for (int x = 0; x < achievedOutput.length; x++) {
					delta[x] = achievedOutput[x] * (1 - achievedOutput[x])
							* (Utils.matrixMultiply(deltaNext, network.get(i + 1).getMatrixRow(x)));
				}
			}
			network.get(i).setDelta(delta);
		}

		// update the weight matrix
	}
}
