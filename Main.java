/* ECS 170 Assignment 3 */

import java.io.*;
import java.io.PrintWriter;
import java.lang.Object;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;

public class Main {

	/* Default values */ 
	private static boolean train		= false;
	private static boolean test		= false;
	private static boolean fold		= false;
	private static String maleDirectory	= "Male/";
	private static String femaleDirectory	= "Female/";
	private static String testDirectory	= "Test/";
	private static int numMale		= 0;
	private static int numFemale		= 0;
	private static int numTest		= 0;
	private static ArrayList<Image> faces;
	private static ArrayList<Image> tFaces;
	private static NeuralNetwork network;
 
	/* main */
	public static void main(String[] args) {
		/* Parse command-line input */
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-train")) {
				train = true;
				if ((i+1 < args.length) && !args[i+1].equals("-test")) {
					maleDirectory = args[i+1];
					if ((i+2 < args.length) && !args[i+2].equals("-test")) {
						femaleDirectory = args[i+2];
						i = i+2;
					}
					else {
						System.out.println("Error : Need male AND female directory.");
						System.exit(1);
					}
				}
			}
			else if (args[i].equals("-test")) {
				test = true;
				if ((i+1 < args.length) && !args[i+1].equals("-train")) {
					testDirectory = args[i+1];
					i++;
				}
			}
			else {
				System.out.println("Error : Incorrect format.");
				System.exit(1);
			}
			i++;
		}

		String c = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("5-fold cross validation? y/n");
		try {
			c = br.readLine();
		} catch (IOException e) {
			System.out.println("Error in input, please use 'y' or 'n'.");
			System.exit(1);
		}
		if (c.equals("y")) {
			fold = true;
		} else if (c.equals("n")) {
			fold = false;
		} else {
			System.out.println("ERROR in input, please use 'y' or 'n'.");
			System.exit(1);
		}
		
		/* Train */
		if (train) {
			/* Compose new face set to train off of */
			faces = new ArrayList<Image>();
			faces= compose(0);
			
			/* Initialize neural network */
			network = new NeuralNetwork();
			network.randomizeWeights();

			/* Fold if toggled */
			//if (fold) fold();

			/* Initialize j for future use */

			int j = 0;

			/* Use neural network */
			do {
				for (i = 0; i < faces.size(); i++) {
					network.propogate(faces.get(i),1);
					network.backpropogate();
				}
				j++;
					//I think we need an error calculator here to tell us when our weights are good enough
			} while (j < 4);
		}

		/* Test */
		if (test) {
			/* Compose new face set to test off of */
			tFaces = new ArrayList<Image>();
			tFaces = compose(1);
			//NeuralNetwork.Output_Node prediction = new NeuralNetwork.Output_Node();
			int prediction;
			try {
			PrintWriter out1 = new PrintWriter("AI_Am_Boss.predictions", "UTF-8");

		//	System.out.println(tFaces.size());
			for (i = 0; i < tFaces.size(); i++) {
				prediction = network.test(tFaces.get(i)).gender;
				double confidence = network.Confidence();
				if (prediction == 1){
					System.out.println(i+" Female at a confidence level of "+confidence);
					out1.println(i+" Female at a confidence level of "+confidence);
				}
				else{
					System.out.println(i+" Male at a confidence level of "+confidence);
					out1.println(i+" Female at a confidence level of "+confidence);
				}
			}
			out1.close();
			} catch(IOException e) {
				System.out.println("file error");
				System.exit(1);
			}
		}
	}

	/* Composes a new face set for either training or testing */
	public static ArrayList<Image> compose(int command) {
		faces = new ArrayList<Image>();
		tFaces = new ArrayList<Image>();

		/* Filter to accept only .txt files */
		FilenameFilter fnFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		};
		/* For training */
		if (command == 0) {
			/* Forward declarations */
			File 		mDirectory 	= new File(maleDirectory);
			File		fDirectory	= new File(femaleDirectory);

			/* Create list of files in each directory */
			String[] 	maleFaces 	= mDirectory.list(fnFilter);
			String[] 	femaleFaces 	= fDirectory.list(fnFilter);

			/* Add these faces to training list */
			for (String img : maleFaces) {
				img = maleDirectory+"/"+img;
				faces.add(new Image(0, img, imageRead(img)));
				numMale++;
			}
			for (String img2 : femaleFaces) {
				img2 = femaleDirectory+"/"+img2;
				faces.add(new Image(1, img2, imageRead(img2)));
				numFemale++;
			}

			/* Randomize face ordering */
			long seed = System.nanoTime();
			Collections.shuffle(faces, new Random(seed));
		}

		/* For testing */
		else if (command == 1) {
			/* Get file list */
			File tDirectory 	= new File(testDirectory);
			String[] testFaces	= tDirectory.list(fnFilter);

			/* Add these faces to testing list */
			for (String img : testFaces) {
				img = testDirectory+"/"+img;
				tFaces.add(new Image(2, img, imageRead(img)));
				numTest++;
			}
			return tFaces;
		}

		return faces;
	}

	/* Read the data from the image file */
	public static String imageRead(String absp) {
		/* FileInputStream stream = new FileInputStream(new File(absp));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}*/
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(absp));
			Charset encoding = Charset.defaultCharset();
			return encoding.decode(ByteBuffer.wrap(encoded)).toString();
		} catch (Exception e) {
			System.out.println("Corrupt image file : "+absp);
			System.exit(1);
			return "";
		}
	}
	
	/* Diagnostics */
	public static void diagnostics() {
		System.out.println("train = "+train);
		System.out.println("test  = "+test);
		System.out.println("maleDirectory = "+maleDirectory);
		System.out.println("femaleDirectory = "+femaleDirectory);
		System.out.println("testDirectory = "+testDirectory);
	}

	/* Fold for cross validation */
	public static void fold() {
		while(true) {
			for(int fold = 0; fold < 5; fold++) {
				ArrayList<Image> train = new ArrayList<Image>();
				ArrayList<Image> test = new ArrayList<Image>();
		
				/* Partition */
//				for (int i =0; i < test.size(); i++) {
//					if (i >= 5 * (faces.size() % partSize)) 
//					test.add(tFaces.
//
//				
			}
		}
	}
}
