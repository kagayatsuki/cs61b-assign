package timingtest;

/** An SLList is a list of integers, which hides the terrible truth
 * of the nakedness within. */
public class SLList<Item> {
	private class IntNode {
		public Item item;
		public IntNode next;

		public IntNode(Item i, IntNode n) {
			item = i;
			next = n;
		}
	}

	/* The first item (if it exists) is at sentinel.next. */
	private IntNode sentinel;
	private int size;

	/** Creates an empty timingtest.SLList. */
	public SLList() {
		sentinel = new IntNode(null, null);
		size = 0;
	}

	public SLList(Item x) {
		sentinel = new IntNode(null, null);
		sentinel.next = new IntNode(x, null);
		size = 1;
	}
//空的头结点
	/** Adds x to the front of the list. */
	public void addFirst(Item x) {
		sentinel.next = new IntNode(x, sentinel.next);
		size ++;
	}
//头插法
	/** Returns the first item in the list. */
	public Item getFirst() {
		return sentinel.next.item;
	}
//头结点
	/** Adds x to the end of the list. */
	public void addLast(Item x) {
		size = size + 1;//不用加也可以（）

		IntNode p = sentinel;

		/* Advance p to the end of the list. */
		while (p.next != null) {
			p = p.next;
		}

		p.next = new IntNode(x, null);
	}
//尾插法
	/** returns last item in the list */
	public Item getLast() {
		IntNode p = sentinel;

		/* Advance p to the end of the list. */
		while (p.next != null) {
			p = p.next;
		}

		return p.item;
	}


	/** Returns the size of the list. */
	public int size() {
		return size;
	}
	public void print() {
		IntNode p = sentinel.next;
		while (p.next != null) {
			System.out.print(p.item + "->");
			p = p.next;
		}
		System.out.println(p.item+"->null");
	}//自己加的
	public static void main(String[] args) {
		/* Creates a list of one integer, namely 10 */
		SLList L = new SLList();
		SLList L2 = new SLList(12);
		L.addLast(20);
		L.addLast(30);
		L.addLast(40);
		L.addLast(50);
		System.out.println(L.size());
		L.print();
	}
}
