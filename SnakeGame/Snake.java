public class Snake extends Object
{
	// Snake's Body 
	private node head;
	private node tail;
	private int length;

	/** Constructor */
	public Snake()
	{
		head 	= new node(21*(18/2)+21/2);
		node n	= new node(head.pos-1);
		tail 	= new node(n.pos-1);
		head.next 	= n;
		tail.prev	= n;
		n.next		= tail;
		n.prev		= head;
		length+=3;
	}
	/** Snake eat the food */
	public void eatFood(){
		node n 		= new node(tail.pos,tail,null);
		tail.next 	= n;
		tail 		= n;
		length++;
	}
	public void moveUp(){
		move(UP);
	}
	public void moveDown(){
		move(DOWN);
	}
	public void moveLeft(){
		move(LEFT);
	}
	public void moveRight(){
		move(RIGHT);
	}
	public static final int RIGHT 	= 1;
	public static final int LEFT 	= -1;
	public static final int UP 	= -21;
	public static final int DOWN 	= 21;
	private void move(int md)
	{
		// 移動方向確認
		switch( head.next.pos-head.pos ){
			case 20:
				md = (md==LEFT)?RIGHT:md;
				System.err.println(" default ");
				break;
			case -20:
				md = (md==RIGHT)?LEFT:md;
				break;
			case 21*17:
				md = (md==UP)?DOWN:md;
				break;
			case -21*17:
				md = (md==DOWN)?UP:md;
				break;
			default:
				md = (head.pos+md==head.next.pos)? -md:md;
			break;
		}
		// 界外銜接
		int pos = 0;
		switch ( md ){
			case UP:
				pos = (head.pos/21==0)? 21*18+(head.pos+UP):head.pos+UP;
				break;
			case DOWN:
				pos = (head.pos/21==17)? (head.pos+UP)%21:head.pos+DOWN;
				break;
			case LEFT:
				pos = (head.pos%21==0)? (head.pos+20):head.pos+LEFT;
				break;
			case RIGHT:
				pos = (head.pos%21==20)? (head.pos-20):head.pos+RIGHT;
				break;
			default: System.err.println("error!"); break;
		}
		
		node n 		= new node(pos,null,head);
		head.prev	= n;
		head 		= n;
		tail		= tail.prev;
		tail.next	= null;
	}// end move
	
	/** Return the all node of SnakeBody */
	public int[] getSnakeBody()
	{
		int reVal[] = new int[length];
		node travel = head;
		for (int i=0;i<length;i++){
			reVal[i]	= travel.pos;
			travel		= travel.next;
		}
		return reVal;
	}// end getSnakeBody

}//end class

/** 蛇身體的每一個節點 */
class node{
	public node 	next;		// next node
	public node 	prev;		// previous node
	public int 	pos;			// position

	final static int NoPosAssigned = -1;
 
	node(){
		this(-1,null,null);		// no assign node position
	}
	node(int it){
		this(it,null,null);
	}
	node(int it ,node p ,node n){
		prev = p;
		next = n;
		pos = it;
	}
}