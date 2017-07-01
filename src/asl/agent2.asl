// Agent agent in project foraging
success(0).
success_clustering(0).
fail_clustering(0).
fail(0).
leave(1).
clustering(1).
search(item).
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
	
+!search: (not nest & not item & search(nest)) | (item & search(nest)) <-
	?alpha(Alpha);
	?beta(Beta);
	?position(_,_,P);
	releasePheromone;
	moveToNextPosition(Alpha,Beta,nest,P);
	.wait(50);
	!search.

+!search: (not nest & not item & search(item)) | (nest & search(item)) <-
	?alpha(Alpha);
	?beta(Beta);
	?position(_,_,P);
	moveToNextPosition(Alpha,Beta,item,P);
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
	?position(X,Y,Z);
	moveTo(X,Y,Z);
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
	-+search(item);
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
		
+!pickUpItem <-
	?position(XCoord,YCoord,T);
	moveTo(XCoord,YCoord,"E");
	?weight(W);
	pickUpItem(W);
	+carrying;
	-+search(nest).
	
-!pickUpItem: not heavy & not ready <-
	-+search(nest).
	
-!pickUpItem<- .drop_intention(search).	

+!releasePheromoneItem: not ready <-
	releasePheromoneItem;
	releasePheromone;
	.wait(2000);
	!releasePheromoneItem.
	
-!releasePheromoneItem.
	
+!moveTo(X,Y,_) : X==-1 & Y==-1 & not nest<-
	releasePheromone.
	
+!moveTo(X,Y,Z): not (X==-1) & not (Y==-1) & not nest<-
	moveTo(X,Y,Z);
	releasePheromone.

-!moveTo(_,_,_).

+!moveItem(O) <-
	?alpha(Alpha);
	?beta(Beta);
	?weight(W);
	moveItem(Alpha,Beta,nest,W,O).

+item: search(item) <-
	!pickUpItem.

+nest: carrying & search(nest)<-
	?position(XCoord,YCoord,_);
	moveTo(XCoord,YCoord,"O");
	-carrying;
	dropItem;
	?success(X);
	-+success(X+1);
	-+fail(0);
	?leave(P);
	.min([1,P+(X+1)*0.2],Y);
	-+leave(Y);
	!evaluate.
		
+nest: not carrying & search(nest)<-
	?position(XCoord,YCoord,_);
	moveTo(XCoord,YCoord,"O");
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
	?position(_,_,O);
	!moveItem(O);
	.wait(50).

+next_position(X,Y,Z) <-
	removeNextPosition;
	.wait(50);
	!moveTo(X,Y,Z);
	!moveItem(Z).

+onItem: not carrying <- 
	itemInNeighborhood;
	?item(Item);
	math.pow(4/(4+Item),2,Result);
	!pickUpItemNest(Result).