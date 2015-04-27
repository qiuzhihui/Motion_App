import java.util.Stack;


class Node{
	Node next = null;
	int data;

	public Node(int d){
		data = d;
	}

	public Node(){
	}


	void add(int d){
		Node b = new Node(d);
		Node a = this;
		while(a.next!=null){
			a = a.next;
		}
		a.next = b;
	}

	int length(){
		int count = 0;
		Node a = this;
		while(a.next !=null){
			a = a.next;
			count++;
		}
		return count;
	}

	void delect(int c){
		if(c > this.length()){
			return;
		}

		Node a = this;
		int count = 0;
		c = c-2;

		while(count!=c){
			a = a.next;
			count++;
		}

		a.next = a.next.next;

	}



	Node partition(int c){
		Node a = this;
		Node b = new Node();
		c=c-2;

		if(c > this.length()){
			return null;
		}


		for(int i = 0; i<c; i++){
			a = a.next;
		}



		b = a.next;
		a.next = null;
		a = this;


		while(a!=null){
			b.add(a.data);
			a=a.next;
		}
	
		return b;

	}


	Node nodelocation(int c){

		Node a = this;

		for(int i =0; i<c; i++){
			a = a.next;

		}
		return a;
	}


}



class stack{
	public Node top;

	public void push(int a){
		Node tmp = new Node(a);
		tmp.next = top;
		top = tmp;

	}


	public Object pop(){
		if(top!=null){
			int tmp = top.data;
			top = top.next;
			return tmp;
		}
		return null;
	}

	public Object peek(){
		if(isEmpty()){
			return null;
		}
		return top.data;
	}

	public Boolean isEmpty() {
		 return (top == null);
	}

}


class stack1 extends stack{
	stack s2;

	void stack1(){
		s2 = new stack();
	}

	public void push(int a){

		super.push(a);
	}


	public Object pop(){
		Object value = super.pop();
		if(value == min()){
			s2.pop();
		}
		return value;
	}

	public Object min(){

		if(s2.isEmpty()){
			return Integer.MAX_VALUE;
		}else{
			return s2.peek();
		}
	}



}






class test3_2{
	public static void main(String[] argv){


		stack1 a = new stack1();


		a.push(3);
		System.out.println(a.isEmpty());

		a.push(2);
		a.push(1);
		a.push(0);

		a.pop();
		System.out.println(a.peek());




		System.out.println(a.pop());
		System.out.println(a.pop());
		System.out.println(a.pop());


	}

}


















