package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Move.
 * Maximiza a cobertura de caminhos (branches) e valida a geração de JSON.
 * * @author Fredson 111825
 * @version 1.2
 */
class MoveTest {

    /**
     * Mock de navio para isolar os testes da lógica do Move.
     * Implementa IShip respeitando o retorno Integer para getSize().
     */
    static class FakeShip implements IShip {
        private final String category;
        private final Integer size;

        FakeShip(String category, int size) {
            this.category = category;
            this.size = size;
        }

        @Override public String getCategory() { return category; }
        @Override public Integer getSize() { return size; }

        // --- Implementações obrigatórias (Stubs) ---
        @Override public List<IPosition> getPositions() { return null; }
        @Override public List<IPosition> getAdjacentPositions() { return null; }
        @Override public IPosition getPosition() { return null; }
        @Override public Compass getBearing() { return null; }
        @Override public boolean stillFloating() { return false; }
        @Override public int getTopMostPos() { return 0; }
        @Override public int getBottomMostPos() { return 0; }
        @Override public int getLeftMostPos() { return 0; }
        @Override public int getRightMostPos() { return 0; }
        @Override public boolean occupies(IPosition pos) { return false; }
        @Override public boolean tooCloseTo(IShip other) { return false; }
        @Override public boolean tooCloseTo(IPosition pos) { return false; }
        @Override public void shoot(IPosition pos) {}
        @Override public void sink() {}
    }

    /**
     * Helper para criar resultados de tiro de forma concisa.
     */
    private IGame.ShotResult shot(boolean valid, boolean repeated, IShip ship, boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    @Test
    @DisplayName("Deve validar construtor, getters e toString")
    void testBasics() {
        List<IPosition> shots = new ArrayList<>();
        List<IGame.ShotResult> results = new ArrayList<>();
        Move move = new Move(1, shots, results);

        assertEquals(1, move.getNumber());
        assertEquals(shots, move.getShots());
        assertEquals(results, move.getShotResults());
        assertTrue(move.toString().contains("number=1"));
    }

    @Test
    @DisplayName("Cobre: Cenário complexo (Acertos, Barco ao fundo e Água)")
    void testComplexScenario() {
        List<IGame.ShotResult> results = new ArrayList<>();
        IShip cruiser = new FakeShip("Cruiser", 3);
        IShip sub = new FakeShip("Submarine", 1);

        results.add(shot(true, false, cruiser, false)); // Acerto (não afundou)
        results.add(shot(true, false, sub, true));     // Acerto (afundou)
        results.add(shot(true, false, null, false));   // Água

        Move move = new Move(1, new ArrayList<>(), results);
        String json = move.processEnemyFire(true);

        // Valida chaves do JSON (em inglês como no código original)
        assertTrue(json.contains("\"validShots\" : 3"));
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("Cruiser"));
        assertTrue(json.contains("Submarine"));
        assertTrue(json.contains("sunkBoats"));
        assertTrue(json.contains("hitsOnBoats"));
    }

    @Test
    @DisplayName("Cobre: Apenas tiros repetidos (Ramo validShots == 0)")
    void testOnlyRepeatedShots() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(shot(true, true, null, false));

        Move move = new Move(1, new ArrayList<>(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"repeatedShots\" : 1"));
        assertTrue(json.contains("\"validShots\" : 0"));
    }

    @Test
    @DisplayName("Cobre: Tiros repetidos misturados com tiros válidos (Cobre separador vírgula)")
    void testRepeatedWithValid() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(shot(true, false, null, false)); // 1 válido (água)
        results.add(shot(true, true, null, false));  // 1 repetido

        Move move = new Move(1, new ArrayList<>(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }

    @Test
    @DisplayName("Cobre: Tiros exteriores (Outside Shots)")
    void testOutsideShots() {
        // Se enviarmos uma lista vazia, todos os Game.NUMBER_SHOTS são exteriores
        Move move = new Move(1, new ArrayList<>(), new ArrayList<>());
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("\"outsideShots\""));
    }

    @Test
    @DisplayName("Cobre: Tiros inválidos (Ramo !result.valid())")
    void testInvalidShots() {
        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(shot(false, false, null, false)); // Inválido

        Move move = new Move(1, new ArrayList<>(), results);
        String json = move.processEnemyFire(false);

        assertTrue(json.contains("\"validShots\" : 0"));
    }

    @Test
    @DisplayName("Cobre: Pluralização de barcos afundados")
    void testMultipleSinksPlural() {
        List<IGame.ShotResult> results = new ArrayList<>();
        IShip sub = new FakeShip("Submarine", 1);

        // Simula afundar dois barcos do mesmo tipo (se a lógica permitir)
        results.add(shot(true, false, sub, true));
        results.add(shot(true, false, sub, true));

        Move move = new Move(1, new ArrayList<>(), results);
        String json = move.processEnemyFire(true);

        assertTrue(json.contains("Submarine"));
        assertTrue(json.contains("\"count\" : 2"));
    }
}