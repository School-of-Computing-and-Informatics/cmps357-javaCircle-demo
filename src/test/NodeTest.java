import edu.louisiana.cmps357.C00546097.CirclePanel;
import edu.louisiana.cmps357.C00546097.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NodeTest {
    @Test
    void testImportLayout() throws IOException {
        // 1. Create a temporary JSON file with node data
        String json = """
            {"x":100,"y":200,"radius":50,"r":255,"g":0,"b":0}
            {"x":150,"y":250,"radius":30,"r":0,"g":255,"b":0}
            """;

        Path tempFile = Files.createTempFile("testLayout", ".json");
        Files.writeString(tempFile, json);

        // 2. Create instance of CirclePanel
        CirclePanel panel = new CirclePanel();

        // 3. Call importLayout
        panel.importLayout(tempFile.toString());

        // 4. Check nodes
        List<Node> nodes = panel.getNodesForTest();
        assertEquals(2, nodes.size());

        Node first = nodes.get(0);
        assertEquals(new Point(100, 200), first.getPosition());
        assertEquals(50, first.getRadius());
        assertEquals(new Color(255, 0, 0), first.getColor());

        Node second = nodes.get(1);
        assertEquals(new Point(150, 250), second.getPosition());
        assertEquals(30, second.getRadius());
        assertEquals(new Color(0, 255, 0), second.getColor());

        // 5. Clean up temporary file
        Files.deleteIfExists(tempFile);
    }


}
