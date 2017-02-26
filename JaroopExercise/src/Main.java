import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
/**
 * I chose to import Jsoup because I needed something to help deal with the html 
 * in the log file. After researching some options Jsoup seemed like a good option 
 * to use.
 * */

/**
 * The Main class represents the command line money log programming exercise. 
 * @author  Taylor Gamache
 * @version 1.0, February 2017
 */
public class Main {
	/** Scanner to be used when ever user input is need in the command line. */
	private static Scanner scanner = new Scanner(System.in);
	
	/** The file of the log to be updated and read. */
	private static File myFile = new File("src/log.html");
	
	/** The format the money values will be presented in. */
 	private static DecimalFormat df = new DecimalFormat("0.00");

	/**
	 * Get the user's input and determine which action to do. 
	 * <p>
	 * This method has the user decide which method to choose. It asks 
	 * the user to type in the action it wants it to perform in the command line. 
	 * If it recognizes the command it will call the appropriate method. If 
	 * it does not recognize the command it will tell the user on the command line
	 * that entered command is invalid and to try again. If withdraw, deposit, or 
	 * balance are input than it will run the withdraw, deposit, or balance methods. 
	 * If Exit is input than it ends the program. 
	 * 
	 */
	private static void run() {
		System.out
				.print("Please enter in a command (Deposit, Withdraw, Balance, Exit) :");
		String cmd = scanner.nextLine();

		if (cmd.equalsIgnoreCase("Deposit")) {
			deposit();
		} else if (cmd.equalsIgnoreCase("Withdraw")) {
			withdraw();
		} else if (cmd.equalsIgnoreCase("Balance")) {
			balance();
		} else if (cmd.equalsIgnoreCase("Exit")) {
			System.exit(0);
		} else {
			System.out
					.println("Sorry, the inputed command was not recognized. Please try again.");
		}
		// for spacing purposes
		System.out.println("");
	}

	/**
	 * Process for updating a log for a deposit.  
	 * <p>
	 * This method asks the user to type in the amount to deposit in the 
	 * command line. If the amount input is a positive amount and appropriately 
	 * formatted it proceeds to update the log file. If it is not properly
	 * formatted than it tells the user that the input was invalid and than 
	 * calls itself recursively to restart the method. 
	 * 
	 * @exception IOException If the html file is not found
	 */
	private static void deposit() {
		// get the deposit amount from the user
		System.out.print("Please enter a dollar amount to deposit (0.00):");

		String dAmount = scanner.nextLine();
		if (isFormatCorrect(dAmount) == false || dAmount.contains("-")) {
			System.out
					.println("The deposit amount was incorrectly formated. Please try again.");
			deposit();
		} else {
			// update users account
			Document doc;
			try {
				// finds correct table and updates it in html log
				doc = Jsoup.parse(myFile, "utf-8");
				Element trans = doc.getElementById("transactions");
				trans.select("tbody").last().append("<tr><td>" + dAmount + "</td></tr>");

				// saves changes
				FileWriter fWriter = new FileWriter(myFile, false);
				fWriter.write(doc.html());
				fWriter.close();

			} catch (IOException e) {
				System.out.println("html file could not be found.");
			}

		}
	}

	/**
	 * Process for updating a log for a withdraw.  
	 * <p>
	 * This method asks the user to type in the amount to withdraw in the 
	 * command line. If the amount input is a positive amount and appropriately 
	 * formatted it proceeds to update the log file. If it is not properly
	 * formatted than it tells the user that the input was invalid and than 
	 * calls itself recursively to restart the method. 
	 * 
	 * @exception IOException If the html file is not found
	 */
	private static void withdraw() {
		// get the withdraw amount from the user
		System.out.print("Please enter a dollar amount to withdraw (0.00):");

		String wAmount = scanner.nextLine();
		if (isFormatCorrect(wAmount) == false || wAmount.contains("-")) {
			System.out
					.println("The withdraw amount was incorrectly formated. Please try again.");
			withdraw();
		} else {
			// update users account
			Document doc;
			try {
				//finds correct table and updates it in html log
				doc = Jsoup.parse(myFile, "utf-8");
				Element trans = doc.getElementById("transactions");
				trans.select("tbody").last().append("<tr><td>-"+wAmount+"</td></tr>");
				
				//saves changes
				FileWriter fWriter = new FileWriter(myFile, false); 
				fWriter.write(doc.html());
				fWriter.close();
				
			} catch (IOException e) {
				System.out.println("html file could not be found.");
			}
		}
	}

	/**
	 * Process for checking the balance of an account
	 * <p>
	 * This method reads the file and finds all the transaction log's data. It than
	 * adds all the data and displays the result in the command line. If the log's 
	 * file is not in the correct location than it catches the exception and displays
	 * a message telling the user the file could not be found.
	 * 
	 * @exception IOException If the html file is not found
	 */
	private static void balance() {
		Document doc;
		try {
			// Gets the transaction log's contents of html.log
			doc = Jsoup.parse(myFile, "utf-8");
			Element trans = doc.getElementById("transactions");
			// turns the log into a string and splits it up by every space
			String[] money = trans.text().split(" ");
			// adds every correctly formated entry in html.log
			// which should only be the none "Amount" values
			double total = 0.00;
			for (int i = 0; i < money.length; i++) {
				if (isFormatCorrect(money[i])) {
					total = total + Double.valueOf(money[i]);
				}
			}
			total = Math.round(total * 100.0) / 100.0;
			System.out.println("The current balance is: $" + df.format(total));

		} catch (IOException e) {
			System.out.println("html file could not be found.");
		}
	}

	/**
	 * Used for checking if input is in correct money format.
	 * <p>
	 * Takes a string input and tries to convert it into a double. If an exception
	 * is caught than the function returns false. If one is not caught than it proceeds
	 * to test whether the input has at least one number input to the left of the decimal
	 * point and 2 digits to the right. If it does than the function returns true and if
	 * not than it returns false.
	 * 
	 * @exception Exception If the input cannot be converted to a double
	 * @param input 	string to be tested
	 * @return boolean 	result of whether the string fits the desired format
	 */
	public static boolean isFormatCorrect(String input) {
		try {
			Double.parseDouble(input);
			// checks for if input has at least 1 none decimal values and 2
			// decimal values
			String[] splitter = input.split("\\.");
			if (splitter[0].length() > 0 && splitter[1].length() == 2) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Main method that runs when the program is started. Will continually do
	 * the method run.
	 */
	public static void main(String[] args) {
		while (true) {
			run();
		}
	}

}

