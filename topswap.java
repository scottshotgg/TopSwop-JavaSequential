/****

	Made by Scott C. Gaydos using Java in jGrasp on 10/12/2013 at approximately 3PM.
	
	TopSwap takes a string of size determined in here and creates an array with one
	through that value. It then randomizes the order of those elements. After the 
	randomizing, it creates  the next permutation based on the first number of the 
	current permutation by taking the first number and swapping that many numbers 
	with each other. The process is halted when a one is the first number in the 
	permutation. While doing this the program also counts the amount of permutations 
	created while swapping and at the end displays this along with the amount of 
	time that it took for the operator.
   
   // **** OUTDATED **** \\ but mostly correct
	
****/

package topswap;

import java.util.Random;
import java.io.*;
import java.util.Scanner;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class topswap
{
	public static void main(String args[])
	{
		MyFrame frame = new MyFrame();
	}
}

class MyFrame extends JFrame     //this doesnt actually do anything yet
{
   private JLabel label;
   private JButton button;
   private JTextField threadsTextfield;
   private JTextField valuesTextfield;
   private JTextField runsTextfield;
   private JTextField trialsTextfield;
   private JLabel label1;
   private JLabel BLANK_label;
   
   int NUMBER_OF_THREADS;
   int NUMBER_OF_VALUES;				//use this value to set the amount of numbers you want
	int NUMBER_OF_RUNS;
	int NUMBER_OF_TRIALS;				//max int value is "Integer.MAX_VALUE"
   int count = 0;
         
	public MyFrame()
	{ 
	   label = new JLabel();
      label1 = new JLabel();
      BLANK_label = new JLabel();
      valuesTextfield = new JTextField("Enter permutation length here");
      threadsTextfield = new JTextField("Enter amount of threads here");
      runsTextfield = new JTextField("Enter amount of runs");
      trialsTextfield = new JTextField("Enter amount of trials here");
      button = new JButton("Run");
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new GridLayout(2, 5, 10, 10));
      add(valuesTextfield);
      add(threadsTextfield);
      add(runsTextfield);
      add(trialsTextfield);
      add(label1);
      add(label);
      add(BLANK_label);
      add(button);
      show();
      setSize(750, 200);
      setTitle("TopSwap Exaustion Program");

      event e = new event();
      button.addActionListener(e);
	}
   class event implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         try
         {
            NUMBER_OF_VALUES = Integer.parseInt(valuesTextfield.getText());         
            NUMBER_OF_THREADS = Integer.parseInt(threadsTextfield.getText());
            NUMBER_OF_RUNS = Integer.parseInt(runsTextfield.getText());
            NUMBER_OF_TRIALS = Integer.parseInt(trialsTextfield.getText());
  
				count += NUMBER_OF_THREADS;
            for(int h = 1; h <= NUMBER_OF_THREADS; h++)
   		   {
               label.setText("");
               label1.setText("Threads started: " + count);
               new topswapThread(h, NUMBER_OF_VALUES, NUMBER_OF_THREADS, NUMBER_OF_RUNS, NUMBER_OF_TRIALS);
            }
         }
         catch(Exception ey)
         {
            label.setText("YOU DIDNT ENTER A NUMBER!");
         }
      }
   }
}

class topswapThread extends com.amd.aparapi.Kernel 
{
	private final int NUMBER_OF_VALUES_c; //had to make copies of the variable to pass them into the threads
   private final int NUMBER_OF_THREADS_c;
   private final int NUMBER_OF_RUNS_c;
   private final int NUMBER_OF_TRIALS_c;
   private final int h_c;
   
	public topswapThread(int h, int NUMBER_OF_VALUES, int NUMBER_OF_THREADS, int NUMBER_OF_RUNS, int NUMBER_OF_TRIALS)
	{
      NUMBER_OF_VALUES_c = NUMBER_OF_VALUES;
      NUMBER_OF_RUNS_c = NUMBER_OF_RUNS;
      NUMBER_OF_TRIALS_c = NUMBER_OF_TRIALS;
      NUMBER_OF_THREADS_c = NUMBER_OF_THREADS;
      h_c = h;
		Kernel t = new Kernel();
		t.start();
	}
   
