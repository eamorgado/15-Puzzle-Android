package com.example.a15puzzle;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    //Define max columns and rows
    private static final int MCOL = 4, MROW = 4, MDIM = 16;
    private static final String TAG = "TestActivity"; //To use on logs
    public static  final  String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right";

    public static int[][] BOARD = new int[MROW][MCOL];
    public static int pieces_width, pieces_height;
    private static ArrayList<Button> buttons;

    private static GestureDetectGridView grid_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        initBoard();
    }


    private void setDimensions(){
        ViewTreeObserver observer = grid_view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                grid_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int display_width = grid_view.getMeasuredWidth();
                int display_height = grid_view.getMeasuredHeight();

                float dip = 350f;
                Resources r = getResources();
                int height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dip,
                        r.getDisplayMetrics()
                );

                pieces_width = display_width / MCOL;
                pieces_height = height / MCOL;

                displayBoard(getApplicationContext());
            }
        });
    }
    private static void displayBoard(Context context){
        buttons = new ArrayList<>();
        Button button;
        //Generate Buttons
        for(int r = 0; r < MROW; r++){
            for(int c = 0; c < MCOL; c++){
                button = new Button(context);
                //add correct index
                button.setBackgroundResource(R.drawable.button_border);
                //button.setBackgroundColor(getResources().getColor(R.color.colorRedMix));
                button.setText(BOARD[r][c] != 0 ? ""+BOARD[r][c] : "");
                button.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteShy));
                button.setId(BOARD[r][c]);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(10,10,10,10);
                button.setLayoutParams(params);
                buttons.add(button);
            }
        }
        grid_view.setAdapter(new PuzzleAdapter(buttons,pieces_width,pieces_height));
    }


    private void initBoard() {
        //Function to initiate data and shuffle
        ArrayList<Integer> all_values = new ArrayList<>();
        int i = 0;
        for (; i < MDIM; i++) all_values.add(i);
        i = 0;
        //shuffle
        Collections.shuffle(all_values);
        for (int r = 0; r < MROW; r++) {
            for (int c = 0; c < MCOL; c++) {
                BOARD[r][c] = all_values.get(i++);
            }
        }


        //Check if board is valid aka if it is solvable and not the final setup
        PuzzleLogic logic = new PuzzleLogic(BOARD);
        Log.i(TAG, "initBoard() - Board: " + logic);

        if(logic.isGoal()){
            Log.i(TAG, "initBoard() - ERROR: already goal");
            initBoard();
            return;
        }
        if(!(logic.isSolvable(true) == logic.isSolvable(false))){
            //states are never reachable
            Log.i(TAG, "initBoard() - ERROR: Solution unreachable");
            initBoard();
            return;
        }

        //Table init = new Table(initial, 0, str);
        //Valid configuration -> add display
        grid_view = (GestureDetectGridView) findViewById(R.id.grid);
        grid_view.setNumColumns(MCOL);
        setDimensions();
    }


    public static void swap(Context context, int choice, int to_swap){
        int target = Integer.parseInt(buttons.get(choice).getText().toString());
        int goal = Integer.parseInt(buttons.get(to_swap).getText().toString());

        //find target/goal row and col
        int tr, tc, gr,gc;
        tr = tc = gr = gc = 0;
        for(int r = 0; r < MROW; r++){
            for(int c = 0; c < MCOL; c++){
                int tmp = BOARD[r][c];
                if(tmp == target){ tr = r; tc = c;}
                else if(tmp == goal){ gr = r; gc = c; }
            }
        }
        //Swap
        int tmp = BOARD[gr][gc];
        BOARD[gr][gc] = BOARD[tr][tc];
        BOARD[tr][tc] = tmp;

        buttons.get(choice).setText(""+goal);
        buttons.get(to_swap).setText(""+target);
        displayBoard(context);
    }
    public static void moveTiles(Context context, String direction, int tile_id){
        int[][] cond = {
                {0,1,2,3},
                {12,13,14,15},
                {0,4,8,12},
                {3,7,11,15}
        };
        List top = Arrays.asList(cond[0]);
        List bottom = Arrays.asList(cond[1]);
        List left = Arrays.asList(cond[2]);
        List right = Arrays.asList(cond[3]);

        boolean c0 = !buttons.get(tile_id).getText().toString().equals("");
        boolean c1 = top.contains(tile_id) && direction.equals(UP);
        boolean c2 = bottom.contains(tile_id) && direction.equals(DOWN);
        boolean c3 = left.contains(tile_id) && direction.equals(LEFT);
        boolean c4 = right.contains(tile_id) && direction.equals(RIGHT);

        if(c0) Toast.makeText(context,"Invalid Piece", Toast.LENGTH_SHORT).show();
        else if(c1 || c2 || c3 || c4)
            Toast.makeText(context,"Invalid Move", Toast.LENGTH_SHORT).show();
        else{
            int v = 0;
            v = direction.equals(LEFT) ? -1 : direction.equals(RIGHT) ? 1 : 0;
            int goal = direction.equals(DOWN) ? tile_id + MROW
                    : direction.equals(UP) ? tile_id - MROW : tile_id + v;

            swap(context,tile_id,goal);
        }
    }
}
