package ru.beetlerat.obsidianrestructer.obsidiandirectoryhandler.zerolinkremover;

import ru.beetlerat.obsidianrestructer.obsidiandirectoryhandler.ObsidianDirectoryHandler;

import java.io.RandomAccessFile;
import java.util.Optional;

public class ZeroLinkRemover extends ObsidianDirectoryHandler {
    private final String zeroLinkStart = "[[00 ";

    public ZeroLinkRemover(Optional<String> possibleFilePath) {
        super(possibleFilePath);
    }

    public void zeroLinksToTags() {
        forEachMDFile(this::turnZeroLinksToTag);
    }

    private void turnZeroLinksToTag(RandomAccessFile accessFile) {
        StringBuilder fileText = getFileText(accessFile);
        int originalFileLength = fileText.length();

        String tagsFromZeroLinks = getTagsFromZeroLinks(fileText);

        if (tagsFromZeroLinks.length() != 0) {
            addNewTags(fileText, tagsFromZeroLinks);
        }
        removeZeroLinkHeader(fileText);


        if (originalFileLength != fileText.length()) {
            writeTextToFile(fileText, accessFile);
            System.out.println("ZeroLinks переработаны в теги.");
        } else {
            System.out.println("В файл не внесены изменения.");
        }
    }

    private void addNewTags(StringBuilder fileText, String tagsFromZeroLinks) {
        String tagHeaderStart = "tags: [";
        int tagHeaderStartIndex = fileText.indexOf(tagHeaderStart);
        int insertTagIndex = fileText.indexOf("]", tagHeaderStartIndex);
        try {
            fileText.insert(insertTagIndex, tagsFromZeroLinks);
        } catch (StringIndexOutOfBoundsException e) {
            return;
        }
    }

    private String getTagsFromZeroLinks(StringBuilder fileText) {
        StringBuilder tagsFromZeroLinks = new StringBuilder();

        Optional<String> zeroLink = extractZeroLinkFromText(fileText);
        while (zeroLink.isPresent()) {
            String tag = getTagFromZeroLink(zeroLink.get());
            tagsFromZeroLinks.append(", ").append(tag);
            zeroLink = extractZeroLinkFromText(fileText);
        }

        return tagsFromZeroLinks.toString();
    }

    private void removeZeroLinkHeader(StringBuilder fileText) {
        String zeroLinkHeader = "### Zero-Links";
        int headerStartIndex = fileText.indexOf(zeroLinkHeader);
        int headerEndIndex = fileText.indexOf("---", headerStartIndex) + "---".length() + 1;
        try {
            fileText.replace(headerStartIndex, headerEndIndex, "");
        } catch (StringIndexOutOfBoundsException e) {
            return;
        }
    }

    private Optional<String> extractZeroLinkFromText(StringBuilder fileText) {
        int zeroLinkStartIndex = fileText.indexOf(zeroLinkStart);
        if (zeroLinkStartIndex == -1) {
            return Optional.empty();
        }

        zeroLinkStartIndex = fileText.lastIndexOf("\n", zeroLinkStartIndex) + 1;

        int zeroLinkEndIndex = fileText.indexOf("\n", zeroLinkStartIndex);

        String zeroLink = "";
        try {
            zeroLink = fileText.substring(zeroLinkStartIndex, zeroLinkEndIndex);
        } catch (StringIndexOutOfBoundsException e) {
            return Optional.empty();
        }


        fileText.replace(zeroLinkStartIndex, zeroLinkEndIndex + 1, "");

        return Optional.of(zeroLink);
    }

    private String getTagFromZeroLink(String zeroLink) {
        int startTagIndex = zeroLink.indexOf(zeroLinkStart) + zeroLinkStart.length();
        int endTagIndex = zeroLink.indexOf("]]");

        return zeroLink.substring(startTagIndex, endTagIndex);
    }
}
