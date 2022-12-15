import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class LockoutMaker {
    public static void main(String[] args) {
        if (args.length != 1) {
            args = new String[]{"Lockout.txt"};
        }

        File file = new File(args[0]);
        String contents;
        try {
            contents = Files.readString(file.toPath());
        } catch (IOException e) {
            System.err.println("Could not load lock file: " + e.getMessage());
            System.exit(1);
            return;
        }

        String[] lines = contents.split("\n");
        String[] lockLines = new String[lines.length - 1];
        System.arraycopy(lines, 1, lockLines, 0, lockLines.length);

        Set<Lock> locks = Arrays.stream(lockLines)
                .map(line->line.split("\t"))
                .map(lineParts->new Lock(lineParts[0], lineParts[1]))
                .collect(Collectors.toSet());

        Map<String, Set<Lock>> lockGroups = new HashMap<>();

        for (Lock lock : locks) {
            String groupName = lock.name.substring(0,1);
            Set<Lock> group = lockGroups.computeIfAbsent(groupName, name -> new HashSet<>());
            group.add(lock);
        }

        for (String groupName : lockGroups.keySet()) {
            Set<Lock> lockGroup = lockGroups.get(groupName);

            // choose a random ordering of algorithms for the locks
            int[] algorithms = new int[ImageGenerator.NUM_ALGORITHMS];
            for (int i = 0; i < algorithms.length; i++) {
                algorithms[i] = i;
            }
            for (int i = 0; i < algorithms.length; i++) {
                int pos = (int)(Math.random() * algorithms.length);
                int tmp = algorithms[i];
                algorithms[i] = algorithms[pos];
                algorithms[pos] = tmp;
            }

            int algorithmIndex = 0;
            for (Lock lock : lockGroup) {

                ImageGenerator gen = new ImageGenerator(lock.name + "-" + lock.code, 200, 150);
                gen.doAlgorithm(algorithms[algorithmIndex]);
                try {
                    String filename = String.format("%s%05d.png", groupName, (int)(Math.random() * 99999));
                    gen.saveImage(filename);
                } catch (IOException e) {
                    System.err.println("Cannot save image: " + e.getMessage());
                }

                algorithmIndex++;
            }

        }
    }
}

class Lock {
    final String name;
    final String code;

    public Lock(String name, String code) {
        this.name = name;
        this.code = code;
    }
}