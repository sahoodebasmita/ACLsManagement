package au.sagamore.acl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * ACLs Management Automation
 */

/**
 * @author Debasmita.Sahoo
 * 
 */
public class FileUtils {
	/**
	 * List all the files and folders from a directory
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFilesAndFolders(String directoryName) {

		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			System.out.println(file.getName());
		}
	}

	/**
	 * List all the files under a directory
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFiles(String directoryName) {

		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				System.out.println(file.getName());
			}
		}
	}

	/**
	 * List all the folder under a directory
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFolders(String directoryName) {

		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isDirectory()) {
				System.out.println(file.getName());
			}
		}
	}

	/**
	 * List all files from a directory and its subdirectories
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public List<File> listFilesAndFilesSubDirectories(String directoryName) {

		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		List<File> files = new ArrayList<File>();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
				System.out.println(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				List<File> subDirectoryFiles =listFilesAndFilesSubDirectories(file.getAbsolutePath());
				files.addAll(subDirectoryFiles);
			}
		}
		return files;
	}

	public static void main(String[] args) throws IOException {

		FileUtils fileUtils = new FileUtils();
		// Windows directory example
		final String directoryWindows = args[0];
		List<File> files = fileUtils
				.listFilesAndFilesSubDirectories(directoryWindows);
		final String PROPFILE = System.getProperty("user.home")+"\\GoogleDrive\\settings.properties";
		Properties props;
		Properties myNewProp;

		// Input Properties File
		props = loadProperties(PROPFILE);
		Long lastExecutionTime = !props.getProperty(
				"last.script.execution.time").isEmpty() ? Long.parseLong(props
				.getProperty("last.script.execution.time")) : null;
		List<String> users= getUsers();
		if (lastExecutionTime != null) {
			List<File> filtererFileList = new ArrayList<>();
			for (File file : files) {
				if (file.lastModified() > lastExecutionTime) {
					filtererFileList.add(file);
				}
			}
			changeFileACL(filtererFileList,users);
		} else {
			changeFileACL(files,users);
		}
		// Modified Properties File
		myNewProp = alterProperties(props);
		
		saveProperties(myNewProp, PROPFILE);
		
	}
	/**
	 * get List of all users/groups defined in users.txt
	 *
	 */
	private static List<String> getUsers() throws FileNotFoundException {

		ArrayList<String> users = new ArrayList<String>();
		String line = null;
	    try {
	        @SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"\\GoogleDrive\\users.txt"));
	        while((line = reader.readLine()) != null){
	            System.out.println(line);
	            if(!line.trim().isEmpty()){
	            	users.add(line);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

		return users;
	}
	/**
	 * apply ACL to all modified files for all users/groups defined in users.txt
	 * 
	 * @param fileList
	 *            on whcih ACL to be applied
	 * @param users
	 *            for whom the ACL to be applied
	 */
	private static void changeFileACL(List<File> fileList,List<String> users) throws IOException {
		for (File file : fileList) {
			for (String user : users) {
				String command = "icacls \""+file.getAbsolutePath()+"\" /grant :r \""+user+"\":(D,WDAC,RC,WD,GA)";
				Process proc = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
				BufferedReader stdInput = new BufferedReader(new 
			             InputStreamReader(proc.getInputStream()));

			        BufferedReader stdError = new BufferedReader(new 
			             InputStreamReader(proc.getErrorStream()));

			        // read the output from the command
			      //  System.out.println("Here is the standard output of the command:\n");
			        String s ="";
			        while ((s = stdInput.readLine()) != null) {
			            System.out.println(s);
			        }

			        // read any errors from the attempted command
			      //  System.out.println("Here is the standard error of the command (if any):\n");
			        while ((s = stdError.readLine()) != null) {
			            System.out.println(s);
			        }
			}
			
		}

	}
	/**
	 * reads settings.properties to get the last execution time
	 * 
	 * @param fileName
	 *            to be read
	 */
	private static Properties loadProperties(String fileName) {

		InputStream inPropFile;
		Properties tempProp = new Properties();

		try {
			inPropFile = new FileInputStream(fileName);
			tempProp.load(inPropFile);
			inPropFile.close();
		} catch (IOException ioe) {
			System.out.println("I/O Exception.");
			ioe.printStackTrace();
			System.exit(0);
		}

		return tempProp;

	}
	/**
	 * updates settings.properties for last execution time
	 * @param properties
	 *            to be modified
	 * @param fileName
	 *            to be updated
	 */
	private static void saveProperties(Properties properties, String fileName) {

		try {
			PrintWriter pw = new PrintWriter(new FileWriter(new File(fileName)));
			p.store(pw, null);
			pw.close();
		} catch (IOException ioe) {
			System.out.println("I/O Exception.");
			ioe.printStackTrace();
			System.exit(0);
		}

	}
	/**
	 * alters properties for last execution time
	 * @param properties
	 *            to be modified
	 */
	private static Properties alterProperties(Properties p) {

		Properties newProps = new Properties();
		Enumeration<?> enumProps = p.propertyNames();
		String key = "";

		while (enumProps.hasMoreElements()) {

			key = (String) enumProps.nextElement();

			if (key.equals("last.script.execution.time")) {
				newProps.setProperty(key, new Date().getTime()+"");
			} 

		}

		return newProps;

	}

}
