// 
// 使用 Java 內建 Linked-List 
//
public class EightQueen
{
	private int QueenNum;				// 皇后的個數 ( the number of queen )
	private int solutions;				// 全部解達的數目 ( the number of solution )
	private int stat;					// 目前解答的編號 ( Now locate at Solution No. )
										// stat-1才是lsit中的資料編號
	private boolean[][] mp;
		// 計算解答所使用的棋盤(a broad for computing all solution)
	private java.util.LinkedList list;	// 所有解答 ( 資料編號由0開始 )


// 建構子 -----------------------------------------------------------
	EightQueen(){
		this(8);
	}

	EightQueen(int num)
	{
		if ( num<4 ){
			System.err.println( 
				"Queen Number is to small too find a solution.\n"+
				"The Queen Number automatically set 4.\n");
			num = 4;
		}
		QueenNum = num;
		solutions = 0;
		stat = 0;
		mp = new boolean[QueenNum][QueenNum];
		list = new java.util.LinkedList();
		// 計算解答
		ini_solution(0);
		mp = null;			// 使Java資源回收 , 減少記憶體使用量
		solutions = list.size();
	}

// 計算解答所使用的函式 ---------------------------------------------
	/** 計算 n-queen 的遞迴式 */
	private void ini_solution(int col)
	{
		if ( col==QueenNum )
			saveSolution();
		else{
			for (int row=0 ; row<QueenNum ; row++){
				if ( !canAttack(row,col) ){
					mp[row][col] = true;
					ini_solution(col+1);
					mp[row][col] = false;
				}
			}
		}//end else
	}//end ini_solution

	/** 判斷此位置是否可放皇后 */
	private boolean canAttack(int r ,int c)
	{
		for ( int col=0 ; col<c ; col++ ){
			for ( int row=0 ; row<QueenNum ; row++ ){
				if ( mp[row][col] == true )
				{
					if ( row==r || Math.abs(r-row)==Math.abs(c-col) )
						return true;
					break;
					// 1個column只找1個皇后後就跳到下一列 , 增加執行效率!
				}
			}
		}//end for
		return false;
	}//end canAttack

	/** 儲存解答 */
	private void saveSolution()
	{
		// 由於物件是用'參考'到實體的方式 ,所以不能用 list.add( mp ); ,必須要多做個陣列給它
		boolean[][] forlist = new boolean[QueenNum][QueenNum];
		for (int i=0;i<QueenNum;i++)
		{
			for (int j=0;j<QueenNum;j++)
			{
				forlist[i][j] = mp[i][j];
			}
		}
		list.add( forlist );

	}//end saveSolution

// 操作解答的方法 ---------------------------------------------------
	/** 目前解答編號 */
	public int currSolution(){
		return stat;
	}

	/** 所有解答個數 */
	public int countSolution(){
		return solutions;
	}

	/** 是否有前一個解答 */
	public boolean hasPrevSolution(){
		return (stat>1);
	}

	/** 是否有後一個解答 */
	public boolean hasMoreSolution(){
		return (stat<solutions);
	}

	/** 往前一個解答 */
	public boolean[][] nextSolution()
	{
		if ( hasMoreSolution()==false )
			return null;
		int tp = stat++;
		return (boolean[][])list.get(tp);
	}//end nextSolution

	/** 往前一個解答 */
	public boolean[][] prevSolution()
	{
		if ( hasPrevSolution()==false )
			return null;

		return (boolean[][])list.get(--stat-1);
			//--stat 是現在的解答 ,要在-1才是前一個解答
	}//end prevSolution

	/** 往第 n 個解答 */
	public boolean[][] gotoSolution(int Indx)
	{
		if ( Indx>solutions || Indx<1 )
			return null;

		return (boolean[][])list.get((stat = Indx)-1);
			//state = Indx ,then return list.get(stat-1);
	}//end gotoSolution

// 測試用主程式 -----------------------------------------------------
	public static void main(String[] args){
		EightQueen q = new EightQueen(8);
		boolean[][] m;
		while( q.hasMoreSolution() )
		{
			m = q.nextSolution();
			for (int r=0 ;r<m.length ;r++){
				for (int c=0 ;c<m[r].length ;c++)
					System.out.print( m[r][c]?" Q ":" _ ");
				System.out.println();
			}
			System.out.println();
			System.out.println(q.currSolution());
		}
	}//end main
}//end class Queen
