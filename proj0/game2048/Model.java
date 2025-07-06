package game2048;

import java.util.Formatter;
import java.util.Observable;

//好想写一辈子的代码~~~
/** The state of a game of 2048.
 *  @author hyh
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean canMerge(Tile a, Tile b) {
        return a != null && b != null && a.value() == b.value();
    }//辅助函数，判断能否融合
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        /*   I will use board.move,board.set_view  board.tile  */
        board.setViewingPerspective(side);
        boolean[][] has_merged = new boolean[2][4];
        int row1= 2;//左下角是坐标原点
        for (int col = 0; col < board.size(); col++) {
            Tile t=board.tile(col, row1);
            if(t==null) {
                continue;
            }
            if (board.tile(col, row1+1)==null) {
                changed = true;
            }
            if(secure_move(col,row1+1,t)){
                changed = true;
                score += board.tile(col,row1+1).value();
                has_merged[0][col] = true;
            }
        }//遍历第二行，判断是否能与第一行合并，能合并返回true并加分
        int row2= 1;
        for (int col = 0; col < board.size(); col++) {
            Tile t=board.tile(col, row2);
            if(t==null) {
                continue;
            }
            if (board.tile(col,2)==null) {
                if(board.tile(col,3)==null) {
                    changed = true;
                    board.move(col,row2+2,t);
                }else{
                    if(!has_merged[0][col]) {
                        if(secure_move(col,3,t)) {
                            changed = true;
                            score += board.tile(col, row2 + 2).value();
                            has_merged[0][col] = true;
                        }//这种情况涉及多merge
                        else{
                            changed = true;
                            board.move(col,row2+1,t);
                        }
                    }else{
                        changed = true;
                        board.move(col,row2+1,t);
                    }
                }
            }
            else {
                if (secure_move(col, row2 + 1, t)) {
                    changed = true;
                    score += board.tile(col, row2 + 1).value();
                    has_merged[1][col] = true;
                }
            }
        }//遍历第三行，判断是否能移至第一行,或者与第二行合并
        int row3=0;
        for (int col = 0; col < board.size(); col++) {
            Tile t=board.tile(col, row3);
            if(t==null) {
                continue;
            }
            if (board.tile(col,1)==null) {
                if(board.tile(col,2)==null) {
                    if(board.tile(col,3)==null) {
                        changed = true;
                        board.move(col,row3+3,t);
                    }else{
                        changed = true;
                        if(!has_merged[0][col]) {
                            if (secure_move(col, row3 + 3, t)) {
                                score += board.tile(col, row3 + 3).value();
                            }else{
                                board.move(col,row3+2,t);
                            }
                        }else{
                            board.move(col,row3+2,t);
                        }//这种情况涉及多merge
                    }
                }else{
                    changed = true;
                    if(!has_merged[1][col]) {
                        if(secure_move(col,row3+2,t)){
                            score += board.tile(col,row3+2).value();
                        }else {
                            board.move(col,row3+1,t);
                        }
                    }
                    else{
                        board.move(col,row3+1,t);
                    }//这种情况涉及多merge
                }
            }else{
                if(secure_move(col,row3+1,t)) {
                    changed = true;
                    score += board.tile(col,row3+1).value();
                }
            }
        }//遍历第四行，判断是否能移至第一行,或者第二行,或者与第三行合并
        checkGameOver();
        board.setViewingPerspective(Side.NORTH);
        if (changed) {
            setChanged();
        }
        return changed;
    }
    private boolean secure_move(int col,int row,Tile b){
        if(board.tile(col,row)==null) {
            return board.move(col,row,b);
        } else if (nulltozero(board.tile(col,row))==nulltozero(b)) {
            return board.move(col,row,b);
        }else {
            return false;
        }
    }
    private static int nulltozero(Tile t){
        if(t==null) {
            return 0;
        }
        else{
            return t.value();
        }
    }
//这里结束
    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        for(int row = 0; row < b.size(); row++) {
            for(int col = 0; col < b.size(); col++) {
                if(b.tile(row,col)==null) return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for(int row = 0; row < b.size(); row++) {
            for(int col = 0; col < b.size(); col++) {
                if(b.tile(row,col)!=null&&b.tile(row,col).value()==MAX_PIECE) return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean judge(Board b) {
        for (int row = 0; row < b.size(); row++) {
            for (int col = 0; col < b.size(); col++) {
                Tile currentTile = b.tile(row, col);
                if (currentTile == null) {
                    continue; // 如果当前方块为空，跳过当前循环
                }

                // 检查上方相邻方块
                if (row > 0) {
                    Tile aboveTile = b.tile(row - 1, col);
                    if (aboveTile != null && currentTile.value() == aboveTile.value()) {
                        return true;
                    }
                }

                // 检查下方相邻方块
                if (row < b.size() - 1) {
                    Tile belowTile = b.tile(row + 1, col);
                    if (belowTile != null && currentTile.value() == belowTile.value()) {
                        return true;
                    }
                }

                // 检查左侧相邻方块
                if (col > 0) {
                    Tile leftTile = b.tile(row, col - 1);
                    if (leftTile != null && currentTile.value() == leftTile.value()) {
                        return true;
                    }
                }

                // 检查右侧相邻方块
                if (col < b.size() - 1) {
                    Tile rightTile = b.tile(row, col + 1);
                    if (rightTile != null && currentTile.value() == rightTile.value()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean atLeastOneMoveExists(Board b) {


        if(emptySpaceExists(b)||judge(b)) {
            return true;
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
