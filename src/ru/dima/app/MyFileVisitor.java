package ru.dima.app;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;


public class MyFileVisitor extends SimpleFileVisitor<Path> {
    private List<Path> list = new ArrayList<>();

    private void check(Path p){
        if(p.toFile().isFile() && (p.getFileName().toString().endsWith(".txt") ||  p.getFileName().toString().endsWith(".TXT"))){
            list.add(p);
        }
    }
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        check(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.TERMINATE;
    }

    public List<Path> getList() {
        return list;
    }
}
