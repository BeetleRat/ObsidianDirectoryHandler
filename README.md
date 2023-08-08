# ObsidianDirectoryHandler
Программа для внесения изменений сразу во все файлы Obsidian.

## Добавление функционала
Для добавления новых функций, необходимо унаследоваться от класса ObsidianDirectoryHandler и вызвать его метод forEachMDFile, в который необходимо передать Consumer\<RandomAccessFile\> описывающий действие над каждым .md файлом Obsidian.
