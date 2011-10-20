public abstract class Bot extends AbstractSystemInputParser {
	private Ants ants;

	@Override
	public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2, int attackRadius2, int spawnRadius2) {
		setAnts(new Ants(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2, spawnRadius2));
	}

	public Ants getAnts() {
		return ants;
	}

	protected void setAnts(Ants ants) {
		this.ants = ants;
	}

	@Override
	public void beforeUpdate() {
		ants.setTurnStartTime(System.currentTimeMillis());
		ants.clearMyAnts();
		ants.clearEnemyAnts();
		ants.clearMyHills();
		ants.clearEnemyHills();
		ants.getFoodTiles().clear();
		ants.getOrders().clear();
	}

	@Override
	public void addWater(int row, int col) {
		ants.update(Ilk.WATER, new Tile(row, col));
	}

	@Override
	public void addAnt(int row, int col, int owner) {
		ants.update(owner > 0 ? Ilk.ENEMY_ANT : Ilk.MY_ANT, new Tile(row, col));
	}

	@Override
	public void addFood(int row, int col) {
		ants.update(Ilk.FOOD, new Tile(row, col));
	}

	@Override
	public void removeAnt(int row, int col, int owner) {
		ants.update(Ilk.DEAD, new Tile(row, col));
	}

	@Override
	public void addHill(int row, int col, int owner) {
		ants.updateHills(owner, new Tile(row, col));
	}

	@Override
	public void afterUpdate() {
	}
}
