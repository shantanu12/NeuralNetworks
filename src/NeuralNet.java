import java.util.ArrayList;

public class NeuralNet {
	public Double[] feedForward(ArrayList<Layer> network, Double[] input) {
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
		return network.get(network.size() - 1).getOutput(); // return the output
	}

	public void backPropagate(ArrayList<Layer> network, Double[] output, Double eta) {
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
		for (int i = 0; i < network.size(); i++) {
			Layer currentLayer = network.get(i);
			Double delta[] = currentLayer.getDelta();
			Double inputs[] = currentLayer.getInput();
			for (int j = 0; j < delta.length; j++) { // j is the column of
														// weight matrix
				Double originalWeights[] = currentLayer.getMatrixCol(j);
				Double newWeights[] = new Double[inputs.length];
				for (int k = 0; k < inputs.length; k++) { // k is the index of
															// input
					Double weightChange = eta * inputs[k] * delta[j];
					newWeights[k] = originalWeights[k] + weightChange;
				}
				currentLayer.setMatrixCol(j, newWeights);
			}
		}
	}

	public Double validate(ArrayList<Layer> network, Double[] input, Double[] expectedOutput) {
		Double error = 0.0;
		Double[] achievedOutput = feedForward(network, input);
		for (int i = 0; i < achievedOutput.length; i++) {
			error += Math.pow(achievedOutput[i] - expectedOutput[i], 2);
		}
		error /= achievedOutput.length;
		return error;
	}

	public boolean test(ArrayList<Layer> network, Double[] input, Double[] expectedOutput) {
		boolean result = false;
		Double[] achievedOutput = feedForward(network, input);
		if (achievedOutput.length == 1) {
			if (achievedOutput[0] > 0.5) {
				if (expectedOutput[0] == 1.0) {
					result = true;
				}
			} else {
				if (expectedOutput[0] == 0.0) {
					result = true;
				}
			}
		} else {
			int activatedIndex = Utils.findActivation(achievedOutput);
			if(expectedOutput[activatedIndex] == 1.0){
				result = true;
			}
		}
		return result;
	}
}
