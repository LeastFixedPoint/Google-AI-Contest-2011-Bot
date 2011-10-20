import java.io.IOException;

public class MyBot extends Bot {
	public static void main(String[] args) throws IOException {
		new MyBot().readSystemInput();
	}

	@Override
	public void doTurn() {
		Ants ants = getAnts();
		for (Tile myAnt : ants.getMyAnts()) {
			for (Aim direction : Aim.values()) {
				if (ants.getIlk(myAnt, direction).isPassable()) {
					ants.issueOrder(myAnt, direction);
					break;
				}
			}
		}
	}
}
