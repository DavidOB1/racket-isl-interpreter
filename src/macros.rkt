;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname macros) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; Functions for numbers
(define (add1 n) (+ n 1))
(define (sub1 n) (- n 1))
(define (sqr n) (* n n))
(define (abs n) (if (< n 0) (* n -1) n))
(define (negative? n) (< n 0))
(define (positive? n) (> n 0))
(define (sgn n) (if (< n 0) -1 1))
(define (zero? n) (= n 0))
(define (remainder n m)
  (if (and (integer? n) (integer? m))
      (modulo n m)
      (error "remainder must be given two integers")))
(define (even? n)
  (zero? (modulo n 2)))
(define (odd? n)
  (not (even? n)))
(define-struct posn [x y])

; Functions for strings
(define key=? string=?)

; Functions for lists
(define head first)
(define tail rest)
(define car first)
(define cdr rest)
(define (second l)
  (first (rest l)))
(define (third l)
  (first (rest (rest l))))
(define (fourth l)
  (first (rest (rest (rest l)))))
(define (fifth l)
  (first (rest (rest (rest (rest l))))))
(define (sixth l)
  (first (rest (rest (rest (rest (rest l)))))))
(define (seventh l)
  (first (rest (rest (rest (rest (rest (rest l))))))))
(define (eighth l)
  (first (rest (rest (rest (rest (rest (rest (rest l)))))))))
(define (ninth l)
  (first (rest (rest (rest (rest (rest (rest (rest (rest l))))))))))
(define (reverse l)
  (rev-helper l empty))
(define (rev-helper l acc)
  (if (empty? l)
      acc
      (rev-helper (rest l) (cons (first l) acc))))
(define (map f lox)
  (if (empty? lox)
      empty
      (cons (f (first lox)) (map f (rest lox)))))
(define (ormap pred lox)
  (if (empty? lox)
      false
      (or (pred (first lox)) (ormap pred (rest lox)))))
(define (andmap p lox)
  (if (empty? lox)
      true
      (and (p (first lox)) (andmap p (rest lox)))))
(define (filter p lox)
  (if (empty? lox)
      empty
      (if (p (first lox))
          (cons (first lox)
                (filter p (rest lox)))
          (filter p (rest lox)))))
(define (foldr f base lox)
  (if (empty? lox)
      base
      (f (first lox)
         (foldr f base (rest lox)))))
(define (foldl f base lox)
  (foldr f base (reverse lox)))
(define (build-list n f)
  (build-list-helper (sub1 n) f empty))
(define (build-list-helper n f acc)
  (if (< n 0)
      acc
      (build-list-helper (sub1 n) f (cons (f n) acc))))
(define (list? l)
  (or (empty? l) (cons? l)))	
(define (append l1 l2)
  (if (empty? l1)
      l2
      (cons (first l1) (append (rest l1) l2))))   
(define (length l)
  (if (empty? l)
      0
      (add1 (length (rest l)))))
(define (sort l f)
  (if (empty? l)
      l
      (insertion-sort-helper (first l) (sort (rest l) f) f)))
(define (insertion-sort-helper a l f)
  (if (or (empty? l) (f a (first l)))
      (cons a l)
      (cons (first l) (insertion-sort-helper a (rest l) f))))


; Functions for images
(define (square s o c)
  (rectangle s s o c))
(define (empty-scene w h)
  (rectangle w h "outline" "black"))
(define (place-images imgs posns scene)
  (cond
    [(and (empty? imgs) (empty? posns)) scene]
    [(and (cons? imgs) (cons? posns))
     (place-image (first imgs)
                  (posn-x (first posns))
                  (posn-y (first posns))
                  (place-images (rest imgs) (rest posns) scene))]
    [else (error "place-images must be given lists of posns and images of the same length")]))
