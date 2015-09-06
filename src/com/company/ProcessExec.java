package com.company;

/**
 * Created by Ronniet on 9/5/2015.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Class is moving the board next by one generation.
 *I used threads for speedup processosrs.
 *
 * to change the max number of possible thread please adjust the below constant.

 * @author Ronniet
 */
public class ProcessExec implements FixedData
{

  /**
   * Retuns the next generation of the board
   * @param board
   * @return
   */
  public static boolean[][] stepGrid(Panel board)
  {
    long startTime = 0;

    ExecutorService exec = Executors.newFixedThreadPool(FixedData.MAX_NUMBER_THREADS);
    boolean[][] target = new boolean[board.getSize()][board.getSize()];

    CellWorker.setBoard(board);
    CellWorker.getTarget(target);

    if (FixedData.TIMING)
    {
      startTime = System.currentTimeMillis();
    }

    for (int i = 0; i < board.getSize(); i++)
    {
      exec.execute(new CellWorker(i));
    }
    exec.shutdown();

    while (!exec.isTerminated())
    {
      // waiting for threads termination
    }

    if (FixedData.TIMING)
    {
      long timedf = System.currentTimeMillis() - startTime;
      System.out.println("# seconds: " + timedf / 1000.0);
    }
    return target;
  }


  /* Class that paralellizes rendering the next generations,
   * each Cell worker must  write a row of the resulting board */
  private static class CellWorker implements Runnable
  {
    // all class  share same resources avoiding duplicate code
    private static Panel board;
    private static boolean[][] target;

    public static void setBoard(Panel board)
    {
      CellWorker.board = board;
    }

    public static void getTarget(boolean[][] target)
    {
      CellWorker.target = target;
    }

    /* instance members */
    private int rowIndex;

    /**
     * Constructor for the class assigns a row index to each cell
     */
    public CellWorker(int rowIndex)
    {
      this.rowIndex = rowIndex;
    }

    @Override
    public void run()
    {

      for (int col = 0; col < board.getSize(); col++)
      {
        target[rowIndex][col] = this.board.aliveNext(rowIndex, col);
      }
    }
  }

}
