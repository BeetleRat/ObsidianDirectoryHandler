package ru.beetlerat.obsidianrestructer;

import ru.beetlerat.obsidianrestructer.obsidiandirectoryhandler.zerolinkremover.ZeroLinkRemover;

import java.util.Optional;

public class App {
    public static void main(String[] args) {
        new ZeroLinkRemover(Optional.ofNullable(args[0])).zeroLinksToTags();
    }
}
