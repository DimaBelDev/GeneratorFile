package ru.dima.app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class WorkWithFileImpl {
    private static List<Path> paths;

    public static void work(String from, String to) {
      init(from, to);
    }

    private static void init(String from, String to){
        MyFileVisitor fileVisitor = new MyFileVisitor();
        try {
            Files.walkFileTree(Path.of(from), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        paths = fileVisitor.getList();
        sort(paths);
        copy(paths, Path.of(to));
    }


    public static List<Path> sort(List<Path> list){
        list.sort(Comparator.comparing(o -> o.getFileName().toString()));
        return list;
    }


    public static void copy(List<Path> paths, Path out){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out.toFile(), true))) {
            for(Path p : paths) {
                try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
                    while (br.ready()) {
                        bw.write(br.readLine());
                        bw.newLine();
                    }
                }
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
