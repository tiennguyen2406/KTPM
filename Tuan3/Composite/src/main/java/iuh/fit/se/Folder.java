package iuh.fit.se;

import java.util.ArrayList;
import java.util.List;

public class Folder extends FileSystemComponent{
    private List<FileSystemComponent> components = new ArrayList<>();

    public Folder(String name) {
        super(name);
    }

    public void addComponent(FileSystemComponent component) {
        components.add(component);
    }

    public void removeComponent(FileSystemComponent component) {
        components.remove(component);
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "└── [Folder] " + name);
        for (FileSystemComponent component : components) {
            component.showDetails(indent + "    ");
        }
    }
}
