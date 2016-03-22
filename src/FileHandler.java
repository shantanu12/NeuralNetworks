import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FileHandler {
	private String missingDataChar;
	public String targetAttribute;
	public int targetAttributeIndex;
	public int attributeCount;
	public int recordCount;
	public ArrayList<Integer> valuesPerAttribute;
	private ArrayList<Attribute> attributes;
	private ArrayList<Record> records;
	private ArrayList<IOArray> ipop;
	public int inputSize;
	public int outputSize;

	public ArrayList<Attribute> readAttributes(String fileName) {
		attributes = new ArrayList<Attribute>();
		valuesPerAttribute = new ArrayList<Integer>();
		attributeCount = 0;
		int lineNumber = 0;
		File file = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts[1].equals("target-attribute")) {
					targetAttribute = parts[2];
				} else if (parts[1].equals("missing-data")) {
					missingDataChar = parts[2];
				} else {
					if (parts[0].equals("discrete")) {
						valuesPerAttribute.add(parts.length - 2);
						ArrayList<String> values = new ArrayList<String>();
						for (int j = 2; j < parts.length; j++) {
							values.add(parts[j]);
						}
						attributes.add(new Attribute(parts[1], values, false));
						attributeCount++;
						if (parts[1].equals(targetAttribute)) {
							targetAttributeIndex = lineNumber - 2;
						}
					} else {
						valuesPerAttribute.add(1);
						ArrayList<String> values = new ArrayList<String>();
						values.add("number");
						attributes.add(new Attribute(parts[1], values, true));
						attributeCount++;
					}

				}
				lineNumber++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return attributes;
	}

	public ArrayList<Record> readData(String fileName) {
		records = new ArrayList<Record>();
		ArrayList<ArrayList<Integer>> missingValueMap = new ArrayList<ArrayList<Integer>>();
		for (int x = 0; x < attributeCount; x++) {
			missingValueMap.add(new ArrayList<Integer>());
		}
		recordCount = 0;
		int lineNumber = 0;
		File file = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].equals(missingDataChar)) {
						missingValueMap.get(i).add(lineNumber);
					}
				}
				ArrayList<String> values = new ArrayList<String>();
				for (int j = 0; j < parts.length; j++) {
					values.add(parts[j]);
				}
				records.add(new Record(values));
				lineNumber++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Handle missing values
		ArrayList<ArrayList<String>> approximationMap = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < valuesPerAttribute.get(targetAttributeIndex); i++) {
			ArrayList<String> temp = new ArrayList<String>(attributeCount);
			for (int j = 0; j < attributeCount; j++) {
				temp.add("?");
			}
			approximationMap.add(temp);
		}
		for (int x = 0; x < missingValueMap.size(); x++) {
			if (x == targetAttributeIndex) { // assuming the target index will
												// always have a value
				continue;
			} else {

				ArrayList<Integer> attributeXsMissingList = missingValueMap.get(x);
				for (int a = 0; a < attributeXsMissingList.size(); a++) {
					int recordIndex = attributeXsMissingList.get(a);
					Record r = records.get(recordIndex);
					String target = r.getValue(targetAttributeIndex);
					for (int b = 0; b < valuesPerAttribute.get(targetAttributeIndex); b++) {
						if (target.equals(attributes.get(targetAttributeIndex).getValue(b))) {
							if (!(approximationMap.get(b).get(x).equals("?"))) {
								// we must have found the replacement value
								// before
								r.setValue(x, approximationMap.get(b).get(x));
							} else {
								// find the approximate value by calculating
								// frequency/average
								if (!attributes.get(x).isContinuous()) {
									// frequency approximation
									int[] countKeeper = new int[valuesPerAttribute.get(x)];
									for (int t = 0; t < countKeeper.length; t++) {
										countKeeper[t] = 0;
									}
									for (Record rec : records) {
										if (rec.getValue(targetAttributeIndex).equals(target)) {
											if (!rec.getValue(x).equals("?")) {
												for (int in = 0; in < valuesPerAttribute.get(x); in++) {
													if (rec.getValue(x).equals(attributes.get(x).getValue(in))) {
														countKeeper[in]++;
													}
												}
											}
										}
									}
									int maxIndex = -1;
									int maxCount = -1;
									for (int t = 0; t < countKeeper.length; t++) {
										if (countKeeper[t] > maxCount) {
											maxCount = countKeeper[t];
											maxIndex = t;
										}
									}
									r.setValue(x, attributes.get(x).getValue(maxIndex));
									approximationMap.get(b).set(x, attributes.get(x).getValue(maxIndex));
								} else {
									// average approximation
									double sum = 0.0;
									int count = 0;
									for (Record rec : records) {
										if (rec.getValue(targetAttributeIndex).equals(target)) {
											if (!rec.getValue(x).equals("?")) {
												sum += Double.parseDouble(rec.getValue(x));
												count++;
											}
										}
									}
									double average = sum / count;
									r.setValue(x, String.valueOf(average));
									approximationMap.get(b).set(x, String.valueOf(average));
								}
							}
						}
					}
				}
			}
		}
		for (Record rec : records) {
			for (int i = 0; i < attributeCount; i++) {
				Attribute attr = attributes.get(i);
				if (attr.isContinuous()) {
					if (attr.getMax() < Double.parseDouble(rec.getValue(i))) {
						attr.setMax(Double.parseDouble(rec.getValue(i)));
					} else if (attr.getMin() > Double.parseDouble(rec.getValue(i))) {
						attr.setMin(Double.parseDouble(rec.getValue(i)));
					}
				}
			}
		}
		Collections.shuffle(records);
		return records;
	}

	public ArrayList<IOArray> normalize() {
		ipop = new ArrayList<IOArray>();
		for (int i = 0; i < records.size(); i++) {
			ArrayList<Double> ip = new ArrayList<Double>();
			ArrayList<Double> op = new ArrayList<Double>();
			for (int j = 0; j < attributeCount; j++) {
				if (j != targetAttributeIndex) {
					// create input array
					Attribute attr = attributes.get(j);
					if (!attr.isContinuous()) {
						for (int k = 0; k < valuesPerAttribute.get(j); k++) {
							if (attr.getValue(k).equals(records.get(i).getValue(j))) {
								ip.add(1.0);
							} else {
								ip.add(0.0);
							}
						}
					} else {
						Double recordVal = Double.parseDouble(records.get(i).getValue(j));
						Double min = attr.getMin();
						Double max = attr.getMax();
						Double res = (recordVal - min) / (max - min);
						ip.add(res);
					}
				} else {
					// create output array
					if (valuesPerAttribute.get(j) == 2) {
						if (records.get(i).getValue(targetAttributeIndex)
								.equals(attributes.get(targetAttributeIndex).getValue(0))) {
							op.add(1.0);
						} else {
							op.add(0.0);
						}
					} else {
						for (int k = 0; k < valuesPerAttribute.get(j); k++) {
							if (attributes.get(j).getValue(k).equals(records.get(i).getValue(j))) {
								op.add(1.0);
							} else {
								op.add(0.0);
							}
						}
					}
				}
			}
			ip.add(1.0); // bias input
			inputSize = ip.size();
			outputSize = op.size();
			IOArray obj = new IOArray();
			obj.setInput(ip.toArray(new Double[0]));
			obj.setOutput(op.toArray(new Double[0]));
			ipop.add(obj);
		}
		return ipop;
	}
}
