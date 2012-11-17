import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Parser all .jack files in a directory...
 * 
 * @author John Ness
 */
public class JackAnalyzer {

	private static String dirName = "ClassWithSubroutines";
	
	/**
	 * Parse all .jack files in a directory; create a corresponding
	 * .xml file for each one to show the parse tree.
	 * 
	 * @param args Command-line arguments (single argument interpreted
	 *             as directory where .jack files are located).
	 */
	public static void main(String[] args) {
		
		if (args.length == 1)
			dirName = args[0];
		
		String[] inputFileNames = (new File(dirName)).list(
				
				// Instead of defining a class that implements the
				// FilenameFilter interface, you can create a single
				// instance, including the implementation of required
				// methods, like this (called an "anonymous inner
				// class").
				new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jack");
					}
				});
		
		try {
			if (inputFileNames.length == 0)
				throw new FileNotFoundException();
			
			for (String inputFileName : inputFileNames) {
				String outputFileName = inputFileName.replace(".jack", ".xml");
				new CompilationEngine(dirName + "/" + inputFileName,
						dirName + "/" + outputFileName);
			}
			
		} catch (FileNotFoundException e) {
			System.err.println(
					"Trouble finding file.  Bad input directory name?");
		
		} catch (IOException e) {
			System.err.println("Trouble closing an input file.");
		}
	}
}
