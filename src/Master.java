import java.util.ArrayList;

public class Master {

	public static void main(String[] args) {
		String dataset = "voting";
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
			break;
		case "banknote":
			attributes = reader.readAttributes("banknote_authentication_dataset_attributes.data");
			records = reader.readData("banknote_authentication_dataset_data.data");
			break;
		case "credit":
			attributes = reader.readAttributes("credit_approval_dataset_attributes.data");
			records = reader.readData("credit_approval_dataset_data.data");
			break;
		}
		System.out.println("Test");
	}

}
