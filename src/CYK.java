
import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class CYK {
	/* Table for the CYK algorithm */
	private static ArrayList<String>[][] cykTable;
	/*
	 * Hash Map to store non_terminal mappings of the form S-->TA, it stores it
	 * in the form of Key: TA, Value: [S,A] etc..
	 */
	public HashMap<String, ArrayList<String>> non_terminals = new HashMap<String, ArrayList<String>>();
	/*
	 * Hash Map to store terminal mappings of the form B-->a stores the values
	 * in the form Key: a, value: [S,A]
	 */
	public HashMap<Character, ArrayList<String>> terminals = new HashMap<Character, ArrayList<String>>();
	/*
	 * start symbol, it is always considered to be the first symbol of the
	 * grammar.
	 */
	public static String startSymbol;

	/*Stores the alpbahet for regex matching of input string.*/
	public static String terminals_string = "";

	/* Grammar File path */
	public static String GrammarFilePath = "Grammar.txt";
	/* Strings to be parsed file path */
	public static String StringFilePath = "Strings.txt";
	/* Output of the parsed strings. */
	public static String OutputFilePath = "StringsResult.txt";

	/* Array list to store the srings. */
	public static ArrayList<String> strings = new ArrayList<String>();

	/* Array List to store string results */
	public static ArrayList<String> stringResults = new ArrayList<String>();

	/*
	 * Function to parse the grammar file to populate the grammar into above
	 * variables. The mapping is as follows terminal:Productions leading to the
	 * terminal example a=[A,C] non-terminal string i.e. TA or BA etc to the
	 * productions leading to it example:AB=[S, T]
	 */
	public void processGrammarFile() throws Exception {
		File grammarFile = null;
		Scanner scanner = null;
		try {
			grammarFile = new File(GrammarFilePath);
			scanner = new Scanner(grammarFile);
			String[] line = scanner.nextLine().split(":");
			startSymbol = line[0];
			do {
				String variable = line[0];
				if ((line[1].length() == 1)) {
					char terminal = line[1].charAt(0);
					if (!Character.isLowerCase(terminal)) {
						throw new Exception("Invalid grammar: Unit Transaction");
					} else {
						if (terminals.containsKey(terminal)) {
							terminals.get(terminal).add(variable);
						} else {
							ArrayList<String> l = new ArrayList<String>();
							l.add(variable);
							terminals.put(terminal, l);
							terminals_string = terminals_string + terminal;
						}
						
					}

				} else {
					String non_terminal = line[1];
					if (Character.isLowerCase(non_terminal.charAt(0))) {
						throw new Exception("Invalid grammar");
					} else {
						if (non_terminals.containsKey(non_terminal)) {
							non_terminals.get(non_terminal).add(variable);
						} else {
							ArrayList<String> l = new ArrayList<String>();
							l.add(variable);
							non_terminals.put(non_terminal, l);
						}
					}
				}
				if (scanner.hasNextLine())
					line = scanner.nextLine().split(":");
				else
					line = null;
			} while (line != null);
			scanner.close();
			System.out.println(terminals_string);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Function to parse the string file to populate the strings into above
	 * variables.
	 */
	public void parseStringFile() throws Exception {
		File stringsFile = null;
		Scanner scanner = null;
		try {
			stringsFile = new File(StringFilePath);
			scanner = new Scanner(stringsFile);
			String[] line = scanner.nextLine().split("=");
			do {
				String string = line[1];
				strings.add(string);
				if (scanner.hasNextLine())
					line = scanner.nextLine().split("=");
				else
					line = null;
			} while (line != null);
			File results = new File(OutputFilePath);
			if (!results.exists()) {
				results.createNewFile();
			}
			FileWriter fop = new FileWriter(results.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fop);

			writer.close();
		} catch (Exception ex) {
			throw ex;
		}
	}

	/*
	 * Function to parse the string and create the CYK table and check if the
	 * string is accepted or not. The String is accepted if the CYK table ends
	 * with the Start symbol.
	 */
	public boolean parseString(String w) {
		int length = w.length();
		int itr = 0;
		int diag = 0;
		cykTable = new ArrayList[length][];
		for (int i = 0; i < length; ++i) {
			char letter = w.charAt(i);
			ArrayList<String> value = terminals.get(letter);
			cykTable[i] = new ArrayList[length];
			for (int j = 0; j < length; ++j) {
				cykTable[i][j] = new ArrayList<String>();
			}
			cykTable[i][i] = value;
		}
		/*
		 * Populating the CYK table diagonally. [A, C] [A] [A] [S, T] [S] [S]
		 * [A, C] [A] [S, T] [S] [S] [A, C] [S, T] [S] [S] [S, B] [S] [S] [A, C]
		 * [A] [A, C]
		 */
		while (itr < length) {
			for (int i = 0; i + diag < length; i++) {
				int j = i + diag;
				for (int k = j, l = j - 1; k > i && l >= i; k--, l--) {
					ArrayList<String> presentValue = cykTable[i][j];
					ArrayList<String> val2 = cykTable[k][j];
					ArrayList<String> val1 = cykTable[i][l];
					for (String s1 : val1) {
						for (String s2 : val2) {
							String val = s1 + s2;
							if (non_terminals.containsKey(val)) {
								presentValue.addAll(non_terminals.get(val));
							}
						}
					}
				}
			}
			diag++;
			itr++;
		}
		/* Code to print the CYK table uncomment if required. */
		/*
		 * for (int i = 0; i < length; i++) { for (int k = 0; k < i; k++) {
		 * System.out.print("\t"); } for (int j = i; j < length; j++) {
		 * System.out.print(cykTable[i][j] + "\t");
		 * 
		 * } System.out.println(""); }
		 */
		/* Checking for the start symbol */
		if (cykTable[0][length - 1].contains(startSymbol)) {
			return true;
		}
		return false;
	}

	public static void main(String args[]) throws Exception {
		CYK objCYK = new CYK();
		try {
			objCYK.processGrammarFile();
			objCYK.parseStringFile();
			File results = new File(OutputFilePath);
			if (!results.exists()) {
				results.createNewFile();
			}
			FileWriter fop = new FileWriter(results.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fop);
			writer.write("Grammar Description: ");
			writer.newLine();
			writer.write("Terminal Rules: " + objCYK.terminals.toString());
			writer.newLine();
			writer.write("Non Terminal Rules: " + objCYK.non_terminals.toString());
			writer.newLine();
			writer.write("Input Strings: ");
			writer.newLine();
			writer.write("Strings=" + strings.toString());
			writer.newLine();
			writer.write("Output: ");
			writer.newLine();
			for (String input : strings) {
				/*Checking if the string contains characters out of the input symbol.*/
				if (!input.matches("[" + terminals_string + "]+")) {
					writer.write(input + ": Invalid String");
					writer.newLine();
				} else {
					boolean result = objCYK.parseString(input);
					String output;
					if (result) {
						stringResults.add("Accepted");
						writer.write(input + ": Accepted");
					} else {
						stringResults.add("Not Accepted");
						writer.write(input + ": Not Accepted");
					}
					writer.newLine();
				}
			}

			writer.close();
			System.out.println("Done");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			throw ex;
		}
	}

}
