// 
// �ϥ� Java ���� Linked-List 
//
public class EightQueen
{
	private int QueenNum;				// �ӦZ���Ӽ� ( the number of queen )
	private int solutions;				// �����ѹF���ƥ� ( the number of solution )
	private int stat;					// �ثe�ѵ����s�� ( Now locate at Solution No. )
										// stat-1�~�Olsit������ƽs��
	private boolean[][] mp;
		// �p��ѵ��ҨϥΪ��ѽL(a broad for computing all solution)
	private java.util.LinkedList list;	// �Ҧ��ѵ� ( ��ƽs����0�}�l )


// �غc�l -----------------------------------------------------------
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
		// �p��ѵ�
		ini_solution(0);
		mp = null;			// ��Java�귽�^�� , ��ְO����ϥζq
		solutions = list.size();
	}

// �p��ѵ��ҨϥΪ��禡 ---------------------------------------------
	/** �p�� n-queen �����j�� */
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

	/** �P�_����m�O�_�i��ӦZ */
	private boolean canAttack(int r ,int c)
	{
		for ( int col=0 ; col<c ; col++ ){
			for ( int row=0 ; row<QueenNum ; row++ ){
				if ( mp[row][col] == true )
				{
					if ( row==r || Math.abs(r-row)==Math.abs(c-col) )
						return true;
					break;
					// 1��column�u��1�ӬӦZ��N����U�@�C , �W�[����Ĳv!
				}
			}
		}//end for
		return false;
	}//end canAttack

	/** �x�s�ѵ� */
	private void saveSolution()
	{
		// �ѩ󪫥�O��'�Ѧ�'����骺�覡 ,�ҥH����� list.add( mp ); ,�����n�h���Ӱ}�C����
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

// �ާ@�ѵ�����k ---------------------------------------------------
	/** �ثe�ѵ��s�� */
	public int currSolution(){
		return stat;
	}

	/** �Ҧ��ѵ��Ӽ� */
	public int countSolution(){
		return solutions;
	}

	/** �O�_���e�@�Ӹѵ� */
	public boolean hasPrevSolution(){
		return (stat>1);
	}

	/** �O�_����@�Ӹѵ� */
	public boolean hasMoreSolution(){
		return (stat<solutions);
	}

	/** ���e�@�Ӹѵ� */
	public boolean[][] nextSolution()
	{
		if ( hasMoreSolution()==false )
			return null;
		int tp = stat++;
		return (boolean[][])list.get(tp);
	}//end nextSolution

	/** ���e�@�Ӹѵ� */
	public boolean[][] prevSolution()
	{
		if ( hasPrevSolution()==false )
			return null;

		return (boolean[][])list.get(--stat-1);
			//--stat �O�{�b���ѵ� ,�n�b-1�~�O�e�@�Ӹѵ�
	}//end prevSolution

	/** ���� n �Ӹѵ� */
	public boolean[][] gotoSolution(int Indx)
	{
		if ( Indx>solutions || Indx<1 )
			return null;

		return (boolean[][])list.get((stat = Indx)-1);
			//state = Indx ,then return list.get(stat-1);
	}//end gotoSolution

// ���եΥD�{�� -----------------------------------------------------
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
