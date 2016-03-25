import java.util.ArrayList;

public class Master {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String dataset = args[0];
		int hidden = Integer.parseInt(args[1]);
		int nodes = Integer.parseInt(args[2]);
		Double learningRate = Double.parseDouble(args[3]);
		FileHandler reader = new FileHandler();
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		ArrayList<Record> records = new ArrayList<Record>();
		ArrayList<IOArray> ipop = new ArrayList<IOArray>();
		switch (dataset) {
		case "voting":
			attributes = reader.readAttributes("voting_dataset_attributes.data");
			records = reader.readData("voting_dataset_data.data");
			ipop = reader.normalize();
			break;
		case "iris":
			attributes = reader.readAttributes("iris_dataset_attributes.data");
			records = reader.readData("iris_dataset_data.data");
			ipop = reader.normalize();
			break;
		case "tictactoe":
			attributes = reader.readAttributes("tictactoe_dataset_attributes.data");
			records = reader.readData("tictactoe_dataset_data.data");
			ipop = reader.normalize();
			break;
		case "banknote":
			attributes = reader.readAttributes("banknote_authentication_dataset_attributes.data");
			records = reader.readData("banknote_authentication_dataset_data.data");
			ipop = reader.normalize();
			break;
		case "credit":
			attributes = reader.readAttributes("credit_approval_dataset_attributes.data");
			records = reader.readData("credit_approval_dataset_data.data");
			ipop = reader.normalize();
			break;
		}

		System.out.println("# of hidden layers: " + hidden);
		System.out.println("# of hidden nodes: " + nodes);
		System.out.println("learning rate: " + learningRate);

		int split = ipop.size() / 10;
		ArrayList<Double> accuracy = new ArrayList<Double>();
		ArrayList<Integer> epochs = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			ArrayList<IOArray> buildData = new ArrayList<IOArray>();
			ArrayList<IOArray> trainingData = new ArrayList<IOArray>();
			ArrayList<IOArray> validationData = new ArrayList<IOArray>();
			ArrayList<IOArray> testingData = new ArrayList<IOArray>();
			if ((i != 0) && (i != 9)) {
				for (int j = 0; j < i * split; j++) {
					buildData.add(ipop.get(j));
				}
				for (int j = (i + 1) * split; j < ipop.size(); j++) {
					buildData.add(ipop.get(j));
				}
			} else if (i == 0) {
				for (int j = (i + 1) * split; j < ipop.size(); j++) {
					buildData.add(ipop.get(j));
				}
			} else {
				for (int j = 0; j < i * split; j++) {
					buildData.add(ipop.get(j));
				}
				if ((i + 1) * split < ipop.size()) {
					for (int j = (i + 1) * split; j < ipop.size(); j++) {
						buildData.add(ipop.get(j));
					}
				}
			}
			for (int j = i * split; j < (i + 1) * split; j++) {
				testingData.add(ipop.get(j));
			}
			for (int a = 0; a < (int) (0.7 * buildData.size()); a++) {
				trainingData.add(buildData.get(a));
			}
			for (int a = (int) (0.7 * buildData.size()); a < buildData.size(); a++) {
				validationData.add(buildData.get(a));
			}

			// build the neural network using the parameters : # of hidden
			// layers & nodes per layer
			ArrayList<Layer> network = new ArrayList<Layer>();
			int intialIn = reader.inputSize;
			int finalout = reader.outputSize;
			int ip = intialIn;
			int op = finalout;
			for (int layer = 0; layer <= hidden; layer++) {
				if (layer == hidden) {
					network.add(new Layer(ip, op));
				} else {
					op = nodes;
					network.add(new Layer(ip, op));
					ip = op + 1; // +1 for bias for next layer
					op = finalout;
				}
			}

			NeuralNet ANN = new NeuralNet();
			ArrayList<Layer> bestNetwork = new ArrayList<Layer>();
			Double mse = 0.0;
			Double bestMSE = Double.MAX_VALUE;
			int bestEpoch = 0;
			int epoch = 1;
			do {
				for (IOArray data : trainingData) {
					ANN.feedForward(network, data.getInput());
					ANN.backPropagate(network, data.getOutput(), learningRate);
				}
				Double error = 0.0;
				for (IOArray data : validationData) {
					error += ANN.validate(network, data.getInput(), data.getOutput());
				}
				mse = error / validationData.size();
				if (mse < bestMSE) {
					bestMSE = mse;
					bestEpoch = epoch;
					bestNetwork = new ArrayList<Layer>();
					for (int l = 0; l < network.size(); l++) {
						bestNetwork.add(new Layer(network.get(l)));
					}
				}
				epoch++;
			} while (epoch < 100);

			do {
				for (IOArray data : trainingData) {
					ANN.feedForward(network, data.getInput());
					ANN.backPropagate(network, data.getOutput(), learningRate);
				}
				Double error = 0.0;
				for (IOArray data : validationData) {
					error += ANN.validate(network, data.getInput(), data.getOutput());
				}
				mse = error / validationData.size();
				if (mse < bestMSE) {
					bestMSE = mse;
					bestEpoch = epoch;
					bestNetwork = new ArrayList<Layer>();
					for (int l = 0; l < network.size(); l++) {
						bestNetwork.add(new Layer(network.get(l)));
					}
				}
				epoch++;
			} while (epoch <= 5000 && mse <= bestMSE);

			int firstBreak = epoch;
			while (epoch < 2 * firstBreak) {
				for (IOArray data : trainingData) {
					ANN.feedForward(network, data.getInput());
					ANN.backPropagate(network, data.getOutput(), learningRate);
				}
				Double error = 0.0;
				for (IOArray data : validationData) {
					error += ANN.validate(network, data.getInput(), data.getOutput());
				}
				mse = error / validationData.size();
				if (mse < bestMSE) {
					bestMSE = mse;
					bestEpoch = epoch;
					bestNetwork = new ArrayList<Layer>();
					for (int l = 0; l < network.size(); l++) {
						bestNetwork.add(new Layer(network.get(l)));
					}
				}
				epoch++;
			}
			System.out.println("Iteration " + (i + 1));
			System.out.println("	Epochs to convergence: " + bestEpoch);
			epochs.add(bestEpoch);
			// test the network
			int passed = 0;
			for (IOArray data : testingData) {
				if (ANN.test(bestNetwork, data.getInput(), data.getOutput())) {
					passed++;
				}
			}
			Double correctness = Utils.round(passed / (double) testingData.size() * 100, 2);
			accuracy.add(correctness);
			System.out.println("	Accuracy: " + correctness);
		}
		System.out.println("----------OVERALL----------");
		Utils.calculateStats(accuracy, epochs);
	}
}
