(set-logic BV)

(define-fun shr1 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000001))
(define-fun shr4 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000004))
(define-fun shr16 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000010))
(define-fun shl1 ((x (BitVec 64))) (BitVec 64) (bvshl x #x0000000000000001))
(define-fun if0 ((x (BitVec 64)) (y (BitVec 64)) (z (BitVec 64))) (BitVec 64) (ite (= x #x0000000000000001) y z))

(synth-fun f ( (x (BitVec 64))) (BitVec 64)
(

(Start (BitVec 64) (#x0000000000000000 #x0000000000000001 x (bvnot Start)
                    (shl1 Start)
 		    (shr1 Start)
		    (shr4 Start)
		    (shr16 Start)
		    (bvand Start Start)
		    (bvor Start Start)
		    (bvxor Start Start)
		    (bvadd Start Start)
		    (if0 Start Start Start)
 ))
)
)
(constraint (= (f #xB26A03BC7B2BAF8B) #xC9A80EF1ECAEBE2D))
(constraint (= (f #x7F37A8B72575A52E) #xFCDEA2DC95D694B9))
(constraint (= (f #x5E137993BB490903) #x784DE64EED24240D))
(constraint (= (f #x59AAAEC41B8E1034) #x66AABB106E3840D1))
(constraint (= (f #x0F1D04A8278BF7D5) #x3C7412A09E2FDF55))
(constraint (= (f #x67CC741A0823B4DC) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #xCAEAA4EE4A52E991) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #xBD331A0E9C9038CF) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x943E05AD8803A68D) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x6C6E51542E93D459) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x0000000000000000) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFD))
(check-synth)
