package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para a classe Tasks")
class TasksTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    // ====================== HELPERS ======================

    private void setUpOutputCapture() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    private void restoreOutput() {
        System.setOut(originalOut);
    }

    private Scanner createScanner(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        return new Scanner(in);
    }

    // ====================== TESTES BÁSICOS ======================

    @Test
    @DisplayName("readPosition deve ler corretamente linha e coluna numéricas")
    void testReadPosition_ValidInput() {
        Scanner scanner = createScanner("5 7\n");
        Position pos = Tasks.readPosition(scanner);
        assertEquals(5, pos.getRow());
        assertEquals(7, pos.getColumn());
    }

    @Test
    @DisplayName("readClassicPosition deve aceitar formato compacto 'A3'")
    void testReadClassicPosition_CompactFormat_Valid() {
        Scanner scanner = createScanner("A3\n");
        IPosition pos = Tasks.readClassicPosition(scanner);
        assertEquals(0, pos.getRow());
        assertEquals(2, pos.getColumn());
        assertTrue(pos.isInside());
    }

    @Test
    @DisplayName("readClassicPosition deve aceitar formato separado 'J 10'")
    void testReadClassicPosition_SeparatedFormat_Valid() {
        Scanner scanner = createScanner("J 10\n");
        IPosition pos = Tasks.readClassicPosition(scanner);
        assertEquals(9, pos.getRow());
        assertEquals(9, pos.getColumn());
        assertTrue(pos.isInside());
    }

    @Test
    @DisplayName("readClassicPosition deve aceitar letras minúsculas")
    void testReadClassicPosition_LowercaseLetter() {
        Scanner scanner = createScanner("b 4\n");
        IPosition pos = Tasks.readClassicPosition(scanner);
        assertEquals(1, pos.getRow());
        assertEquals(3, pos.getColumn());
        assertTrue(pos.isInside());
    }

    @Test
    @DisplayName("readClassicPosition deve lançar exceção quando não há tokens suficientes")
    void testReadClassicPosition_NoInput() {
        Scanner scanner = createScanner("");
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner));
    }

    @Test
    @DisplayName("readClassicPosition deve lançar exceção com formato completamente inválido")
    void testReadClassicPosition_CompletelyInvalidFormat() {
        Scanner scanner = createScanner("123 ABC\n");
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner));
    }

    // ====================== TESTES readShip ======================

    @Test
    @DisplayName("readShip deve construir corretamente um navio com input válido")
    void testReadShip_ValidInput() {
        // Input: shipKind row column bearing
        Scanner scanner = createScanner("Barca 3 4 N\n");
        Ship ship;
        ship = Tasks.readShip(scanner);

        assertNotNull(ship, "O navio não deveria ser null");
        assertEquals("Barca", ship.getCategory());
        assertEquals(Compass.NORTH, ship.getBearing());
        assertNotNull(ship.getPosition());
        assertTrue(ship.getPosition().isInside());
    }

    @Test
    @DisplayName("readShip deve lançar IllegalArgumentException quando o tipo de navio é inválido")
    void testReadShip_InvalidShipType() {
        Scanner scanner = createScanner("Submarino 5 5 N\n");

        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.readShip(scanner);
        });
    }

    // ====================== TESTES DE OUTPUT ======================

    @Test
    @DisplayName("menuHelp deve imprimir a ajuda básica do menu")
    void testMenuHelp() {
        setUpOutputCapture();
        try {
            Tasks.menuHelp();
            String output = outContent.toString().toLowerCase();

            assertTrue(output.contains("ajuda do menu"));
            assertTrue(output.contains("gerafrota"));
            assertTrue(output.contains("rajada"));
            assertTrue(output.contains("desisto"));
            assertTrue(output.contains("undo"));
        } finally {
            restoreOutput();
        }
    }

    @Test
    @DisplayName("menuHelpDetailed deve imprimir a ajuda detalhada")
    void testMenuHelpDetailed() {
        setUpOutputCapture();
        try {
            Tasks.menuHelpDetailed();
            String output = outContent.toString();

            assertTrue(output.contains("AJUDA DETALHADA"));
            assertTrue(output.contains("lefrota"));
            assertTrue(output.contains("Barca 3 4 N"));
            assertTrue(output.contains("rajada"));
            assertTrue(output.contains("DICAS"));
        } finally {
            restoreOutput();
        }
    }
}