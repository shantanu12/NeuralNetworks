
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
}
