import java.util.ArrayList;

public class Utils {
	public static Double matrixMultiply(Double[] a, Double[] b) {
		Double res = 0.0;
		for (int i = 0; i < a.length; i++) {
			res += a[i] * b[i];
		}
		return res;
	}

	public static Double sigmoid(Double in) {
		Double out = 0.0;
		out = 1.0 / (1 + Math.pow(Math.E, (-1 * in)));
		return out;
	}

	public static int findActivation(Double[] output) {
		int index = -1;
		Double best = Double.MIN_VALUE;
		for (int i = 0; i < output.length; i++) {
			if (output[i] > best) {
				best = output[i];
				index = i;
			}
		}
		return index;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static void calculateStats(ArrayList<Double> accuracy, ArrayList<Integer> epochs) {
		int epochSum = 0;
		for (int i : epochs) {
			epochSum += i;
		}
		int meanEpochs = epochSum / 10;
		System.out.println("Mean Epochs to convergence: " + meanEpochs);
		Double sumAccuracy = 0.0;
		Double sumError = 0.0;
		for (Double d : accuracy) {
			sumAccuracy += d;
			sumError += 100 - d;
		}
		Double meanAccuracy = round(sumAccuracy / 10, 2);
		System.out.println("Mean Accuracy: " + meanAccuracy + "%");
		Double meanError = round(sumError / 10, 2);
		Double sum = 0.0;
		for (Double d : accuracy) {
			sum += Math.pow((100 - d - meanError), 2);
		}
		double standardDev = round(Math.sqrt(sum / 10), 2);
		System.out.println("Standard Deviation: " + standardDev + "%");
		double standardError = round(standardDev / Math.sqrt(10), 2);
		System.out.println("Standard Error: " + standardError + "%");
		double CIBound = round(2.23 * standardError, 2);
		System.out.println("Confidence Interval: " + meanAccuracy + " +- " + CIBound);
	}
}
