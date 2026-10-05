package java.awt.desktop;

public final class OpenFilesEvent extends FilesEvent {
    private final String searchTerm;

    OpenFilesEvent(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
