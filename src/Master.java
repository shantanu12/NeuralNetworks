import java.util.ArrayList;

public class Master {

	public static void main(String[] args) {
		String dataset = "voting";
		int hidden = Integer.parseInt("2");
		int nodes = Integer.parseInt("5");
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

		int split = ipop.size() / 10;
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

			int epoch = 1;
			NeuralNet ANN = new NeuralNet();
			for (int e = 0; e < 1000; e++) { // TODO: change this
				for (IOArray data : trainingData) {
					ANN.feedForward(network, data.getInput());
					ANN.backPropagate(network, data.getOutput());
				}
			}
		}
	}
}