	public void run()
	{
      int i = 0;
		int count = 0;
		int[]	array	=	new int[NUMBER_OF_VALUES_c];
		int[] arrayHighest = new int[NUMBER_OF_VALUES_c];
		int[] arrayCopy = new int[NUMBER_OF_VALUES_c];
		String formatConvert  = String.valueOf(NUMBER_OF_VALUES_c);
		int formatNum = formatConvert.length() + 1;
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		int highest = 0;
		String highString = "";
      boolean maxAlreadyFound = false;
      
      if(h_c == NUMBER_OF_THREADS_c)
      {
         maxAlreadyFound = true;
      }
		
		File file = new File("topswap" + "-" + NUMBER_OF_VALUES_c + "-" + NUMBER_OF_RUNS_c + "-" + NUMBER_OF_TRIALS_c + ".log");
	   System.out.println("Thread started " + h_c + ":  " + System.currentTimeMillis());
		try
		{
			FileWriter writer = new FileWriter(file, true);
			String endHighest = "";
			
			for(int p = 0; p < NUMBER_OF_RUNS_c; p++)
			{
				highest = computePermutation(startTime, i, NUMBER_OF_VALUES_c, NUMBER_OF_TRIALS_c, array, arrayCopy, count, arrayHighest, endTime, highest);
				highString = System.getProperty("line.separator") + highest + "	";		
				for(int e = 0; e < NUMBER_OF_VALUES_c; e++)
				{
					highString += " " + arrayHighest[e];
				}
				highString += System.getProperty("line.separator");
		   	endTime = System.currentTimeMillis();
				//long elapse = endTime - startTime;
				//double seconds = (double)elapse / 1000;
			//	System.out.println("\n\nProcess ended at   " + endTime);
				writer.write(highString);
			}
			writer.close();
         if(maxAlreadyFound == true)
         {
   			writer = new FileWriter(file, true);
   			endHighest = System.getProperty("line.separator") + findHighest(file) + "		MAX_IN-FILE: " + System.getProperty("line.separator") + System.getProperty("line.separator");
   			writer.write(endHighest);
            writer.close();
         } 
         System.out.println("Thread finished " + h_c + ": " + System.currentTimeMillis());
         // purgeVariables(); // **** NEED TO FIGURE THIS OUT ****\\
			//System.out.println("Try!");
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
			System.out.println("Could not find the file specified " + file);
			System.out.println("Catch!");
		}
	}
	
	public static int computePermutation(long startTime, int i, int NUMBER_OF_VALUES, int NUMBER_OF_TRIALS, int array[], int arrayCopy[], int count, int arrayHighest[], long endTime, int highest)
	{
		Random rand	= new	Random();
	//	System.out.println("JAVA \n\nProcess started at " + startTime);
		//System.out.print("\nInitial array setup:\n");
		for(i =	0;	i < NUMBER_OF_VALUES; i++)
		{
			array[i]	= i +	1;
			//System.out.print(String.format("%-" + formatNum + "s", array[i]));
		}
		int k = 0;
		highest = 0;
		count = 0;
		//System.out.println();
		//System.out.println();
		//System.out.print("Randomizing the array:\n");
		for(k = 0; k < NUMBER_OF_TRIALS; k++)
		{
			count = 0;
			for(i =	0;	i < NUMBER_OF_VALUES; i++)
			{
				int randNum	= rand.nextInt(NUMBER_OF_VALUES - 1);		//using random function to determine a number to switch with
				int temp	= array[i];
				array[i]	= array[randNum];
				array[randNum]	= temp;
	         //array = new int[] {24, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 21, 2, 24, 19, 1, 23, 25, 22}; //use this to put a specific
				for(int j =	0;	j < NUMBER_OF_VALUES; j++)                              //permutation into the program
				{                                   
					//System.out.print(String.format("%-" + formatNum + "s" ,array[j]));
					arrayCopy[j] = array[j];
				}
			}	
			//System.out.print("<--- Random array that will be used");
			//System.out.println("\n");
			//System.out.print("Swapping the values:");
			while(array[0]	!=	1)                                                     
	  		{
				int first = 0;
				int last =  array[0] - 1;
				while(first < last)						//flipping the values until we get to the middle basically
				{
					int temp = array[first];
					array[first] = array[last];
					array[last] = temp;
					first++;									//incrementing/decrementing the values so that they will meet
					last--;
				}
				//System.out.println();
				/*for(int j =	0;	j < NUMBER_OF_VALUES; j++)
				{
					System.out.print(String.format("%-" + formatNum + "s" ,array[j]));
				}*/
				count++;	
	      }
			//System.out.print("<--- End array with a 1 in front after " + count + " swaps");
			endTime = System.currentTimeMillis();
			long elapse = endTime - startTime;
			//double seconds = (double)elapse / 1000;
			//System.out.println("\n\nProcess ended at   " + endTime + "\n" + elapse + " " + seconds);
	      
			//System.out.println("\nThere was a total of " + count + " topswaps performed in " + elapse + " milliseconds / " + seconds + " seconds.");
			if(count > highest)
			{
				highest = count;
			//	System.out.print("\n" + highest);
			//	System.out.print("	");
				for(int e = 0; e < NUMBER_OF_VALUES; e++)
				{
				//	System.out.print(" " + arrayCopy[e]);
					arrayHighest[e] = arrayCopy[e];
				}
	       //  System.out.print("      " + k + "\n");
			}
		}
		return highest;
	}
	
	public static String findHighest(File file) throws IOException
	{
		Scanner inputFile = new Scanner(new FileReader(file));
		String lineConvert = "";
		String test = "";
		int lineValue = 0;
		int highestInFile = 0;
		String highestLine = "";
		
		while(inputFile.hasNextInt())
		{
			lineValue = inputFile.nextInt();
			test = inputFile.nextLine();
		//	System.out.println(test);
		//	System.out.println(lineValue);
			if(lineValue > highestInFile)
			{
				highestInFile = lineValue;
				highestLine = lineValue + test;
			}	
		}
	//	System.out.println(highestLine);
		return highestLine;
	}
   
   public void purgeVariables()
   {
      
   }
}