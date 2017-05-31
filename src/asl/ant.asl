// Agent ant in project foraging
success(0).
fail(0).
leave(1).
search(food).
step(0).
/* Initial beliefs and rules */

/* Initial goals */

!initialize.

/* Plans */

+!initialize <-
	parameters.randomAlphaBeta(Alpha,Beta);
	.random(X);
	+weight(X*5);
	+alpha(Alpha);
	+beta(Beta);
	!search.
	
+!search: (not nest & not food) | (nest & search(food)) | (food & search(nest)) <-
	?alpha(Alpha);
	?beta(Beta);
	?search(T);
	releasePheromone(T);
	moveToNextPosition(Alpha,Beta,T);
	.wait(200);
	!search.

-!search.

+!pickUpItemNest(Result): .random(X) & Result>=X <-
	+carrying;
	pickUpItemNest.

-!pickUpItemNest(Result).

+!walkIntoNest: carrying & step(C) <- 
	-+step(C+1); 
	walkIntoNest;
	itemInNeighborhood;
	.wait(200);
	?item(Item);
	math.pow(Item/(4+Item),2,Result);
	.random(X);
	?(Result>=X);
	dropItemNest; 
	-carrying;
	!walkIntoNest.

+!walkIntoNest: step(C) & C<100 <-
	walkIntoNest;
	.wait(200);
	-+step(C+1);
	!walkIntoNest.

+!walkIntoNest: step(C) & C>=100 <-
	?position(X,Y);
	moveTo(X,Y);
	?leave(P);
	.random(T);
	!evaluate(T,P).

-!walkIntoNest <- !walkIntoNest.

+!evaluate(N,P): P>=N <-
	.wait(1000);
	-+search(food);
	!search.

+!evaluate(N,P) <-
	-+step(0);
	moveIntoNest;
	!walkIntoNest.
	/*.wait(1000)
	.random(X)
	!evaluate(X,P).*/
	
+!pickUpFood <-
	removePerceptCooperativeTransport;
	.abolish(ant_position(_,_,_,_));
	?weight(W);
	pickUpFood(W);
	+carrying;
	-+search(nest).
	
-!pickUpFood: not heavy & not ready(_,_)<-
	-+search(nest).
	
-!pickUpFood<- .drop_intention(search).	

+!releasePheromoneItem: not ready(_,_) <-
	releasePheromoneItem;
	?search(T);
	releasePheromone(T);
	.wait(2000);
	!releasePheromoneItem.
	
-!releasePheromoneItem.
	
+!moveTo(X,Y) : X==-1 & Y==-1 & not nest<- 
	?search(T);
	?weight(Weight);
	releasePheromone(T)./*;
	?alpha(Alpha);
	?beta(Beta);
	?search(T);
	getNextPosition(Alpha,Beta,T).*/
	
+!moveTo(X,Y): not (X==-1) & not (Y==-1) & not nest<-
	moveTo(X,Y);
	?search(T);
	?weight(Weight);
	releasePheromone(T)./*;
	?alpha(Alpha);
	?beta(Beta);
	?search(T);
	getNextPosition(Alpha,Beta,T).*/

-!moveTo(_,_).

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
	.random(T);
	!evaluate(T,Y).
		
+nest: not carrying & search(nest)<-
	?fail(X);
	-+fail(X+1);
	-+success(0);
	?leave(P);
	.max([0.3,P-(X+1)*0.1],Y);
	-+leave(Y);
	.random(T);
	!evaluate(T,Y).

+heavy <-
	!releasePheromoneItem.
	
+ready(N,List) <-
	-+search(nest);
	+carrying;
	?alpha(Alpha);
	?beta(Beta);
	?search(T);
	getNextPosition(Alpha,Beta,T);
	.wait(200);
	?next_position(NextX,NextY);
	.my_name(Name);
	?weight(W);
	.send(List,tell,ant_position(Name,NextX,NextY,W)).

+ant_position(_,_,_,_) : .count(ant_position(_,_,_,_),X) & ready(Y,List) & X==Y  & search(nest)<- 
	.findall(position(N,W,S,Z),ant_position(N,W,S,Z),L);
	.abolish(ant_position(_,_,_,_));
	.wait(100);
	?weight(Weight);
	?next_position(MyXcoord,MyYcoord);
	movement.calculateNextMove(L,position("",MyXcoord,MyYcoord,Weight),Xcoord,Ycoord);
	//.println("Prossima pos: ",Xcoord," ",Ycoord);
	!moveTo(Xcoord,Ycoord);
	.my_name(Name);
	?weight(W);
	?alpha(Alpha);
	?beta(Beta);
	?search(T);
	getNextPosition(Alpha,Beta,T);
	.wait(200);
	?next_position(NextX,NextY);
	.send(List,tell,ant_position(Name,NextX,NextY,W)).

+onItem: not carrying <- 
	itemInNeighborhood;
	?item(Item);
	math.pow(4/(4+Item),2,Result);
	!pickUpItemNest(Result).