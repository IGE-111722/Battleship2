package battleship;
import java.util.Scanner;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	@DisplayName("fireSingleShot inválido deve aumentar contador de inválidos")
	void fireSingleShotInvalidPosition() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	@DisplayName("fireSingleShot repetido deve aumentar contador de repetidos")
	void fireSingleShotRepeated() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	// # adiciona estes testes novos dentro da classe GameTest

	@Test
	@DisplayName("getMyMoves deve começar vazio")
	void getMyMovesInitiallyEmpty() {
		assertNotNull(game.getMyMoves(), "MyMoves não deve ser null.");
		assertTrue(game.getMyMoves().isEmpty(), "MyMoves deve começar vazio.");
	}

	@Test
	@DisplayName("getAlienFleet devolve a referência atual usada pela implementação")
	void getAlienFleetCurrentBehavior() {
		assertSame(game.getMyFleet(), game.getAlienFleet(),
				"Neste momento a implementação devolve a mesma referência de myFleet.");
	}

	@Test
	@DisplayName("fireSingleShot em água não altera hits nem sinks")
	void fireSingleShotMiss() {
		Position water = new Position(0, 0);

		var result = game.fireSingleShot(water, false);

		assertNotNull(result, "O resultado do tiro não deve ser null.");
		assertEquals(0, game.getHits(), "Não deve aumentar hits quando o tiro cai na água.");
		assertEquals(0, game.getSunkShips(), "Não deve aumentar sinks quando o tiro cai na água.");
		assertEquals(0, game.getInvalidShots(), "Não deve contar como inválido.");
		assertEquals(0, game.getRepeatedShots(), "Não deve contar como repetido.");
	}

	@Test
	@DisplayName("fireSingleShot num navio deve contar hit sem afundar um navio maior")
	void fireSingleShotHit() {
		IFleet fleet = game.getMyFleet();
		Ship ship = new Galleon(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship);

		game.fireSingleShot(new Position(4, 4), false);

		assertEquals(1, game.getHits(), "Deve contar um acerto.");
		assertEquals(0, game.getSunkShips(), "Um Galleon não deve afundar com apenas um tiro.");
	}

	@Test
	@DisplayName("fireSingleShot deve afundar navio de tamanho 1")
	void fireSingleShotSinkSizeOneShip() {
		IFleet fleet = game.getMyFleet();
		Ship ship = new Barge(Compass.NORTH, new Position(3, 3));
		fleet.addShip(ship);

		game.fireSingleShot(new Position(3, 3), false);

		assertEquals(1, game.getHits(), "Deve contar um acerto.");
		assertEquals(1, game.getSunkShips(), "Um navio de tamanho 1 deve ficar afundado.");
		assertEquals(0, game.getRemainingShips(), "Não devem restar navios a flutuar.");
	}

	@Test
	@DisplayName("fireShots deve lançar exceção se a lista não tiver exatamente 3 tiros")
	void fireShotsInvalidNumberOfShots() {
		List<IPosition> shots = List.of(new Position(1, 1), new Position(1, 2));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> game.fireShots(shots));

		assertEquals("Must fire exactly 3 shots per move.", exception.getMessage());
	}

	@Test
	@DisplayName("fireShots com 3 tiros válidos deve criar uma jogada")
	void fireShotsValidMove() {
		List<IPosition> shots = List.of(new Position(1, 1), new Position(1, 2), new Position(1, 3));

		game.fireShots(shots);

		assertEquals(1, game.getAlienMoves().size(), "Deve existir uma jogada registada.");
		assertEquals(3, game.getTotalShots(), "Uma jogada deve equivaler a 3 tiros.");
	}

	@Test
	@DisplayName("getMissShots deve contar falhas corretamente")
	void getMissShots() {
		IFleet fleet = game.getMyFleet();
		Ship ship = new Frigate(Compass.NORTH, new Position(5, 5));
		fleet.addShip(ship);

		List<IPosition> shots = List.of(
				new Position(5, 5),   // # hit
				new Position(-1, 0),  // # inválido
				new Position(0, 0)    // # água
		);

		game.fireShots(shots);

		assertEquals(3, game.getTotalShots(), "Devem existir 3 tiros no total.");
		assertEquals(1, game.getHits(), "Deve existir 1 acerto.");
		assertEquals(1, game.getInvalidShots(), "Deve existir 1 tiro inválido.");
		assertEquals(0, game.getRepeatedShots(), "Não deve existir tiro repetido.");
		assertEquals(1, game.getMissShots(), "Deve existir 1 falha na água.");
	}

	@Test
	@DisplayName("getAccuracy deve devolver 0 quando não há tiros válidos")
	void getAccuracyNoValidShots() {
		List<IPosition> shots = List.of(
				new Position(-1, 0),
				new Position(-2, 0),
				new Position(-3, 0)
		);

		game.fireShots(shots);

		assertEquals(0.0, game.getAccuracy(), 0.0001, "A precisão deve ser 0 sem tiros válidos.");
	}

	@Test
	@DisplayName("getAccuracy deve calcular percentagem corretamente")
	void getAccuracyWithValidShots() {
		IFleet fleet = game.getMyFleet();
		Ship ship = new Frigate(Compass.NORTH, new Position(2, 2));
		fleet.addShip(ship);

		List<IPosition> shots = List.of(
				new Position(2, 2), // # hit
				new Position(0, 0), // # miss
				new Position(0, 1)  // # miss
		);

		game.fireShots(shots);

		assertEquals(33.3333, game.getAccuracy(), 0.01, "A precisão deve ser 1/3 * 100.");
	}

	@Test
	@DisplayName("readEnemyFire deve aceitar 3 posições no formato clássico")
	void readEnemyFireValidInput() {
		Scanner scanner = new Scanner("A1 B1 C1");

		String json = game.readEnemyFire(scanner);

		assertNotNull(json, "O JSON devolvido não deve ser null.");
		assertEquals(1, game.getAlienMoves().size(), "Deve ser criada uma jogada.");
		assertTrue(json.contains("\"row\""), "O JSON deve conter a chave row.");
		assertTrue(json.contains("\"column\""), "O JSON deve conter a chave column.");
	}

	@Test
	@DisplayName("readEnemyFire deve falhar quando não existem exatamente 3 posições")
	void readEnemyFireInvalidNumberOfPositions() {
		Scanner scanner = new Scanner("A1 B1");

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(scanner));

		assertEquals("Você deve inserir exatamente 3 posições!", exception.getMessage());
	}

	@Test
	@DisplayName("undoLastMove não deve falhar sem jogadas")
	void undoLastMoveWhenEmpty() {
		assertDoesNotThrow(() -> game.undoLastMove());
		assertEquals(0, game.getAlienMoves().size(), "Continua sem jogadas.");
		assertEquals(0, game.getTotalShots(), "Continua sem tiros.");
	}

	@Test
	@DisplayName("undoLastMove deve remover a última jogada")
	void undoLastMoveRemovesMove() {
		List<IPosition> shots = List.of(new Position(1, 1), new Position(1, 2), new Position(1, 3));
		game.fireShots(shots);

		game.undoLastMove();

		assertEquals(0, game.getAlienMoves().size(), "A jogada deve ser removida.");
		assertEquals(0, game.getTotalShots(), "Após desfazer, não deve haver tiros contabilizados.");
	}

	// # em GameTest.java adiciona estes testes novos dentro da classe

	@Test
	@DisplayName("jsonShots deve serializar corretamente as posições")
	void jsonShots() {
		List<IPosition> shots = List.of(
				new Position('A', 1),
				new Position('C', 4),
				new Position('J', 10)
		);

		String json = Game.jsonShots(shots);

		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
		assertTrue(json.contains("A"));
		assertTrue(json.contains("C"));
		assertTrue(json.contains("J"));
		assertTrue(json.contains("1"));
		assertTrue(json.contains("4"));
		assertTrue(json.contains("10"));
	}

	@Test
	@DisplayName("readEnemyFire deve aceitar formato separado por letra e número")
	void readEnemyFireSeparatedTokens() {
		Scanner scanner = new Scanner("A 1 B 1 C 1");

		String json = game.readEnemyFire(scanner);

		assertNotNull(json);
		assertEquals(1, game.getAlienMoves().size());
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
	}

	@Test
	@DisplayName("readEnemyFire deve falhar quando a coluna não é seguida por linha")
	void readEnemyFireIncompletePosition() {
		Scanner scanner = new Scanner("A B1 C1");

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(scanner));

		assertTrue(exception.getMessage().contains("Posição incompleta!"));
	}

	@Test
	@DisplayName("randomEnemyFire deve devolver JSON e criar uma jogada")
	void randomEnemyFire() {
		String json = game.randomEnemyFire();

		assertNotNull(json);
		assertEquals(1, game.getAlienMoves().size(), "Deve ser criada uma jogada aleatória.");
		assertEquals(3, game.getAlienMoves().get(0).getShots().size(), "A jogada deve ter 3 tiros.");
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
	}

	@Test
	@DisplayName("randomEnemyFire deve funcionar quando existem menos de 3 posições úteis")
	void randomEnemyFireWithFewAvailablePositions() {
		for (int r = 0; r < Game.BOARD_SIZE; r++) {
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				if (!((r == 0 && c == 0) || (r == 0 && c == 1))) {
					game.fireSingleShot(new Position(r, c), false);
				}
			}
		}

		String json = game.randomEnemyFire();

		assertNotNull(json);
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(3, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("printBoard sem legendas e sem tiros não deve falhar")
	void printBoardWithoutShotsAndLegend() {
		IFleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));

		assertDoesNotThrow(() -> Game.printBoard(fleet, List.of(), false, false));
	}

	@Test
	@DisplayName("printBoard com tiros e legenda não deve falhar")
	void printBoardWithShotsAndLegend() {
		IFleet fleet = new Fleet();
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		List<IPosition> shots = List.of(
				new Position(1, 1),
				new Position(0, 0),
				new Position(2, 2)
		);

		List<IGame.ShotResult> results = List.of(
				new IGame.ShotResult(true, false, ship, true),
				new IGame.ShotResult(true, false, null, false),
				new IGame.ShotResult(true, false, null, false)
		);

		Move move = new Move(1, shots, results);

		assertDoesNotThrow(() -> Game.printBoard(fleet, List.of(move), true, true));
	}

	@Test
	@DisplayName("printMyBoard não deve falhar")
	void printMyBoard() {
		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@Test
	@DisplayName("printAlienBoard não deve falhar")
	void printAlienBoard() {
		assertDoesNotThrow(() -> game.printAlienBoard(true, true));
	}

	@Test
	@DisplayName("showStatistics não deve falhar e deve imprimir informação")
	void showStatistics() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(output));

		try {
			game.showStatistics();
		} finally {
			System.setOut(originalOut);
		}

		String text = output.toString();
		assertTrue(text.contains("ESTATÍSTICAS DO JOGO"));
		assertTrue(text.contains("Total de tiros"));
		assertTrue(text.contains("Precisão"));
	}

	@Test
	@DisplayName("showReplay deve imprimir o cabeçalho e o fim")
	void showReplay() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(output));

		try {
			game.showReplay();
		} finally {
			System.setOut(originalOut);
		}

		String text = output.toString();
		assertTrue(text.contains("REPLAY DO JOGO"));
		assertTrue(text.contains("FIM DO REPLAY"));
	}

	@Test
	@DisplayName("over não deve falhar")
	void over() {
		assertDoesNotThrow(() -> game.over());
	}
}