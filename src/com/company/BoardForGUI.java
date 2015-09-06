package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 *Class handels all the presentation logic. Including controlls.
 to controll the number of threads the program uses please look in the
 * stepProcessor class
 * @author Ronniet
 */

public class BoardForGUI extends JPanel implements ActionListener, FixedData
{
    private Panel board;
    private JPanel life;
    private Timer timerOfGame;
    private boolean isPaused;
    private int cellSize = 5;
    private JButton next, pausePlay, reset, preset;
    private JScrollPane scrollPane;

    // for camera movement.
    private AffineTransform affineTransform;
    private boolean upPressed, leftPressed, downPressed,spacePressed,
        rightPressed;

    // handles drawing and showing cells to life.
    private MouseAdapter mouseAdapter = new MouseAdapter()
    {
        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (isPaused) drawAtPoint(e);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (isPaused) drawAtPoint(e);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe)
        {
            int notches = mwe.getWheelRotation();

            if ((cellSize >= 3) || (cellSize <= 50))
            {
                if (notches < 0)
                {
                    cellSize++;
                    System.out.println("mouseweel<0");////ok
                } else
                {
                    cellSize--;
                    System.out.println("mouseweel>0");////ok
                }
                if ((cellSize == 1))
                {
                    cellSize = 2;
                }
            }
        }
    };

    /**
     * Constructor. Panel GUI must be initialized with the board filled of cells
     * that is showed.
     *
     * @param board board being visualized.
     */

    public BoardForGUI(Panel board)
    {
        this.board = board;
        this.setLayout(new BorderLayout());
        this.isPaused = true;
        this.timerOfGame = new Timer(10, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (! board.isUpdating()) stepBoard();
            }
        });

        //  up the main panel
        life = getLifePanel();
        life.setPreferredSize(new Dimension(FixedData.WINDOW_SIZE,
            FixedData.WINDOW_SIZE));
        life.setBackground(FixedData.BG_COLOR);
        life.addMouseMotionListener(mouseAdapter);
        life.addMouseListener(mouseAdapter);
        life.addMouseWheelListener(mouseAdapter);
        this.add(life, BorderLayout.CENTER);

        /**
         *
         * @param e
         */

        JPanel controlls = new JPanel();
        next = new JButton("NEXT");
        next.addActionListener(this);
        controlls.add(next);

        pausePlay = new JButton("GROWTH");
        pausePlay.setActionCommand("GROWTH/PAUSE");
        pausePlay.addActionListener(this);
        controlls.add(pausePlay);

        reset = new JButton("RESET");
        reset.addActionListener(this);
        controlls.add(reset);

        preset = new JButton("PRESET");
        preset.addActionListener(this);
        controlls.add(preset);

        controlls.setBackground(this.getBackground().darker());
        this.addKeyListener(getKeySetUP());
        this.add(controlls, BorderLayout.SOUTH);
    }


    // used to translate a gui point into the board grid.
    private void drawAtPoint(MouseEvent e)
    {
        int x = (int) (e.getPoint().x - affineTransform.getTranslateX()) / cellSize;
        int y = (int) (e.getPoint().y - affineTransform.getTranslateY()) / cellSize;
        board.spawn(x, y);
        repaint();
    }

    /* returns a key adapter to handle the keys being pressed.
     * For panning and tilting. */
    private KeyAdapter getKeySetUP()
    {
        return new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case 38:
                        upPressed = true;
                        break;
                    case 40:
                        downPressed = true;
                        break;
                    case 39:
                        rightPressed = true;
                        break;
                    case 37:
                        leftPressed = true;
                    case 32:
                        spacePressed = true;
                        timerOfGame.start();
                        break;
                    case 27:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case 38:
                        upPressed = false;
                        break;
                    case 40:
                        downPressed = false;
                        break;
                    case 39:
                        rightPressed = false;
                        break;
                    case 37:
                        leftPressed = false;
                        break;
                    default:
                        break;
                }
            }
        };
    }

    // moves the board forward on its own thread.
    private void stepBoard()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                board.step();
            }
        });
        BoardForGUI.this.grabFocus();
        repaint();
    }

    /**
     * gets and returns the Life display panel.
     *
     * @return
     */
    private JPanel getLifePanel()
    {
        return new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                boolean[][] grid = board.getGrid();

                shift(g); // adjust the panel reflecting spectators translations
                g.setColor(FixedData.CELL_COLOR);


                // the additional cell visibility checks (rowView, columnView)
                // skip some cells from being processed that are not in view.
                for (int i = 0; i < grid.length; i++)
                {
                    if (rowView(i)) continue;

                    for (int j = 0; j < grid.length; j++)
                    {
                        if (columnView(j)) continue;

                        if (grid[i][j])
                        {
                            g.fillRect(i * cellSize, j * cellSize,
                                cellSize, cellSize);
                        }

                        if (columnView(j)) break;
                    }

                    if (rowView(i)) break;
                }
            }
        };

    }

    /* Used when drawing to determine is a given column is out of view
    * and rid some of column that are out of vieew*/
    private boolean columnView(int j)
    {
        return ((j * cellSize) + affineTransform.getTranslateY() < 0)
            || ((j * cellSize) + affineTransform.getTranslateY()
            > (FixedData.WINDOW_SIZE +800));
    }

    /* Used when drawing to determine is a given column is out of view
    * and rid some of rows that are out of vieew*/
    private boolean rowView(int i)
    {
        return ((i * cellSize) + affineTransform.getTranslateX() < 0)
            || ((i * cellSize) + affineTransform.getTranslateX()
            > (FixedData.WINDOW_SIZE + 800) );
    }

    /*
     * modifies the graphic context to support controlls object
     */
    private void shift(Graphics g)
    {
        int STEP = 5;
        Graphics2D g2d = (Graphics2D) g;

        if (affineTransform == null) affineTransform = g2d.getTransform();

        if (upPressed) affineTransform.translate(0, STEP);
        if (downPressed) affineTransform.translate(0, -STEP);
        if (leftPressed) affineTransform.translate(STEP, 0);
        if (rightPressed) affineTransform.translate(-STEP, 0);

        g2d.setTransform(affineTransform);
    }

    // all the buttons control
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "NEXT":
                stepBoard();
                break;

            case "GROWTH/PAUSE":
                isPaused = !isPaused; //toggle button
                pausePlay.setText(isPaused ? "GROWTH"
                    + "" : "PAUSE");

                next.setEnabled(isPaused);
                reset.setEnabled(isPaused);

                if (isPaused) timerOfGame.stop();
                else timerOfGame.start();
                BoardForGUI.this.grabFocus(); //wrestling with swing...
                break;

            case ("RESET"):
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        board.randomize(new Random(), FixedData.PROB_OF_LIVE);
                    }
                });

            case ("PRESET"):
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        board.randomize(new Random(), FixedData.PROB_OF_LIVE);
                    }
                });
                BoardForGUI.this.grabFocus();
                repaint();
                break;

            default:
                System.out.println(e.getActionCommand());
        }
    }

    public static void main(String[] args)
    {
        Panel board = new Panel(FixedData.BOARD_SIZE);
        board.randomize(new Random(4), FixedData.PROB_OF_LIVE);

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                final BoardForGUI boardGui = new BoardForGUI(board);
                JFrame frame = new JFrame("Conway's Game of Life");
                frame.add(boardGui);
                frame.pack();
                frame.setVisible(true);
                frame.setResizable(true);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                boardGui.grabFocus();
                frame.setLocationRelativeTo(null);


                new Timer(10, new AbstractAction()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        boardGui.repaint();
                    }
                }).start();
            }
        });
    }

}