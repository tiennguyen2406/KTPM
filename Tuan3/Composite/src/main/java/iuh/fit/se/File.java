package iuh.fit.se;

public class File extends FileSystemComponent{
    private int size;

    public File(String name, int size) {
        super(name);
        this.size = size;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "├── [File] " + name + " (" + size + "KB)");
    }
}
