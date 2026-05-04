package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Ship.
 */
public abstract class Ship implements IShip {

	private static final String GALEAO = "galeao";
	private static final String FRAGATA = "fragata";
	private static final String NAU = "nau";
	private static final String CARAVELA = "caravela";
	private static final String BARCA = "barca";

	/**
	 * Factory method to create ships
	 */
	static Ship buildShip(String shipKind, Compass bearing, Position pos) {
		Objects.requireNonNull(shipKind);
		Objects.requireNonNull(bearing);
		Objects.requireNonNull(pos);

		return switch (shipKind) {
			case BARCA -> new Barge(bearing, pos);
			case CARAVELA -> new Caravel(bearing, pos);
			case NAU -> new Carrack(bearing, pos);
			case FRAGATA -> new Frigate(bearing, pos);
			case GALEAO -> new Galleon(bearing, pos);
			default -> throw new IllegalArgumentException("Invalid ship type: " + shipKind);
		};
	}

	private final String category;
	private final Compass bearing;
	private final IPosition pos;
	private final int size;

	protected List<IPosition> positions;

	public Ship(String category, Compass bearing, IPosition pos, int size) {
		this.category = Objects.requireNonNull(category, "Ship's category must not be null");
		this.bearing = Objects.requireNonNull(bearing, "Ship's bearing must not be null");
		this.pos = Objects.requireNonNull(pos, "Ship's position must not be null");
		this.size = size;
		this.positions = new ArrayList<>();
	}

	@Override
	public String getCategory() {
		return category;
	}

	public List<IPosition> getPositions() {
		return positions;
	}

	public List<IPosition> getAdjacentPositions() {
		List<IPosition> adjacentPositions = new ArrayList<>();

		for (IPosition position : positions) {
			for (IPosition adj : position.adjacentPositions()) {
				if (!positions.contains(adj) && !adjacentPositions.contains(adj)) {
					adjacentPositions.add(adj);
				}
			}
		}
		return adjacentPositions;
	}

	@Override
	public IPosition getPosition() {
		return pos;
	}

	@Override
	public Compass getBearing() {
		return bearing;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean stillFloating() {
		return positions.stream().anyMatch(p -> !p.isHit());
	}

	@Override
	public int getTopMostPos() {
		return getExtremeRow(true);
	}

	@Override
	public int getBottomMostPos() {
		return getExtremeRow(false);
	}

	@Override
	public int getLeftMostPos() {
		return getExtremeColumn(true);
	}

	@Override
	public int getRightMostPos() {
		return getExtremeColumn(false);
	}

	private int getExtremeRow(boolean findMin) {
		int value = positions.get(0).getRow();

		for (IPosition pos : positions) {
			int row = pos.getRow();
			if (findMin ? row < value : row > value) {
				value = row;
			}
		}
		return value;
	}

	private int getExtremeColumn(boolean findMin) {
		int value = positions.get(0).getColumn();

		for (IPosition pos : positions) {
			int col = pos.getColumn();
			if (findMin ? col < value : col > value) {
				value = col;
			}
		}
		return value;
	}

	@Override
	public boolean occupies(IPosition pos) {
		Objects.requireNonNull(pos);

		for (IPosition p : positions) {
			if (p.equals(pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean tooCloseTo(IShip other) {
		Objects.requireNonNull(other);

		for (IPosition pos : other.getPositions()) {
			if (tooCloseTo(pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean tooCloseTo(IPosition pos) {
		Objects.requireNonNull(pos);

		for (IPosition p : positions) {
			if (p.isAdjacentTo(pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void shoot(IPosition pos) {
		Objects.requireNonNull(pos);

		for (IPosition position : positions) {
			if (position.equals(pos)) {
				position.shoot();
			}
		}
	}

	@Override
	public void sink() {
		for (IPosition position : positions) {
			position.shoot();
		}
	}

	@Override
	public String toString() {
		return "[" + category + " " + bearing + " " + pos + "]";
	}
}