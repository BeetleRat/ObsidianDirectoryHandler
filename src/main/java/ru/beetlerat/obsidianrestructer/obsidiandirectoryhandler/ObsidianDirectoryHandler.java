package ru.beetlerat.obsidianrestructer.obsidiandirectoryhandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

public class ObsidianDirectoryHandler {
    private Optional<File> obsidianDirectory;

    public ObsidianDirectoryHandler(Optional<String> possibleFilePath) {
        setObsidianDirectory(possibleFilePath);
    }

    public void setObsidianDirectory(Optional<String> possibleFilePath) {
        this.obsidianDirectory = createFileFromDirectoryPath(possibleFilePath);
    }

    public boolean isObsidianDirectoryExists() {
        if (obsidianDirectory.isEmpty()) {
            System.out.println("Отсутствует путь к хранилищу.");
        }

        return obsidianDirectory.isPresent();
    }

    private Optional<File> createFileFromDirectoryPath(Optional<String> possibleFilePath) {
        if (possibleFilePath.isEmpty()) {
            System.out.println("Не указан путь к хранилищу.\nПуть к хранилищу должен указываться первым входным аргументом");
            return Optional.empty();
        }

        File directory = Paths.get(possibleFilePath.get()).toFile();

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Указанный путь к хранилищу не корректен.");
            return Optional.empty();
        }

        return Optional.of(directory);
    }

    protected void forEachMDFile(Consumer<RandomAccessFile> fileTransformMethod) {
        if (isObsidianDirectoryExists()) {
            Optional<File[]> files = Optional.ofNullable(obsidianDirectory.get().listFiles());
            fileFinder(files, fileTransformMethod);
        }
    }

    private void fileFinder(Optional<File[]> optionalFiles, Consumer<RandomAccessFile> fileTransformMethod) {
        if (optionalFiles.isEmpty()) {
            return;
        }

        File[] files = optionalFiles.get();
        for (File file : files) {
            if (file.isDirectory()) {
                fileFinder(Optional.ofNullable(file.listFiles()), fileTransformMethod);
            } else {
                if (file.getName().endsWith(".md")) {
                    editFile(file, fileTransformMethod);
                }
            }
        }
    }

    private void editFile(File file, Consumer<RandomAccessFile> fileTransformMethod) {
        try (RandomAccessFile accessFile = new RandomAccessFile(file.getPath(), "rw")) {
            System.out.printf("Файл %s. ", file.getName());
            fileTransformMethod.accept(accessFile);
        } catch (IOException e) {
            System.out.printf("Ошибка чтения файла %s.\n%s", file.getName(), e);
        }
    }


    protected StringBuilder getFileText(RandomAccessFile accessFile) {
        StringBuilder fileText = new StringBuilder();
        String line = "";
        try {
            while ((line = accessFile.readLine()) != null) {
                line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                fileText.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.printf("Ошибка чтения файла %s.\n", e);
        }

        return fileText;
    }

    protected void writeTextToFile(StringBuilder fileText, RandomAccessFile accessFile) {
        try {
            accessFile.setLength(0);
            accessFile.write(fileText.toString().getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            System.out.printf("Ошибка чтения файла %s.\n", e);
        }
    }
}
