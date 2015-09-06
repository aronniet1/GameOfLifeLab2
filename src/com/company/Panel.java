package com.company;

/**
 * Created by Ronniet on 9/5/2015.
 */
import java.util.Random;

/**
 *This class for the grid setttings and generations
 * @author Ronniet
 */
public class Panel implements FixedData
{
  private boolean isUpdating;

  /**
   * Constructor for board.
   * @param size width and height of life square grid
   */
  public Panel(int size)
  {
    this.grid = new boolean[size][size];
  }

  public boolean isUpdating()

  {
    return isUpdating;
  }

  // base repesentation is simply grid of booleans value,
  // true is  alive, otherwise false.
  private boolean[][] grid;

  /**
   * Get the life grid
   * @return the grid representing the life universe.
   */
  public boolean[][] getGrid()
  {
    return grid;
  }

  /**
   * sets the grid of the game. This method is called after
   * updating the board to the next generation.
   * @param grid
   */
  public void setGrid(boolean[][] grid)
  {
    this.grid = grid;
  }

  /**
   * the height and the width of the board
   * @return the size of dimentions of the game.
   */
  public int getSize()
  {
    return grid.length;
  }

  /**
   *This method implements rule set and determines if the cell is alive
   * in the next generation.
   * @param x row index of the cell in question
   * @param y colum index of the cell in question
   * @return true if the cell should survive to the next generation, false
   * otherwise.
   */
  public boolean aliveNext(int x, int y)
  {
    int numOfNeigh = getNumOflivingNeighbors(x, y);
    boolean isAliveNow = grid[x][y];

    if (!isAliveNow && numOfNeigh == 3) return true;
    if (isAliveNow && (numOfNeigh == 2 || numOfNeigh == 3)) return true;

    return false;
  }

  /* are the x,y coord in bounds of the board */
  private boolean validCoords(int x, int y)
  {
    return (x >= 0 && x < this.getSize())
        && (y >= 0 && y < this.getSize());
  }

  /*
   * returns the square at the specifed x,y point
   * if the x,y are invalid  returns false
   * */
  private boolean getTile(int x, int y)
  {
    return validCoords(x, y) ? grid[x][y] : false;
  }

  /* get the number of cells that are currently alive
   * around  the cell  x,y.
   */
  private int getNumOflivingNeighbors(int x, int y)
  {
    int neighborsAlive = 0;

    if (getTile(x - 1, y)) neighborsAlive++;
    if (getTile(x - 1, y - 1)) neighborsAlive++;
    if (getTile(x - 1, y + 1)) neighborsAlive++;

    if (getTile(x, y - 1)) neighborsAlive++;
    if (getTile(x, y + 1)) neighborsAlive++;

    if (getTile(x + 1, y)) neighborsAlive++;
    if (getTile(x + 1, y - 1)) neighborsAlive++;
    if (getTile(x + 1, y + 1)) neighborsAlive++;

    return neighborsAlive;
  }

  public void step()
  {
    isUpdating = true;
    this.setGrid(ProcessExec.stepGrid(this));
    isUpdating = false;
  }

  /**
   * used to random spaw live across out the board.
   *
   * @param random random number generator to use.
   * @param p probablity value in square will be with live.
   */
  public void randomize(Random random, double p)
  {
    for (int i = 0; i < getSize(); i++)
    {
      for (int j = 0; j < getSize(); j++)
      {
        grid[i][j] = random.nextDouble() < p;
      }
    }
  }

  /**
   * Brings the specified square to life. Is called inside the GUI to draw life
   * on the board.
   * @param x row of cell.
   * @param y colum of cell.
   */
  public void spawn(int x, int y)
  {
    if (validCoords(x, y)) grid[x][y] = true;
  }
}

