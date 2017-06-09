// Agent agent in project foraging
success(0).
success_clustering(0).
fail_clustering(0).
fail(0).
leave(1).
clustering(1).
search(food).
step(0).
/* Initial beliefs and rules */

/* Initial goals */

!initialize.

/* Plans */

+!initialize <-
	.random(X);
	+weight(X*5);
	!search.
	
+!search: (not nest & not food) | (nest & search(food)) | (food & search(nest)) <-
	?search(T);
	releasePheromone(T);
	moveToNextPosition(T);
	.wait(50);
	!search.

-!search.

+!pickUpItemNest(Result): .random(X) & Result>=X <-
	+carrying;
	pickUpItemNest;
	+item_moved;
	?success_clustering(T);
	-+success_clustering(T+1);
	-+fail_clustering(0);
	?clustering(P);
	.min([1,P+(T+1)*0.2],Y);
	-+clustering(Y).

-!pickUpItemNest(Result).

+!dropItemNest(Item): math.pow(Item/(4+Item),2,Result) & .random(X) & Result>=X <-
	dropItemNest;
	-carrying.
	
-!dropItemNest(_).

+!walkIntoNest: carrying & step(C) <- 
	-+step(C+1); 
	walkIntoNest;
	itemInNeighborhood;
	.wait(50);
	?item(Item);
	!dropItemNest(Item);
	!walkIntoNest.

+!walkIntoNest: step(C) & C<100 <-
	walkIntoNest;
	.wait(50);
	-+step(C+1);
	!walkIntoNest.

+!walkIntoNest: step(C) & C>=100 <-
	?position(X,Y);
	moveTo(X,Y);
	?item_moved;
	-item_moved;
	?leave(P);
	!evaluate(P).
	
-!walkIntoNest <-
	?fail_clustering(X);
	-+fail_clustering(X+1);
	-+success_clustering(0);
	?clustering(P);
	.max([0.3,P-(X+1)*0.1],Y);
	-+clustering(Y);
	?leave(Leave);
	!evaluate(Leave).


+!evaluate(P): .random(N) & P>=N <-
	.wait(1000);
	-+search(food);
	!search.

-!evaluate(_) <-
	.wait(1000);
	!evaluateNest.
	
+!evaluateNest: clustering(P) & .random(N) & P>=N <-
	-+step(0);
	moveIntoNest;
	!walkIntoNest.
	
-!evaluateNest <-
	?leave(P);
	!evaluate(P).
		
+!pickUpFood <-
	removePerceptCooperativeTransport;
	.abolish(agent_position(_,_,_,_));
	?weight(W);
	pickUpFood(W);
	+carrying;
	-+search(nest).
	
-!pickUpFood: not heavy & not ready(_,_)<-
	-+search(nest).
	
-!pickUpFood<- .drop_intention(search).	

+!releasePheromoneItem: not ready(_,_) <-
	releasePheromoneItem;
	releasePheromone(food);
	.wait(2000);
	!releasePheromoneItem.
	
-!releasePheromoneItem.
	
+!moveTo(X,Y) : X==-1 & Y==-1 & not nest<- 
	?weight(Weight);
	releasePheromone(nest).
	
+!moveTo(X,Y): not (X==-1) & not (Y==-1) & not nest<-
	moveTo(X,Y);
	?weight(Weight);
	releasePheromone(nest).

-!moveTo(_,_).

+!sendNextPosition: ready(_,List) <-
	?next_position(NextX,NextY);
	.my_name(Name);
	?weight(W);
	.send(List,tell,agent_position(Name,NextX,NextY,W)).

-!sendNextPosition <- !sendNextPosition.

+food: search(food) <-
	!pickUpFood.

+nest: carrying & search(nest)<-
	-carrying;
	dropFood;
	?success(X);
	-+success(X+1);
	-+fail(0);
	?leave(P);
	.min([1,P+(X+1)*0.2],Y);
	-+leave(Y);
	!evaluate(Y).
		
+nest: not carrying & search(nest)<-
	?fail(X);
	-+fail(X+1);
	-+success(0);
	?leave(P);
	.max([0.3,P-(X+1)*0.1],Y);
	-+leave(Y);
	!evaluate(Y).

+heavy <-
	!releasePheromoneItem.
	
+ready(N,List) <-
	-+search(nest);
	+carrying;
	getNextPosition(nest);
	.wait(100);
	!sendNextPosition.

+agent_position(_,_,_,_) : .count(agent_position(_,_,_,_),X) & ready(Y,List) & X==Y  & search(nest)<- 
	.findall(position(N,W,S,Z),agent_position(N,W,S,Z),L);
	.abolish(agent_position(_,_,_,_));
	.wait(50);
	?weight(Weight);
	?next_position(MyXcoord,MyYcoord);
	movement.calculateNextMove(L,position("",MyXcoord,MyYcoord,Weight),Xcoord,Ycoord);
	!moveTo(Xcoord,Ycoord);
	getNextPosition(nest);
	.wait(50);
	!sendNextPosition.

+onItem: not carrying <- 
	itemInNeighborhood;
	?item(Item);
	math.pow(4/(4+Item),2,Result);
	!pickUpItemNest(Result).