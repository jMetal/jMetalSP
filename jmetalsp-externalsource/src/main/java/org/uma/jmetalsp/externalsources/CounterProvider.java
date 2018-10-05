package org.uma.jmetalsp.externalsources;

import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * This class writes the value of a counter into a file periodically. The generated files are stored in a directory.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CounterProvider {

	private void start(String directory, long frequency) throws InterruptedException, IOException {
		int counter = 0 ;

		while (true) {
			System.out.println("Counter:" + counter) ;

			FileOutputContext fileOutputContext = new DefaultFileOutputContext(directory+"/time."+counter) ;
			BufferedWriter bufferedWriter = fileOutputContext.getFileWriter() ;

			bufferedWriter.write("" + counter) ;
			bufferedWriter.close();

			Thread.sleep(frequency);

			counter ++ ;
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new Exception("Invalid number of arguments. " +
							"Usage: java org.uma.jmetalsp.externalsources.CounterProvider directory frequency") ;
		}

		String directory = args[0] ;
		long frequency = Long.valueOf(args[1]) ;

		createDataDirectory(directory);

		new CounterProvider().start(directory, frequency);
	}

	private static void createDataDirectory(String outputDirectoryName) {
		File outputDirectory = new File(outputDirectoryName);

		if (outputDirectory.isDirectory()) {
			System.out.println("The output directory exists. Deleting and creating ...");
			for (File file : outputDirectory.listFiles()) {
				file.delete();
			}
			outputDirectory.delete();
		} else {
			System.out.println("The output directory doesn't exist. Creating ...");
		}

		new File(outputDirectoryName).mkdir();
	}
}
