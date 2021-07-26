// javac main.java && java main ./AVATAR_executablecode ./out

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;

// import InputStream
import java.io.InputStream;

// import OutStream
import java.io.OutputStream;

// import FileInputStream
import java.io.FileInputStream;

// import FileOutputStream
import java.io.FileOutputStream;

// import list
import java.util.List;

import java.io.File;

// import Collectors
import java.util.stream.Collectors;

// main function
public class main {

    public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    public static void main(String[] args) {

        String AVATARCodePathStr = args[0];

        String ttoolGeneratedPathStr = AVATARCodePathStr + "/generated_src";

        Path fstarGeneratedPath = Paths.get(args[1]);

        // copy file to fstarGeneratedPath directory
        try {
            copyDirectoryCompatibityMode(new File("include/kremlib.h"), new File(args[1] + "/kremlib.h"));
        } catch (IOException e) {

        }

        try {
            copyDirectoryCompatibityMode(new File("include/kremlin"), new File(args[1] + "/kremlin"));

        } catch (IOException e) {

        }

        // directory list
        try {
            Stream<Path> generatedCFiles = Files.list(fstarGeneratedPath).filter(p -> p.toString().endsWith(".c"));

            // get file Names and strip exstention
            Stream<String> generatedCFilesNames = generatedCFiles
                    .map(p -> p.getFileName().toString().replace(".c", ""));

            // to string
            List<String> generatedCFilesNamesList = generatedCFilesNames.collect(Collectors.toList());

            String headersString = "\n\n";
            String makeFilesString = "";
            for (String generatedCFileName : generatedCFilesNamesList) {
                // System.out.println(generatedCFileName);
                headersString += "#include \"../../out/" + generatedCFileName + ".h\"\n";

                makeFilesString += "../../out/" + generatedCFileName + ".c" + " ";
            }

            File headerFile = new File(ttoolGeneratedPathStr + "/main.h");

            FileWriter headerFileWriter = new FileWriter(headerFile, true);

            try {
                // mainHeaderPath
                headerFileWriter.write(headersString);
                headerFileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            File makeFile = new File(AVATARCodePathStr + "/Makefile.src");
            FileWriter makeFileWriter = new FileWriter(makeFile, true);
            try {
                makeFileWriter.write(makeFilesString);
                makeFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Path ttoolGenerated = Paths.get(ttoolGeneratedPathStr);

        // get ttoolGenerated have c files
        try {
            Stream<Path> cFiles = Files.list(ttoolGenerated).filter(p -> p.toString().endsWith(".c"));

            cFiles.forEach(p -> {
                try {
                    String lines = Files.readString(p);
                    String regex = "(#define STATE__STOP__STATE \\d+\\n)([\\s\\S]*)(void \\*mainFunc__.*\\(void *\\*arg\\)\\{)";
                    String removedUserFunc = lines.replaceAll(regex, "$1\n$3");

                    Files.writeString(p, removedUserFunc);

                } catch (IOException e) {
                    System.out.println(e);
                }
            });
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
