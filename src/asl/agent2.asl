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
	parameters.randomAlphaBeta(Alpha,Beta);
	.random(X);
	+weight(X*5);
	+alpha(Alpha);
	+beta(Beta);
	!search.
	
+!search: (not nest & not food & search(nest)) | (food & search(nest)) <-
	?alpha(Alpha);
	?beta(Beta);
	releasePheromone;
	moveToNextPosition(Alpha,Beta,nest);
	.wait(50);
	!search.

+!search: (not nest & not food & search(food)) | (nest & search(food)) <-
	?alpha(Alpha);
	?beta(Beta);
	moveToNextPosition(Alpha,Beta,food);
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
	!evaluate.

-!walkIntoNest <-
	?fail_clustering(X);
	-+fail_clustering(X+1);
	-+success_clustering(0);
	?clustering(P);
	.max([0.3,P-(X+1)*0.1],Y);
	-+clustering(Y);
	!evaluate.

	
+!evaluate: leave(P) & .random(N) & P>=N <-
	.wait(1000);
	-+search(food);
	!search.

-!evaluate <-
	.wait(1000);
	!evaluateNest.
	
+!evaluateNest: clustering(P) & .random(N) & P>=N <-
	-+step(0);
	moveIntoNest;
	!walkIntoNest.
	
-!evaluateNest <-
	!evaluate.
		
+!pickUpFood <-
	?weight(W);
	pickUpFood(W);
	+carrying;
	-+search(nest).
	
-!pickUpFood: not heavy & not ready <-
	-+search(nest).
	
-!pickUpFood<- .drop_intention(search).	

+!releasePheromoneItem: not ready <-
	releasePheromoneItem;
	.wait(2000);
	!releasePheromoneItem.
	
-!releasePheromoneItem.
	
+!moveTo(X,Y) : X==-1 & Y==-1 & not nest<-
	releasePheromone.
	
+!moveTo(X,Y): not (X==-1) & not (Y==-1) & not nest<-
	moveTo(X,Y);
	releasePheromone.

-!moveTo(_,_).

+!moveItem <-
	?alpha(Alpha);
	?beta(Beta);
	?weight(W);
	moveItem(Alpha,Beta,nest,W).

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
	!evaluate.
		
+nest: not carrying & search(nest)<-
	?fail(X);
	-+fail(X+1);
	-+success(0);
	?leave(P);
	.max([0.3,P-(X+1)*0.1],Y);
	-+leave(Y);
	!evaluate.

+heavy <-
	!releasePheromoneItem.
	
+ready<-
	removePerceptCooperativeTransport;
	-+search(nest);
	+carrying;
	!moveItem;
	.wait(50).

+next_position(X,Y) <-
	removeNextPosition;
	.wait(50);
	!moveTo(X,Y);
	!moveItem.

+onItem: not carrying <- 
	itemInNeighborhood;
	?item(Item);
	math.pow(4/(4+Item),2,Result);
	!pickUpItemNest(Result).