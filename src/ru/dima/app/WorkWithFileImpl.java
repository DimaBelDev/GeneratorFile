package ru.dima.app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkWithFileImpl {
    private static List<Path> paths;

    public static void work(String from, String to) {
      init(from, to);
    }

    private static void init(String from, String to) {
        MyFileVisitor fileVisitor = new MyFileVisitor();
        try {
            Files.walkFileTree(Path.of(from), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        paths = fileVisitor.getList();
        sortWithDependencies(paths);
        copy(paths, Path.of(to));
    }

    public static List<Path> sort(List<Path> list) {
        list.sort(Comparator.comparing(o -> o.getFileName().toString()));
        return list;
    }

    public static void copy(List<Path> paths, Path to) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(to.toFile(), true))) {
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

    public static List<String> dependencies(StringBuilder text) {
        Pattern pattern = Pattern.compile("(require ‘<(\\S+)>’)"); // из-за разный операционных систем Pattern такой
        Matcher matcher = pattern.matcher(text);
        List<String> dependence = new ArrayList<>();
        while (matcher.find()) {
            dependence.add(matcher.group(2));
        }
        return dependence.stream().filter(p -> !p.trim().equals("")).toList();
    }
    // вытаскивает все зависимости из файла

    public static Map<String,Path> fileWithDependencies(List<Path> list) {
        List<Path> listSorted = sort(list);
        Map<String, Path> result = new LinkedHashMap<>();
        for(Path p : listSorted) {
            try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
                StringBuilder text = new StringBuilder();
                while (br.ready()){
                    text.append(br.readLine());
                }
                List<String> urls = dependencies(text);
                for(String i : urls)
                    result.put(i.trim(), p);
               } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    // сопоставляю файл в котором есть зависимость и саму зависимость

    public static List<Path> sortWithDependencies(List<Path> list) {
        List<Path> resultList = sort(list);
        Map<String, Path> dependence = fileWithDependencies(resultList);
        cyclicDependency(dependence);
        for (Map.Entry<String, Path> i : dependence.entrySet()) {
            int tempValue = 0;
            int tempKey = 0;
            for (int j = 0; j < resultList.size(); j++) {
                tempValue = i.getValue().equals(resultList.get(j)) ? j : tempValue;
                tempKey = i.getKey().equals(resultList.get(j).toString()) ? j : tempKey;
                if (tempValue < tempKey) {
                    swap(tempValue, tempKey, resultList);
                    tempValue = 0;
                    tempKey = 0;
                }
            }
        }
        return resultList;
    }
    // сортировка (если зависимый файл выше ,то его нужно поменять местами)
    private static void swap(int one, int two, List<Path> list) {
        Path temp = list.get(one);
        list.set(one, list.get(two));
        list.set(two, temp);
    }


    private static void cyclicDependency(Map<String, Path> map) {
        List<String> key = new ArrayList<>(map.keySet());
        List<Path> value = new ArrayList<>(map.values());
        for(int i = 0; i < map.size(); i++){
            for(int j = 0; j < map.size(); j++){
                if(key.get(i).equals(value.get(j).toString()) && key.get(j).equals(value.get(i).toString())){
                    throw new CyclicExсeption("Cуществует циклическая зависимость между файлом ",
                            value.get(i).getFileName().toString() + " ", value.get(j).getFileName().toString());
                }
            }
        }
    }
    // циклическая зависимость


}
