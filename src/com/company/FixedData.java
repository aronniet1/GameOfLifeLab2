package com.company;

/**
 * Created by Ronniet on 9/5/2015.
 */
import java.awt.Color;

/**
 *This interface holds all constant of the project
 * @author Ronniet
 */
public interface FixedData
{
  double PROB_OF_LIVE = 0.2;
  int WINDOW_SIZE = 800;
  //int CELL_SIZE = 10;
  int BOARD_SIZE = 10000;// Matrix of 10 000 x 10 000
  Color CELL_COLOR = new Color(0, 0, 0);
  Color BG_COLOR = new Color(240, 230, 240);

  boolean TIMING = false; // outputs timing info.
  int MAX_NUMBER_THREADS = 50;

}
