package com.example.a15puzzle;

class PuzzleLogic {
    private final String solution_str = "1234567891011121314150";
    private final int[] final_values = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0};
    private int[][] table = new int[4][4];
    private int blank;
    private String curr_str = "";

    protected PuzzleLogic(int[][] table){
        for(int r = 0; r < 4; r++){
            for(int c = 0; c < 4; c++){
                int tmp = table[r][c];
                this.table[r][c] = tmp;
                this.curr_str += tmp;
                if(tmp == 0) this.blank = r;
            }
        }
    }

    protected boolean isGoal(){
        return this.curr_str.equals(this.solution_str);
    }
    protected boolean isSolvable(boolean curr){
        /*An even table is solvable if:
            a)the blank_row from bottom is odd and #inversions is even
            b)the blank_row from bottom is odd and #inversions is even
            In general, (bottom_even) == (inversions %2 == 0)
        */
        int[] table;
        if(!curr) table = this.final_values;
        else {
            int i = 0;
            table = new int[16];
            for (int[] row : this.table) for (int col : row) table[i++] = col;
        }

        int inversions = 0;
        boolean bottom_even=!(this.blank%2==0);
        for(int i=0; i<16; i++){
            if(table[i]==0) continue;
            for(int j = i+1; j<16; j++){
                if(table[j] < table[i] && table[j] != 0)
                    inversions++;
            }
        }return (bottom_even) == (inversions %2 == 0);
    }

    private String printTable(){
        String s = "{";
        for(int r = 0; r < 3; r++){
            s += " r-"+r+" = (";
            for(int c = 0; c < 3; c++) s += this.table[r][c] + ", ";
            s += this.table[r][3] +") | ";
        }
        s += "r-3 = (";
        for(int c = 0; c < 3; c++) s += this.table[3][c] + ", ";
        s += this.table[3][3] +") }";
        return s;
    }

    @Override
    public String toString(){
        return this.printTable();
    }
}
