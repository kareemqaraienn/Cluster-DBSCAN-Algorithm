import:-
    csv_read_file('partition65.csv', Data65, [functor(partition)]),maplist(assert, Data65),
    csv_read_file('partition74.csv', Data74, [functor(partition)]),maplist(assert, Data74),
    csv_read_file('partition75.csv', Data75, [functor(partition)]),maplist(assert, Data75),
    csv_read_file('partition76.csv', Data76, [functor(partition)]),maplist(assert, Data76),
    csv_read_file('partition84.csv', Data84, [functor(partition)]),maplist(assert, Data84),
    csv_read_file('partition85.csv', Data85, [functor(partition)]),maplist(assert, Data85),
    csv_read_file('partition86.csv', Data86, [functor(partition)]),maplist(assert, Data86),listing(partition).


%find the intersecion between 2 lists and returns a list
%intersection([], _, []).
%intersection([[PointID|Rest]|T], L2, [H1|Res]) :-
    %member(PointID, L2),
    %intersection(Rest, L2, Res).

%intersection(T1, L2, Res) :-
    %intersecion(R, L2, Res).





%finds the union of 2 lists and returns them as a list
union([], L, L).
union([Head|L1tail], L2, L3) :-
        memberchk(Head, L2),
        !,
        union(L1tail, L2, L3).
union([Head|L1tail], L2, [Head|L3tail]) :-
        union(L1tail, L2, L3tail). 





%returns the clusters in the list
getCluster(L):-
    setof(C, P^D^X^Y^partition(P, D, X, Y, C), L).


%returns all attributes of the partition except for partitionID
knowledgeBaseFunction(L):-
    findall([D,X,Y,C],partition(_,D,X,Y,C),L).

%relabels the intersecionList ids with clusters id
relabel(_,_,[],[]):-!. 
relabel(Val,Rep,[Val|B],[Rep|Q]) :-
    relabel(Val,Rep,B,Q), !.
relabel(Val,Rep,[A|B],[A|Q]) :-
    A \= Val,
    relabel(Val,Rep,B,Q), !.