package battleship;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Represents a position on the game board.
 * A position is defined by its row and column coordinates,
 * and it can be occupied or hit during the game.
 */
public class Position implements IPosition {

	public static final char ASCII_A_OFFSET = 'A';
	/**
	 * The row coordinate of the position.
	 */
	private final int row;

	/**
	 * The column coordinate of the position.
	 */
	private final int column;

	/**
	 * Indicates whether the position is occupied by a ship.
	 */
	private boolean isOccupied;

	/**
	 * Indicates whether the position has been hit by an attack.
	 */
	private boolean isHit;

	//------------------------------------------------------------------
	public static Position randomPosition() {
		// Generate random position on the board
		int row = (int) (Math.random() * Game.BOARD_SIZE);
		int col = (int) (Math.random() * Game.BOARD_SIZE);
		return new Position(row, col);
	}
	/**
	 * Constructs a new Position with the specified row and column.
	 * By default, the position is not occupied and not hit.
	 *
	 * @param classicRow    the row coordinate of the position
	 * @param classicColumn the column coordinate of the position
	 */
	public Position(char classicRow, int classicColumn) {
		this.row = Character.toUpperCase(classicRow) - ASCII_A_OFFSET;
		this.column = classicColumn-1;
		this.isOccupied = false;
		this.isHit = false;
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The classic position that has been read
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {
		// Verifica se ainda há tokens disponíveis
		if (!in.hasNext()) {
			throw new IllegalArgumentException("Nenhuma posição válida encontrada!");
		}

		String part1 = in.next(); // Primeiro token
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next(); // Segundo token, se disponível
		}

		String input = (part2 != null) ? part1 + part2 : part1;

		// Normalizar o input para tratar letras maiúsculas e minúsculas
		input = input.toUpperCase();

		// Verificar os dois formatos possíveis: compactos e com espaço
		if (input.matches("[A-Z]\\d+")) {
			char column = input.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(input.substring(1)); // Extrair a linha
			return new Position(column, row);
		} else if (part2 != null && part1.matches("[A-Z]") && part2.matches("\\d+")) {
			char column = part1.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(part2); // Extrair a linha
			return new Position(column, row);
		} else {
			throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
		}
	}

	public void unshoot()
	{
		this.isHit = false;
	}

	/**
	 * Constructs a new Position with the specified row and column.
	 * By default, the position is not occupied and not hit.
	 *
	 * @param row    the row coordinate of the position
	 * @param column the column coordinate of the position
	 */
	public Position(int row, int column) {
		this.row = row;
		this.column = column;
		this.isOccupied = false;
		this.isHit = false;
	}

	/**
	 * Returns the row coordinate of the position.
	 *
	 * @return the row coordinate
	 */
	@Override
	public int getRow() {
		return row;
	}

	/**
	 * Returns the column coordinate of the position.
	 *
	 * @return the column coordinate
	 */
	@Override
	public int getColumn() {
		return column;
	}

	/**
	 * Gets traditional row.
	 *
	 * @return the traditional row within [A-J]
	 */
	public char getClassicRow() {
		return (char) (ASCII_A_OFFSET + row);
	}

	/**
	 * Gets traditional column.
	 *
	 * @return the traditional column within [1-10]
	 */
	public int getClassicColumn() {
		return column + 1;
	}
	/**
	 * Checks if this position is valid on the game board.
	 * A position is "inside" if its row and column are within the board's boundaries.
	 *
	 * @return true if the position is within the board, false otherwise
	 */
	@Override
	public boolean isInside(int boardSize) {
		return row >= 0 && column >= 0 && row < boardSize && column < boardSize;
	}

	/**
	 * Checks if this position is adjacent to another position.
	 * Two positions are adjacent if they are next to each other horizontally, vertically, or diagonally.
	 *
	 * @param other the other position to compare
	 * @return true if the positions are adjacent, false otherwise
	 */
	@Override
	public boolean isAdjacentTo(IPosition other) {
		return Math.abs(this.row - other.getRow()) <= 1 && Math.abs(this.column - other.getColumn()) <= 1;
	}

	/**
	 * Returns all valid adjacent positions (up, right, down, left) for this position.
	 * A valid position is one that exists within the board boundaries.
	 * @return List of valid adjacent positions
	 */
	@Override
	public List<IPosition> adjacentPositions() {

		List<IPosition> adjacents = new ArrayList<IPosition>();

		int row = this.getRow();
		int col = this.getColumn();

		// Define possible directions (up, right, down, left)
		int[][] directions = {
				{-1, 0},  // north
				{0, 1},   // east
				{1, 0},   // south
				{0, -1},   // west
				{1, 1},   // northeast
				{1, -1},  // northwest
				{-1, 1},  // southeast
				{-1, -1} // southwest
		};

		// Check each possible direction
		for (int[] dir : directions) {
			Position newPosition = new Position(row + dir[0], col + dir[1]);
			// Only add the position if it's inside the board boundaries
			if (newPosition.isInside(Game.BOARD_SIZE)) {
				adjacents.add(newPosition);
			}
		}

		return adjacents;
	}

	/**
	 * Checks if this position is occupied by a ship.
	 *
	 * @return true if the position is occupied, false otherwise
	 */
	@Override
	public boolean isOccupied() {
		return isOccupied;
	}

	/**
	 * Checks if this position has been hit by an attack.
	 *
	 * @return true if the position is hit, false otherwise
	 */
	@Override
	public boolean isHit() {
		return isHit;
	}

	/**
	 * Marks this position as occupied by a ship.
	 */
	@Override
	public void occupy() {
		isOccupied = true;
	}

	/**
	 * Marks this position as hit by an attack.
	 */
	@Override
	public void shoot() {
		isHit = true;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return row == position.row && column == position.column;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, column);
	}

	/**
	 * Returns a string representation of this position.
	 * The string includes the row and column coordinates.
	 *
	 * @return the string representation of the position
	 */
	@Override
	public String toString() {
		return (char) (ASCII_A_OFFSET + row) + "" + (column + 1);
//		return "Row = " + (char) ('A' + row) + ", Column = " + (column + 1);
	}
}