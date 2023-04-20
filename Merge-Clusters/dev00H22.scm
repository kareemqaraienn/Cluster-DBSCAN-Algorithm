#lang scheme
(define (readlist filename)
 (call-with-input-file filename
  (lambda (in)
    (read in))))
(define (import)
  (let ((p65 (readlist "partition65.scm"))
        (p74 (readlist "partition74.scm"))
        (p75 (readlist "partition75.scm"))
        (p76 (readlist "partition76.scm"))
        (p84 (readlist "partition84.scm"))
        (p85 (readlist "partition85.scm"))
        (p86 (readlist "partition86.scm")))
    (append p65 p74 p75 p76 p84 p85 p86)))

(define (saveList filename L)
 (call-with-output-file filename
 (lambda (out)
 (write L out))))

;;returns a cluster from the list using its ID
(define (getC list ID)
    (cond
      ((null? list) '())
      ((eqv? (cadr (cdr (cdr (cdr (car list))))) ID)
          (cons (car list) (getC (cdr list) ID)))
          (else (getC (cdr list) ID))
      )
  )


;;removes a cluster from the list using its ID
(define (removeC list ID)
  (cond
    ((null? list) '())
      ((eqv? (cadr (cdddr (car list))) ID) (removeC (cdr list) ID))
          (else (cons (car list) (removeC (cdr list) ID)))
      )
  )

;;shortens the format of the cluster to be 4 elements by cutting off the partition ID
(define (shortenCluster list)
(cond
  ((null? list) '())
        (else (cons (cdr (car list)) (shortenCluster (cdr list))))))

;;sorts a list using the built in sort method that sorts the point IDs in ascending order
(define (sortID list)
  ;;returns a boolean that compares 2 cluster's point IDs together
  (define (isClusterSmaller cluster1 cluster2)
      (<= (cadr cluster1) (cadr cluster2))
  )
  (sort list isClusterSmaller)
)

;;returns the cluster id of the cluster in list using the point id (pID)
(define (getClID pID list)
(cond
  ((null? list) #f)
  ((eqv? (car (car list)) pID) (car (cdr (cdr (cdr (cdr (car list)))))))
  (else (getClID pID (cdr list))))
)

;;checks if a point ID (x) is in the database (list)
(define (isXinList x list)
(cond
  ((null? list) #f)
  ((eqv? (car (cdr (car list))) x) #t)
  (else (isXinList x (cdr list))))
)


;;returns a list of intersection elements between two lists (list1 and list2)
(define (listIntersection list1 list2)
(cond
  ((null? list1) '())
  ('((isXinList (car (cdr (car list1))) list2)) (cons (car list1) (listIntersection (cdr list1) list2)))
  (else (listIntersection (cdr list1) list2))
  )
)


;;returns a list of union elements between two lists (list1 and list2)
(define (union list1 list2)
  (cond ((null? list2) list1)
        ((isXinList (car (cdr (car list2))) list1)
         (union list1 (cdr list2)))
        (else (union (cons (car list2) list1) (cdr list2)))))



;;relabels the intersecionList id's with cluster's id
(define (UnionRelabel intersectionList c clusterList)
  (union (relabel intersectionList c clusterList) clusterList)
)



;;helper function for UnionRebales that grabs the intersectionList elements
(define (relabel intersectionList c clusterList)
  (cond
    ((null? intersectionList) '())
      (else (cons (list (car (car intersectionList)) (car (cdr (car intersectionList))) (car (cdr (cdr (car intersectionList)))) (car (cdr (cdr (cdr (car intersectionList))))) (getClID (car (car intersectionList)) c)) (relabel (cdr intersectionList) c clusterList)))
  )
)




;;merges all points in seperate partitions into 1 whole partition then sorts it and shortens it
(define (mergeClusters list)
  (shortenCluster (sortID (mergeClustersOnly list '())))
)


;;merges all points in seperate partitions into 1 whole partition
(define (mergeClustersOnly originalList finalList)
  (cond 
         ((null? originalList) finalList)
      (else (mergeClustersOnly (removeC originalList (car (cdr (cdr (cdr (cdr (car originalList))))))) (union (union (UnionRelabel (listIntersection (getC originalList (car (cdr (cdr (cdr (cdr (car originalList))))))) finalList) (getC originalList (car (cdr (cdr (cdr (cdr (car originalList))))))) finalList) finalList) (getC originalList (car (cdr (cdr (cdr (cdr (car originalList))))))))))
  )
)

(mergeClusters (import))
