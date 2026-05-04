package battleship;

import java.util.ArrayList;
import java.util.List;

public class Fleet implements IFleet {

	private final List<IShip> ships;

	public Fleet() {
		this.ships = new ArrayList<>();
	}

	public static IFleet createRandom() {

		Fleet randomFleet = new Fleet();

		String[] shipTypes = {
				"galeao",
				"fragata",
				"nau", "nau",
				"caravela", "caravela", "caravela",
				"barca", "barca", "barca", "barca"
		};

		int index = 0;

		while (index < shipTypes.length) {
			Ship ship = Ship.buildShip(
					shipTypes[index],
					Compass.randomBearing(),
					Position.randomPosition()
			);

			if (ship != null && randomFleet.addShip(ship)) {
				index++;
			}
		}

		return randomFleet;
	}

	@Override
	public List<IShip> getShips() {
		return ships;
	}

	@Override
	public boolean addShip(IShip s) {
		if (s == null) return false;

		if (isFleetFull() || !isInsideBoard(s) || hasCollision(s)) {
			return false;
		}

		ships.add(s);
		return true;
	}

	private boolean isFleetFull() {
		return ships.size() >= FLEET_SIZE;
	}

	@Override
	public List<IShip> getShipsLike(String category) {
		List<IShip> result = new ArrayList<>();

		for (IShip s : ships) {
			if (s.getCategory().equals(category)) {
				result.add(s);
			}
		}
		return result;
	}

	@Override
	public List<IShip> getFloatingShips() {
		List<IShip> result = new ArrayList<>();

		for (IShip s : ships) {
			if (s.stillFloating()) {
				result.add(s);
			}
		}
		return result;
	}

	@Override
	public List<IShip> getSunkShips() {
		List<IShip> result = new ArrayList<>();

		for (IShip s : ships) {
			if (!s.stillFloating()) {
				result.add(s);
			}
		}
		return result;
	}

	@Override
	public IShip shipAt(IPosition pos) {
		if (pos == null) return null;

		for (IShip ship : ships) {
			if (ship.occupies(pos)) {
				return ship;
			}
		}
		return null;
	}

	private boolean isInsideBoard(IShip s) {
		return s.getLeftMostPos() >= 0 &&
				s.getRightMostPos() <= Game.BOARD_SIZE - 1 &&
				s.getTopMostPos() >= 0 &&
				s.getBottomMostPos() <= Game.BOARD_SIZE - 1;
	}

	private boolean hasCollision(IShip s) {
		for (IShip ship : ships) {
			if (ship.tooCloseTo(s)) {
				return true;
			}
		}
		return false;
	}

	public void printShips(List<IShip> ships) {
		if (ships == null) return;

		for (IShip ship : ships) {
			System.out.println(ship);
		}
	}

	public void printStatus() {
		System.out.println("Estado da Frota: " +
				getFloatingShips().size() + " a flutuar, " +
				getSunkShips().size() + " afundados!");
	}

	public void printShipsByCategory(String category) {
		printShips(getShipsLike(category));
	}

	public void printFloatingShips() {
		printShips(getFloatingShips());
	}

	void printAllShips() {
		printShips(ships);
	}
}