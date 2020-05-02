package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ArrayDisplay {
	public static String display(List<String> array) {
		if (array.isEmpty()) {
			return "None";
		}
		StringBuilder temp = new StringBuilder();
		//temp.append("[");
		for (Object current : array) {
			temp.append(current);
			temp.append(", ");
		}
		return temp.toString().substring(0, temp.toString().length() - 2); // + "]";
	}

	public static String tagDisplay(List<String> array) {
		StringBuilder temp = new StringBuilder();
		for (Object current : array) {
			temp.append("[");
			temp.append(current);
			temp.append("]");
		}
		return temp.toString();
	}

	public static String logDisplay(List<String> array) {
		StringBuilder temp = new StringBuilder();
		for (Object current : array) {
			temp.append(current);
			temp.append("\n");
		}
		return temp.toString();
	}

	public static String sDisplay(List<String> array) {
		StringBuilder temp = new StringBuilder();
		String s = read("C:\\Users\\sting\\Desktop\\Appender.txt");
		for (Object current : array) {
			temp.append(s);
			temp.append(current);
			temp.append("\n");
		}
		return temp.toString();
	}

	private static String read(String fileName) {
		try {
			String readout;
			readout = new String(Files.readAllBytes(Paths.get(fileName)));
			return readout;
		} catch (Exception e) {
			System.out.println("File not found");
			return null;
		}
	}
}
